package com.midas.githubstars.util;

import android.content.Context;
import android.util.Log;

import com.midas.githubstars.common.Constant;


//----------------------------------------------------------------------------------------------------
//
public class LogUtil
{
	/****************************** Define ******************************/
	
	/****************************** Member ******************************/
	private Context m_Context = null;
	
	/****************************** Event ******************************/
	//----------------------------------------------------------------------------------------------------
	//
	public LogUtil()
	{
		
	}
	
	/****************************** Function ******************************/
	//----------------------------------------------------------------------------------------------------
	//초기설정
	public boolean Init(Context pContext)
	{
		if( pContext == null )
			return false;
		
		m_Context = pContext;		
		return true;
	}
	//----------------------------------------------------------------------------------------------------
	//
	public static void PrintLog(String str)
	{
		if(str == null)
			return;

		if(Constant.IS_LOG_PRINT == true)
			Log.d("LOG : ",str);
	}
	
	//----------------------------------------------------------------------------------------------------
	//
	public static void PrintLog(String strTag, String strMsg)
	{
		if(strTag == null || strMsg == null)
			return;

		if(Constant.IS_LOG_PRINT == true)
			Log.d(strTag, strMsg);
	}
}
//----------------------------------------------------------------------------------------------------
//