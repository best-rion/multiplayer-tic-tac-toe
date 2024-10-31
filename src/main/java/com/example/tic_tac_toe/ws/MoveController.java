package com.example.tic_tac_toe.ws;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.tic_tac_toe.User;
import com.example.tic_tac_toe.UsersList;

@Controller
public class MoveController
{
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	
	@MessageMapping(value="/makeMove")
	public void makeMove(Move move, @Header("simpSessionId") String sessionId)
	{
		
		sendMessage(sessionId,"/queue/move", move);
			
		String opponentSessionId = UsersList.socketToSocket.get(sessionId);
		sendMessage( opponentSessionId  ,"/queue/move", move);
	}

	
	
	@MessageMapping(value="/I_am_active_now")
	@SendTo("/topic/updateUsers")
	public  Set<String> dashboardActive(User myName,  @Header("simpSessionId") String sessionId)
	{
		UsersList.DASHBOARD_usernameToSocketSession.put(myName.getUsername(), sessionId);
		return UsersList.DASHBOARD_usernameToSocketSession.keySet();
	}
	
	
	@MessageMapping(value="/I_am_leaving_now")
	@SendTo("/topic/updateUsers")
	public Set<String> dashboardLeave(User myName,  @Header("simpSessionId") String sessionId)
	{
		UsersList.DASHBOARD_usernameToSocketSession.remove(myName.getUsername());
		return UsersList.DASHBOARD_usernameToSocketSession.keySet();
	}
	
	
	
	
	@MessageMapping(value="/inHome")
	public void inHome(User myName,  @Header("simpSessionId") String sessionId)
	{
		System.out.println("I am home");
		UsersList.HOME_usernameToSocketSession.put(myName.getUsername(), sessionId);
		
		
		String opponentUsername = UsersList.usernameToOpponent.get(myName.getUsername());
		
		if ( ! UsersList.HOME_usernameToSocketSession.keySet().contains(opponentUsername) )
		{
			String opponentSessionId = UsersList.DASHBOARD_usernameToSocketSession.get(opponentUsername);
			sendMessage( opponentSessionId  ,"/queue/knockFrom", myName );
		}
		else
		{
			String opponentSessionId = UsersList.HOME_usernameToSocketSession.get(opponentUsername);
			sendMessage( opponentSessionId,"/queue/opponentMessage", new Message("Ready") );
			sendMessage( sessionId,"/queue/opponentMessage", new Message("Let's go") );
			
			UsersList.socketToSocket.put(sessionId, opponentSessionId);
			UsersList.socketToSocket.put(opponentSessionId, sessionId);
		}
		
		
		for (String user: UsersList.usernameToOpponent.keySet())
		{
			String hisOpponent = UsersList.usernameToOpponent.get(user);
			if (hisOpponent.equals(myName.getUsername()) && (!user.equals(opponentUsername)))
			{
				String opponentSessionId = UsersList.HOME_usernameToSocketSession.get(user);
				sendMessage( opponentSessionId, "/queue/opponentMessage",  new Message("Rejected") );	
			}
		}
	}
	
	
	
	@MessageMapping(value="/outtaHome")
	public void outtaHome(User myName,  @Header("simpSessionId") String sessionId)
	{
		System.out.println("I am going");
		UsersList.HOME_usernameToSocketSession.remove(myName.getUsername());
		
		String opponentUsername = UsersList.usernameToOpponent.get(myName.getUsername());
		
		if ( UsersList.DASHBOARD_usernameToSocketSession.keySet().contains(opponentUsername) ) // it means they are still on dashboard
		{
			String opponentSessionId = UsersList.DASHBOARD_usernameToSocketSession.get(opponentUsername);
			sendMessage( opponentSessionId, "/queue/unknockFrom", myName);
			UsersList.usernameToOpponent.remove(myName.getUsername());
		}
		
		if ( UsersList.HOME_usernameToSocketSession.keySet().contains(opponentUsername) )
		{
			String opponentSessionId = UsersList.HOME_usernameToSocketSession.get(opponentUsername);
			sendMessage( opponentSessionId, "/queue/opponentMessage", new Message("Quitted"));
			UsersList.usernameToOpponent.remove(myName.getUsername());
			
			UsersList.socketToSocket.remove(sessionId);
			UsersList.socketToSocket.remove(opponentSessionId);
		}
	}
	
	
	
	@MessageMapping(value="/letsPlayAgain")
	public void playAgain(User myName,  @Header("simpSessionId") String sessionId)
	{
		String opponentSessionId = UsersList.socketToSocket.get(sessionId);
		
		sendMessage( opponentSessionId, "/queue/opponentMessage", new Message("Play Again"));
	}
	
	
	
	private void sendMessage(String user, String destination, Object payload)
	{
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId( user );
		headerAccessor.setLeaveMutable(true);
		messagingTemplate.convertAndSendToUser( user, destination, payload, 
				headerAccessor.getMessageHeaders());
	}
}