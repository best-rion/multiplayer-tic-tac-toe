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
		// CHECK IF THIS SESSION IS REGISTERED
		if ( UsersList.httpSession_username.containsKey( session.getId() ) )
		{	// IF REGISTERED, SHOW HOME PAGE WITH MY NAME AND OPPONENT'S NAME
			String principal = UsersList.httpSession_username.get(session.getId());
					
			model.addAttribute( "principal", UsersList.httpSession_username.get(session.getId()) );
			model.addAttribute( "opponent", UsersList.usernameToOpponent.get(principal) );
			return "home";
		}
		// OTHERWISE TAKE ME TO THE REGISTER PAGE
		return "redirect:/register";
	}
	
	
	@GetMapping(value = "/register")
	public String registerGet(Model model)
	{	// NOTHING. JUST A FORM TO REGISTER MY NAME
		model.addAttribute("user", new User());
		return "register";
	}
	
	@PostMapping(value = "/register")
	public String registerPost(@ModelAttribute User user, HttpSession session)
	{	// TRY TO INSERT MY NAME IN httpSession_Username Hash List
		if( !UsersList.insert(session.getId(), user.getUsername()) )
		{	// IF insert RETURNS FALSE IT MEANS VALUE IS ALREADY THERE
			return "NameAlreadyTaken";
		}
		// IF SUCCESSFULLY REGISTERED, GO TO THE DASHBOARD
		return "redirect:/dashboard";
	}
	
	@GetMapping(value = "/dashboard")
	public String dashboardGet(Model model, HttpSession session)
	{	

		String principal = UsersList.httpSession_username.get( session.getId() );
		
		List<String> activeUsers = new ArrayList<>();
		
		for (String user: UsersList.httpSession_username.values())
		{
			if (!user.equals(principal))
			{
				activeUsers.add(user);
			}
		}
		
		
		List<String> knockers = new ArrayList<>();
		for (String user: UsersList.usernameToOpponent.keySet())
		{
			if (principal.equals(UsersList.usernameToOpponent.get(user)))
			{
				knockers.add(user);
			}
		}
		
		
		model.addAttribute("principal", principal);
		model.addAttribute("opponent", new User());
		model.addAttribute("activeUsers", activeUsers);
		model.addAttribute("knockers", knockers);
		return "dashboard";
	}
	
	@PostMapping(value = "/dashboard")
	public String dashboardPost(@ModelAttribute User opponent, HttpSession session)
	{	
		String principal = UsersList.httpSession_username.get( session.getId() );
		
		UsersList.usernameToOpponent.put(principal, opponent.getUsername());
		
		return "redirect:/";
	}
}