package com.example.tic_tac_toe.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
	
	HashMap<String, String> sessionToName = new HashMap<String, String>();
	HashMap<String, String> nameToSession = new HashMap<String, String>();
	
	List<String> messageForOpponent = new ArrayList<>();
	
	@MessageMapping(value="/map")
	public void map(User myName,  @Header("simpSessionId") String sessionId)
	{
		sessionToName.put( sessionId, myName.getUsername() );
		nameToSession.put( myName.getUsername(), sessionId );
		
		
		String opponentSessionId = nameToSession.get( UsersList.usernameToOpponent.get( sessionToName.get(sessionId) ) );
		
		if (opponentSessionId != null)
		{
			SimpMessageHeaderAccessor opponentHeaderAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
			opponentHeaderAccessor.setSessionId( opponentSessionId );
			opponentHeaderAccessor.setLeaveMutable(true);
			System.out.println("sending notification to: "+opponentSessionId);
			// send to my opponent
			messagingTemplate.convertAndSendToUser( opponentSessionId  ,"/queue/knock", myName, 
					opponentHeaderAccessor.getMessageHeaders());
		}
		else
		{
			// keep the notification for later
			messageForOpponent.add(myName.getUsername());
		}
		
		
		if ( messageForOpponent.contains( UsersList.usernameToOpponent.get(myName.getUsername()) ) )
		{
			SimpMessageHeaderAccessor opponentHeaderAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
			opponentHeaderAccessor.setSessionId( sessionId );
			opponentHeaderAccessor.setLeaveMutable(true);
			// send to my opponent
			
			User u = new User();
			u.setUsername(UsersList.usernameToOpponent.get(myName.getUsername()));
			messagingTemplate.convertAndSendToUser( sessionId  ,"/queue/knock", u,
					opponentHeaderAccessor.getMessageHeaders());
		}

		
	}
	
	
	
	@MessageMapping(value="/move")
	public void send(Move move, @Header("simpSessionId") String sessionId)
	{
		SimpMessageHeaderAccessor myHeaderAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		myHeaderAccessor.setSessionId(sessionId);
		myHeaderAccessor.setLeaveMutable(true);
			
		// send back to me
		messagingTemplate.convertAndSendToUser(sessionId,"/queue/move", move, 
				myHeaderAccessor.getMessageHeaders());
			
		
		String opponentSessionId = nameToSession.get( UsersList.usernameToOpponent.get( sessionToName.get(sessionId) ) );
			
		SimpMessageHeaderAccessor opponentHeaderAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		opponentHeaderAccessor.setSessionId( opponentSessionId  );
		opponentHeaderAccessor.setLeaveMutable(true);
		
		// send to my opponent
		messagingTemplate.convertAndSendToUser( opponentSessionId  ,"/queue/move", move, 
				opponentHeaderAccessor.getMessageHeaders());
	}
}