package com.badlogic.androidgame.authentication;

public interface Authentication {

	public boolean initialize() throws Exception;
	
	public void safeWrite(String msg) throws Exception;
	
	public String safeRead() throws Exception;
	
}
