package com.endro32.proadmin.cli;

import java.util.Arrays;

import com.endro32.proadmin.Main;

public class HelpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(String command, String[] parameters) {
		CLI cli = Main.getCLI();
		if(parameters.length >= 1 && cli.executors.containsKey(parameters[0])) {
			cli.executors.get(parameters[0]).showUsage();
			return true;
		}
		
		// Sorts all registered commands by alphabetical order and prints them out along with their descriptions
		System.out.println("The following commands are available:");
		Object[] keys = cli.executors.keySet().toArray();
		Arrays.sort(keys);
		for(Object key : keys) {
			String s = key.toString();
			char[] padding = new char[24 - s.length()];
			Arrays.fill(padding, '.');
			padding[0] = ' ';
			padding[padding.length - 1] = ' ';
			System.out.println("- " + s + String.valueOf(padding) + cli.executors.get(key).getDescription());
		}
		return true;
	}

	@Override
	public void showUsage() {
		// TODO Auto-generated method stub
		System.out.println("There are dragon eggs around here somewhere...");
	}
	
	@Override
	public String getDescription() {
		return "Prints a list of available commands";
	}

}
