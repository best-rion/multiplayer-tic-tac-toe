package com.example.tic_tac_toe;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.tic_tac_toe.ws.UsernameDTO;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController
{
	@GetMapping(value="/")
	public String home(Model model, HttpSession session)
	{
		if (UsersList.users != null) {
			for (User user: UsersList.users)
			{
				if (user.getSession().equals(session.getId()))
				{
					model.addAttribute("principal", user);
					return "home";
				}
			}
		}
		return "redirect:/register";
	}
	
	@GetMapping(value = "/register")
	public String registerGet(Model model, HttpSession session)
	{
		model.addAttribute("user", new User());
		model.addAttribute("sessionId",session.getId());
		return "register";
	}
	
	@PostMapping(value = "/register")
	public String registerPost(@ModelAttribute User user)
	{
		if(!UsersList.add(user))
		{
			return "ThisNameAlreadyExists";
		}
		UsersList.print()
;
		return "redirect:/opponent";
	}
	
	@GetMapping(value = "/opponent")
	public String opponentGet(Model model, HttpSession session )
	{
		List<UsernameDTO> listOfOpponents = new ArrayList<>();
		
		if (UsersList.users != null) {
			for (User user: UsersList.users)
			{
				if (!user.getSession().equals(session.getId()) )
				{
					UsernameDTO userDTO = new UsernameDTO();
					userDTO.setUsername(user.getUsername());
					listOfOpponents.add(userDTO);
				}
			}
		}
		
		
		model.addAttribute("opponentObject", new UsernameDTO());
		model.addAttribute("opponentList", listOfOpponents);
		return "opponent";
	}
	
	@PostMapping(value = "/opponent")
	public String opponentPost(@ModelAttribute UsernameDTO opponent, HttpSession session)
	{
		// CHECK IF THE OPPONENT EXISTS IN LIST
		boolean opponentExists = false;
		
		if (UsersList.users != null) {
			for (User user: UsersList.users)
			{
				if ( user.getUsername().equals(opponent.getUsername())  && (!user.getSession().equals(session.getId())) )
				{
					opponentExists = true;
					break;
				}
			}
		}
		
		if (!opponentExists)
		{
			return "NoSuchUser";
		}

		// IF OPPONENT EXIST THEN START THE GAME
		if (UsersList.users != null) {
			for (User user: UsersList.users)
			{
				if (user.getSession().equals(session.getId())) // it means I have completed registration
				{
					user.setOpponent(opponent.getUsername());
				}
			}
		}
		return "redirect:/";
	}
}