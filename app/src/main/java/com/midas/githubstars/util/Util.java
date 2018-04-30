package com.midas.githubstars.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * Created by midas on 2017-05-15.
 */

public class Util
{
    //----------------------------------------------------------------------------------------------------
    // Thread Sleep : Thread.sleep
    public static void SleepInThread(int nSleepTime)
    {
        try
        {
            Thread.sleep(nSleepTime);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    //----------------------------------------------------------------------------------------------------
    // String Encodig Base64
    public static String EncodeBase64String(String strString)
    {
        if( strString == null || strString.length() <= 0 )
            return "";

        // Convert String -> byte[]
        byte[] btSource = new byte[strString.length()];
        btSource = strString.getBytes();

        // Base64 Encoding
        byte[] btResult = null;
        try
        {
            Class pBase64 = Class.forName("org.apache.commons.codec.binary.Base64");
            Class[] pParameterTypes = new Class[] { byte[].class };
            Method pEncodeBase64 = pBase64.getMethod("encodeBase64", pParameterTypes);

            btResult = (byte[])pEncodeBase64.invoke(pBase64, btSource);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        // Check Result
        if( btResult == null )
            return "";

        // Convert byte[] -> String
        String strResult = new String(btResult);
        return strResult;
    }

    //----------------------------------------------------------------------------------------------------
    // String Decoding Base64
    public static String DecodeBase64String(String strString)
    {
        if( strString == null || strString.length() <= 0 )
            return "";

        // Convert String -> byte[]
        byte[] btSource = new byte[strString.length()];
        btSource = strString.getBytes();

        // Base64 Encoding
        byte[] btResult = null;
        try
        {
            Class pBase64 = Class.forName("org.apache.commons.codec.binary.Base64");
            Class[] pParameterTypes = new Class[] { byte[].class };
            Method pDecodeBase64 = pBase64.getMethod("decodeBase64", pParameterTypes);

            btResult = (byte[])pDecodeBase64.invoke(pBase64, btSource);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        // Check Result
        if( btResult == null )
            return "";

        // Convert byte[] -> String
        String strResult = new String(btResult);
        return strResult;
    }

    //----------------------------------------------------------------------------------------------------
    //
    public static String getNumberCommaFormat(int nValue)
    {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(nValue);
    }
    //----------------------------------------------------------------------------------------------------
    // App 설치유무 확인
    public static boolean IsPackageInstalled(Context pContext, String strPackageName)
    {
        boolean bResult = false;

        if( pContext == null || strPackageName == null || strPackageName.length() <= 0 )
            return false;

        try
        {
            pContext.getPackageManager().getPackageInfo(strPackageName, PackageManager.GET_ACTIVITIES);
            bResult = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            bResult = false;
        }

        return bResult;
    }
    //----------------------------------------------------------------------------------------------------
    //isAppForground
    public static boolean isAppInForground(Context context, String packageName)
    {
        String[] activePackages;
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        activePackages = getActivePackages(am);

        if (activePackages != null)
        {
            return activePackages[0].equals(packageName);
        }
        return false;
    }
    //----------------------------------------------------------------------------------------------------
    //
    public static String[] getActivePackages(ActivityManager mActivityManager)
    {
        final Set<String> activePackages = new HashSet<>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos)
        {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }

    //----------------------------------------------------------------------------------------------------
    // Url공유
    public static void setShareUrl(Context context, String strPackageName, String strUrl)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, strUrl);

        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches)
        {
            if (info.activityInfo.packageName.toLowerCase().startsWith(strPackageName))
            {
                intent.setPackage(info.activityInfo.packageName);
                break;
            }
        }
        context.startActivity(intent);
    }
    //----------------------------------------------------------------------------------------------------
    // Service 실행여부
    public static boolean isServiceRunning(Context context, String serviceName)
    {
        ActivityManager manager = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            String strServiceName = "com.siksin.oneshot"+serviceName;
            if(strServiceName.equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
    //----------------------------------------------------------------------------------------------------
    //
    public static int getResourceID(final String resName, final String resType, final Context ctx)
    {
        final int ResourceID = ctx.getResources().getIdentifier(resName, resType, ctx.getApplicationInfo().packageName);
        if (ResourceID == 0)
        {
            throw new IllegalArgumentException("No resource string found with name " + resName);
        }
        else
        {
            return ResourceID;
        }
    }

    //----------------------------------------------------------------------------------------------------
    // 핸드폰 번호 추출
    @SuppressLint("MissingPermission")
    public static String getPhoneNumber(Context context)
    {
        if( context == null )
        {
            return "";
        }

        String strPhoneNum ="";

        if (strPhoneNum.equals(""))
        {
            TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            if( telephony == null )
            {
                return "";
            }

            strPhoneNum = telephony.getLine1Number();//need permisstion

            if (strPhoneNum != null && strPhoneNum.startsWith("+"))
            {
                strPhoneNum = strPhoneNum.replace("+82", "0");
                strPhoneNum = strPhoneNum.replace("-", ""); //하이픈제거
            }
            else if(strPhoneNum==null)
            {
                strPhoneNum = "";
            }
        }
        return strPhoneNum;
    }
    //----------------------------------------------------------------------------------------------------
    //actionCall
    @SuppressLint("MissingPermission")
    public static void actionCall(Context context, String strPhone)
    {
        if(context == null || strPhone == null)
            return;

        //check Usim
        if(checkUsimState(context))
        {
            Intent pIntent = new Intent(Intent.ACTION_CALL);
            pIntent.setData(Uri.parse("tel:"+strPhone));
            try
            {
                ((Activity)context).startActivity(pIntent);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            //
            Toast.makeText(context, "NOT EXIST USIM", Toast.LENGTH_SHORT).show();
        }
    }
    //----------------------------------------------------------------------------------------------------
    //check usim
    public static boolean checkUsimState(Context context)
    {
        boolean bResult = false;
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT)//not exiest usim..
        {
            bResult = false;
        }
        else//exist Usim..
        {
            bResult = true;
        }

        return bResult;
    }
    //----------------------------------------------------------------------------------------------------
    //
    public static String getExternalStorageDir()
    {
        String path = null;
        String ext = Environment.getExternalStorageState();

        if(ext.equals(Environment.MEDIA_MOUNTED))
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        else
           path = Environment.MEDIA_UNMOUNTED;

        return path;
    }
    //----------------------------------------------------------------------------------------------------
    //check directory exist
    public static boolean fileExist(String path)
    {
        if(path == null)
            return false;

        boolean bResult = false;
        File pFile = new File(path);
        if(pFile.exists())
            bResult = true;
        else
            bResult = false;

        return bResult;
    }
    //----------------------------------------------------------------------------------------------------
    //make dir
    public static void fileMake(String path)
    {
        if(path == null)
            return;

        File pFile = new File(path);
        pFile.mkdir();
    }
    //----------------------------------------------------------------------------------------------------
    //get String from FilePath
    public static String readStringFromFile(Context context, String path)
    {
        String strResult = null;

        File pFile = new File(path);
        StringBuilder sb = new StringBuilder();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(pFile));
            String line;

            while ((line = br.readLine()) != null)
            {
                sb.append(line);
                sb.append('\n');
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        strResult = sb.toString();
        return strResult;
    }
    //------------------------------------------------------------------------------------
    //KeyValueString =>JSON
    public static JsonObject convertStringToJson(String strData)
    {
        String[] parts = strData.split("&");

        JsonObject jsonResult = new JsonObject();

        for(String part: parts)
        {
            String[] keyVal = part.split("="); // The equal separates key and values
            jsonResult.addProperty(keyVal[0], keyVal[1]);
        }

        return jsonResult;
    }
    //------------------------------------------------------------------------------------
    //
    public static void setStatusBarColor(Activity activity, int color, View pView)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)//api level 23이상.
        {
            if (pView != null)
            {
                //text theme
                pView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//배경이 흰색일경우에 대비해 text색상을 어두운계열로 해줌

                //bg color
                Window pWindow = activity.getWindow();
                pWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                pWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                pWindow.setStatusBarColor(color);
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)//api level 21이상이면..
        {
            if(pView == null)
            {
                //bg color
                Window pWindow = activity.getWindow();
                pWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                pWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                pWindow.setStatusBarColor(color);
            }
        }
    }
    /******************** getDeviceName *******************/
    //----------------------------------------------------------------------------------------------------
    //
    public static String getDeviceName()
    {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer))
        {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }
    //----------------------------------------------------------------------------------------------------
    //
    private static String capitalize(String str)
    {
        if (TextUtils.isEmpty(str))
        {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr)
        {
            if (capitalizeNext && Character.isLetter(c))
            {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            }
            else if (Character.isWhitespace(c))
            {
                capitalizeNext = true;
            }
            phrase.append(c);
        }
        return phrase.toString();
    }

    /************************************* DateTime *************************************/
    //----------------------------------------------------------------------------------------------------
    //
    public static long GetCurrentDateTime()
    {
        return System.currentTimeMillis();
    }
    //----------------------------------------------------------------------------------------------------
    //
    public static String GetCurrentDateTime(String strFormat)
    {
        if( strFormat == null || strFormat.length() <= 0 )
            strFormat = "yyyy-MM-dd HH:mm:ss";

        SimpleDateFormat pDateFormat = new SimpleDateFormat(strFormat);
        Date pDate = new Date();
        return pDateFormat.format(pDate);
    }
    //----------------------------------------------------------------------------------------------------
    //
    public static String GetCurrentYear()
    {
        Date pDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(pDate);
        int year = cal.get(Calendar.YEAR);
        String strYear = String.format("%s", year);
        return strYear;
    }
    //----------------------------------------------------------------------------------------------------
    //MM
    public static String GetCurrentMonth()
    {
        Date pDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(pDate);
        int month = cal.get(Calendar.MONTH) + 1;

        String strMonth = null;
        if(month < 10)
            strMonth = String.format("0%s", month);
        else
            strMonth = String.format("%s", month);

        return strMonth;
    }
    //----------------------------------------------------------------------------------------------------
    //
    public static String GetYearFromCalendar(Calendar pCal)
    {
        if(pCal == null)
            return null;

        String strResult = null;
        strResult = String.format("%s",pCal.get(Calendar.YEAR));

        return strResult;
    }
    //----------------------------------------------------------------------------------------------------
    //
    public static String GetMonthFromCalendar(Calendar pCal)
    {
        if(pCal == null)
            return null;

        String strResult = null;

        int nMonth = pCal.get(Calendar.MONTH);
        if(nMonth < 10)
            strResult = String.format("0%s",nMonth);
        else
            strResult = String.format("%s",nMonth);

        return strResult;
    }
    //----------------------------------------------------------------------------------------------------
    //
    public static String GetDayFromCalendar(Calendar pCal)
    {
        if(pCal == null)
            return null;

        String strResult = null;

        int nDate = pCal.get(Calendar.DATE);
        if(nDate < 10)
            strResult = String.format("0%s",nDate);
        else
            strResult = String.format("%s",nDate);

        return strResult;
    }
    //----------------------------------------------------------------------------------------------------
    //이전 월  yyyyMM
    public static String getPrevMonth(String yyyy, String MM)
    {
        String yyyyMM = null;
        int nYear = 0;
        int nMonth = 0;
        if(MM.equals("01") || MM.equals("1"))//1월이면 year --;
        {
            try
            {
                nYear = Integer.parseInt(yyyy);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            nYear = nYear - 1;
            nMonth = 12;
        }
        else
        {
            try
            {
                nYear = Integer.parseInt(yyyy);
                nMonth = Integer.parseInt(MM);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            nMonth = nMonth - 1;
        }

        String strYear = String.format("%s", nYear);
        String strMonth = String.format("%s", nMonth);

        if(nMonth < 10)
            strMonth = "0"+strMonth;

        yyyyMM = String.format("%s%s", strYear, strMonth);

        return yyyyMM;
    }
    //----------------------------------------------------------------------------------------------------
    //다음 월  yyyyMM
    public static String getNextMonth(String yyyy, String MM)
    {
        String yyyyMM = null;

        int nYear = 0;
        int nMonth = 0;
        if(MM.equals("12"))//12월이면 year ++;
        {
            try
            {
                nYear = Integer.parseInt(yyyy);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            nYear = nYear + 1;
            nMonth = 1;
        }
        else
        {
            nYear = Integer.parseInt(yyyy);
            nMonth = Integer.parseInt(MM);
            nMonth = nMonth + 1;
        }

        String strYear = String.format("%s", nYear);
        String strMonth = String.format("%s", nMonth);
        if(nMonth < 10)
            strMonth = "0"+strMonth;

        yyyyMM = String.format("%s%s", strYear, strMonth);

        return yyyyMM;
    };

    //----------------------------------------------------------------------------------------------------
    //timestamp convert => String
    public static String DateTimeFormat(long nDateTime, String strFormat)
    {
        if( nDateTime <= 0 )
            return "";

        if( strFormat == null || strFormat.length() <= 0 )
            strFormat = "yyyy-MM-dd HH:mm:ss";

        SimpleDateFormat pDateFormat = new SimpleDateFormat(strFormat);
        Date pDate = new Date(nDateTime);
        return pDateFormat.format(pDate);
    }
    //----------------------------------------------------------------------------------------------------
    //
    public static String calculateTime(long seconds)
    {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - TimeUnit.DAYS.toHours(day);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.DAYS.toMinutes(day) - TimeUnit.HOURS.toMinutes(hours);
        long second = TimeUnit.SECONDS.toSeconds(seconds) -TimeUnit.DAYS.toSeconds(day) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minute);
        return day+"일 "+ hours+"시간 " + minute+"분 " + second+"초";
    }
    //----------------------------------------------------------------
    //
    public static int getDayOfWeekFromCalendar(Calendar pCalendar)
    {
        if(pCalendar == null)
            return 0;

        int nResult = 0;
        nResult = pCalendar.get(Calendar.DAY_OF_WEEK);

        return nResult;
    }
    //----------------------------------------------------------------
    //일수계산
    public static long diffOfDate(String begin, String end) throws Exception
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        Date beginDate = formatter.parse(begin);
        Date endDate = formatter.parse(end);

        long diff = endDate.getTime() - beginDate.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return diffDays;
    }

    //------------------------------------------------------------------------
    /**
     * 특정 날짜에 대하여 요일을 구함(일 ~ 토)
     * @param date
     * @param dateType
     * @return
     * @throws Exception
     */
    public static String getDayOfWeekFromDate(String date, String dateType) throws Exception
    {
        String day = "" ;

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType) ;
        Date nDate = dateFormat.parse(date) ;

        Calendar cal = Calendar.getInstance() ;
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK) ;

        switch(dayNum){
            case 1:
                day = "일";
                break ;
            case 2:
                day = "월";
                break ;
            case 3:
                day = "화";
                break ;
            case 4:
                day = "수";
                break ;
            case 5:
                day = "목";
                break ;
            case 6:
                day = "금";
                break ;
            case 7:
                day = "토";
                break ;

        }



        return day ;
    }

    /************************************* TextView *************************************/
    //--------------------------------------------------------------------------------------------
    //set Color
    public static SpannableString spanColor(CharSequence text, int pColor)
    {
        SpannableString str = new SpannableString(text);
        if(pColor != 0)
            str.setSpan(new ForegroundColorSpan(pColor), 0, str.length(), 0);

        return str;
    }
    //--------------------------------------------------------------------------------------------
    //bold
    public static SpannableString spanBold(CharSequence text, int pColor)
    {
        SpannableString str = new SpannableString(text);

        StyleSpan bss = new StyleSpan(Typeface.BOLD);
        str.setSpan(bss, 0, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        if(pColor != 0)
            str.setSpan(new ForegroundColorSpan(pColor), 0, str.length(), 0);

        return str;
    }
    //--------------------------------------------------------------------------------------------
    //under line
    public static SpannableString spanUnderLine(CharSequence text)
    {
        SpannableString str = new SpannableString(text);
        str.setSpan(new UnderlineSpan(), 0, str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return str;
    }
    //--------------------------------------------------------------------------------------------
    //spannableString Click  이용약관
    public static SpannableString makeLinkSpan(CharSequence text, int pColor, View.OnClickListener listener)
    {
        SpannableString str = new SpannableString(text);

        str.setSpan(new ClickableString(listener), 0, text.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);//onClick

        if(pColor != 0)
            str.setSpan(new ForegroundColorSpan(pColor), 0, text.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);//setColor

        return str;
    }
    //--------------------------------------------------------------------------------------------
    //spannableString Click  이용약관
    public static void makeLinksFocusable(TextView tv)
    {
        MovementMethod m = tv.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod))
        {
            if (tv.getLinksClickable())
            {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }
    //--------------------------------------------------------------------------------------------
    //spannableString Click  이용약관
    public static class ClickableString extends ClickableSpan
    {
        private View.OnClickListener mListener;
        public ClickableString(View.OnClickListener listener)
        {
            mListener = listener;
        }
        @Override
        public void onClick(View v)
        {
            mListener.onClick(v);
        }
    }
    /****************************** inner class ******************************/
    //-------------------------------------------------------------------------------------------
    //editText 검색
    public static class WordSearcher
    {
        private static final char HANGUL_BEGIN_UNICODE = 44032; // 가
        private static final char HANGUL_LAST_UNICODE = 55203; // 힣
        private static final char HANGUL_BASE_UNIT = 588;//각자음 마다 가지는 글자수
        //자음
        private static final char[] INITIAL_SOUND = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

        /**
         * 해당 문자가 INITIAL_SOUND인지 검사.
         * @param searchar
         * @return
         */

        private static boolean isInitialSound(char searchar)
        {
            for(char c:INITIAL_SOUND)
            {
                if(c == searchar)
                {
                    return true;
                }
            }
            return false;
        }

        /**
         * 해당 문자의 자음을 얻는다.
         *
         * @param c 검사할 문자
         * @return
         */

        private static char getInitialSound(char c)
        {
            int hanBegin = (c - HANGUL_BEGIN_UNICODE);
            int index = hanBegin / HANGUL_BASE_UNIT;
            return INITIAL_SOUND[index];
        }

        /**
         * 해당 문자가 한글인지 검사
         * @param c 문자 하나
         * @return
         */

        private static boolean isHangul(char c)
        {
            return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
        }
        /**
         * 생성자.
         */
        public WordSearcher()
        {

        }
        /** * 검색을 한다. 초성 검색 완벽 지원함.
         * @param value : 검색 대상 ex> 초성검색합니다
         * @param search : 검색어 ex> ㅅ검ㅅ합ㄴ
         * @return 매칭 되는거 찾으면 true 못찾으면 false. */
        public static boolean matchString(String value, String search)
        {
            int t = 0;
            int seof = value.length() - search.length();
            int slen = search.length();
            if(seof < 0)
                return false; //검색어가 더 길면 false를 리턴한다.

            for(int i = 0;i <= seof;i++)
            {
                t = 0;
                while(t < slen)
                {
                    if(isInitialSound(search.charAt(t))==true && isHangul(value.charAt(i+t)))
                    {
                        //만약 현재 char이 초성이고 value가 한글이면
                        if(getInitialSound(value.charAt(i+t))==search.charAt(t))//각각의 초성끼리 같은지 비교한다
                            t++;
                        else
                            break;
                    }
                    else
                    {
                        //char이 초성이 아니라면
                        if(value.charAt(i+t)==search.charAt(t))//그냥 같은지 비교한다.
                            t++;
                        else
                            break;
                    }
                }

                if(t == slen)
                    return true; //모두 일치한 결과를 찾으면 true를 리턴한다.
            }

            return false; //일치하는 것을 찾지 못했으면 false를 리턴한다.
        }
    }

}
