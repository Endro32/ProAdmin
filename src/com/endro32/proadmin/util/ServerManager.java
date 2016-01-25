package com.endro32.proadmin.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.endro32.proadmin.config.BungeeConfig;
import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.config.ServerProperties;

public class ServerManager {
	
	public static void update(String group, String server) {
		if(Config.getServerNamesForGroup(group).contains(server)) {
			String app;
			if(Config.getMode(group).equals(GroupMode.INDIVIDUAL)) {
				app = Config.getAppForServer(group, server);
			} else if(Config.getMode(group).equals(GroupMode.CLONED)){
				app = Config.getAppForGroup(group);
			} else return;
			if(app.equals("default")) {
				app = Config.getDefaultApp();
			}
			app += ".jar";
			File a = new File(FileManager.appdir+"/apps/"+app);
			File b = new File(FileManager.appdir+"/"+"groups/"+group+"/"+server+"/server.jar");
			try {
				Files.copy(a.toPath(), b.toPath(),
						REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else return;
	}
	
	public static void updateBungeecord() {
		if(!Config.getBungeecordEnabled()) return;
		AppManager.updateBungeecord();
		int version = 0;
		try {
			version = Integer.parseInt(AppManager.latestyaml.getValue
					("installed.bungeecord").toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			FileManager.updateFileSystem();
			return;
		}
		File a = new File(FileManager.appdir+"/apps/bungeecord-"+version+".jar");
		File b = new File(FileManager.appdir+"/bungeecord/bungee.jar");
		try {
			Files.copy(a.toPath(), b.toPath(),
					REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateAll() {
		String defaultapp = Config.getDefaultApp();
		for(String group : Config.getServerGroupNames()) {
			GroupMode mode = Config.getMode(group);
			String app;
			if(mode.equals(GroupMode.CLONED)) {
				app = Config.getAppForGroup(group);
			} else if(mode.equals(GroupMode.INDIVIDUAL)) {
				app = "";
			} else continue;
			if(app.equals("default")) app = defaultapp;
			app += ".jar";
			for(String name : Config.getServerNamesForGroup(group)) {
				if(mode.equals(GroupMode.INDIVIDUAL)) {
					app = Config.getAppForServer(group, name);
					if(app.equals("default")) app = defaultapp;
					app += ".jar";
				}
				File a = new File(FileManager.appdir+"/apps/"+app);
				File b = new File(FileManager.appdir+"/"+"groups/"+group+"/"+name+"/server.jar");
				try {
					Files.copy(a.toPath(), b.toPath(),
							REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(Config.getServerIconsEnabled()) updateIcons();
		PluginManager.loadPlugins();
		ServerManager.updatePlugins();
		MapManager.loadMaps();
		ServerManager.updateMaps();
		updateBungeecord();
		updatePorts();
	}
	
	public static boolean updatePorts() {
		ServerProperties sp;
		int port = Config.getGlobalStartPort();
		if(Config.getBungeecordEnabled()) {
			BungeeConfig.updateHost();
		}
		for(String group : Config.getServerGroupNames()) {
			for(String name : Config.getServerNamesForGroup(group)) {
				sp = new ServerProperties("groups/"+group+"/"+name);
				sp.load();
				sp.setProperty("server-port", Integer.toString(port++));
				sp.save();
			}
		}
		return true;
	}
	
	public static boolean updateIPs() {
		ServerProperties sp;
		String ip = Config.getGlobalIP();
		if(Config.getBungeecordEnabled()) {
			BungeeConfig.updateHost();
			if(!Config.getProxyBypassEnabled()) {
				ip = "127.0.0.1";
			}
		}
		for(String group : Config.getServerGroupNames()) {
			for(String name : Config.getServerNamesForGroup(group)) {
				sp = new ServerProperties("groups/"+group+"/"+name);
				sp.load();
				sp.setProperty("server-ip", ip);
				sp.save();
			}
		}
		return true;
	}
	
	public static void updateBungeecordRegistry() {
		BungeeConfig.clearRegisteredServers();
		ServerProperties sp;
		int port;
		String motd;
		for(String group : Config.getServerGroupNames()) {
			for(String name : Config.getServerNamesForGroup(group)) {
				sp = new ServerProperties("groups/"+group+"/"+name);
				sp.load();
				port = Integer.parseInt(sp.getProperty("server-port"));
				motd = sp.getProperty("motd");
				BungeeConfig.registerServer(name, port, motd, false);
			}
		}
	}
	
	public static void updatePlugins() {
		for(String group : Config.getServerGroupNames()) {
			for(String server : Config.getServerNamesForGroup(group)) {
				updatePlugins(group, server);
			}
		}
	}
	
	public static void updatePlugins(String group, String server) {
		String[] dat;
		List<String> plugins = new ArrayList<String>();
		switch(Config.getMode(group)) {
		case INDIVIDUAL:
			plugins = Config.getPluginsForServer(group, server);
			break;
		case CLONED:
			plugins = Config.getPluginsForGroup(group);
			break;
		}
		for(String s : plugins) {
			dat = s.split("§", -1);
			String name = dat[0];
			String version = "";
			if(dat.length >= 2) {
				version = dat[1];
			} else {
				version = PluginManager.getLatestVersion(name);
			}
			PluginManager.installToServer(name, version, group, server);
		}
	}
	
	public static void updateMaps() {
		for(String group : Config.getServerGroupNames()) {
			for(String server : Config.getServerNamesForGroup(group)) {
				updateMaps(group, server);
			}
		}
	}
	
	public static void updateMaps(String group, String server) {
		List<String> maps;
		if(Config.getMode(group).equals(GroupMode.INDIVIDUAL)) {
			maps = Config.getMapsForServer(group, server);
		} else if(Config.getMode(group).equals(GroupMode.CLONED)){
			maps = Config.getMapsForGroup(group);
		} else return;
		for(String s : maps) {
			MapManager.installToServer(s, group, server);
		}
	}
	
	public static void updateIcons() {
		for(String group : Config.getServerGroupNames()) {
			for(String server : Config.getServerNamesForGroup(group)) {
				updateIcon(group, server);
			}
		}
	}
	
	public static void updateIcon(String group, String server) {
		File icon;
		String iconName;
		File target = new File(FileManager.appdir+"/groups/"+group+"/"+server+"/server-icon.png");
		if(Config.getMode(group).equals(GroupMode.INDIVIDUAL)) {
			iconName = Config.getIconForServer(group, server);
		} else if(Config.getMode(group).equals(GroupMode.CLONED)){
			iconName = Config.getIconForGroup(group);
		} else return;
		if(iconName.equals("default")) iconName = Config.getDefaultIcon();
		icon = new File(FileManager.appdir+"/icons/"+iconName+".png");
		if(!icon.exists()) return;
		try {
			Files.copy(icon.toPath(), target.toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
