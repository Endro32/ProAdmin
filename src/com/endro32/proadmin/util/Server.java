package com.endro32.proadmin.util;

import com.endro32.proadmin.config.ServerProperties;

public class Server {

	ServerProperties properties;
	String group;
	
	public Server(String group, String name) {
		properties = new ServerProperties(group+"/"+name);
		this.group = group;
	}
	
}
