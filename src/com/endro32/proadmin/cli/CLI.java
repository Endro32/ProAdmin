package com.endro32.proadmin.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.util.Server;
import com.endro32.proadmin.util.ServerManager;

public class CLI {
	
	Map<String, CommandExecutor> executors;
	
	ServerManager serverManager;
	
	BufferedReader input;
	String lastInput;
	
	String lastCommand;
	String[] parameters;
	
	private String selGroup; // Group name
	private String selServer; // Server key
	private boolean bungeeSelected;
	
	public CLI() {
		input = new BufferedReader(new InputStreamReader(System.in));
		executors = new HashMap<String, CommandExecutor>();
		serverManager = new ServerManager();
		
		bungeeSelected = false;
	}
	
	/**
	 * Begins an infinite loop of reading input lines, ending when the user types 'exit'
	 */
	public void listen() {
		while(true) {
			String pre = "~";
			try {
				if(isGroupSelected())
					pre = pre.concat("/"+selGroup);
				if(isServerSelected())
					pre = pre.concat("/"+selServer.substring(selServer.lastIndexOf('.')+1));
				System.out.print(pre+"$ ");
				lastInput = input.readLine();
			} catch(IOException e) {
				e.printStackTrace();
			}
			parseLastInput();
			if(lastCommand.equals("exit")) break;
			executeLastCommand();
		}
	}
	
	/**
	 * Print a beautiful ProAdmin text art banner
	 */
	public void printHeader() {
		System.out.println(
				  "    ____                ___        __            _         \n"
				+ "   / __ \\ _____ ____   /   |  ____/ /____ ___   (_)____   \n"
				+ "  / /_/ // ___// __ \\ / /| | / __  // __ `__ \\ / // __ \\\n"
				+ " / ____// /   / /_/ // ___ |/ /_/ // / / / / // // / / /   \n"
				+ "/_/    /_/    \\____//_/  |_|\\__,_//_/ /_/ /_//_//_/ /_/  \n\n"
				+ "Version: 0.0.1                                By Endro32\n"
				+ "Enter 'exit' to quit, 'help' for help\n"
				+ "------------------------------------------------------------");
	}
	
	/**
	 * Parses lastInput string to 'lastCommand' string and 'parameters' string array
	 */
	private void parseLastInput() {
		List<String> list = new ArrayList<String>();
		try {
			list.addAll(Arrays.asList(lastInput.split(" ")));
		} catch(NullPointerException e) {
			lastInput = "";
			lastCommand = "";
			parameters = new String[0];
			return;
		}
		lastCommand = list.remove(0).toLowerCase();
		parameters = list.toArray(new String[list.size()]);
	}
	
	/**
	 * Finds the executor responsible for the last command entered by the user,
	 * and invokes the onCommand method of that executor.
	 * Yells at user if the command isn't registered,
	 * shows usage if onCommand returns false
	 */
	private void executeLastCommand() {
		if(lastCommand == null || parameters == null) parseLastInput();
		if(!executors.containsKey(lastCommand)) {
			System.out.println("Unknown Command!");
			return;
		}
		CommandExecutor exec = executors.get(lastCommand);
		boolean success = exec.onCommand(lastCommand, parameters);
		if(!success) {
			System.out.println("Invalid usage!");
			exec.showUsage();
		}
	}
	
	/**
	 * Register an executor in the map
	 * @param command String the user will enter
	 * @param executor CommandExecutor object that will handle the command
	 */
	public void registerExecutor(String command, CommandExecutor executor) {
		if(executors.containsKey(command.toLowerCase())) return;
		executors.put(command.toLowerCase(), executor);
	}
	
	/**
	 * Takes a string from a yes/no prompt and generates a boolean
	 * @param in String input from the user
	 * @return Boolean representation
	 */
	public static boolean confirm(String in) {
		in = in.toLowerCase();
		if(in.isEmpty() || in.startsWith("y"))
			return true;
		return false;
	}
	
	// Next comes the getter/setter methods for the selection variables
	
	public void deselectAll() {
		selServer = null;
		selGroup = null;
		bungeeSelected = false;
	}
	
	public void selectBungee() {
		selGroup = null;
		selServer = null;
		bungeeSelected = true;
	}
	
	public boolean isBungeeSelected() {
		return bungeeSelected;
	}
	
	/**
	 * Selects the given group
	 * @param name
	 * @return False if group doesn't exist
	 */
	public boolean selectGroup(String name) {
		if(Config.getServerGroupNames().contains(name)) {
			selGroup = name;
			return true;
		}
		System.out.println(name+" is not a valid group!");
		return false;
	}
	
	public String getSelectedGroup() {
		return selGroup;
	}
	
	public boolean isGroupSelected() {
		return !(selGroup == null || selGroup.isEmpty());
	}
	
	/**
	 * Select group with given name and select server with given name inside given group
	 * @param group
	 * @param name
	 * @return False if group or server doesn't exist
	 */
	public boolean selectServer(String group, String name) {
		String key = group+"."+name;
		if(!selectGroup(group) ||
				!serverManager.getServers().containsKey(key))
			return false;
		selServer = key;
		return true;
	}
	
	/**
	 * Select server with given name inside currently selected group
	 * @param name
	 * @return False if server doesn't exist or no group is currently selected
	 */
	public boolean selectServer(String name) {
		if(!isGroupSelected() ||
				!serverManager.getServers().containsKey(selGroup+"."+name))
			return false;
		selServer = selGroup+"."+name;
		return true;
	}
	
	public void deselectServer() {
		selServer = null;
	}
	
	public Server getSelectedServer() {
		return serverManager.getServer(selServer);
	}
	
	public boolean isServerSelected() {
		return selServer != null;
	}
	
}
