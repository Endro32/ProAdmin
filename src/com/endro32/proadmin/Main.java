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
		initiate();
		if(args.length > 0 && args[0].equals("gui")) {
			// Start GUI
			launch(args);
		} else {
			// Start CLI
		}
		
		
		// Crap to be done
		System.out.println("Done initializing!");
		
		
		
		System.exit(0);
	}
	
	static void initiate() {
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
	
}
