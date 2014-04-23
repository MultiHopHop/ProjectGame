package com.badlogic.androidgame.authentication;

public interface Authentication {

	public boolean initialize() throws Exception;
	
	public void send(String msg) throws Exception;
	
	public String receive() throws Exception;
	
}
