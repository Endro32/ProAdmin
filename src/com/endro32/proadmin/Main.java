package com.endro32.proadmin;
	
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.endro32.proadmin.cli.CLI;
import com.endro32.proadmin.config.BungeeConfig;
import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.util.AppManager;
import com.endro32.proadmin.util.FileManager;
import com.endro32.proadmin.util.PluginManager;
import com.endro32.proadmin.util.ServerManager;


public class Main {
	
	static CLI cli;
	static PluginManager pluginManager;
	
	public static void main(String[] args) {
		
		cli = new CLI();
		pluginManager = new PluginManager();
		
		cli.printHeader();
		
		// Prompt to run wizard if there is no global config
		if(FileManager.isBlankSlate()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String in = "";
			System.out.print("It appears there is no global config! Run wizard? [Y/n]: ");
			try {
				in = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(CLI.confirm(in)) {
				; // Run wizard
			} else {
				System.out.println("Generating new config instead");
				Config.resetConfig(false);
			}
		}
		
		cli.listen(); // Start Command-Line Interface
		
		System.exit(0);
	}
	
	static void initialize() {
		FileManager.updateFileSystem();
		if(Config.getAutoupdate(Config.AutoUpdate.VANILLA)) {
			AppManager.updateVanilla();
		}
		if(Config.getAutoupdate(Config.AutoUpdate.SNAPSHOT)) {
			AppManager.updateSnapshot();
		}
		if(Config.getAutoupdate(Config.AutoUpdate.SPIGOT)) {
			AppManager.updateBuildTools();
			AppManager.runBuildTools();
			AppManager.updateSpigot();
		}
		if(Config.getAutoupdate(Config.AutoUpdate.BUNGEECORD)) {
			AppManager.updateBungeecord();
		}
		boolean bungee = Config.getBungeecordEnabled();
		if(bungee) {
			BungeeConfig.initialize();
		}
		if(Config.getAutoupdate(Config.AutoUpdate.SERVERS)) {
			ServerManager.updateAll();
		}
		if(bungee) {
			ServerManager.updateBungeecordRegistry();
		}
		System.out.println("Done initializing!");
	}
	
	/**
	 * Returns the main CLI object
	 * @return CLI
	 */
	public static CLI getCLI() {
		return cli;
	}
	
	/**
	 * Returns the main PluginManager
	 * @return Plugin Manager
	 */
	public static PluginManager getPluginManager() {
		return pluginManager;
	}
	
	/*
	 * Task list:
	 * Add installToServer() method in appManager
	 * Add app management to 'new' command
	 */
	
}
