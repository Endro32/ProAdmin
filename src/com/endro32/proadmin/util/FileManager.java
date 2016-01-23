package com.endro32.proadmin.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

import com.endro32.proadmin.Main;
import com.endro32.proadmin.config.Config;

public class FileManager {
	
	public static String appdir = System.getProperty("user.dir");
	
	public static boolean mkdir(String path) {
		File dir = new File(appdir+"/"+path);
		return dir.mkdir();
	}
	
	public static boolean delFile(String path) {
		File file = new File(appdir+"/"+path);
		return file.delete();
	}
	
	public static boolean delDir(String path) {
		File dir = new File(appdir+"/"+path);
		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean updateFileSystem() {
		Config.resetConfig(false);
		mkdir("plugins");
		mkdir("maps");
		mkdir("apps");
		mkdir("groups");
		extractResource("latest.yml", "apps/latest.yml", false);
		if(Config.getAutoupdate(Config.AutoUpdate.SPIGOT)) {
			mkdir("buildtools");
		}
		if(Config.getBungeecordEnabled()) {
			mkdir("bungeecord");
			extractResource("bungeeconfig.yml", "bungeecord/config.yml", false);
		}
		if(Config.getServerIconsEnabled()) {
			mkdir("icons");
		}
		for(String group : Config.getServerGroupNames()) {
			mkdir("groups/"+group);
			for(String name : Config.getServerNamesForGroup(group)) {
				mkdir("groups/"+group+"/"+name);
				mkdir("groups/"+group+"/"+name+"/plugins");
				File eula = new File("groups/"+group+"/"+name+"/eula.txt");
				if(!eula.exists()) {
					FileGenerator.generateEULA("groups/"+group+"/"+name, 
							Config.getEULAStatus());
				} else if(eula.exists()){
					FileGenerator.updateEULA("groups/"+group+"/"+name, 
							Config.getEULAStatus());
				}
				extractResource("server.properties", "groups/"+group+"/"+name+"/server.properties",
						false);
			}
		}
		return true;
	}
	
	public static boolean download(URL url, String path, boolean replaceExisting) {
		try {
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream("tmp");
			fos.getChannel().transferFrom(rbc,  0, Long.MAX_VALUE);
			File tmp = new File(appdir+"/tmp");
			File target = new File(appdir+"/"+path);
			if(replaceExisting) {
				Files.move(tmp.toPath(), target.toPath(), REPLACE_EXISTING);
			} else {
				Files.move(tmp.toPath(), target.toPath());
			}
			fos.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean extractResource(String name, String target, boolean replaceExisting) {
		InputStream link = Main.class.getResourceAsStream("resources/"+name);
		File file = new File(appdir+"/"+target);
		try {
			if(replaceExisting) {
				Files.copy(link, file.toPath(), REPLACE_EXISTING);
			} else {
				Files.copy(link, file.toPath());
			}
		} catch(IOException e) {
			return false;
		}
		return true;
	}
	
}
