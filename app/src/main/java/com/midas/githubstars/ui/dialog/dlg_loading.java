package com.midas.githubstars.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.midas.githubstars.R;


//---------------------------------------------------------------------------------------------------
//
public class dlg_loading extends Dialog
{
	/****************************** Define ******************************/
	
	/****************************** UI Control ******************************/
	private RelativeLayout m_ly_DlgBase = null;
	private ProgressBar m_pBar = null;
	/****************************** Member ******************************/
	private Context m_Context = null;
	private Dialog m_This = null;

	private String m_strTitle = "";
	private String m_strMessage = "";

	// Callback Interface
	private OnCallbackIF m_CallbackIF = null;

	/****************************** System Event ******************************/
	//---------------------------------------------------------------------------------------------------
	//
	public dlg_loading(Context context, String strTitle, String strMessage, OnCallbackIF pCallbackIF)
	{
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		// Set Dialog UI XML
		setContentView(R.layout.dlg_loading);
		m_Context = context;
		m_This = this;
		
		// Set Dialog Data
		m_strTitle			=	strTitle;
		m_strMessage		=	strMessage;
		
		// Set Callback Interface
		m_CallbackIF		=	pCallbackIF;
		
		// Set Dialog's UI
		SetBaseCtrl();
	}
	
	//---------------------------------------------------------------------------------------------------
	//
	@Override
	public void cancel()
	{
		boolean bCancel = true;
		
		// Call Callback Function
		if( m_CallbackIF != null )
			bCancel = m_CallbackIF.OnClose(m_This);

		if( bCancel )
			super.cancel();
	}	
	/****************************** User Function ******************************/
	//---------------------------------------------------------------------------------------------------
	//
	public void SetBaseCtrl()
	{
		m_ly_DlgBase = (RelativeLayout) findViewById(R.id.ly_DlgBase);
		m_pBar = (ProgressBar)findViewById(R.id.pb_Loading);
		m_pBar.setIndeterminate(true);
		
		m_ly_DlgBase.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 로딩중 다이얼로그는 외곽 터치 이벤트 없음..
				//cancel();
			}
		});	
	}
	
	/****************************** Callback Interface ******************************/
	//---------------------------------------------------------------------------------------------------
	//
	public interface OnCallbackIF
	{
		boolean OnClose(Dialog pDialog);
	}
}
//---------------------------------------------------------------------------------------------------
//