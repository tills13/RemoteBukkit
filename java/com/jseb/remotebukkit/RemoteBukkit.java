package com.jseb.remotebukkit;

import com.jseb.remotebukkit.commands.RBCommands;
import com.jseb.remotebukkit.network.AsyncServer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.ChatColor;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

import com.jseb.remotebukkit.utils.Utils;

public class RemoteBukkit extends JavaPlugin {
	private static String TAG = "[RB] ";
	public String title = "[" + ChatColor.RED + "RB" + ChatColor.WHITE + "] " + ChatColor.GREEN;

	private AsyncServer server;

	public void onEnable() {
		Utils.init(this);
		init();
	}

	public void onDisable() {
		server.stopServer();
		server = null;
	}

	public AsyncServer getRBServer() {
		return this.server;
	}

	public void init() {
		getCommand("rb").setExecutor(new RBCommands(this));
		getCommand("rba").setExecutor(new RBCommands(this));
		startServer();
	}

	public void startServer() {
		try {
			server = AsyncServer.getInstance(this, 8000);
			server.start();
		} catch (IOException e) {
			getLogger().warning(TAG + "encountered an error trying to start the server");
		}
	}

	public String getPIN() {
		return "1234";
	}
}