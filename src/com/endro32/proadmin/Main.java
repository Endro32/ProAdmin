package com.endro32.proadmin;
	
import com.endro32.proadmin.config.BungeeConfig;
import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.util.AppManager;
import com.endro32.proadmin.util.FileManager;
import com.endro32.proadmin.util.ServerManager;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		
	}
	
	public static void main(String[] args) {
		//initiate();
		if(args.length > 0 && args[0].equals("gui")) {
			// Start GUI
			launch(args);
		} else {
			// Start CLI
		}
		
		
		// Crap to be done
		System.out.println("Done initializing!");
		System.out.println(Config.getGlobalName());
		
		
		System.exit(0);
	}
	
	static void initiate() {
		FileManager.updateFileSystem();
		if(Config.getBoolean("global.autoupdate.vanilla")) {
			AppManager.updateVanilla();
		}
		if(Config.getBoolean("global.autoupdate.snapshot")) {
			AppManager.updateSnapshot();
		}
		if(Config.getBoolean("global.autoupdate.spigot")) {
			AppManager.updateBuildTools();
			AppManager.runBuildTools();
			AppManager.updateSpigot();
		}
		if(Config.getBoolean("global.autoupdate.bungeecord")) {
			AppManager.updateBungeecord();
		}
		if(Config.getBoolean("bungeecord.bungeecord")) {
			BungeeConfig.initialize();
		}
		if(Config.getBoolean("global.autoupdate.servers")) {
			ServerManager.updateAll();
		}
		if(Config.getBoolean("bungeecord.bungeecord")) {
			ServerManager.updateBungeecordRegistry();
		}
		return;
	}
	
}
