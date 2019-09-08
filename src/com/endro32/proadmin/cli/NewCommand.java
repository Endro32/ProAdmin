package com.endro32.proadmin.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.util.FileManager;
import com.endro32.proadmin.util.GroupMode;

public class NewCommand implements CommandExecutor {

	@Override
	public boolean onCommand(String command, String[] parameters) {
		if(!command.equalsIgnoreCase("new") || parameters.length < 1) return false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		if(parameters.length == 1) {
			switch(parameters[0]) {
			case "group":
				String gname;
				String in;
				GroupMode mode;
				try {
					System.out.print("Group name: ");
					gname = br.readLine();
					System.out.print("Individual or cloned? [I/c]: ");
					in = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
					return true;
				}
				
				in = in.toLowerCase();
				if(in.startsWith("c"))
					mode = GroupMode.CLONED;
				else
					mode = GroupMode.INDIVIDUAL;
				Config.createGroup(gname, mode);
				FileManager.updateFileSystem();
				
				return true;
			case "server":
				return true;
			}
		} else if(parameters.length >= 2 && parameters[0].equals("server")) {
			if(!Config.getServerGroupNames().contains(parameters[1])) {
				System.out.println(parameters[1]+" is not a valid group!");
				return true;
			} else if(Config.getMode(parameters[1]) == GroupMode.CLONED) {
				System.out.println("Cannot create server in cloned group");
				return true;
			}
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
