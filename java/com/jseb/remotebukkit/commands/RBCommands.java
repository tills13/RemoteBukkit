package com.jseb.remotebukkit.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.StringBuilder;

import com.jseb.remotebukkit.RemoteBukkit;
import com.jseb.remotebukkit.network.Channel;
import com.jseb.remotebukkit.utils.Utils;

public class RBCommands implements CommandExecutor {
    private static final String TAG = "[RB] ";
	private RemoteBukkit plugin;

	public RBCommands(RemoteBukkit plugin) {
		this.plugin = plugin;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rb")) {
            if (args.length == 0) return true;
            
            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("connected")) {
                    int i = 1;
                    sender.sendMessage(TAG + plugin.getRBServer().getConnected().size() + " clients connected");
                    for (Channel channel : plugin.getRBServer().getConnected()) sender.sendMessage("\t" + i++ + ": " + channel.getLongName());
                } else if (args[0].equalsIgnoreCase("start")) {
                    if (plugin.getRBServer() == null) plugin.startServer();
                    if (plugin.getRBServer() != null && plugin.getRBServer().isActive) {
                        
                    } 
                } else if (args[0].equalsIgnoreCase("stop")) {
                    plugin.getRBServer().stopServer();
                } else if (args[0].equalsIgnoreCase("restart")) {
                    //stop
                    //wait
                    //start
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("rba")) {
            if (args.length == 0) sender.sendMessage("enter a messsage");
            else {                
                StringBuilder sb = new StringBuilder();
                for (String arg : args) sb.append(arg + " ");

                Utils.writeChat(sender.getName(), sb.toString());
                sender.sendMessage(TAG + "message sent");
            }
        }

        return true;
    }
}