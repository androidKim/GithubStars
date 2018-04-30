package com.midas.githubstars.core;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.midas.githubstars.structure.json.core.user;
import com.midas.githubstars.structure.json.db.tbUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by midas on 2016-03-31.
 */
public class LocalDbCtrl
{
    /**************************** Define ***************************/
    //Local Database
    final private static String ROOT_DIR = "/data/data/com.midas.githunstars/databases/";
    final private static String LOCAL_DB_NAME = "mydb";
    final private static int LOCAL_DB_VERSION = 1;

    //table
    final private static String TB_USER = "tb_user";//카테고리 리스
    /**************************** Member ***************************/
    public SQLiteDatabase m_Db = null;
    public LocalDatabaseHelper m_DbHelper = null;

    //---------------------------------------------------------------------------------------------------
    // Local DB Helper
    public LocalDbCtrl()
    {

    }

    //---------------------------------------------------------------------------------------------------
    // OpenLocalDb
    public boolean Open(Context context)
    {
        if (context == null)
            return false;

        m_DbHelper = new LocalDatabaseHelper(context);
        m_Db = m_DbHelper.getWritableDatabase();

        return isOpen();
    }

    //---------------------------------------------------------------------------------------------------
    // Check LocalDb Open
    public boolean isOpen()
    {
        if (m_DbHelper == null || m_Db == null || m_Db.isOpen() == false)
            return false;

        return true;
    }
    //---------------------------------------------------------------------------------------------------
    // Check LocalDb Close
    public boolean Close()
    {
        if (m_DbHelper == null)
            return false;

        m_DbHelper.close();
        m_DbHelper = null;
        m_Db = null;

        return true;
    }
    //---------------------------------------------------------------------------------------------------
    //
    public static void setDatabases(Context context)
    {
        File folder = new File(ROOT_DIR);
        if(folder.exists())
        {

        }
        else
        {
            folder.mkdirs();
        }

        AssetManager assetManager = context.getResources().getAssets(); //ctx가 없으면 assets폴더를 찾지 못한다.
        File outfile = new File(ROOT_DIR + LOCAL_DB_NAME);
        InputStream is = null;
        FileOutputStream fo = null;
        long filesize = 0;
        try
        {
            is = assetManager.open(LOCAL_DB_NAME, AssetManager.ACCESS_BUFFER);
            filesize = is.available();
            if (outfile.length() <= 0)
            {
                byte[] tempdata = new byte[(int) filesize];
                is.read(tempdata);
                is.close();
                outfile.createNewFile();
                fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            }
            else
            {

            }
        }
        catch (IOException e)
        {

        }
    }
    //---------------------------------------------------------------------------------------------------
    // Local DB Helper
    private static class LocalDatabaseHelper extends SQLiteOpenHelper
    {
        //---------------------------------------------------------------------------------------------------
        //
        public LocalDatabaseHelper(Context context)
        {
            super(context, LOCAL_DB_NAME, null, LOCAL_DB_VERSION);
            setDatabases(context);
        }

        //---------------------------------------------------------------------------------------------------
        // 최초 DB 파일 생성
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(String.format("CREATE TABLE %s ("
                            + "login  TEXT, "
                            + "id  TEXT, "
                            + "avatar_url  TEXT, "
                            + "fav_status  TEXT"
                            + ");",
                    TB_USER));
        }

        //---------------------------------------------------------------------------------------------------
        // DB 업그레이드
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            // Clear All Data
            DropAllTables(db);

            // Create All New Table
            onCreate(db);
        }

        //---------------------------------------------------------------------------------------------------
        //
        public void DropAllTables(SQLiteDatabase db)
        {
            // System
            db.execSQL(String.format("DROP TABLE IF EXISTS %s;", TB_USER));
        }
    }

    /**************************** Query Base ***************************/
    //---------------------------------------------------------------------------------------------------
    // Exist
    public static boolean Exists(SQLiteDatabase db, String strQuery)
    {
        if(db == null || strQuery == null || strQuery.length() <= 0)
            return false;

        boolean bExists = true;
        Cursor result = db.rawQuery(strQuery, null);

        try
        {
            if(result == null   //검색실패
                    || result.getCount() <= 0   //검색결과 없음
                    || result.moveToFirst() == false    //검색결과 없음
                    || result.getInt(0) <= 0)   //검색결과 Count 0
            {
                bExists = false;
            }
        }
        finally
        {
            result.close();
        }

        return bExists;
    }

    /**************************** Query Function ***************************/
    //---------------------------------------------------------------------------------------------------
    //
    public static ArrayList<user> getUserList(SQLiteDatabase db, String search_text, int limit_start, int limit_end)
    {
        if(db == null || search_text == null)
            return null;

        //select * from tb_user where login LIKE '%Taejun%' LIMIT 0, 1;
        //String query = String.format("select * from %s where login LIKE '%s' LIMIT %d,%d;", TB_USER, search_text, limit_start, limit_end);
        String query = "select * from "+ TB_USER+" where login LIKE '%"+search_text+"%' LIMIT "+ limit_start+","+limit_end;

        ArrayList<user> arrResult = null;//Result

        Cursor result = db.rawQuery(query, null);
        try
        {
            if(result == null || result.moveToFirst() == false)
                return null;

            arrResult = new ArrayList<user>();
            user pInfo = null;

            pInfo = new user(result.getString(0), result.getString(1), result.getString(2), result.getString(3));
            arrResult.add(pInfo);

            while(result.moveToNext())
            {
                pInfo = new user(result.getString(0), result.getString(1), result.getString(2), result.getString(3));
                arrResult.add(pInfo);
            }
        }
        finally
        {
            result.close();
        }
        return arrResult;
    }
    //---------------------------------------------------------------------------------------------------
    // select item
    public static tbUser getUserInfo(SQLiteDatabase db, String id)
    {
        if(db == null || id == null )
            return null;

        String query = String.format("select * from %s where id='%s';", TB_USER, id);

        tbUser pInfo = null;

        Cursor result = db.rawQuery(query, null);
        try
        {
            if(result == null || result.moveToFirst() == false)
                return null;

            pInfo = new tbUser(result.getString(0), result.getString(1), result.getString(2), result.getString(3));
        }
        finally
        {
            result.close();
        }


        return pInfo;
    }

    //---------------------------------------------------------------------------------------------------
    // Insert
    public static boolean setUserInfo(SQLiteDatabase db, tbUser pInfo)
    {
        if(db == null || pInfo == null || pInfo.id == null )
            return false;

        if(existInfo(db, pInfo))//Exist Update
            return updateInfo(db, pInfo);
        else//Insert
            return insertInfo(db, pInfo);
    }
    //---------------------------------------------------------------------------------------------------
    // Exist Info
    private  static boolean existInfo(SQLiteDatabase db, tbUser pInfo)
    {
        if(db == null || pInfo.id == null)
            return false;

        String query = String.format("select * from %s where id='%s';", TB_USER, pInfo.id);

        return Exists(db, query);
    }

    //---------------------------------------------------------------------------------------------------
    // Insert
    public static boolean insertInfo(SQLiteDatabase db, tbUser pInfo)
    {
        if (db == null || pInfo == null || pInfo.id == null)
            return false;

        String query = String.format("insert into %s values('%s', '%s', '%s', '%s');",
                TB_USER, pInfo.login, pInfo.id, pInfo.avatar_url, pInfo.fav_status);

        db.execSQL(query);
        return true;
    }
    //---------------------------------------------------------------------------------------------------
    // Update
    public static boolean updateInfo(SQLiteDatabase db, tbUser pInfo)
    {
        if(db == null || pInfo == null || pInfo.id == null)
            return false;

        String query = String.format("update %s set fav_status='%s' where id='%s';",
                TB_USER, pInfo.fav_status, pInfo.id);

        db.execSQL(query);
        return true;
    }
    //---------------------------------------------------------------------------------------------------
    // Delete
    public static void deleteInfo(SQLiteDatabase db, String id)
    {
        String query = String.format("delete from %s where id = '%s';",TB_USER, id);
        Cursor result = db.rawQuery(query, null);
        try
        {
            if(result == null || result.moveToFirst() == false)
                return;
        }
        finally
        {
            result.close();
        }
    }
}
