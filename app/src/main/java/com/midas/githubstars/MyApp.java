package com.midas.githubstars;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.midas.githubstars.core.LocalDbCtrl;
import com.midas.githubstars.core.SharedPrefCtrl;
import com.midas.githubstars.core.WebReqCtrl;
import com.midas.githubstars.ui.act.ActMain;
import com.midas.githubstars.ui.dialog.dlg_loading;
import com.midas.githubstars.util.LogUtil;
import com.midas.githubstars.util.NetworkCtrl;
import com.midas.githubstars.util.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Application Class
 */
public class MyApp extends MultiDexApplication
{
    /************************ Define ************************/
    private static final String RELEASE_DATE = "2018.00.00";    // 배포일
    private static volatile MyApp m_App = null;
    public static final int CLOSE_APP_COUNT	= 2000; // 앱 종료 대기시간
    /************************ Member ************************/
    //public FirebaseAnalytics m_FirebaseAnalytics = null;
    public Context m_Context = null;//Context
    public WebReqCtrl m_WebReqCtrl = null;//웹서버통신
    public SharedPrefCtrl m_SpCtrl = null;//프리퍼런스
    public LocalDbCtrl m_DbCtrl = null;//Sqlite
    public NetworkCtrl m_NetworkCtrl = null;
    public InputMethodManager m_InputMethodManager = null;

    // Application Version
    public String m_strLocalVersion = "";//local version "1.0.0"
    public String m_strServerVersion = "";//server version "1.0.0"
    public String m_strPackageName = "";
    public int m_nVersionCode = 0;

    //
    public int m_nDeviceWidth = 0;
    public int m_nDeviceHeight = 0;
    public int m_nDeviceDens = 0;
    public long m_nInstallDt = 0;
    public long m_nServerTime = 0;

    public boolean m_bInit = false;
    //프로그램 종료 처리..
    private boolean m_bAskFinishApp = false;
    private boolean m_bFinishApp = false;
    /************************ UIControll ************************/
    public dlg_loading m_DlgLoading = null;//loading

    /************************ Controller ************************/

    /************************ 생성자 ************************/
    //기본생성자
    public MyApp()
    {

    }

    //
    public MyApp(Context context)
    {
        init(context);
    }

    /************************ System Function ************************/
    //-------------------------------------------------------------
    //
    @Override
    public void onCreate()
    {
        super.onCreate();
        m_App = this;

        //getHashKey();
    }

    //-------------------------------------------------------------
    //
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
    }

    //-------------------------------------------------------------
    //애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
    @Override
    public void onTerminate()
    {
        super.onTerminate();
        m_App = null;
    }

    /************************ User Function ************************/
    //-------------------------------------------------------------
    //
    public void getHashKey()
    {
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo("com.siksin.todayfood", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                LogUtil.PrintLog("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }
    //------------------------------------------------------------------
    //Init
    public void init(Context context)
    {
        if (m_bInit)//
            return;

        m_Context = context;
        m_NetworkCtrl = new NetworkCtrl(m_Context);
        //m_FirebaseAnalytics = FirebaseAnalytics.getInstance(m_Context);
        m_WebReqCtrl = new WebReqCtrl(this, context);
        m_SpCtrl = new SharedPrefCtrl();
        m_SpCtrl.Init(m_Context);

        m_InputMethodManager = (InputMethodManager)getSystemService(m_Context.INPUT_METHOD_SERVICE);

        //Open Local Db
        if (m_DbCtrl == null)
        {
            m_DbCtrl = new LocalDbCtrl();
            m_DbCtrl.Open(m_Context);
        }



        LoadDeviceSize(m_Context);//Device Size
        checkLocalVersion();//app version

        m_bInit = true;
    }


    //----------------------------------------------------------------------------------------------------
    // 화면 해상도 확인
    public boolean LoadDeviceSize(Context pContext)
    {
        if (m_nDeviceDens > 0)
            return true;

        if (pContext == null)
        {
            pContext = m_Context;
            if (pContext == null)
                return false;
        }

        DisplayMetrics displayMetrics = pContext.getResources().getDisplayMetrics();
        //((Activity)pContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        m_nDeviceWidth = displayMetrics.widthPixels;
        m_nDeviceHeight = displayMetrics.heightPixels;
        m_nDeviceDens = displayMetrics.densityDpi;

        if (m_nDeviceWidth <= 0 || m_nDeviceHeight <= 0)
            return false;

        return true;
    }
    //----------------------------------------------------------------------------------------------------
    // get Device width
    public int getDeviceWidth()
    {
        return m_nDeviceWidth;
    }
    //----------------------------------------------------------------------------------------------------
    // get Device height
    public int getDeviceHeight()
    {
        return m_nDeviceHeight;
    }

    //---------------------------------------------------------------------------------------------------
    //go Main Refresh
    public void refreshGoMain(Context context)
    {
        Intent pIntent = new Intent(context, ActMain.class);
        pIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity)context).startActivity(pIntent);
        ((Activity)context).finish();
    }
    //---------------------------------------------------------------------------------------------------
    //앱종료 메세지
    public void requestFinish(final Context context)
    {
        // 앱 종료 메시지 출력중...
        if( m_bAskFinishApp )
        {
            // 프로그램 종료..
            m_bFinishApp = true;

            //super.onBackPressed();
            ((Activity)context).finish();
            return;
        }

        // 앱 종료 메시지 출력
        Toast.makeText(m_Context, m_Context.getResources().getString(R.string.str_msg_close_app), Toast.LENGTH_SHORT).show();

        // 종료 카운트다운 스레드 시작!!
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                m_bAskFinishApp = true;

                int nCount = 0;

                // 앱 종료 카운트다운..
                while( nCount <= CLOSE_APP_COUNT )
                {
                    if( m_bFinishApp )
                        break;

                    nCount += 100;
                    Util.SleepInThread(100);
                }

                // 카운트다운 내 종료하지 않았을 때..
                if( m_bFinishApp == false )
                    m_bAskFinishApp = false;
            }
        }).start();
    };


    //---------------------------------------------------------------------------------------------------
    //push notification tab event
    /*
    public Intent pushNotificationTabEvent(push pInfo)
    {
        Intent pIntent = null;

        if (pInfo.type.equals(push.TYPE_PUSH_NOTI_00001))//push type Noti
        {
            pIntent = new Intent(m_Context, ActPushNotiDetail.class);//공지상세전용 activity
            if (pInfo.tid != null)
                pIntent.putExtra(Constant.INTENT_DATA_NOTI_DETAIL_NUM, pInfo.tid);//noti num
        }
        else if (pInfo.type.equals(push.TYPE_PUSH_NOTI_00002))//push type PaymentList
        {
            if (!m_App.isLogin())//login false
            {
                pIntent = new Intent(m_Context, ActLogin.class);
                pIntent.putExtra(Constant.INTENT_DATA_IS_PUSH_NOTIFICATION, true);//frag page index
            }
            else//login true
            {
                //fragment page_num
                pIntent = new Intent(m_Context, ActPayment.class);
                pIntent.putExtra(Constant.INTENT_DATA_IS_PUSH_NOTIFICATION, true);//frag page index
                pIntent.putExtra(Constant.INTENT_DATA_PAY_FRAG_PAGE_INDEX, ActPayment.PAGE_PAY_MEMBER_INFO);//frag page index
            }
        }
        else
        {
            pIntent = new Intent(m_Context, ActMain.class);
        }

        return pIntent;
    }
    */
    //----------------------------------------------------------------------------------------------
    //local data init
    public void clearSpData(Context context)
    {
        m_SpCtrl.clearData(context);
    }

    //---------------------------------------------------------------------------------------------------
    // 로컬 리소스 버전확인
    private boolean checkLocalVersion()
    {
        if (m_Context == null)
            return false;

        // Current Application Version from Resource
        m_nVersionCode = 0;
        m_strLocalVersion = "";
        try
        {
            PackageInfo pPI = m_Context.getPackageManager().getPackageInfo(m_Context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (pPI != null)
            {
                // Package Name(com.siksin.todayfood)
                m_strPackageName = pPI.packageName;
                LogUtil.PrintLog(String.format("Package Name : %s", m_strPackageName));

                // Version Code(22)
                m_nVersionCode = pPI.versionCode;
                LogUtil.PrintLog(String.format("App Version Code : %d", m_nVersionCode));

                // Version Name(1.0.0)
                m_strLocalVersion = pPI.versionName;
                LogUtil.PrintLog(String.format("App Version Name : %s", m_strLocalVersion));

                // Release Date
                LogUtil.PrintLog(String.format("Release Date : %s", RELEASE_DATE));
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    //---------------------------------------------------------------------------------------------------
    //go playstore
    public void goPlayStore(Context context, String strPakageName)
    {
        Intent pIntent = new Intent(Intent.ACTION_VIEW);
        pIntent.setData(Uri.parse("market://details?id=" + strPakageName));
        ((Activity) context).startActivity(pIntent);
    }

    //------------------------------------------------------------------
    //키보드를 가림.
    public void hideKeyboard(EditText pEditText)
    {
        if(m_InputMethodManager == null || pEditText == null)
            return;

        m_InputMethodManager.hideSoftInputFromWindow(pEditText.getWindowToken(), 0);
    }
    //------------------------------------------------------------------
    //키보드를 보여줌.
    public void showKeyboard(EditText pEditText)
    {
        if(m_InputMethodManager == null || pEditText == null)
            return;

        m_InputMethodManager.showSoftInput(pEditText, 0);
    }
    /*************************** Custon Dialog ***************************/
    //---------------------------------------------------------------------------------------------------
    // 메시지전용 alert dialog
    public void alertDialog(Context pContext, String title, String msg, String ok, String cancel)
    {
        //Show alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(pContext);

        if(msg != null)
            dialog.setMessage(msg);

        //ok
        if(ok != null)
        {
            dialog.setPositiveButton(ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
        }

        if(cancel != null)
        {
            //cancel
            dialog.setNegativeButton(cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
        }

        AlertDialog alert = dialog.create();
        //alert.setIcon(R.mipmap.app_icon);
        if(title != null)
            alert.setTitle(title);

        alert.show();
    }
    //---------------------------------------------------------------------------------------------------
    // 로딩중 메시지박스 보이기
    public void ShowLoadingDlg(Context pContext)
    {
        if (!((Activity) pContext).isFinishing())//Activity종료여부
        {
            // Already Exists..
            if (m_DlgLoading != null) {
                // Close Dialog
                m_DlgLoading.dismiss();
                m_DlgLoading = null;
            }

            // Create New Dialog..
            m_DlgLoading = new dlg_loading(pContext, null, null, new dlg_loading.OnCallbackIF()
            {
                @Override
                public boolean OnClose(Dialog pDialog)
                {
                    return true;
                }
            });
            m_DlgLoading.show();
        }
    }
    //---------------------------------------------------------------------------------------------------
    // Dialog Dismiss
    public void HideLoadingDlg()
    {
        // Check Exists..
        if (m_DlgLoading == null)
        {
            return;
        }
        else
        {
            // Close Dialog
            m_DlgLoading.dismiss();
            m_DlgLoading = null;
        }
    }

}
