package com.endro32.proadmin.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.config.YAMLConfig;
import com.gotoquiz.TitleExtractor;

public class AppManager {
	
	public static YAMLConfig latestyaml = new YAMLConfig("apps/latest.yml", false);
	
	public static boolean retrieveLatest() {
		URL url = null;
		try {
			url = new URL("https://s3.amazonaws.com/Minecraft.Download/"
					+ "versions/versions.json");
			BufferedReader in = new BufferedReader (
					new InputStreamReader(url.openStream()));
			in.skip(35);
			latestyaml.setValue("available.vanilla.snapshot",
					StringUtils.removeEnd(in.readLine(), "\","));
			in.skip(16);
			latestyaml.setValue("available.vanilla.release",
					StringUtils.removeEnd(in.readLine(), "\""));
			in.close();
			latestyaml.setValue("available.bungeecord",
					versionToInt(TitleExtractor.getPageTitle(
						"http://ci.md-5.net/job/BungeeCord/lastBuild/")));
		} catch (MalformedURLException e) { // Should never occur
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean checkForVanillaUpdate() {
		int latest;
		try {
			latest = versionToInt(latestyaml.getValue(
					"available.vanilla.release").toString());
		} catch (FileNotFoundException e1) {
			return false;
		}
		int installed = 0;
		try {
			String latestInstalled =
					latestyaml.getValue("installed.vanilla.release").toString();
			installed = versionToInt(latestInstalled);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(latest > installed) return true;
		return false;
	}
	
	public static boolean checkForSnapshotUpdate() {
		int latest;
		try {
			latest = versionToInt(latestyaml.getValue(
					"available.vanilla.snapshot").toString());
		} catch (FileNotFoundException e1) {
			return false;
		}
		int installed = 0;
		try {
			String latestInstalled =
					latestyaml.getValue("installed.vanilla.snapshot").toString();
			installed = versionToInt(latestInstalled);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(latest > installed) return true;
		return false;
	}
	
	public static boolean updateAll() {
		updateVanilla();
		if(Config.getAutoupdate(Config.AutoUpdate.SPIGOT)) {
			updateBuildTools();
			runBuildTools();
		}
		return true;
	}
	
	public static boolean updateVanilla() {
		retrieveLatest();
		if(!checkForVanillaUpdate()) return true;
		String version;
		try {
			version = latestyaml.getValue("available.vanilla.release").toString();
		} catch (FileNotFoundException e1) {
			return false;
		}
		URL url = null;
		try {
			url = new URL("https://s3.amazonaws.com/Minecraft.Download/"
					+ "versions/"+version+"/minecraft_server."+version+".jar");
		} catch (MalformedURLException e) {
			return false;
		}
		FileManager.download(url, "apps/vanilla-"+version+".jar", true);
		try {
			latestyaml.setValue("installed.vanilla.release", version);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean updateSnapshot() {
		retrieveLatest();
		if(!checkForSnapshotUpdate()) return true;
		String version;
		try {
			version = latestyaml.getValue("available.vanilla.snapshot").toString();
		} catch (FileNotFoundException e1) {
			return false;
		}
		URL url = null;
		try {
			url = new URL("https://s3.amazonaws.com/Minecraft.Download/"
					+ "versions/"+version+"/minecraft_server."+version+".jar");
		} catch (MalformedURLException e) {
			return false;
		}
		FileManager.download(url, "apps/vanilla-"+version+".jar", true);
		try {
			latestyaml.setValue("installed.vanilla.snapshot", version);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Downloads latest stable BuildTools jar from SpigotMC.org
	 * Creates buildtools directory if not already present
	 * @return True if update is successful
	 */
	public static boolean updateBuildTools() {
		FileManager.mkdir("buildtools");
		URL url = null;
		try {
			url = new URL("https://hub.spigotmc.org/jenkins/job/BuildTools/lastStableBuild/"
					+ "artifact/target/BuildTools.jar");
		} catch (MalformedURLException e) { // Should never occur
			e.printStackTrace();
		}
		return FileManager.download(url, "buildtools/BuildTools.jar", true);
	}
	
	/**
	 * Runs Build Tools to download Vanilla, Craftbukkit, and Spigot jars for specified version
	 * Puts jar files in apps directory
	 * @param mcversion Version of Minecraft to build for
	 * @return True if BuildTools runs successfully
	 */
	public static boolean runBuildTools(String mcversion) {
		File dir = new File(FileManager.appdir+"/buildtools");
		ProcessBuilder pb = new ProcessBuilder("java", "-jar", "BuildTools.jar", "--rev",
				mcversion, "--output-dir", FileManager.appdir+"/apps");
		pb.directory(dir);
		try {
			Process proc = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while((line = in.readLine()) != null)
					System.out.println(line);
			
			// Copy vanilla server jar to apps folder
			File work = new File(FileManager.appdir+"/buildtools/work");
			for(File f : work.listFiles()) {
				if(f.getName().startsWith("minecraft_server.")) {
					String target = FileManager.appdir+"/apps/"+f.getName().replace("minecraft_server.", "vanilla-");
					System.out.println("Copying "+f.getName()+" to "+target);
					Files.copy(f.toPath(), Paths.get(target));
					System.out.println("  - Saved as "+target);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Runs Build Tools to download Vanilla, Craftbukkit, and Spigot jars for latest Minecraft version
	 * @return True if BuildTools runs successfully
	 */
	public static boolean runBuildTools() {
		return runBuildTools("latest");
	}
	
	public static boolean updateSpigot() {
		String version;
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					FileManager.appdir+"/buildtools/BuildData/info.json"));
			in.skip(24);
			version = StringUtils.removeEnd(in.readLine(), "\",");
			in.close();
		} catch (IOException e) {
			return false;
		}
		File spigota = new File(FileManager.appdir+"/buildtools/spigot-"
				+version+".jar");
		File spigotb = new File(FileManager.appdir+"/apps/spigot-"
				+version+".jar");
		File bukkita = new File(FileManager.appdir+"/buildtools/craftbukkit-"
				+version+".jar");
		File bukkitb = new File(FileManager.appdir+"/apps/craftbukkit-"
				+version+".jar");
		try {
			Files.copy(spigota.toPath(), spigotb.toPath(), REPLACE_EXISTING);
			Files.copy(bukkita.toPath(), bukkitb.toPath(), REPLACE_EXISTING);
			latestyaml.setValue("installed.spigot", version);
			latestyaml.setValue("installed.craftbukkit", version);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean checkForBungeecordUpdate() {
		retrieveLatest();
		int installed = 0;
		int available = 0;
		try {
			installed = Integer.parseInt(latestyaml.getValue(
					"installed.bungeecord").toString());
			available = Integer.parseInt(latestyaml.getValue(
					"available.bungeecord").toString());
		} catch (NumberFormatException e) {
			return false;
		} catch (FileNotFoundException e) {
			FileManager.updateFileSystem();
			return false;
		}
		if(available > installed) return true;
		return false;
	}
	
	public static boolean updateBungeecord() {
		if(!checkForBungeecordUpdate()) return false;
		int version;
		try {
			version = Integer.parseInt(latestyaml.getValue(
					"available.bungeecord").toString());
		} catch (NumberFormatException e) {
			return false;
		} catch (FileNotFoundException e) {
			FileManager.updateFileSystem();
			return false;
		}
		URL url = null;
		try {
			url = new URL("http://ci.md-5.net/job/BungeeCord/"+version
					+ "/artifact/bootstrap/target/BungeeCord.jar");
		} catch (MalformedURLException e) { // Should never happen
			e.printStackTrace();
		}
		if(!FileManager.download(url, "apps/bungeecord-"+version+".jar",
				true)) return false;
		try {
			latestyaml.setValue("installed.bungeecord", version);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static int versionToInt(String version) {
		return Integer.parseInt(version.replaceAll("[^\\d]", ""));
	}
	
}
