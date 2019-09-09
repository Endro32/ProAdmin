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
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.endro32.proadmin.Main;
import com.endro32.proadmin.config.Config;

public class FileManager {
	
	//public static String appdir = System.getProperty("user.dir");
	public static String appdir = "D:\\ProAdmin"; // Non-workspace directory for testing
	
	/**
	 * Creates a directory at the specified path if it doesn't already exist
	 * If a file exists at the path but isn't a directory, it will be overwritten
	 * @param path
	 * @return True if directory created, false if not
	 */
	public static boolean mkdir(String path) {
		File dir = new File(appdir+"/"+path);
		if(dir.exists() && !dir.isDirectory())
			dir.delete();
		if(!dir.exists()) {
			System.out.println("Creating directory "+appdir+"/"+path);
			return dir.mkdir();
		} else {
			return false;
		}
	}
	
	public static boolean delFile(String path) {
		File file = new File(appdir+"/"+path);
		if(file.exists() && !file.isDirectory()) {
			System.out.println("Attempting to delete file "+appdir+"/"+path);
			return file.delete();
		} else return false;
	}
	
	public static boolean delDir(String path) {
		File dir = new File(appdir+"/"+path);
		if(dir.exists() && dir.isDirectory()) {
			System.out.println("Attempting to delete directory "+appdir+"/"+path);
			try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				return false;
			}
			return true;
		} else return false;
	}
	
	public static boolean updateFileSystem() {
		Config.resetConfig(false);
		mkdir("plugins");
		mkdir("maps");
		mkdir("apps");
		mkdir("groups");
		/*extractResource("latest.yml", "apps/latest.yml", false);
		if(Config.getAutoupdate(Config.AutoUpdate.SPIGOT)) {
			mkdir("buildtools");
		}*/
		if(Config.getBungeecordEnabled()) {
			mkdir("bungeecord");
			extractResource("bungeeconfig.yml", "bungeecord/config.yml", false);
		}
		if(Config.getServerIconsEnabled()) {
			mkdir("icons");
		}
		return true;
	}
	
	/**
	 * Updates server file tree to match the servers and groups in the config.
	 * Does not remove old servers and groups.
	 * @return True if successful
	 */
	public static boolean updateServerTree() {
		mkdir("groups");
		for(String group : Config.getServerGroupNames()) {
			mkdir("groups/"+group);
			if(Config.getMode(group).equals(GroupMode.INDIVIDUAL)) {
				for(String name : Config.getServerNamesForGroup(group)) {
					updateServerDirectory(group, name);
				}
			} else if(Config.getMode(group).equals(GroupMode.CLONED)) {
				List<String> servers = Config.getServerNamesForGroup(group);
				String first = servers.get(0);
				updateServerDirectory(group, first);
				File firstDir = new File(appdir+"/groups/"+group+"/"+first);
				for(String name : servers) {
					File target = new File(appdir+"/groups/"+group+"/"+name);
					if(name.equals(first)) continue;
					try {
						FileUtils.copyDirectory(firstDir, target);
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}
	
	public static boolean updateServerDirectory(String group, String server) {
		try {
			System.out.println("Attempting to update "+server+" in "+group+"...");
			mkdir("groups/"+group+"/"+server);
			mkdir("groups/"+group+"/"+server+"/plugins");
			File eula = new File("groups/"+group+"/"+server+"/eula.txt");
			if(!eula.exists()) {
				FileGenerator.generateEULA("groups/"+group+"/"+server, 
						Config.getEULAStatus());
			} else if(eula.exists()){
				FileGenerator.updateEULA("groups/"+group+"/"+server, 
						Config.getEULAStatus());
			}
			if(!extractResource("server.properties", "groups/"+group+"/"+server+"/server.properties",
					false)) {
				System.out.print("Server.properties extraction failed for server "+server+" in group "+group);
			}
			System.out.println("Successfully updated "+server);
			return true;
		} catch(Exception e) {
			System.out.println("Failed to update "+server);
			return false;
		}
	}
	
	/**
	 * Downloads a file from the internet
	 * @param url URL of file to download
	 * @param path Path of downloaded file within app directory
	 * @param replaceExisting Whether or not to overwrite an already existing file
	 * @return True if download is successful, false if not
	 */
	public static boolean download(URL url, String path, boolean replaceExisting) {
		try {
			System.out.println("Attempting to download "+url+"...");
			File tmp = new File(appdir+"/tmp");
			if(tmp.exists())
				tmp.delete();
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(appdir+"/tmp");
			fos.getChannel().transferFrom(rbc,  0, Long.MAX_VALUE);
			fos.close();
			File target = new File(appdir+"/"+path);
			if(replaceExisting) {
				Files.move(tmp.toPath(), target.toPath(), REPLACE_EXISTING);
			} else {
				Files.move(tmp.toPath(), target.toPath());
			}
			System.out.println("Successfully downloaded to "+appdir+"/"+path);
		} catch (IOException e) { 
			System.out.println("Failed to download "+url);
			return false;
		}
		return true;
	}
	
	public static boolean extractResource(String name, String target, boolean replaceExisting) {
		System.out.println("Attempting to extract "+name+"...");
		InputStream link = Main.class.getResourceAsStream("resources/"+name);
		File file = new File(appdir+"/"+target);
		try {
			if(replaceExisting) {
				Files.copy(link, file.toPath(), REPLACE_EXISTING);
			} else {
				if(file.exists())
					return true;
				Files.copy(link, file.toPath());
			}
			System.out.println("Succesfully extracted to "+appdir+"/"+target);
		} catch(IOException e) {
			System.out.println("Failed to extract "+name);
			return false;
		}
		return true;
	}
	
	/**
	 * Checks to see if config.yml exists in the server's root directory
	 */
	public static boolean isBlankSlate() {
		File file = new File(appdir+"/config.yml");
		if (file.exists())
			return false;
		return true;
	}
	
}
