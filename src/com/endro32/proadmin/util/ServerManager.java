package com.endro32.proadmin.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.endro32.proadmin.config.BungeeConfig;
import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.config.ServerProperties;

public class ServerManager {
	
	@SuppressWarnings("unchecked")
	public static List<String> getPluginsForServer(String type, String server) {
		String mode = getMode(type);
		if(mode.equals("individual")) {
			return (List<String>) Config.getMap("servers."+type+"."+server+".plugins");
		} else if(mode.equals("cloned")) {
			return (List<String>) Config.getMap("servers."+type+".plugins");
		} else return null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getMapsForServer(String type, String server) {
		String mode = getMode(type);
		if(mode.equals("individual")) {
			return (List<String>) Config.getMap("servers."+type+"."+server+".maps");
		} else if(mode.equals("cloned")) {
			return (List<String>) Config.getMap("servers."+type+".maps");
		} else return null;
	}
	
	public static void update(String group, String server) {
		if(getServerNamesForGroup(group).contains(server)) {
			String app;
			if(getMode(group).equals("individual")) {
				app = Config.getString("servers."+group+"."+server+".app");
			} else if(getMode(group).equals("cloned")){
				app = Config.getString("servers."+group+".app");
			} else return;
			if(app.equals("default")) {
				app = Config.getString("global.defaultapp");
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
		if(!Config.getBoolean("bungeecord.bungeecord")) return;
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
		String defaultapp = Config.getString("global.defaultapp");
		for(String group : getServerGroupNames()) {
			String mode = getMode(group);
			String app;
			if(mode.equals("cloned")) {
				app = Config.getString("servers."+group+".app");
			} else if(mode.equals("individual")) {
				app = "";
			} else continue;
			if(app.equals("default")) app = defaultapp;
			app += ".jar";
			for(String name : getServerNamesForGroup(group)) {
				if(mode.equals("individual")) {
					app = Config.getString("servers."+group+"."+name+".app");
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
		if(Config.getBoolean("global.server-icons")) updateIcons();
		PluginManager.loadPlugins();
		ServerManager.updatePlugins();
		MapManager.loadMaps();
		ServerManager.updateMaps();
		updateBungeecord();
		updatePorts();
	}
	
	public static boolean updatePorts() {
		ServerProperties sp;
		int port = Config.getInt("global.startport");
		if(Config.getBoolean("bungeecord.bungeecord")) {
			BungeeConfig.updateHost();
		}
		for(String group : getServerGroupNames()) {
			for(String name : getServerNamesForGroup(group)) {
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
		String ip = Config.getString("global.ip");
		if(Config.getBoolean("bungeecord.bungeecord")) {
			BungeeConfig.updateHost();
			if(!Config.getBoolean("bungeecord.bypassproxy")) {
				ip = "127.0.0.1";
			}
		}
		for(String group : getServerGroupNames()) {
			for(String name : getServerNamesForGroup(group)) {
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
		for(String group : getServerGroupNames()) {
			for(String name : getServerNamesForGroup(group)) {
				sp = new ServerProperties("groups/"+group+"/"+name);
				sp.load();
				port = Integer.parseInt(sp.getProperty("server-port"));
				motd = sp.getProperty("motd");
				BungeeConfig.registerServer(name, port, motd, false);
			}
		}
	}
	
	public static void updatePlugins() {
		for(String group : getServerGroupNames()) {
			for(String server : getServerNamesForGroup(group)) {
				updatePlugins(group, server);
			}
		}
	}
	
	public static void updatePlugins(String group, String server) {
		String[] dat;
		List<Object> objects;
		if(getMode(group).equals("individual")) {
			objects = Config.getList("servers."+group+"."+server+".plugins");
		} else if(getMode(group).equals("cloned")){
			objects = Config.getList("servers."+group+".plugins");
		} else return;
		for(Object o : objects) {
			dat = o.toString().split("§", -1);
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
		for(String group : getServerGroupNames()) {
			for(String server : getServerNamesForGroup(group)) {
				updateMaps(group, server);
			}
		}
	}
	
	public static void updateMaps(String group, String server) {
		List<Object> objects;
		if(getMode(group).equals("individual")) {
			objects = Config.getList("servers."+group+"."+server+".maps");
		} else if(getMode(group).equals("cloned")){
			objects = Config.getList("servers."+group+".maps");
		} else return;
		for(Object o : objects) {
			MapManager.installToServer(o.toString(), group, server);
		}
	}
	
	public static void updateIcons() {
		for(String group : getServerGroupNames()) {
			for(String server : getServerNamesForGroup(group)) {
				updateIcon(group, server);
			}
		}
	}
	
	public static void updateIcon(String group, String server) {
		File icon;
		String iconName;
		File target = new File(FileManager.appdir+"/groups/"+group+"/"+server+"/server-icon.png");
		if(getMode(group).equals("individual")) {
			iconName = Config.getString("servers."+group+"."+server+".icon");
		} else if(getMode(group).equals("cloned")){
			iconName = Config.getString("servers."+group+".icon");
		} else return;
		if(iconName.equals("default")) iconName = Config.getString("global.defaulticon");
		icon = new File(FileManager.appdir+"/icons/"+iconName+".png");
		if(!icon.exists()) return;
		try {
			Files.copy(icon.toPath(), target.toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
