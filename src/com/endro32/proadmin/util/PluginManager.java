package com.endro32.proadmin.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.endro32.proadmin.config.YAMLConfig;

public class PluginManager {

	static List<Plugin> plugins = new ArrayList<Plugin>();
	
	public static void loadPlugins() {
		plugins.clear();
		File folder = new File(FileManager.appdir+"/plugins");
		File[] listOfFiles = folder.listFiles();
		List<File> jars = new ArrayList<File>();
		for (File file : listOfFiles) {
			String filename = file.getName();
			String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
			if(extension.equals("jar")) jars.add(file);
		}
		URLClassLoader loader;
		YAMLConfig yaml;
        for (File jar : jars) {
            // Ensure that the JAR exists
            if (jar.isFile()) {
            	String abspath = jar.getAbsolutePath();
            	String name = "";
            	String version = "";
            	File tmp = null;
                try {
                	loader = new URLClassLoader(new URL[]{jar.toURI().toURL()});
                	InputStream input = loader.getResourceAsStream("plugin.yml");
                    tmp = new File(FileManager.appdir+"/plugins/tmp");
                    OutputStream out = new FileOutputStream(tmp);
                    int read;
                    byte[] bytes = new byte[1024];
                    while ((read = input.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    out.close();
                    tmp.deleteOnExit();
                	yaml = new YAMLConfig(tmp.getAbsolutePath(), true);
                	name = yaml.getValue("name").toString();
                	version = yaml.getValue("version").toString();
                } catch (MalformedURLException e) {
                    // This should never happen.
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
					// Also should never happen.
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            	plugins.add(new Plugin(name, version, abspath));
            	tmp.delete();
            }
        }
	}
	
	public static List<Plugin> getInstalledPlugins() {
		return plugins;
	}
	
	public static List<String> getPluginNames() {
		List<String> names = new ArrayList<String>();
		for(Plugin plugin : plugins) {
			String name = plugin.getName();
			if(!names.contains(name)) {
				names.add(name);
			}
		}
		return names;
	}
	
	public static List<String> getVersionsForPlugin(String pluginName) {
		List<String> versions = new ArrayList<String>();
		for(Plugin plugin : plugins) {
			String name = plugin.getName();
			String version = plugin.getVersion();
			if(name.equals(pluginName) && !versions.contains(version)) {
				versions.add(version);
			}
		}
		return versions;
	}
	
	public static String getLatestVersion(String name) {
		String version = "0";
		int ver = 0;
		for(Plugin plugin : plugins) {
			if(plugin.getName().equals(name) &&
					AppManager.versionToInt(plugin.getVersion()) > ver) {
				version = plugin.getVersion();
				ver = AppManager.versionToInt(plugin.getVersion());
			}
		}
		return version;
	}
	
	public static boolean installToServer(String plugin, String version, String group, String name) {
		for(Plugin p : plugins) {
			if(p.getName().equals(plugin) && p.getVersion().equals(version)) {
				File a = new File(p.getAbsolutePath());
				if(!a.exists()) return false;
				File b = new File(FileManager.appdir+"/groups/"+group+"/"+name+
						"/plugins/"+p.getFileName());
				try {
					Files.copy(a.toPath(), b.toPath(), REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return true;
	}
	
}
