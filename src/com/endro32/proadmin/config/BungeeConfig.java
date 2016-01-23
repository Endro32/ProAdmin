package com.endro32.proadmin.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BungeeConfig {
	
	public static YAMLConfig yaml;
	
	public static void initialize() {
		yaml = new YAMLConfig("bungeecord/config.yml", false);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListeners() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = (List<Map<String, Object>>) yaml.getValue("listeners");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list.get(0);
	}
	
	public static void setListeners(Map<String, Object> map) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(map);
		try {
			yaml.setValue("listeners", list);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateHost() {
		String ip = Config.getIP(true);
		int port = Config.getInt("bungeecord.port");
		Map<String, Object> map = getListeners();
		map.put("host", ip+":"+port);
		map.put("query_port", port);
		setListeners(map);
	}
	
	public static void clearRegisteredServers() {
		try {
			yaml.setValue("servers", new HashMap<Object, Object>());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static boolean unregisterServer(String name) {
		try {
			Map<String, Object> servers = (Map<String, Object>) yaml.getValue("servers");
			servers.remove(name);
			yaml.setValue("servers", servers);
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static Set<String> getRegisteredServers() {
		Map<String, Object> servers = null;
		try {
			servers = (Map<String, Object>)
					yaml.getValue("servers");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			servers = new HashMap<String, Object>();
		}
		return servers.keySet();
	}
	
	public static boolean registerServer(String name, int port, String motd, boolean restricted) {
		String s = name;
		int i = 1;
		while(getRegisteredServers().contains(s)) {
			s = name+"("+ i++ +")";
		}
		Map<String, Object> server = new HashMap<String, Object>();
		server.put("motd", motd);
		server.put("restricted", restricted);
		server.put("address", Config.getIP(false)+":"+port);
		try {
			yaml.setValue("servers."+s, server);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean registerServer(String name, int port, boolean restricted) {
		return registerServer(name, port, null, restricted);
	}
	
	public static boolean registerServer(String name, int port) {
		return registerServer(name, port, null, false);
	}
	
}
