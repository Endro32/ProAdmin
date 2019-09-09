# ProAdmin
## The Most Powerful Minecraft Server Remote Management Suite
Designed for servers ranging from the humble Vanilla server all the way to massive minigame servers, ProAdmin is packed with power and flexibility. It features a powerful command-line interface, designed to be usable over SSH, that allows for complete management of your minecraft server. ProAdmin will help guide you through the Minecraft server's various configuaration files and automates many tasks that can be tedious on huge servers. One of the premiere features of ProAdmin is its automatic management of a Bungeecord network, automatically setting network configurations and distributing server jars, plugins, apps, and maps to the various servers on the network.
#### Currently in development
Altough it's not done yet, I'm working hard to get it finished. The initial release will be exclusively for single-host servers, with the next major release being focused on adding multi-host support to handle Bungeecord networks hosted on multiple physical machines, allowing you to manage the entire network from the ProAdmin installation on the central Bungeecord server.
##### In the meantime, feel free to enjoy the largely uncommented source code and currently working features.

## Designed for your PC and for your remote server
Because most big minecraft servers are run on remote servers such as [Amazon Web Services](http://aws.amazon.com/) or [Rackspace](https://www.rackspace.com/), ProAdmin features a fully-capable command-line-interface to allow you to manage your server over SSH. In addition, it features the ability to build and run your minecraft server on your local PC using its beautifully crafted graphical interface, then deploy it to your remote server where it can be put into action.

## What it does
#### Some of its many features include:
- Organize servers into groups
- Automatically generate EULA.txt files for each individual server
- Automatically manage a [Bungeecord](https://www.spigotmc.org/wiki/bungeecord/) installation
- Automatically update the server applications
- Distribute plugins to each server from a central `plugins` folder
- Distribute maps to each server from a central `maps` folder
- Distrubte server icons to each server from a central `icons` folder
- Manage server configuration files from within the GUI
- Automatically assign a unique port number to every individual server
- Automatically connect supported applications and plugins to a central MySQL server

#### What it specifically supports:
- Vanilla Server
- [Bungeecord](https://www.spigotmc.org/wiki/bungeecord/)
- [Bukkit](https://bukkit.org/)
- [Spigot](https://www.spigotmc.org/)
- [PermissionsEx](http://dev.bukkit.org/bukkit-plugins/permissionsex/) Bukkit plugin
- [WorldEdit](http://dev.bukkit.org/bukkit-plugins/worldedit/) Bukkit plugin
- [WorldGuard](http://dev.bukkit.org/bukkit-plugins/worldguard/) Bukkit Plugin

#### What else it supports, but with less features:
- Any other minecraft server application that is a jar file such as [Sponge](https://www.spongepowered.org/)
- Every Bukkit plugin in existence (yes, that includes ones you make yourself)
- Every Minecraft world in existence (especially the ones you make yourself
