package com.wmclient.gps;

import android.content.Context;

public class ContextOpt {

	private static ContextOpt sIns=null;

	private Context mAppContext=null;
	
	private ContextOpt()
	{
		
	  		
	}
	
	public static ContextOpt getInstance()
	{
		if(sIns==null)
			sIns=new ContextOpt();
		
		return sIns;
		
		
	}
	
	public void setAppContext(Context context)
	{
		mAppContext=context;
	}
	
	public Context getAppContext()
	{
		return mAppContext;
	}
   
}
