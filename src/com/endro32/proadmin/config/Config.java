package com.endro32.proadmin.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.endro32.proadmin.util.FileManager;
import com.endro32.proadmin.util.GroupMode;

public class Config {
	
	/*
	 * The purpose of this class is quite simple: to manage the primary
	 * config for this application. It is responsible for maintaining the
	 * structure of the config, getting data, and adding data.
	 * Here are the primary functions of this class:
	 * *Create Servers
	 * *Delete Servers
	 * *Modify Servers
	 * *Provide getter and setter methods for all global options
	 * *Provide getter and setter methods for all bungeecord options
	 * *Allow for quick modifications to the names of options by making
	 *  full use of the power of Object-Oriented programming
	 */
	
	/*
	 * This is the instance of YAMLConfig used to access the file itself.
	 * The second parameter in its constructor means that the first parameter
	 * is not an absolute path, but instead a path inside the application's
	 * directory. In this case, it is simply 'config.yml'
	 */
	static YAMLConfig yaml = new YAMLConfig("config.yml", false);
	
	/*
	 * This enumerator holds all the possible autoupdaters. It really
	 * just makes life easier when changing autoupdate values.
	 */
	public static enum AutoUpdate {
	SERVERS,
	VANILLA,
	SNAPSHOT,
	SPIGOT,
	BUNGEECORD}
	
	/**
	 * Re-extracts the the default configuration from the resources package
	 * @param override
	 * Whether to override the existing config file.
	 */
	public static void resetConfig(boolean override) {
		FileManager.extractResource("config.yml", "config.yml", override);
	}
	
	/**
	 * Sets the server's global name in the config
	 * If the option has been deleted somehow, it will be re-created
	 * @param name
	 * Name to set in the config
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
	 * @return Global name in the config
	 */
	public static String getGlobalName() {
		try {
			return getString("global.name");
		} catch(NullPointerException e) { // Will occur if global name doesn't exist
			setGlobalName("Minecraft Server");
			return "Minecraft Server";
		}
	}
	
	/**
	 * Sets the global message of the day in the config.
	 * If the option has been deleted somehow, it will be re-created
	 * @param motd
	 * Message of the day to put in the config
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
	 * Will return null if the option has been deleted
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
	 * Sets the global ip in the config.
	 * If the option has been deleted, it will be re-created.
	 * Before changing the value, this method will first delete any
	 * non-numeric characters other than a period, then will check to
	 * make sure that it is a valid IPv4 address.
	 * @param ip
	 * IP to set in the config
	 */
	public static void setGlobalIP(String ip) {
		String numericip = ip.replaceAll("[^\\d.]", "");
		String[] dat = numericip.split(Pattern.quote("."));
		if(dat.length != 4) return;
		for(String data : dat) {
			int x = Integer.parseInt(data);
			if(!(0<=x && x <= 255)) return;
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
	 * Will return null if the option has been deleted
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
	 * Sets the global starting port in the config.
	 * This setting specifies the first port that will be used by the
	 * individual servers. Each server's port will be automatically
	 * assigned, starting with this starting point. This value must be
	 * between 1024 and 65536
	 * @param port
	 * Port to set in the config
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
	
	/**
	 * Sets the default application to use for the individual servers.
	 * This will be used by any server whose app preference is set
	 * to 'default'.
	 * @param appname
	 * Application name excluding the extension, because all apps are jars
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
	 * @return Default application excluding extension (.jar)
	 */
	public static String getDefaultApp() {
		try {
			return getString("global.defaultapp");
		} catch(NullPointerException e) { // Will occur if global name doesn't exist
			setGlobalName("vanilla-latest");
			return "vanilla-latest";
		}
	}
	
	/**
	 * Enables or disables an autoupdater. All available updates are stored
	 * in the `AutoUpdate` enumerator of this class.
	 * @param option
	 * Autoupdater to set
	 * @param value
	 * What to set the autoupdater to
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
	
	/**
	 * @param option
	 * Autoupdater to get the value of
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
	 * @param enabled
	 * Whether to enable or disable server icons
	 */
	public static void setServerIconsEnabled(Boolean enabled)  {
		try {
			setObject("global.server-icons", enabled);
		} catch(NullPointerException e) {
			resetGlobal();
			setObject("global.server-icons", enabled);
		}
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

	/**
	 * Sets the default server icon.
	 * @param icon
	 * Name of the file excluding the extension, because all icons are png's
	 */
	public static void setDefaultIcon(String icon) {
		try {
			setObject("global.defaulticon", icon);
		} catch(NullPointerException e) {
			resetGlobal();
			setObject("global.defaulticon", icon);
		}
	}

	/**
	 * @return Name of the default icon excluding the extension '.png'
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
	 * Above this point are the global settings
	 * Below are the bungeecord settings
	 */
	
	public static void setBungeecordEnabled(boolean enabled) {
		try {
			setObject("bungeecord.bungeecord", enabled);
		} catch(NullPointerException e) {
			resetBungeecord();
			setObject("bungeecord.bungeecord", enabled);
		}
	}
	
	public static boolean getBungeecordEnabled() {
		try {
			return getBoolean("bungeecord.bungeecord");
		} catch(NullPointerException e) {
			setBungeecordEnabled(false);
			return false;
		}
	}
	
	public static void setProxyBypassEnabled(boolean enabled) {
		try {
			setObject("bungeecord.proxybypass", enabled);
		} catch(NullPointerException e) {
			resetBungeecord();
			setObject("bungeecord.proxybypass", enabled);
		}
	}
	
	public static boolean getProxyBypassEnabled() {
		try {
			return getBoolean("bungeecord.proxybypass");
		} catch(NullPointerException e) {
			setProxyBypassEnabled(false);
			return false;
		}
	}
	
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
			} catch (NumberFormatException e) {
				return -1;
			}
		}
	}
	
	public static void setBungeecordPort(int port) {
		if(!(1024 < port && port <= 65536)) return;
		try {
			setObject("bungeecord.port", port);
		} catch(NullPointerException e) {
			resetGlobal();
			setObject("bungeecord.port", port);
		}
	}
	
	public static int getBungeecordPort() {
		try {
			return getInt("bungeecord.port");
		} catch(NullPointerException e) {
			setGlobalStartPort(25565);
			return 25565;
		}
	}
	
	/*
	 * Above this point are the bungeecord settings
	 * Below are the server management settings
	 */
	
	/**
	 * Creates a server group in the config
	 * This action will be reflected on the filesystem almost immediately,
	 * as this method will then update the filesystem
	 */
	public static void createGroup(String name, GroupMode mode) {
		if(getServerGroupNames().contains(name)) return;
	}
	
	public static void removeGroup(String name) {
		if(!getServerGroupNames().contains(name)) return;
	}
	
	public static List<String> getServerGroupNames() {
		Map<Object, Object> map = (Map<Object, Object>) Config.getMap("servers");
		List<String> names = new ArrayList<String>();
		for(Object o : map.keySet()) {
			names.add(o.toString());
		}
		return names;
	}
	
	public static GroupMode getMode(String type) {
		String mode = Config.getString("servers."+type+".mode");
		if(mode.equals("individual")) {
			return GroupMode.INDIVIDUAL;
		} else if(mode.equals("cloned")) {
			return GroupMode.CLONED;
		} else return null;
	}
	
	public static void createServer(String group, String name) {
		
	}
	
	public static void removeServer(String group, String name) {
		
	}
	
	public static List<String> getServerNamesForGroup(String group) {
		GroupMode  mode = getMode(group);
		if(mode.equals(GroupMode.INDIVIDUAL)) {
			Map<Object, Object> map = (Map<Object, Object>) Config.getMap("servers."+group);
			List<String> names = new ArrayList<String>();
			map.remove("mode");
			for(Object o : map.keySet()) {
				names.add(o.toString());
			}
			return names;
		} else if(mode.equals(GroupMode.CLONED)){
			int count = Config.getInt("servers."+group+".count");
			List<String> names = new ArrayList<String>();
			for(int i = 1; i <= count; i++) {
				String base = Integer.toString(i);
				String id = "000".substring(base.length()) + base;
				names.add(group+"-"+id);
			}
			return names;
		} else return null;
	}
	
	/*
	 * Following are some simple methods used to modify the data in the config
	 * As they are only used by this class, I will not provide explanations
	 */
	
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
	
	static String getString(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
	
	static int getInt(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(obj.toString());
	}
	
	static float getFloat(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return Float.parseFloat(obj.toString());
	}
	
	static double getDouble(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return Double.parseDouble(obj.toString());
	}
	
	static boolean getBoolean(String key) {
		Object obj = new Object();
		try {
			obj = yaml.getValue(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return Boolean.parseBoolean(obj.toString());
	}
	
	@SuppressWarnings("unchecked")
	static Map<Object, Object> getMap(String key) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		try {
			map = (Map<Object, Object>) yaml.getValue(key);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	static List<Object> getList(String key) {
		List<Object> list = new ArrayList<Object>();
		try {
			list = (List<Object>) yaml.getValue(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	static void setObject(String key, Object data) {
		try {
			yaml.setValue(key, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void setString(String key, String value) {
		try {
			yaml.setValue(key, value);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void setList(String key, List<String> data) {
		try {
			yaml.setValue(key, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static boolean addToList(String key, String value) {
		try {
			return yaml.addToList(key, value);
		} catch (IOException e) {
			return false;
		}
	}
	
	static boolean removeFromList(String key, String value) {
		try {
			return yaml.removeFromList(key, value);
		} catch (IOException e) {
			return false;
		}
	}
	
	static String getIP(boolean bungeecord) {
		if(!bungeecord && getBoolean("bungeecord.bungeecord")
				&& !getBoolean("bungeecord.proxybypass")) {
			return "127.0.0.1";
		} else return getString("global.ip");
	}
	
}
