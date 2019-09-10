package com.endro32.proadmin.cli;

import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.util.GroupMode;

public class SelectCommand implements CommandExecutor {

	CLI cli;
	
	public SelectCommand(CLI cli) {
		this.cli = cli;
	}
	
	@Override
	public boolean onCommand(String command, String[] parameters) {
		if(!command.equalsIgnoreCase("select") || parameters.length < 1)
			return false;
		if(parameters[0].equals("home") || parameters[0].equals("root")) {
			cli.deselectAll();
			
		} else if(parameters[0].equals("up") || parameters[0].equals("..")) {
			if(cli.isGroupSelected() && !cli.isServerSelected())
				cli.deselectAll();
			else if(cli.isGroupSelected() && cli.isServerSelected())
				cli.deselectServer();
			else if(cli.isBungeeSelected())
				cli.deselectAll();
			
		} else if(parameters[0].equals("bungeecord")) {
			cli.selectBungee();
			
		} else if(parameters.length >= 2 && parameters[0].equals("group")) {
			cli.selectGroup(parameters[1]);
			
		} else if(parameters[0].equals("server")) {
			if(parameters.length == 2 && cli.isGroupSelected()) {
				if(!cli.selectServer(parameters[1])) {
					System.out.println("That server does not exist");
				}
			} else if(parameters.length >= 3) {
				if(!Config.getServerGroupNames().contains(parameters[1])) {
					System.out.println(parameters[1]+" is not a valid group!");
				} else if(Config.getMode(parameters[1]) == GroupMode.CLONED) {
					System.out.println("Cannot select server inside cloned group "+parameters[1]);
				} else {
					if(!cli.selectServer(parameters[1], parameters[2])) {
						System.out.println("That server does not exist");
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void showUsage() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
