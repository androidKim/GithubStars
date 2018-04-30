package com.midas.githubstars.structure.json.function.get_user_list;

import com.midas.githubstars.structure.json.core.user;
import com.midas.githubstars.structure.json.function.ResBase;

import java.util.ArrayList;

//---------------------------------------------------------------------------------------------------
//
public class res_get_user_list extends ResBase
{
	/****************************** Member ******************************/
	// Response
	public String total_count = null;
	public ArrayList<user> items = null;
	/****************************** Event ******************************/
	//---------------------------------------------------------------------------------------------------
	//
	public res_get_user_list()
	{

	}
}
//---------------------------------------------------------------------------------------------------
//