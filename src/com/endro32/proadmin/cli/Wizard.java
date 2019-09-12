package com.endro32.proadmin.cli;

import java.util.Scanner;

public class Wizard implements CommandExecutor {

	Scanner scnr;
	
	public Wizard(Scanner scanner) {
		scnr = scanner;
	}
	
	@Override
	public boolean onCommand(String command, String[] parameters) {
		switch(parameters[0]) {
		case "setup": // Initial setup wizard
			return setupWizard();
		case "new": // New server or group wizard
			return newWizard();
		case "delete": // Delete server or group wizard
			return deleteWizard();
		case "config": // Wizard for managing supported configuration files
			return configWizard();
		}
		return false;
	}

	@Override
	public void showUsage() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Runs one of the various wizards";
	}
	
	public boolean setupWizard() {
		return true;
	}
	
	public boolean newWizard() {
		return true;
	}
	
	public boolean deleteWizard() {
		return true;
	}
	
	public boolean configWizard() {
		return true;
	}

}
