package com.endro32.proadmin.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLI {
	
	BufferedReader input;
	String lastInput;
	
	String lastCommand;
	String[] parameters;
	
	Map<String, CommandExecutor> executors;
	
	public CLI() {
		input = new BufferedReader(new InputStreamReader(System.in));
		executors = new HashMap<String, CommandExecutor>();
	}
	
	public void listen() {
		while(true) {
			try {
				System.out.print("$: ");
				lastInput = input.readLine();
			} catch(IOException e) {
				e.printStackTrace();
			}
			parseLastInput();
			if(lastCommand.equals("exit")) break;
			executeLastCommand();
		}
	}
	
	public void printHeader() {
		System.out.println(
				"    ____                ___        __            _         \n" + 
				"   / __ \\ _____ ____   /   |  ____/ /____ ___   (_)____   \n" + 
				"  / /_/ // ___// __ \\ / /| | / __  // __ `__ \\ / // __ \\\n" + 
				" / ____// /   / /_/ // ___ |/ /_/ // / / / / // // / / /   \n" + 
				"/_/    /_/    \\____//_/  |_|\\__,_//_/ /_/ /_//_//_/ /_/  \n");
	}
	
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
	
	public void registerExecutor(String command, CommandExecutor executor) {
		if(executors.containsKey(command.toLowerCase())) return;
		executors.put(command.toLowerCase(), executor);
	}
	
	/**
	 * Takes a string from a yes/no prompt and generates a boolean
	 * @param in
	 * @return
	 */
	public static boolean confirm(String in) {
		in = in.toLowerCase();
		if(in.isEmpty() || in.startsWith("y"))
			return true;
		return false;
	}
	
}
