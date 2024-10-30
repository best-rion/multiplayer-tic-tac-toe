package com.example.tic_tac_toe.ws;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MoveController
{
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	HashMap<String, String> nameToSession = new HashMap<String, String>();
	
	
	@MessageMapping(value="/map")
	public void map(UsernameDTO myName,  @Header("simpSessionId") String sessionId)
	{
		nameToSession.put(myName.getUsername(), sessionId);
		System.out.println(myName.getUsername()+" was mapped to session "+sessionId);
	}
	
	
	
	@MessageMapping(value="/move")
	public void send(Move move, @Header("simpSessionId") String sessionId)
	{
		SimpMessageHeaderAccessor myHeaderAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		myHeaderAccessor.setSessionId(sessionId);
		myHeaderAccessor.setLeaveMutable(true);
			
		// send back to me
		messagingTemplate.convertAndSendToUser(sessionId,"/queue/private", move, 
				myHeaderAccessor.getMessageHeaders());
			
			
			
		SimpMessageHeaderAccessor opponentHeaderAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		opponentHeaderAccessor.setSessionId(nameToSession.get( move.getOpponent() ));
		opponentHeaderAccessor.setLeaveMutable(true);
		
		// send to my opponent
		messagingTemplate.convertAndSendToUser(nameToSession.get( move.getOpponent() ),"/queue/private", move, 
				opponentHeaderAccessor.getMessageHeaders());
	}
}