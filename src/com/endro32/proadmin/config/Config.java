package com.endro32.proadmin.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.endro32.proadmin.Main;
import com.endro32.proadmin.util.FileManager;
import com.endro32.proadmin.util.GroupMode;
import com.endro32.proadmin.util.MapManager;
import com.endro32.proadmin.util.Plugin;

public class Config {

	/*
	 * The purpose of this class is quite simple: to manage the primary config
	 * for this application. It is responsible for maintaining the structure of
	 * the config, getting data, and adding data. Here are the primary functions
	 * of this class: *Create Servers *Delete Servers *Modify Servers *Provide
	 * getter and setter methods for all global options *Provide getter and
	 * setter methods for all bungeecord options *Allow for quick modifications
	 * to the names of options by making full use of the power of
	 * Object-Oriented programming
	 */

	/*
	 * This enumerator holds all the possible autoupdaters. It really just makes
	 * life easier when changing autoupdate values.
	 */
	public static enum AutoUpdate {
		SERVERS, VANILLA, SNAPSHOT, SPIGOT, BUNGEECORD
	}
	
	/*
	 * This is the instance of YAMLConfig used to access the file itself. The
	 * second parameter in its constructor means that the first parameter is not
	 * an absolute path, but instead a path inside the application's directory.
	 * In this case, it is simply 'config.yml'
	 */
	static YAMLConfig yaml = new YAMLConfig("config.yml", false);

	public static void addMapToGroup(String group, String name, String version) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return;
		if(MapManager.getMaps().contains(name)) {
			addToList("servers."+group+".maps", name+"§"+version);
		}
	}

	public static void addMapToServer(String group, String server, String name, String version) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return;
		if(MapManager.getMaps().contains(name)) {
			addToList("servers."+group+"."+server+".maps", name+"§"+version);
		}
	}

	public static void addPluginToGroup(String group, String name, String version) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return;
		for(Plugin p : Main.getPluginManager().getInstalledPlugins()) {
			if(p.getName().equals(name) && p.getVersion().equals(version)) {
				addToList("servers."+group+".plugins", name+"§"+version);
			}
		}
	}

	public static void addPluginToServer(String group, String server, String name, String version) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return;
		for(Plugin p : Main.getPluginManager().getInstalledPlugins()) {
			if(p.getName().equals(name) && p.getVersion().equals(version)) {
				addToList("servers."+group+"."+server+".plugins", name+"§"+version);
			}
		}
	}

	static boolean addToList(String key, String value) {
		try {
			return yaml.addToList(key, value);
		} catch(IOException e) {
			return false;
		}
	}

	/**
	 * Creates a server group in the config. Does nothing to the filesystem
	 * @param name Name of the group to create
	 * @param mode Individual or cloned
	 */
	public static void createGroup(String name, GroupMode mode) {
		if(getServerGroupNames().contains(name)) return;
		Map<String, Object> group = new HashMap<String, Object>();
		if(mode.equals(GroupMode.INDIVIDUAL)) {
			group.put("mode", "individual");
		} else if(mode.equals(GroupMode.CLONED)) {
			group.put("mode", "cloned");
			group.put("count", 1);
			group.put("app", "default");
			group.put("defaultmap", "world");
			group.put("icon", "default");
			group.put("plugins", new ArrayList<String>());
			group.put("maps", new ArrayList<String>());
		} else
			return; // Should never happen
		try {
			setObject("servers." + name, group);
		} catch(NullPointerException e) { // If the servers entry of the config doesn't exist
			Map<String, Object> servers = new HashMap<String, Object>();
			servers.put(name, group);
			setObject("servers", servers);
		}
	}

	/**
	 * Creates a new server in the config. Does nothing to the filesystem
	 * @param group Group to add the server to
	 * @param name Name of the server to create
	 */
	public static void createServer(String group, String name) {
		if(!getServerGroupNames().contains(name))
			createGroup(group, GroupMode.INDIVIDUAL);
		if(getServerNamesForGroup(group).contains(name))
			return;
		Map<String, Object> server = new HashMap<String, Object>();
		server.put("app", "default");
		server.put("defaultmap", "world");
		server.put("icon", "default");
		server.put("plugins", new ArrayList<String>());
		server.put("maps", new ArrayList<String>());
		try {
			setObject("servers."+group+"."+name, server);
		} catch(NullPointerException e) { // If the servers entry of the config doesn't exist
			e.printStackTrace();
		}
	}

	/**
	 * Gets the app being used by a cloned group
	 * @param group Name of the group
	 * @return Name of the app being used
	 */
	public static String getAppForGroup(String group) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return null;
		try {
			return getString("servers."+group+".app");
		} catch(NullPointerException e) {
			setAppForGroup(group, "default");
			return getDefaultApp();
		}
	}

	/**
	 * Gets the app being used by a server in an individual group
	 * @param group Name of the group the server is in
	 * @param server Name of the server
	 * @return Name of the app being used
	 */
	public static String getAppForServer(String group, String server) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return null;
		try {
			return getString("servers."+group+"."+server+".app");
		} catch(NullPointerException e) {
			setAppForServer(group, server, "default");
			return getDefaultApp();
		}
	}

	/**
	 * @param option Autoupdater to get the value of
	 * @return Whether the specified autoupdater is enabled
	 */
	public static boolean getAutoupdate(AutoUpdate option) {
		try {
			switch(option) {
			case SERVERS:
				return getBoolean("global.autoupdate.servers");
			case VANILLA:
				return getBoolean("global.autoupdate.vanilla");
			case SNAPSHOT:
				return getBoolean("global.autoupdate.snapshot");
			case SPIGOT:
				return getBoolean("global.autoupdate.spigot");
			case BUNGEECORD:
				return getBoolean("global.autoupdate.bungeecord");
			}
			return false;
		} catch(NullPointerException e) {
			if(option.equals(AutoUpdate.SERVERS) || option.equals(AutoUpdate.VANILLA)) {
				setAutoupdate(option, true);
				return true;
			} else {
				setAutoupdate(option, false);
				return false;
			}
		}
	}

	/**
	 * Gets the available apps, plugins, or maps and returns their names as a list.
	 * @param type Which type of object you're looking for: app, plugin, or map
	 * @return List of names
	 */
	public static List<String> getAvailable(AvailableType type) {
		List<String> retData = new ArrayList<String>();
		List<Object> list = new ArrayList<Object>();
		switch(type) {
		case APP:
			list = getList("installed.apps");
			break;
		case PLUGIN:
			list = getList("installed.plugins");
			break;
		case MAP:
			list = getList("installed.maps");
			break;
		}
		for(Object o : list) {
			retData.add(o.toString());
		}
		return retData;
	}

	/**
	 * Gets a boolean from the config
	 * @param key
	 * @return
	 */
	static boolean getBoolean(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return Boolean.parseBoolean(obj.toString());
	}

	/**
	 * @return Whether bungeecord is enabled
	 */
	public static boolean getBungeecordEnabled() {
		try {
			return getBoolean("bungeecord.bungeecord");
		} catch(NullPointerException e) {
			setBungeecordEnabled(false);
			return false;
		}
	}

	/**
	 * @return What port bungeecord is listening on
	 */
	public static int getBungeecordPort() {
		try {
			return getInt("bungeecord.port");
		} catch(NullPointerException e) {
			setGlobalStartPort(25565);
			return 25565;
		}
	}

	/**
	 * Returns 0 or less if latest is to be used
	 * 
	 * @return Version of bungeecord set in the config
	 */
	public static int getBungeecordVersion() {
		String ver;
		try {
			ver = getString("bungeecord.version");
		} catch(NullPointerException e) {
			setBungeecordVersion(-1);
			ver = "latest";
		}
		if(ver.equals("latest")) {
			return -1;
		} else {
			try {
				return Integer.parseInt(ver);
			} catch(NumberFormatException e) {
				return -1;
			}
		}
	}

	/**
	 * @return Default application excluding extension (.jar)
	 */
	public static String getDefaultApp() {
		try {
			return getString("global.defaultapp");
		} catch(NullPointerException e) { // Will occur if global name doesn't
											// exist
			setGlobalName("vanilla-latest");
			return "vanilla-latest";
		}
	}

	/**
	 * @return Name of the default icon excluding the extension (.png)
	 */
	public static String getDefaultIcon() {
		try {
			return getString("global.defaulticon");
		} catch(NullPointerException e) {
			setDefaultIcon("server-icon");
			return "server-icon";
		}
	}

	/*
	 * Above this point are the global settings. Below are the bungeecord
	 * settings
	 */

	static double getDouble(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return Double.parseDouble(obj.toString());
	}

	public static boolean getEULAStatus() {
		try {
			return getBoolean("eula");
		} catch(NullPointerException e) {
			setEULAStatus(false);
			return false;
		}
	}

	static float getFloat(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return Float.parseFloat(obj.toString());
	}

	/**
	 * Will return null if the option has been deleted
	 * 
	 * @return Global IP from config
	 */
	public static String getGlobalIP() {
		try {
			return getString("global.ip");
		} catch(NullPointerException e) {
			setString("global.ip", "0.0.0.0");
			return "0.0.0.0";
		}
	}

	/**
	 * Will return null if the option has been deleted
	 * 
	 * @return Global message of the day in the config
	 */
	public static String getGlobalMOTD() {
		try {
			return getString("global.motd");
		} catch(NullPointerException e) {
			setGlobalMOTD("A Minecraft Server");
			return "A Minecraft Server";
		}
	}

	/**
	 * @return Global name in the config
	 */
	public static String getGlobalName() {
		try {
			return getString("global.name");
		} catch(NullPointerException e) { // Will occur if global name doesn't
											// exist
			setGlobalName("Minecraft Server");
			return "Minecraft Server";
		}
	}

	/**
	 * @return Global startingn port from the config
	 */
	public static int getGlobalStartPort() {
		try {
			return getInt("global.startport");
		} catch(NullPointerException e) {
			setGlobalStartPort(25566);
			return 25566;
		}
	}

	public static String getIconForGroup(String group) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return null;
		try {
			return getString("servers."+group+".icon");
		} catch(NullPointerException e) {
			setIconForGroup(group, "server-icon");
			return "server-icon";
		}
	}

	/*
	 * Above this point are the bungeecord settings. Below are the server
	 * management settings
	 */

	public static String getIconForServer(String group, String server) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return null;
		try {
			return getString("servers."+group+"."+server+".icon");
		} catch(NullPointerException e) {
			setIconForServer(group, server, "server-icon");
			return "server-icon";
		}
	}

	static int getInt(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(obj.toString());
	}

	static String getIP(boolean bungeecord) {
		if(!bungeecord && getBoolean("bungeecord.bungeecord") && !getBoolean("bungeecord.proxybypass")) {
			return "127.0.0.1";
		} else
			return getString("global.ip");
	}

	@SuppressWarnings("unchecked")
	static List<Object> getList(String key) {
		List<Object> list = new ArrayList<Object>();
		try {
			list = (List<Object>) yaml.getValue(key);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	static Map<Object, Object> getMap(String key) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		try {
			map = (Map<Object, Object>) yaml.getValue(key);
		} catch(FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public static String getMapForGroup(String group) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return null;
		try {
			return getString("servers."+group+".defaultmap");
		} catch(NullPointerException e) {
			setMapForGroup(group, "world");
			return "world";
		}
	}

	public static String getMapForServer(String group, String server) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return null;
		try {
			return getString("servers."+group+"."+server+".defaultmap");
		} catch(NullPointerException e) {
			setMapForServer(group, server, "world");
			return "world";
		}
	}

	public static List<String> getMapsForGroup(String group) {
		if(!getServerGroupNames().contains(group)) return null;
		if(getMode(group).equals(GroupMode.INDIVIDUAL)) return null;
		List<String> maps = new ArrayList<String>();
		try {
			for(Object object : getList("servers." + group + ".maps")) {
				maps.add(object.toString());
			}
			return maps;
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	public static List<String> getMapsForServer(String group, String server) {
		if(!getServerGroupNames().contains(group)) return null;
		if(!getServerNamesForGroup(group).contains(server)) return null;
		if(getMode(group).equals(GroupMode.CLONED)) return null;
		List<String> maps = new ArrayList<String>();
		try {
			for(Object object : getList("servers." + group + "." + server + ".maps")) {
				maps.add(object.toString());
			}
			return maps;
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Will return null if the group doesn't exist
	 * @param group
	 * Group to get the mode of
	 * @return
	 * The mode of the specified group
	 */
	public static GroupMode getMode(String group) {
		String mode;
		try {
			mode = getString("servers." + group + ".mode");
		} catch(NullPointerException e) {
			return null;
		}
		if(mode.equals("individual")) {
			return GroupMode.INDIVIDUAL;
		} else if(mode.equals("cloned")) {
			return GroupMode.CLONED;
		} else
			return null;
	}
	
	public static List<String> getPluginsForGroup(String group) {
		if(!getServerGroupNames().contains(group)) return null;
		if(getMode(group).equals(GroupMode.INDIVIDUAL)) return null;
		List<String> plugins = new ArrayList<String>();
		try {
			for(Object object : getList("servers." + group + ".plugins")) {
				plugins.add(object.toString());
			}
			return plugins;
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	public static List<String> getPluginsForServer(String group, String server) {
		if(!getServerGroupNames().contains(group)) return null;
		if(!getServerNamesForGroup(group).contains(server)) return null;
		if(getMode(group).equals(GroupMode.CLONED)) return null;
		List<String> plugins = new ArrayList<String>();
		try {
			for(Object object : getList("servers." + group + "." + server + ".plugins")) {
				plugins.add(object.toString());
			}
			return plugins;
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * @return Whether proxy bypassing is enabled
	 */
	public static boolean getProxyBypassEnabled() {
		try {
			return getBoolean("bungeecord.proxybypass");
		} catch(NullPointerException e) {
			setProxyBypassEnabled(false);
			return false;
		}
	}
	
	/**
	 * @return List of the names of the different server groups
	 */
	public static List<String> getServerGroupNames() {
		Map<Object, Object> map = (Map<Object, Object>) Config.getMap("servers");
		List<String> names = new ArrayList<String>();
		for(Object o : map.keySet()) {
			names.add(o.toString());
		}
		return names;
	}
	
	/**
	 * @return Whether server icons are enabled
	 */
	public static boolean getServerIconsEnabled() {
		try {
			return getBoolean("global.server-icons");
		} catch(NullPointerException e) {
			setServerIconsEnabled(false);
			return false;
		}
	}
	
	public static List<String> getServerNamesForGroup(String group) {
		GroupMode mode = getMode(group);
		if(mode.equals(GroupMode.INDIVIDUAL)) {
			Map<Object, Object> map = (Map<Object, Object>) Config.getMap("servers." + group);
			List<String> names = new ArrayList<String>();
			map.remove("mode");
			for(Object o : map.keySet()) {
				names.add(o.toString());
			}
			return names;
		} else if(mode.equals(GroupMode.CLONED)) {
			int count = Config.getInt("servers." + group + ".count");
			List<String> names = new ArrayList<String>();
			for(int i = 1; i <= count; i++) {
				String base = Integer.toString(i);
				String id = "000".substring(base.length()) + base;
				names.add(group + "-" + id);
			}
			return names;
		} else
			return null;
	}
	
	static String getString(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
	
	static boolean removeFromList(String key, String value) {
		try {
			return yaml.removeFromList(key, value);
		} catch(IOException e) {
			return false;
		}
	}
	
	/**
	 * Removes a group from the servers list in the config
	 * 
	 * @param name
	 *            Group to remove
	 */
	public static void removeGroup(String name) {
		if(!getServerGroupNames().contains(name)) return;
		try {
			Map<Object, Object> groups = getMap("servers");
			groups.remove(name);
			setObject("servers", groups);
		} catch(NullPointerException e) {
			return;
		}
	}	
	
	public static void removeMapFromGroup(String group, String name) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return;
		if(getMapsForGroup(group).contains(name)) {
			addToList("servers."+group+".maps", name);
		}
	}

	public static void removeMapFromServer(String group, String server, String name) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return;
		if(getMapsForServer(group, server).contains(name)) {
			addToList("servers."+group+".maps", name);
		}
	}

	public static void removePluginFromGroup(String group, String name) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return;
		for(String plugin : getPluginsForGroup(group)) {
			String s = "";
			if(plugin.indexOf("§") < 0) {
				name = plugin;
			} else {
				name = plugin.substring(0, plugin.indexOf("§"));
			}
			if(s.equals(name)) {
				removeFromList("servers."+group+".plugins", plugin);
				break;
			}
		}
	}
	
	public static void removePluginFromServer(String group, String server, String name) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return;
		for(String plugin : getPluginsForServer(group, server)) {
			String s = "";
			if(plugin.indexOf("§") < 0) {
				name = plugin;
			} else {
				name = plugin.substring(0, plugin.indexOf("§"));
			}
			if(s.equals(name)) {
				removeFromList("servers."+group+"."+server+".plugins", plugin);
				break;
			}
		}
	}
	
	public static void removeServer(String group, String name) {
		if(getMode(group) == GroupMode.CLONED ||
				!getServerNamesForGroup(group).contains(name))
			return;
		try {
			Map<Object, Object> servers = getMap("servers."+group);
			servers.remove(name);
			setObject("servers."+group, servers);
		} catch(NullPointerException e) {
			return;
		}
	}
	
	static void resetAutoupdate() {
		Map<String, Object> def = new HashMap<String, Object>();
		def.put("servers", true);
		def.put("vanilla", true);
		def.put("snapshot", false);
		def.put("spigot", false);
		def.put("bungeecord", false);
		try {
			setObject("global.autoupdate", def);
		} catch(NullPointerException e) {
			resetGlobal();
		}
	}
	
	static void resetBungeecord() {
		Map<String, Object> def = new HashMap<String, Object>();
		def.put("bungeecord", false);
		def.put("proxybypass", false);
		def.put("version", "latest");
		def.put("port", 25565);
		setObject("bungeecord", def);
	}
	
	/**
	 * Re-extracts the the default configuration from the resources package
	 * 
	 * @param override
	 *            Whether to override the existing config file.
	 */
	public static void resetConfig(boolean override) {
		FileManager.extractResource("config.yml", "config.yml", override);
	}
	
	static void resetGlobal() {
		Map<String, Object> def = new HashMap<String, Object>();
		def.put("name", "Minecraft Server");
		def.put("motd", "A Minecraft Server");
		def.put("ip", "0.0.0.0");
		def.put("startport", "25566");
		def.put("defaultapp", "vanilla-latest");
		def.put("server-icons", "false");
		def.put("defaulticon", "server-icon");
		setObject("global", def);
		resetAutoupdate();
	}
	
	public static void setAppForGroup(String group, String name) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return;
		try {
			setObject("servers."+group+".app", name);
		} catch(NullPointerException e) {
			return; // This should most definitely never happen
		}
	}
	
	public static void setAppForServer(String group, String server, String name) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return;
		try {
			setObject("servers."+group+"."+server+".app", name);
		} catch(NullPointerException e) {
			return; // This should most definitely never happen
		}
	}
	
	/**
	 * Enables or disables an autoupdater. All available updates are stored in
	 * the `AutoUpdate` enumerator of this class.
	 * 
	 * @param option
	 *            Autoupdater to set
	 * @param value
	 *            What to set the autoupdater to
	 */
	public static void setAutoupdate(AutoUpdate option, boolean value) {
		try {
			switch(option) {
			case SERVERS:
				setObject("global.autoupdate.servers", value);
				break;
			case VANILLA:
				setObject("global.autoupdate.vanilla", value);
				break;
			case SNAPSHOT:
				setObject("global.autoupdate.snapshot", value);
				break;
			case SPIGOT:
				setObject("global.autoupdate.spigot", value);
				break;
			case BUNGEECORD:
				setObject("global.autoupdate.bungeecord", value);
				break;
			}
		} catch(NullPointerException e) {
			resetAutoupdate();
			switch(option) {
			case SERVERS:
				setObject("global.autoupdate.servers", value);
				break;
			case VANILLA:
				setObject("global.autoupdate.vanilla", value);
				break;
			case SNAPSHOT:
				setObject("global.autoupdate.snapshot", value);
				break;
			case SPIGOT:
				setObject("global.autoupdate.spigot", value);
				break;
			case BUNGEECORD:
				setObject("global.autoupdate.bungeecord", value);
				break;
			}
		}
	}
	
	
	/*
	 * Following are the EULA status methods. The purpose of holding this value
	 * is to prevent there from being an issue with the fact that the EULA is
	 * essentially bypassed by this application. However, the EULA still has to
	 * be agreed to thanks to the fact that this application itself promts the
	 * user to read and agree to the EULA, then stores the status in the config,
	 * and uses that status to generate the EULA files for the server. In this
	 * way, the user does indeed agree to the EULA, and this application doesn't
	 * simply remove that requirement.
	 */

	public static void setAvailable(AvailableType type, List<String> data) {
		switch(type) {
		case APP:
			setList("installed.apps", data);
			break;
		case PLUGIN:
			setList("installed.plugins", data);
			break;
		case MAP:
			setList("installed.maps", data);
			break;
		}
	}

	/**
	 * @param enabled
	 *            Whether to use bungeecord or not
	 */
	public static void setBungeecordEnabled(boolean enabled) {
		try {
			setObject("bungeecord.bungeecord", enabled);
		} catch(NullPointerException e) {
			resetBungeecord();
			setObject("bungeecord.bungeecord", enabled);
		}
	}
	
	/**
	 * Does nothing if the port to be set is not within range (1024-65536)
	 * 
	 * @param port
	 *            Port bungeecord should listen on
	 */
	public static void setBungeecordPort(int port) {

		if(!(1024 < port && port <= 65536)) return;
		try {
			setObject("bungeecord.port", port);
		} catch(NullPointerException e) {
			resetGlobal();
			setObject("bungeecord.port", port);
		}
	}
	
	/**
	 * Sets the version of bungeecord to use. If it is set to `latest`, the app
	 * manager will automatically keep bungeecord up to date. Latest is the same
	 * as any number 0 or less, since the value is an integer
	 * 
	 * @param version
	 *            Version of bungeecord to use
	 */
	public static void setBungeecordVersion(int version) {
		String ver;
		if(version <= 0) {
			ver = "latest";
		} else {
			ver = Integer.toString(version);
		}
		try {
			setObject("bungeecord.version", ver);
		} catch(NullPointerException e) {
			resetBungeecord();
			setObject("bungeecord.version", ver);
		}
	}

	/*
	 * Following are some simple methods used to modify the data in the config
	 * As they are only used by this class, I will not provide explanations
	 */

	/**
	 * Sets the default application to use for the individual servers. This will
	 * be used by any server whose app preference is set to 'default'.
	 * 
	 * @param appname
	 *            Application name excluding the extension, because all apps are
	 *            jars
	 */
	public static void setDefaultApp(String appname) {
		try {
			setString("global.defaultapp", appname);
		} catch(NullPointerException e) { // Will occur if global doesn't exist
			resetGlobal();
			setString("global.defaultapp", appname);
		}
	}

	/**
	 * Sets the default server icon.
	 * 
	 * @param icon
	 *            Name of the file excluding the extension, because all icons
	 *            are png's
	 */
	public static void setDefaultIcon(String icon) {
		try {
			setObject("global.defaulticon", icon);
		} catch(NullPointerException e) {
			resetGlobal();
			setObject("global.defaulticon", icon);
		}
	}

	public static void setEULAStatus(boolean status) {
		setObject("eula", status);
	}

	/**
	 * Sets the global ip in the config. If the option has been deleted, it will
	 * be re-created. Before changing the value, this method will first delete
	 * any non-numeric characters other than a period, then will check to make
	 * sure that it is a valid IPv4 address.
	 * 
	 * @param ip
	 *            IP to set in the config
	 */
	public static void setGlobalIP(String ip) {
		String numericip = ip.replaceAll("[^\\d.]", "");
		String[] dat = numericip.split(Pattern.quote("."));
		if(dat.length != 4) return;
		for(String data : dat) {
			int x = Integer.parseInt(data);
			if(!(0 <= x && x <= 255)) return;
		}
		// At this point, the numericip is assumed to be valid
		try {
			setString("global.ip", numericip);
		} catch(NullPointerException e) {
			resetGlobal();
			setString("global.ip", numericip);
		}
	}

	/**
	 * Sets the global message of the day in the config. If the option has been
	 * deleted somehow, it will be re-created
	 * 
	 * @param motd
	 *            Message of the day to put in the config
	 */
	public static void setGlobalMOTD(String motd) {
		try {
			setString("global.motd", motd);
		} catch(NullPointerException e) { // Will occur if global doesn't exist
			resetGlobal();
			setString("global.motd", motd);
		}
	}

	/**
	 * Sets the server's global name in the config If the option has been
	 * deleted somehow, it will be re-created
	 * 
	 * @param name
	 *            Name to set in the config
	 */
	public static void setGlobalName(String name) {
		try {
			setString("global.name", name);
		} catch(NullPointerException e) { // Will occur if global doesn't exist
			resetGlobal();
			setString("global.name", name);
		}
	}

	/**
	 * Sets the global starting port in the config. This setting specifies the
	 * first port that will be used by the individual servers. Each server's
	 * port will be automatically assigned, starting with this starting point.
	 * This value must be between 1024 and 65536
	 * 
	 * @param port
	 *            Port to set in the config
	 */
	public static void setGlobalStartPort(int port) {
		if(!(1024 < port && port <= 65536)) return;
		try {
			setObject("global.startport", port);
		} catch(NullPointerException e) {
			resetGlobal();
			setObject("global.startport", port);
		}
	}

	public static void setIconForGroup(String group, String name) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return;
		try {
			setObject("servers."+group+".icon", name);
		} catch(NullPointerException e) {
			return; // This should most definitely never happen
		}
	}

	public static void setIconForServer(String group, String server, String name) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return;
		try {
			setObject("servers."+group+"."+server+".icon", name);
		} catch(NullPointerException e) {
			return; // This should most definitely never happen
		}
	}

	static void setList(String key, List<String> data) {
		try {
			yaml.setValue(key, data);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void setMapForGroup(String group, String name) {
		if(!(getServerGroupNames().contains(group) && getMode(group).equals(GroupMode.CLONED)))
			return;
		try {
			setObject("servers."+group+".defaultmap", name);
		} catch(NullPointerException e) {
			return; // This should most definitely never happen
		}
	}

	public static void setMapForServer(String group, String server, String name) {
		if(!(getServerGroupNames().contains(group) && getServerNamesForGroup(group)
				.contains(server) && getMode(group).equals(GroupMode.INDIVIDUAL))) return;
		try {
			setObject("servers."+group+"."+server+".defaultmap", name);
		} catch(NullPointerException e) {
			return; // This should most definitely never happen
		}
	}

	static void setObject(String key, Object data) {
		try {
			yaml.setValue(key, data);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This preference allows clients to connect directly the the individual
	 * servers in the network without going through the bungeecord proxy. It
	 * does nothing if bungeecord is enabled.
	 * 
	 * @param enabled
	 *            Whether clients should be able to bypass the proxy
	 */
	public static void setProxyBypassEnabled(boolean enabled) {
		try {
			setObject("bungeecord.proxybypass", enabled);
		} catch(NullPointerException e) {
			resetBungeecord();
			setObject("bungeecord.proxybypass", enabled);
		}
	}

	/**
	 * @param enabled
	 *            Whether to enable or disable server icons
	 */
	public static void setServerIconsEnabled(Boolean enabled) {
		try {
			setObject("global.server-icons", enabled);
		} catch(NullPointerException e) {
			resetGlobal();
			setObject("global.server-icons", enabled);
		}
	}

	static void setString(String key, String value) {
		try {
			yaml.setValue(key, value);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
