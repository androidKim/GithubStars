package com.midas.githubstars.structure.json.function;


import com.midas.githubstars.structure.json.JsonBase;

//---------------------------------------------------------------------------------------------------
//
public class ReqBase extends JsonBase
{
	/****************************** Define ******************************/
	// Protocol Method type
	final public static String REQUEST_GET = "GET";
	final public static String REQUEST_POST = "POST";
	final public static String REQUEST_PUT = "PUT";
	final public static String REQUEST_PATCH = "PATCH";
	final public static String REQUEST_DELETE = "DELETE";

	// 등록자 구분
	final public static String USER_DIV_M = "M";//관리자
	final public static String USER_DIV_U = "U";//사용자

	// 계약상태
	final public static String TYPE_CONTRACT_END_Y = "Y";
	final public static String TYPE_CONTRACT_END_N = "N";

	// 학교종료상태??
	final public static String TYPE_USE_Y = "Y";
	final public static String TYPE_USE_N = "N";

	// Web Service Connection URL
	final public static String API_CON_URL	= "";

	//OS type
	final public static String OS_TYPE_ANDROID = "A";

	// Item Count for Paging
	final public static int ITEM_COUNT = 20;
	/****************************** Member ******************************/
	// URL Param
	//public String c; // Category
	public transient String m; // api url
	public transient String method_type;//
	
	// Common Data
	public String foodOauth = null;//jwt token..
	//public common common = null;
	/****************************** Event ******************************/
	//---------------------------------------------------------------------------------------------------
	//
	public ReqBase()
	{
		//c	= "";
		this.m	= "";
		this.method_type = "";
		
		//common = new common();
	}
}
//---------------------------------------------------------------------------------------------------
//