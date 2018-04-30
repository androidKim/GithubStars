package com.midas.githubstars.structure.json.core;


import com.midas.githubstars.structure.json.JsonBase;

/**
 * common
 */

public class common extends JsonBase
{
    /***************************** Define *****************************/
    final public static int RET_CODE_SUCCESS = 0;
    /***************************** Member *****************************/
    //response
    public int ret_code;
    public String ret_msg = null;
    /***************************** User Function *****************************/
    //----------------------------------------------------------------------------------------
    //
    public common()
    {

    }
}
