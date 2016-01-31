package com.endro32.proadmin.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.endro32.proadmin.util.FileManager;

public class ServerProperties {

	Properties config;
	File file;
	
	public ServerProperties(String dir) {
		config = new Properties();
		file = new File(FileManager.appdir+"/"+dir+"/server.properties");
	}
	
	public boolean load() {
		try {
			config.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean save() {
		try {
			config.store(new FileOutputStream(file), 
					"Minecraft Server Properties");
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public String getProperty(String key) {
		return config.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		config.setProperty(key, value);
	}
	
}
