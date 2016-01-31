package com.endro32.proadmin.cli;


public interface CommandExecutor {
	
	public abstract boolean onCommand(String command, String[] parameters);
	public abstract void showUsage();
	
}
