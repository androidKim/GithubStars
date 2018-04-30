package com.midas.githubstars.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by midas on 2017-07-21.
 */
public class BitmapUtil
{
    //---------------------------------------------------------------------------------------------------
    // Get Remote URL Image By Bitmap
    public static Bitmap GetBitmapImageFromURL(String strUrl, boolean bUrlEncode)
    {
        if( strUrl == null || strUrl.length() <= 0 )
            return null;

        Bitmap pDownBitmap = null;

        try
        {
            String strEncodeUrl = strUrl;

            // URL Encoding
            if( bUrlEncode )
                strEncodeUrl = EncodeUrlString(strUrl, true, null);

            // Set URL
            URL stUrl = new URL(strEncodeUrl);

            // Download Image From URL
            HttpURLConnection stConn = (HttpURLConnection)stUrl.openConnection();
            stConn.setDoInput(true);

            // for UI Thread..
            if (android.os.Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            stConn.connect();

            InputStream stIS = stConn.getInputStream();
            if( stIS == null )
                return pDownBitmap;

            // Bitmap 바로이용
            if( false )
            {
                return BitmapFactory.decodeStream(stIS);
            }

            // ByteArray 이용
            else
            {
                int nLen = stConn.getContentLength();
                if( nLen <= 0 )
                    return pDownBitmap;

                byte[] btImgData = new byte[nLen*2];
                byte[] btTmp = new byte[512];
                int nReadLen = 0;
                int nDestPos = 0;

                while( true )
                {
                    // Read Data
                    nReadLen = stIS.read(btTmp);
                    if( nReadLen <= 0 )
                        break;

                    if( nDestPos+nReadLen > (nLen*2) )
                        break;

                    System.arraycopy(btTmp, 0, btImgData, nDestPos, nReadLen);
                    nDestPos += nReadLen;
                }

                //pDownBitmap = BitmapFactory.decodeByteArray(btImgData, 0, btImgData.length);

                // Bitmap의 크기가 너무커서 안될 때.. 압축하자..
                if( pDownBitmap == null )
                {
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inSampleSize	= 1;	// 축소비율 (1/n)
                    option.inPurgeable	= true;	// 메모리를 줄여주는 옵션 (시스템이 메모리를 돌려받기를 원할때 메모리에서 제거될 수 있도록 생성됨)
                    option.inDither		= true;	// 이미지를 깔끔하게 처리해서 보여주는 옵션

                    pDownBitmap = BitmapFactory.decodeByteArray(btImgData, 0, btImgData.length, option);
                }
            }

            return pDownBitmap;
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //----------------------------------------------------------------------------------------------------
    //get Bitmap From Local Path
    public static Bitmap getBitmapFromPath(String path)
    {
        Bitmap bitmap=null;
        try
        {
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
    //----------------------------------------------------------------------------------------------------
    // Convert String To URL Encoding String
    public static String EncodeUrlString(String strSource, boolean bExceptUrlChar, String strTextType)
    {
        if( strSource == null )
            return "";

        // Set Text Encoding Type
        if( strTextType == null || strTextType.length() <= 0 )
            strTextType = "UTF-8";

        String strEncode = "";
        try
        {
            // Parsing Source
            if( bExceptUrlChar == false )
            {
                // Full
                strEncode = URLEncoder.encode(strSource, strTextType);
            }
            else
            {
                // Except >> ':/ ?=&'
                String strWord = "";
                int nTextCnt = strSource.length();
                for(int i = 0; i < nTextCnt; i++)
                {
                    char szChar = strSource.charAt(i);
                    if( szChar == ':' )
                    {
                        strEncode += URLEncoder.encode(strWord, strTextType);
                        strEncode += ":";
                        strWord = "";
                    }
                    else if( szChar == '/' )
                    {
                        strEncode += URLEncoder.encode(strWord, strTextType);
                        strEncode += "/";
                        strWord = "";
                    }
                    else if( szChar == ' ' )
                    {
                        strEncode += URLEncoder.encode(strWord, strTextType);
                        strEncode += " ";
                        strWord = "";
                    }
                    else if( szChar == '?' )
                    {
                        strEncode += URLEncoder.encode(strWord, strTextType);
                        strEncode += "?";
                        strWord = "";
                    }
                    else if( szChar == '=' )
                    {
                        strEncode += URLEncoder.encode(strWord, strTextType);
                        strEncode += "=";
                        strWord = "";
                    }
                    else if( szChar == '&' )
                    {
                        strEncode += URLEncoder.encode(strWord, strTextType);
                        strEncode += "&";
                        strWord = "";
                    }
                    else
                    {
                        strWord += "" + szChar;
                    }
                }

                if( strWord.length() > 0 )
                    strEncode += URLEncoder.encode(strWord, strTextType);
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            strEncode = "";
        }

        return strEncode;
    }
    //------------------------------------------------------------------------------------------
    //Glide
    public static class CustomBitmapPool implements BitmapPool
    {
        @Override
        public int getMaxSize()
        {
            return 0;
        }

        @Override
        public void setSizeMultiplier(float sizeMultiplier)
        {

        }

        @Override
        public boolean put(Bitmap bitmap)
        {
            return false;
        }

        @Override
        public Bitmap get(int width, int height, Bitmap.Config config)
        {
            return null;
        }

        @Override
        public Bitmap getDirty(int width, int height, Bitmap.Config config)
        {
            return null;
        }

        @Override
        public void clearMemory()
        {

        }

        @Override
        public void trimMemory(int level)
        {

        }
    }

    //------------------------------------------------------------------------------------------
    //bitmap resize
    /*
     * 비트맵(Bitmap) 이미지의 가로, 세로 이미지를 리사이징
     * @param bmpSource 원본 Bitmap 객체
     * @param maxResolution 제한 해상도
     * @return 리사이즈된 이미지 Bitmap 객체
    */
    public static Bitmap resizeBitmap(Bitmap bmpSource, int maxResolution)
    {
        int iWidth = bmpSource.getWidth();      //비트맵이미지의 넓이
        int iHeight = bmpSource.getHeight();     //비트맵이미지의 높이
        int newWidth = iWidth ;
        int newHeight = iHeight ;
        float rate = 0.0f;

        //이미지의 가로 세로 비율에 맞게 조절
        if(iWidth > iHeight )
        {
            if(maxResolution < iWidth )
            {
                rate = maxResolution / (float) iWidth ;
                newHeight = (int) (iHeight * rate);
                newWidth = maxResolution;
            }
        }
        else
        {
            if(maxResolution < iHeight )
            {
                rate = maxResolution / (float) iHeight ;
                newWidth = (int) (iWidth * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(bmpSource, newWidth, newHeight, true);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    //bitmap save File
    public static String saveBitmapToJpeg(Context context, Bitmap bitmap, String name)
    {
        if(context == null || bitmap == null || name == null)
            return null;

        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로
        String fileName = name + ".jpg";  // 파일이름은 마음대로!
        File tempFile = new File(storage,fileName);
        try
        {
            tempFile.createNewFile();  // 파일을 생성해주고
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌
            out.close(); // 마무리로 닫아줍니다.
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴해주면 끝!
    }

    //----------------------------------------------------------------------------------------------------
    // 이미지 경로 정확히 가져오기
    public static String getRealPathFromURI(Context context, Uri uri)
    {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst())
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    //----------------------------------------------------------------------------------------------------
    // 파일사이즈 제한
    public static boolean checkImgUploadSize(File pFile)
    {
        //long nMaxSize = 5242880;//5M =>  byte
        //long nMaxSize = 3145728;//3M => byte
        long nMaxSize = 2097152;//2M => byte
        boolean bResult = false;

        if (pFile.exists())
        {
            long nFilesize = pFile.length();
            if(nFilesize > 0)
            {
                if(nFilesize > nMaxSize)//5M초과
                {
                    bResult = false;
                }
                else
                {
                    bResult = true;
                }
            }
            else
            {

            }
        }
        else
        {

        }
        return bResult;
    }
    //----------------------------------------------------------------------------------------------------
    //camera folder or takePhoto 이미지 회전 오류방지
    public static Bitmap getRotatedBitmap(String path)
    {
        Bitmap bitmap = null;

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(path, opts);
        ExifInterface exif = null;
        try
        {
            exif = new ExifInterface(path);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        bitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);

        return bitmap;
    }
}
