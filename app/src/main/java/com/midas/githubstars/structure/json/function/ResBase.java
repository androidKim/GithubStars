package com.midas.githubstars.structure.json.function;

import com.midas.githubstars.structure.json.JsonBase;
import com.midas.githubstars.structure.json.core.error;

import java.util.ArrayList;

//---------------------------------------------------------------------------------------------------
//
public class ResBase extends JsonBase
{
	/****************************** Define ******************************/
	//server res code..

	/****************************** Member ******************************/
	// Common Data
	public String message = null;//Validation Failed",
	public ArrayList<error> errors = null;
	public String documentation_url = null;//https://developer.github.com/v3/search/"
	/****************************** Event ******************************/
	//---------------------------------------------------------------------------------------------------
	//
	public ResBase()
	{

	}
	//---------------------------------------------------------------------------------------------------
	//
	public ResBase(ArrayList<error> errors, String message)
	{
		this.errors = errors;
		this.message = message;
	}
	/****************************** Function ******************************/
	//---------------------------------------------------------------------------------------------------
	//
}
//---------------------------------------------------------------------------------------------------
//