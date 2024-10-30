package com.example.tic_tac_toe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersList
{
	public static List<String> usernames = new ArrayList<>();
	
	public static HashMap<String, String> httpSessionToUsername = new HashMap<String, String>();
	
	public static HashMap<String, String> usernameToOpponent = new HashMap<String, String>();
	
	public static boolean map(String httpSession, String username)
	{
		if( usernames.contains(username) )
		{
			return false;
		}
		
		usernames.add(username);
		httpSessionToUsername.put(httpSession, username);
		return true;
	}
}