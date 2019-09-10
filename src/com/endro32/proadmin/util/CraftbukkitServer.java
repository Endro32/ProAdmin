package com.endro32.proadmin.util;

import com.endro32.proadmin.config.BukkitConfig;

public class CraftbukkitServer extends Server {
	
	BukkitConfig bukkitConfig;
	
	public CraftbukkitServer(String group, String name) {
		super(group, name);
		bukkitConfig = new BukkitConfig();
	}

}
