package com.midas.githubstars.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//---------------------------------------------------------------------------------------------------
//
public class NetworkCtrl
{
    //////////////////////////////////////////////////Define //////////////////////////////////////////////////
    final public static int STAT_ERROR 				= 0;
    final public static int STAT_NOT_CONNECTED 		= 1;
    final public static int STAT_CONNECTING 		= 2;
    final public static int STAT_CONNECTING_WIFI 	= 3;
    final public static int STAT_CONNECTING_MOBILE	= 4;
    final public static int STAT_CONNECTED 			= 5;
    final public static int STAT_CONNECTED_WIFI 	= 6;
    final public static int STAT_CONNECTED_MOBILE	= 7;

    //////////////////////////////////////////////////Member //////////////////////////////////////////////////
    private Context m_ParentContext = null;

    //---------------------------------------------------------------------------------------------------
    // 생성자
    public NetworkCtrl(Context stParent)
    {
        m_ParentContext = stParent;
    }

    //---------------------------------------------------------------------------------------------------
    // Check Network Status
    public int getStatus()
    {
        if( m_ParentContext == null )
            return STAT_ERROR;

        ConnectivityManager stCM = (ConnectivityManager)m_ParentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo stNTInfo = stCM.getActiveNetworkInfo();

        // Not Connected
        if( stNTInfo == null || stNTInfo.isConnected() == false )
            return STAT_NOT_CONNECTED;

        else if( stNTInfo.isConnectedOrConnecting() )
        {
            // Connected
            if( stNTInfo.isAvailable() )
            {
                int iStat = STAT_CONNECTED;

                switch(stNTInfo.getType())
                {
                    case ConnectivityManager.TYPE_WIFI:
                        iStat = STAT_CONNECTED_WIFI;
                        break;

                    case ConnectivityManager.TYPE_MOBILE:
                        iStat = STAT_CONNECTED_MOBILE;
                        break;
                }

                return iStat;
            }

            // Connecting...
            int iStat = STAT_CONNECTING;

            switch(stNTInfo.getType())
            {
                case ConnectivityManager.TYPE_WIFI:
                    iStat = STAT_CONNECTING_WIFI;
                    break;

                case ConnectivityManager.TYPE_MOBILE:
                    iStat = STAT_CONNECTING_MOBILE;
                    break;
            }

            return iStat;
        }

        return STAT_ERROR;
    }
}
//---------------------------------------------------------------------------------------------------
//