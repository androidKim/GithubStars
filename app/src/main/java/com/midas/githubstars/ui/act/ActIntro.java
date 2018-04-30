package com.midas.githubstars.ui.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.midas.githubstars.MyApp;
import com.midas.githubstars.R;
import com.midas.githubstars.util.NetworkCtrl;
import com.midas.githubstars.util.Util;

/*
Intro Splash
 */
public class ActIntro extends AppCompatActivity
{
    /*************************** Define ***************************/

    /*************************** Member ***************************/
    private MyApp m_App = null;
    private Context m_Context = null;
    /*************************** Controll ***************************/
    private ImageView m_iv_Splash = null;

    /*************************** System Function ***************************/
    //---------------------------------------------------------------------------------------------------
    //
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_intro);

        m_Context = this;
        m_App = (MyApp)getApplication();
        if(m_App.m_bInit == false)
            m_App.init(m_Context);

        recvIntentData();
    }
    //---------------------------------------------------------------------------------------------------
    //
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        System.exit(0);
    }
    /*************************** User Function ***************************/
    //---------------------------------------------------------------------------------------------------
    //
    public void recvIntentData()
    {
        /*
        Intent pIntent = getIntent();

        if(pIntent != null)
        {
            //push data payload
            if(pIntent.getExtras() != null)
            {
                //push data..
                String type = "";//push type
                String tid = "";//noti key
                String sttNo = "";//settle key

                if(pIntent.hasExtra("type"))
                    type = pIntent.getExtras().getString("type");

                if(pIntent.hasExtra("tid"))
                    tid = pIntent.getExtras().getString("tid");

                if(pIntent.hasExtra("sttNo"))
                    sttNo = pIntent.getExtras().getString("sttNo");

                if(!type.equals("") || type.length() > 0)
                {
                    push pInfo = new push(null, null, null, type, tid, sttNo);
                    pIntent = m_App.pushNotificationTabEvent(pInfo);
                    startActivity(pIntent);
                    finish();
                    return;
                }
            }
        }

        if(pIntent != null)
        {
            //Siksin odos Login..
            String strSiksinKey = pIntent.getStringExtra(Constant.INTENT_DATA_SIKSIN_KEY_FOR_ONESHOT);
            if(strSiksinKey != null)
                m_App.m_SpCtrl.setSiksinUserKey(strSiksinKey);
        }
        */

        introLoading();
    }
    //---------------------------------------------------------------------------------------------------
    //Splash View
    private void introLoading()
    {
        //SplashImage Random..
        m_iv_Splash = (ImageView)findViewById(R.id.iv_Splash);
        String strResourceName = "ic_launcher_background";
        m_iv_Splash.setImageDrawable(m_Context.getResources().getDrawable(Util.getResourceID(strResourceName, "drawable", m_Context)));
        Handler handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                if(m_App.m_NetworkCtrl.getStatus() == NetworkCtrl.STAT_NOT_CONNECTED)//Network체크
                {
                    Toast.makeText(m_Context, m_Context.getResources().getString(R.string.msg_network_not_connected), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                else
                {
                    //메인화면 이동..
                    Intent pIntent = new Intent(m_Context, ActMain.class);
                    startActivity(pIntent);
                    finish();
                }
            }
        };
        handler.sendEmptyMessageDelayed(0, 1000);//1초
    }
}
