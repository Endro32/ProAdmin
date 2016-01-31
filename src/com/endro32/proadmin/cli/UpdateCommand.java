package com.endro32.proadmin.cli;

import com.endro32.proadmin.util.AppManager;
import com.endro32.proadmin.util.FileManager;
import com.endro32.proadmin.util.ServerManager;

public class UpdateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(String command, String[] parameters) {
		if(!command.equalsIgnoreCase("update") || parameters.length < 1) return false;
		switch(parameters[0]) {
		case "help":
			showUsage();
			return true;
		case "filesystem":
			FileManager.updateFileSystem();
			return true;
		case "servers":
			ServerManager.updateAll();
			return true;
		case "app":
			if(parameters.length <= 1) {
				AppManager.updateAll();
			} else {
				switch(parameters[1]) {
				case "vanilla":
					return AppManager.updateVanilla();
				case "snapshot":
					return AppManager.updateSnapshot();
				}
			}
		}
		return false;
	}

	@Override
	public void showUsage() {
		System.out.println("\nThe update command is used to update the files on the system.");
		System.out.println("Here are the different ways it can be used:");
		System.out.println(
				  "- 'help': Prints the usage info\n"
				+ "- 'filesystem': Updates all the files on the system"
				+ "- 'servers': Updates all the servers on the system");
		
	}

}
