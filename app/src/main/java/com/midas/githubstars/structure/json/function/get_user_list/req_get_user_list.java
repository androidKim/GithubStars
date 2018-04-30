package com.midas.githubstars.structure.json.function.get_user_list;


import com.midas.githubstars.structure.json.function.ReqBase;

//---------------------------------------------------------------------------------------------------
//
public class req_get_user_list extends ReqBase
{
	/****************************** Member ******************************/
	// Request param..

	/****************************** Event ******************************/
	//---------------------------------------------------------------------------------------------------
	//
	public req_get_user_list(String q, int page, int per_page)
	{
		// Set Params..
		m = "/search/users?q="+q+" in:login&page="+page+"&per_page="+per_page;
		method_type = REQUEST_GET;
	}

	/****************************** Function ******************************/
	//---------------------------------------------------------------------------------------------------
	//
}
//---------------------------------------------------------------------------------------------------
//