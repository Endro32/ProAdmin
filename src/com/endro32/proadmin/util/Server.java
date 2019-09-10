package com.endro32.proadmin.util;

import com.endro32.proadmin.config.ServerProperties;

public class Server {

	private ServerProperties properties;
	private String group;
	private String name;
	
	public Server(String group, String name) {
		properties = new ServerProperties(group+"/"+name);
		this.group = group;
		this.name = name;
	}
	
	public boolean reloadProperties() {
		return properties.load();
	}
	
	public boolean saveProperties() {
		return properties.save();
	}
	
	public String getKey() {
		return group+"."+name;
	}
	
}
