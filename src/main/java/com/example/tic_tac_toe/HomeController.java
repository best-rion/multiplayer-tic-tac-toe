package com.example.tic_tac_toe;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController
{
	@GetMapping(value="/")
	public String home(Model model, HttpSession session)
	{
		if ( UsersList.httpSessionToUsername.containsKey( session.getId() ) )
		{
			String principal = UsersList.httpSessionToUsername.get(session.getId());
					
			model.addAttribute( "principal", UsersList.httpSessionToUsername.get(session.getId()) );
			model.addAttribute( "opponent", UsersList.usernameToOpponent.get(principal) );
			return "home";
		}
		return "redirect:/register";
	}
	
	@GetMapping(value = "/register")
	public String registerGet(Model model)
	{
		model.addAttribute("user", new User());
		return "register";
	}
	
	@PostMapping(value = "/register")
	public String registerPost(@ModelAttribute User user, HttpSession session)
	{
		if( !UsersList.map(session.getId(), user.getUsername()) )
		{
			return "NameAlreadyTaken";
		}

		return "redirect:/opponent";
	}
	
	@GetMapping(value = "/opponent")
	public String opponentGet(Model model, HttpSession session )
	{
		List<String> userList = new ArrayList<>();
		
		for (String username: UsersList.usernames )
		{
			if ( ! UsersList.httpSessionToUsername.get(session.getId()).equals(username) )
			{
				userList.add(username);
			}
		}
		model.addAttribute("opponentObject", new User());
		model.addAttribute("opponentList", userList);
		return "opponent";
	}
	
	@PostMapping(value = "/opponent")
	public String opponentPost(@ModelAttribute User opponent, HttpSession session)
	{
		// CHECK IF THE OPPONENT EXISTS IN LIST
		if ( UsersList.usernames.contains(opponent.getUsername())
			&& 
			 (!opponent.getUsername().equals(UsersList.httpSessionToUsername.get(session.getId()))) )
		{
			// IF OPPONENT EXIST THEN SET MY OPPONENT IN HASHMAP AND GO HOME
			UsersList.usernameToOpponent.put(
					UsersList.httpSessionToUsername.get(session.getId()),
					opponent.getUsername()
					);
			return "redirect:/";
		}

		return "NoSuchUser";
	}
}