package com.endro32.proadmin.cli;

import java.util.Arrays;
import java.util.Map;

import com.endro32.proadmin.Main;

public class HelpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(String command, String[] parameters) {
		CLI cli = Main.getCLI();
		System.out.println("The following commands are available:");
		for(Map.Entry<String, CommandExecutor> entry : cli.executors.entrySet()) {
			char[] padding = new char[24 - entry.getKey().length()];
			Arrays.fill(padding, '.');
			padding[0] = ' ';
			padding[padding.length - 1] = ' ';
			System.out.println("- " + entry.getKey() + String.valueOf(padding) + entry.getValue().getDescription());
		}
		return true;
	}

	@Override
	public void showUsage() {
		// TODO Auto-generated method stub
		System.out.println("There are dragon eggs somewhere around here...");
	}
	
	@Override
	public String getDescription() {
		return "Prints a list of available commands";
	}

}
