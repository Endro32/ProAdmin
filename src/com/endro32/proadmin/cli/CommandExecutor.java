package com.endro32.proadmin.cli;


public interface CommandExecutor {
	
	/**
	 * Executes anytime the command is run
	 * @param command
	 * @param parameters
	 * @return false if command execution failed
	 */
	public abstract boolean onCommand(String command, String[] parameters);
	public abstract void showUsage();
	public abstract String getDescription();
	
}
