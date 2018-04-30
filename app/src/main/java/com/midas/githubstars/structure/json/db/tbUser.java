package com.midas.githubstars.structure.json.db;

import java.io.Serializable;

/*
Category
 */
public class tbUser implements Serializable
{
	/****************************** Define ******************************/
	//Favorite Type
	public static final String FAV_TRUE = "TRUE";
	public static final String FAV_FALSE = "FALSE";
	/****************************** Member ******************************/
	public String login = null;//Taejun
	public String id = null;//3479380,
	public String avatar_url = null;//: "https://avatars2.githubusercontent.com/u/3479380?v=4",
	public String fav_status = null;//true, false
	/****************************** Event ******************************/
	//---------------------------------------------------------------------------------------------------
	//
	public tbUser()
	{

	}
	//---------------------------------------------------------------------------------------------------
	//
	public tbUser(String login, String id, String avatar_url, String fav_status)
	{
		this.login = login;
		this.id = id;
		this.avatar_url = avatar_url;
		this.fav_status = fav_status;
	}
}
