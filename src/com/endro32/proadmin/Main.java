package com.endro32.proadmin;
	
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.endro32.proadmin.cli.BuildCommand;
import com.endro32.proadmin.cli.CLI;
import com.endro32.proadmin.cli.HelpCommand;
import com.endro32.proadmin.cli.ListCommand;
import com.endro32.proadmin.cli.UpdateCommand;
import com.endro32.proadmin.cli.Wizard;
import com.endro32.proadmin.config.BungeeConfig;
import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.util.AppManager;
import com.endro32.proadmin.util.FileManager;
import com.endro32.proadmin.util.ServerManager;


public class Main {
	
	static CLI cli;
	
	public static void main(String[] args) {
		//initialize();
		
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
		
		startCLI(); // Start Command-Line Interface
		
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
	 * Creates CLI object, registers command executors, and begins listening for commands
	 */
	static void startCLI() {
		cli = new CLI();
		// Register command executors\
		cli.registerExecutor("help", new HelpCommand());
		cli.registerExecutor("list", new ListCommand());
		cli.registerExecutor("update", new UpdateCommand());
		cli.registerExecutor("wizard", new Wizard());
		cli.registerExecutor("build", new BuildCommand());
		cli.printHeader();
		cli.listen();
	}
	
	public static CLI getCLI() {
		return cli;
	}
	
}
