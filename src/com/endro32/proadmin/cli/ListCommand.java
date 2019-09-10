package com.endro32.proadmin.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.endro32.proadmin.Main;
import com.endro32.proadmin.config.Config;
import com.endro32.proadmin.util.AppManager;
import com.endro32.proadmin.util.GroupMode;
import com.endro32.proadmin.util.MapManager;
import com.endro32.proadmin.util.Plugin;
import com.endro32.proadmin.util.PluginManager;

public class ListCommand implements CommandExecutor {

	CLI cli;
	
	public ListCommand(CLI cli) {
		this.cli = cli;
	}
	
	@Override
	public boolean onCommand(String command, String[] parameters) {
		if(!command.equalsIgnoreCase("list") || parameters.length < 1) return false;
		switch(parameters[0]) {
		case "help":
			showUsage();
			return true;
		case "groups":
			printGroups();
			return true;
		case "servers":
			if(parameters.length >= 2) {
				printServers(parameters[1]);
				return true;
			} else if(parameters.length == 1 && cli.isGroupSelected()) {
				printServers(cli.getSelectedGroup());
				return true;
				
			}
			return false;
		case "plugins":
			if(parameters.length == 1) {
				printAvailablePlugins();
			} else if(parameters.length == 2) {
				printGroupPlugins(parameters[1]);
			} else if(parameters.length >= 3) {
				printServerPlugins(parameters[1], parameters[2]);
			} else return false;
			return true;
		case "maps":
			if(parameters.length == 1) {
				printAvailableMaps();
			} else if(parameters.length == 2) {
				printGroupMaps(parameters[1]);
			} else if(parameters.length >= 3) {
				printServerMaps(parameters[1], parameters[2]);
			} else return false;
			return true;
		case "apps":
			System.out.println("Available apps:");
			for(String s : AppManager.getAvailableApps())
				System.out.println("- "+s);
			return true;
		case "selected":
			System.out.println("Selected group: "+cli.getSelectedGroup());
			if(cli.isServerSelected())
				System.out.println("Selected server: "+cli.getSelectedServer().getKey());
			return true;
		}
		return false;
	}

	@Override
	public void showUsage() {
		System.out.println("\nThe list command is used to get information that is stored in lists.");
		System.out.println("Here are the different ways it can be used:");
		System.out.println(
				  "- 'help': Prints the usage info\n"
				+ "- 'groups': Prints the different server groups\n"
				+ "- 'servers <group>': Prints all the servers in the specified or selected group\n"
				+ "- 'plugins available': Prints all the plugins in the global 'plugins' folder\n"
				+ "- 'plugins installed <group> (server)': Prints all the plugins installed on an"
				+ " individual server or a cloned group\n"
				+ "- 'maps available': Prints all the maps in the global 'maps' folder\n"
				+ "- 'maps installed <group> (server)': Prints all the maps installed on an"
				+ " individual server or a cloned group\n");
	}
	
	void printGroups() {
		System.out.println("Server groups:");
		for(String s : Config.getServerGroupNames()) {
			System.out.println("- "+s);
		}
	}
	
	void printServers(String group) {
		System.out.println("Servers in "+group+":");
		try {
			for(String s : Config.getServerNamesForGroup(group)) {
				System.out.println("- "+s);
			}
		} catch(NullPointerException e) {
			System.out.println(group+" is not a valid group!");
			System.out.println("Use 'list groups' to view valid groups");
		}
	}
	
	void printAvailablePlugins() {
		PluginManager pm = Main.getPluginManager();
		System.out.println("Available plugins:");
		for(Plugin p : pm.getInstalledPlugins()) {
			System.out.println("- "+p.getName()+" version "+p.getVersion());
		}
	}
	
	void printGroupPlugins(String group) {
		if(!Config.getServerGroupNames().contains(group)) {
			System.out.println(group+" is not a valid group");
			return;
		}
		if(Config.getMode(group).equals(GroupMode.INDIVIDUAL)) {
			System.out.println(group+" is  not a cloned group.\n"
					+ "Use 'list plugins installed "+group+" <server>' to get plugins lists for this group.");
			return;
		}
		
		PluginManager pm = Main.getPluginManager();
		System.out.println("Plugins for "+group+":");
		for(String p: Config.getPluginsForGroup(group)) {
			List<String> plugin = new ArrayList<String>(Arrays.asList(p.split("§")));
			if(plugin.size() < 2) {
				plugin.add(pm.getLatestVersion(plugin.get(0)));
			}
			System.out.println(plugin.get(0)+" version "+plugin.get(1));
		}
	}
	
	void printServerPlugins(String group, String server) {
		if(!Config.getServerNamesForGroup(group).contains(server)) {
			System.out.println(group+" is not a valid server in this group");
			return;
		}
		if(Config.getMode(group).equals(GroupMode.CLONED)) {
			System.out.println(group+" is a cloned group.\n"
					+ "Use 'list plugins installed "+group+"' to get plugins list for this group.");
			return;
		}
		
		PluginManager pm = Main.getPluginManager();
		System.out.println("Plugins for "+server+":");
		for(String p: Config.getPluginsForServer(group, server)) {
			List<String> plugin = new ArrayList<String>(Arrays.asList(p.split("§")));
			if(plugin.size() < 2) {
				plugin.add(pm.getLatestVersion(plugin.get(0)));
			}
			System.out.println(plugin.get(0)+" version "+plugin.get(1));
		}
	}
	
	void printAvailableMaps() {
		System.out.println("Available maps:");
		for(String s : MapManager.getMaps()) {
			System.out.println("- "+s);
		}
	}
	
	void printGroupMaps(String group) {
		if(Config.getMode(group).equals(GroupMode.INDIVIDUAL)) {
			System.out.println(group+" is  not a cloned group.\n"
					+ "Use 'list maps installed "+group+" <server>' to get maps lists for this group.");
			return;
		}
		System.out.println("Maps for "+group+":");
		for(String s : Config.getMapsForGroup(group)) {
			System.out.println("- "+s);
		}
	}
	
	void printServerMaps(String group, String server) {
		if(Config.getMode(group).equals(GroupMode.CLONED)) {
			System.out.println(group+" is a cloned group.\n"
					+ "Use 'list maps installed "+group+"' to get maps list for this group.");
			return;
		}
		System.out.println("Maps for "+server+":");
		for(String s : Config.getMapsForServer(group, server)) {
			System.out.println("- "+s);
		}
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Lists components of the server that are setup in the config";
	}
	
}
