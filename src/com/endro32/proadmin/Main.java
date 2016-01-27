package com.endro32.proadmin;
	
import com.endro32.proadmin.cli.CLI;
import com.endro32.proadmin.cli.ListCommand;
import com.endro32.proadmin.config.BungeeConfig;
import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.util.AppManager;
import com.endro32.proadmin.util.FileManager;
import com.endro32.proadmin.util.ServerManager;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
	
	static CLI cli;
	
	@Override
	public void start(Stage primaryStage) {
		
	}
	
	public static void main(String[] args) {
		initialize();
		System.out.println("Done initializing!");
		if(args.length > 0 && args[0].equals("gui")) {
			// Start GUI
			launch(args);
		} else {
			startCLI(); // Start Command-Line Interface
		}
		
		
		// Crap to be done
		
		
		
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
		if(Config.getBungeecordEnabled()) {
			BungeeConfig.initialize();
		}
		if(Config.getAutoupdate(Config.AutoUpdate.SERVERS)) {
			ServerManager.updateAll();
		}
		if(Config.getBungeecordEnabled()) {
			ServerManager.updateBungeecordRegistry();
		}
		return;
	}
	
	static void startCLI() {
		cli = new CLI();
		// Register command executors
		cli.registerExecutor("list", new ListCommand());
		cli.listen();
	}
	
}
