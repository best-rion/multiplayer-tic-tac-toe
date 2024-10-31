package com.example.tic_tac_toe;

import java.util.HashMap;

public class UsersList
{
	public static HashMap<String, String> httpSession_username = new HashMap<String, String>();
	
	public static HashMap<String, String> usernameToOpponent = new HashMap<String, String>();// Bi needed
	
	public static HashMap<String, String> DASHBOARD_usernameToSocketSession = new HashMap<String, String>();
	
	public static HashMap<String, String> HOME_usernameToSocketSession = new HashMap<String, String>();
	
	public static HashMap<String, String> socketToSocket = new HashMap<String, String>();// Bi needed
	
	
	public static boolean insert(String httpSession, String username)
	{
		if( httpSession_username.values().contains(username) )
		{
			return false;
		}
		
		httpSession_username.put(httpSession, username);
		return true;
	}
}