package com.endro32.proadmin.util;

import java.io.File;

public class Plugin {
	
	final String name;
	final String version;
	final File file;
	
	public Plugin(String name, String version, String abspath) {
		this.name = name;
		this.version = version;
		file = new File(abspath);
	}
	
	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public int getVersionAsInt() {
		return AppManager.versionToInt(version);
	}
	
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	
	public String getFileName() {
		return file.getName();
	}
	
}
