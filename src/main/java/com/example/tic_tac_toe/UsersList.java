package com.example.tic_tac_toe;

import java.util.ArrayList;
import java.util.List;

public class UsersList
{
	public static List<User> users;
	
	public static boolean add(User user)
	{
		if (users == null)
		{
			users = new ArrayList<>();
		}
		
		for (User userInList: users)
		{
			if (userInList.getSession().equals(user.getSession()))
			{
				return false;
			}
		}

		users.add(user);
		return true;
	}
	
	public static void print()
	{
		for (User user: users)
		{
			System.out.println(user.getUsername());
			System.out.println(user.getSession());
			System.out.println("-----------------");
		}
	}
}