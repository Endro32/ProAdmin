package com.endro32.proadmin.cli;

import com.endro32.proadmin.util.AppManager;

public class BuildCommand implements CommandExecutor {

	@Override
	public boolean onCommand(String command, String[] parameters) {
		if(!command.equalsIgnoreCase("build")) return false;
		if(parameters.length == 0) {
			AppManager.runBuildTools();
			return true;
		} else if(parameters.length >= 1) {
			if(parameters[0].equals("help")) {
				return false;
			}
			AppManager.runBuildTools(parameters[0]);
			return true;
		}
		return false;
	}

	@Override
	public void showUsage() {
		System.out.println("Used to build the Vanilla, Craftbukkit, and SpigotMC server jars\n"
				+ "Usage:\n"
				+ "build (version)");
	}

	@Override
	public String getDescription() {
		return "Builds the Vanilla, Craftbukkit, and SpigotMC server jars";
	}

}
