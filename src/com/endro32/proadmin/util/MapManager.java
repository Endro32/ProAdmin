package com.endro32.proadmin.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class MapManager {

	static List<String> maps = new ArrayList<String>();
	
	public static void loadMaps() {
		maps.clear();
		File folder = new File(FileManager.appdir+"/maps");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			File level = new File(file.getAbsolutePath()+"/level.dat");
			String name = file.getName();
			if(file.isDirectory() && level.exists() && !maps.contains(name)) 
				maps.add(name);
		}
	}
	
	public static List<String> getMaps() {
		return maps;
	}
	
	public static boolean installToServer(String map, String group, String name) {
		if(!maps.contains(map)) return false;
		File a = new File(FileManager.appdir+"/maps/"+map);
		if(!a.exists()) return false;
		File b = new File(FileManager.appdir+"/groups/"+group+"/"+name+"/"+map);
		try {
			FileUtils.copyDirectory(a, b, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
}
