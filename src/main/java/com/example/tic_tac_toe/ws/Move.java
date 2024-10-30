package com.example.tic_tac_toe.ws;

public class Move
{
	private String x;
	private String y;
	private String opponent;
	
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getOpponent() {
		return opponent;
	}
	public void setOpponent(String opponentName) {
		this.opponent = opponentName;
	}
}