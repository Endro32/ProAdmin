package com.endro32.proadmin.cli;

import java.util.Scanner;

import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.util.FileManager;
import com.endro32.proadmin.util.GroupMode;

public class NewCommand implements CommandExecutor {

	CLI cli;
	Scanner scnr;
	
	public NewCommand(CLI cli, Scanner scan) {
		this.cli = cli;
		scnr = scan;
	}
	
	@Override
	public boolean onCommand(String command, String[] parameters) {
		if(!command.equalsIgnoreCase("new") || parameters.length < 1)
			return false;
		
		if(parameters.length == 1) {
			switch(parameters[0]) {
			case "group":
				String gname;
				String in;
				GroupMode mode;
				
				System.out.print("Group name: ");
				gname = scnr.nextLine();
				System.out.print("Individual or cloned? [I/c]: ");
				in = scnr.nextLine();
				
				in = in.toLowerCase();
				if(in.startsWith("c"))
					mode = GroupMode.CLONED;
				else
					mode = GroupMode.INDIVIDUAL;
				Config.createGroup(gname, mode);
				FileManager.updateServerTree();
				cli.selectGroup(gname);
				return true;
			case "server":
				String group;
				String name;
				if(cli.isGroupSelected()) {
					group = cli.getSelectedGroup();
				} else {
					System.out.print("Group to add server to: ");
					group = scnr.nextLine();
					
					if(!Config.getServerGroupNames().contains(group)) {
						System.out.println(group+" is not a valid group!");
						return true;
					} else if(Config.getMode(group) == GroupMode.CLONED) {
						System.out.println(group+" is a cloned group. Cannot create server.\n"
								+ ""); // TODO add instructions to change number of servers in cloned group
						return true;
					}
					cli.selectGroup(group);
				}
				
				System.out.print("Name: ");
				name = scnr.nextLine();
				Config.createServer(cli.getSelectedGroup(), name);
				FileManager.updateServerDirectory(group, name);
				cli.serverManager.reloadServer(group, name);
				cli.selectServer(name);
				return true;
			}
		} else if(parameters.length == 2 && parameters[0].equals("server")) {
			// New server with group specified in initial command
			if(!Config.getServerGroupNames().contains(parameters[1])) {
				System.out.println(parameters[1]+" is not a valid group!");
				return true;
			} else if(Config.getMode(parameters[1]) == GroupMode.CLONED) {
				System.out.println("Cannot create server in cloned group");
				return true;
			}
		} else if(parameters.length >= 3 && parameters[0].equals("group")) {
			// New group with name and type specified in initial command
		}
		return false;
	}

	@Override
	public void showUsage() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescription() {
		return "Creates a new group or server";
	}

}
