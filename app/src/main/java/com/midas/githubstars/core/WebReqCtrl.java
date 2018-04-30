package com.midas.githubstars.core;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.midas.githubstars.MyApp;
import com.midas.githubstars.R;
import com.midas.githubstars.structure.json.function.ReqBase;
import com.midas.githubstars.structure.json.function.ResBase;
import com.midas.githubstars.structure.json.function.get_user_list.req_get_user_list;
import com.midas.githubstars.structure.json.function.get_user_list.res_get_user_list;
import com.midas.githubstars.structure.json.function.test.req_test;
import com.midas.githubstars.structure.json.function.test.res_test;
import com.midas.githubstars.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 *
 */
public class WebReqCtrl
{
    /****************************** Define *****************************/
    final private static int CONNECT_TIMEOUT = 1000 * 10;
    final public static String SERVER_URL = "https://api.github.com";//
    /****************************** Member *****************************/
    public MyApp m_App = null;
    public Context m_Context = null;
    //----------------------------------------------------------------------------------
    //기본생성자
    public WebReqCtrl()
    {

    }
    //----------------------------------------------------------------------------------
    //
    public WebReqCtrl(MyApp myApp, Context context)
    {
        this.m_App = myApp;
        this.m_Context = context;
    }
    //---------------------------------------------------------------------------------------------------
    // 웹서버 통신 HttpUrlConnection
    public String RequestAPI(String strUrl, JSONObject jsonParam, String method)
    {
        // Check Param
        if( strUrl == null || jsonParam == null )
            return null;

        if(strUrl.length() <= 0)
            return null;

        // Prepare Parameter
        String strEncUrl = strUrl;
        try
        {
            URL pUrl = null;
            HttpsURLConnection pHttpConn = null;
            //HttpURLConnection pHttpConn = null;

            String token = String.format("token %s", m_Context.getResources().getString(R.string.github_token));
            if(method.equals(ReqBase.REQUEST_GET))//GET...
            {
                String queryString = "";
                if(jsonParam.length() > 0)
                {
                    queryString = convertJsonToQueryString(jsonParam);
                }
                strEncUrl = strEncUrl + queryString;

                pUrl = new URL(strEncUrl);
                pHttpConn = (HttpsURLConnection)pUrl.openConnection();
                pHttpConn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                pHttpConn.setDoOutput(false); //output을 사용하도록 설정 (default : false)
                pHttpConn.setConnectTimeout(CONNECT_TIMEOUT); //타임아웃 시간 설정 (default : 무한대기)
                pHttpConn.setRequestMethod(ReqBase.REQUEST_GET); // get방식 통신

                pHttpConn.setRequestProperty("Content-Type", "application/json");
                pHttpConn.setRequestProperty("user-agent", System.getProperty("http.agent"));
                pHttpConn.setRequestProperty("Authorization", token);
            }
            else if(method.equals(ReqBase.REQUEST_PUT)) //PUT..
            {
                pUrl = new URL(strEncUrl);
                pHttpConn = (HttpsURLConnection)pUrl.openConnection();
                pHttpConn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                pHttpConn.setDoOutput(true); //output을 사용하도록 설정 (default : false)
                pHttpConn.setConnectTimeout(CONNECT_TIMEOUT); //타임아웃 시간 설정 (default : 무한대기)
                pHttpConn.setRequestMethod(ReqBase.REQUEST_PUT); // post방식 통신

                pHttpConn.setRequestProperty("Content-Type", "application/json");
                pHttpConn.setRequestProperty("user-agent", System.getProperty("http.agent"));
                pHttpConn.setRequestProperty("Authorization", token);

                //raw(application/josn)방식
                OutputStream os = pHttpConn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();
            }
            else if(method.equals(ReqBase.REQUEST_PATCH))//PATCH
            {
                pUrl = new URL(strEncUrl);
                pHttpConn = (HttpsURLConnection) pUrl.openConnection();
                pHttpConn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                pHttpConn.setDoOutput(false); //output을 사용하도록 설정 (default : false)
                pHttpConn.setConnectTimeout(10000); //타임아웃 시간 설정 (default : 무한대기)
                pHttpConn.setRequestMethod(ReqBase.REQUEST_PATCH);

                pHttpConn.setRequestProperty("Content-Type", "application/json");
                pHttpConn.setRequestProperty("user-agent", System.getProperty("http.agent"));
                pHttpConn.setRequestProperty("Authorization", token);

            }
            else if(method.equals(ReqBase.REQUEST_DELETE))//DELETE
            {
                pUrl = new URL(strEncUrl);
                pHttpConn = (HttpsURLConnection) pUrl.openConnection();
                pHttpConn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                pHttpConn.setDoOutput(true); //output을 사용하도록 설정 (default : false)
                pHttpConn.setConnectTimeout(CONNECT_TIMEOUT); //타임아웃 시간 설정 (default : 무한대기)
                pHttpConn.setRequestMethod(ReqBase.REQUEST_DELETE); //

                pHttpConn.setRequestProperty("Content-Type", "application/json");
                pHttpConn.setRequestProperty("user-agent", System.getProperty("http.agent"));
                pHttpConn.setRequestProperty("Authorization", token);


            }
            else//POST..
            {
                pUrl = new URL(strEncUrl);
                pHttpConn = (HttpsURLConnection)pUrl.openConnection();
                pHttpConn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                pHttpConn.setDoOutput(true); //output을 사용하도록 설정 (default : false)
                pHttpConn.setConnectTimeout(CONNECT_TIMEOUT); //타임아웃 시간 설정 (default : 무한대기)
                pHttpConn.setRequestMethod(ReqBase.REQUEST_POST); // post방식 통신

                //SetHeader
                pHttpConn.setRequestProperty("Content-Type", "application/json");
                pHttpConn.setRequestProperty("user-agent", System.getProperty("http.agent"));
                pHttpConn.setRequestProperty("Authorization", token);

                //raw(application/josn)방식
                OutputStream os = pHttpConn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();
            }
            //Response..
            int nStat = pHttpConn.getResponseCode();

            BufferedReader reader = null;
            if(nStat != 200)
                reader = new BufferedReader(new InputStreamReader(pHttpConn.getErrorStream()));
            else
                reader = new BufferedReader(new InputStreamReader(pHttpConn.getInputStream()));

            String input;
            StringBuffer sb = new StringBuffer();

            while ((input = reader.readLine())!=null)
            {
                sb.append(input);
            }
            String strResponse = sb.toString();

            LogUtil.PrintLog("Method", method);
            LogUtil.PrintLog("Param", jsonParam.toString());
            LogUtil.PrintLog(strEncUrl, strResponse + "(HttpStatusCode : "+nStat+")");

            if( nStat == HttpURLConnection.HTTP_OK)//status code 200..
            {
                Gson pGson = new Gson();
                final ResBase pRes = pGson.fromJson(strResponse, ResBase.class);
                ((Activity)m_Context).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(pRes.message != null)
                            Toast.makeText(m_Context, pRes.message, Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else//status code fail..
            {
                //server message
                Gson pGson = new Gson();
                try
                {
                    final ResBase pRes = pGson.fromJson(strResponse, ResBase.class);
                    ((Activity)m_Context).runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(m_Context, m_Context.getResources().getString(R.string.msg_connect_fail), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }

            // Close Connection
            pHttpConn.disconnect();
            return strResponse;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //------------------------------------------------------------------------------------
    //InputStream => String
    private String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    //------------------------------------------------------------------------------------
    //JSON => QueryString
    private String convertJsonToQueryString(JSONObject jsonObj)
    {
        Iterator<?> keys = jsonObj.keys();
        StringBuffer sb = new StringBuffer();
        sb.append("?");
        while( keys.hasNext() )
        {
            String key = (String)keys.next();
            try
            {
                //키에대한값이 존재하면..
                if(jsonObj.has(key))
                {
                    String value = String.valueOf(jsonObj.get(key));
                    if(keys.hasNext())
                    {
                        sb.append(key).append("=").append(value).append("&");
                    }
                    else//last item..
                    {
                        sb.append(key).append("=").append(value);
                    }
                }
                else
                {

                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    /**************************************** user function ****************************************/

    /**************************************** api function ****************************************/
    //---------------------------------------------------------------------------------------------------
    // test
    public res_test test()
    {
        Gson pGson = new Gson();

        // Create Reqeust Param
        req_test pReq = new req_test();

        // Structure -> Json Object String
        String strJsonObj = pGson.toJson(pReq);
        JSONObject jsonParam = new JSONObject();

        // Requist!!
        String strUrl = SERVER_URL + pReq.m;
        String strRes = RequestAPI(strUrl, jsonParam, pReq.method_type);
        if (strRes == null)
            return null;

        // Response..
        res_test pRes = null;
        try
        {
            pRes = pGson.fromJson(strRes, res_test.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (pRes == null)
            return null;

        return pRes;
    }

    //---------------------------------------------------------------------------------------------------
    // get user list api
    public res_get_user_list get_user_list(String q, int page, int per_page)
    {
        Gson pGson = new Gson();

        // Create Reqeust Param
        req_get_user_list pReq = new req_get_user_list(q, page, per_page);

        // Structure -> Json Object String
        String strJsonObj = pGson.toJson(pReq);
        JSONObject jsonParam = new JSONObject();

        // Requist!!
        String strUrl = SERVER_URL + pReq.m;
        String strRes = RequestAPI(strUrl, jsonParam, pReq.method_type);
        if (strRes == null)
            return null;

        // Response..
        res_get_user_list pRes = null;
        try
        {
            pRes = pGson.fromJson(strRes, res_get_user_list.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (pRes == null)
            return null;

        return pRes;
    }

}