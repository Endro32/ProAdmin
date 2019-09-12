package com.endro32.proadmin.cli;

public class ExitCommand implements CommandExecutor {

	@Override
	public boolean onCommand(String command, String[] parameters) {
		System.out.println("Shutting down...");
		// Do anything that needs to be done, such as saving config files
		System.exit(0);
		return true;
	}

	@Override
	public void showUsage() {
		// TODO Auto-generated method stub
		System.out.println("There are dragon eggs around here somewhere...");
		
	}

	@Override
	public String getDescription() {
		return "Exits the program";
	}

	
	
}
