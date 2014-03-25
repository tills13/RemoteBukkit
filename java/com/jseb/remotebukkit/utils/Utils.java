package com.jseb.remotebukkit.utils;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask; 
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.Random;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.File;

import com.jseb.remotebukkit.json.JSONObject;
import com.jseb.remotebukkit.json.JSONArray;

public class Utils {
	public static final String TAG = "[RB UTIL] ";
	public static final int HISTORY_SIZE = 6;
	public static final int HISTORY_PERIOD = 1200 * 5; // 1200 ticks per minute	
	
	public static int [] playerHistory; // [now, now + 1min, now + 2mins, ...]
	public static JSONArray admin_chat_history = new JSONArray();
	public static JavaPlugin mPlugin;

	public static void init(final JavaPlugin plugin) {
		mPlugin = plugin;

		playerHistory = new int[HISTORY_SIZE];
		for (int i = 0; i < playerHistory.length; i++) playerHistory[i] = 0; // initialize array

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (int i = playerHistory.length - 1; i >= 1; i--) playerHistory[i] = playerHistory[i - 1];
				playerHistory[0] = Bukkit.getServer().getOnlinePlayers().length; 
				plugin.getLogger().info(TAG + "updated history");
			}
		}, 0, HISTORY_PERIOD);
	}

	public static ArrayList<String> getPlayers() {
		ArrayList<String> players = new ArrayList<String>();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) players.add(player.getName());

		return players;
	}

	public static int [] getPlayerHistory() {
		return playerHistory;
	}

	public static void writeChat(String sender, String message) {
		admin_chat_history.put(new JSONObject().put("sender", sender).put("message", message).put("time-sent", System.currentTimeMillis()));
	}

	public static JSONArray getChatHistory() {
		return admin_chat_history;
	}
		
}