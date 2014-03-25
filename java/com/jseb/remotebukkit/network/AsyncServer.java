package com.jseb.remotebukkit.network;

import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.jseb.remotebukkit.RemoteBukkit;

public class AsyncServer extends Thread {
	private static String TAG = "[SERVER] ";
	private static AsyncServer mInstance;
	private static ServerSocket server;
	private static ServerSocketChannel serverChannel;
	private static List<Channel> connected = new ArrayList<Channel>();
	private static RemoteBukkit plugin;

	public static boolean isActive;

	//defaults
	public static final int DEFAULT_PORT = 8000;
	public static final int MAX_CONNECTIONS = 4;

	protected AsyncServer() throws IOException {
		this(DEFAULT_PORT);
	}

	protected AsyncServer(int port) throws IOException {
		serverChannel = ServerSocketChannel.open();
		serverChannel.bind(new InetSocketAddress(port));
		server = serverChannel.socket();
		
		isActive = true;
	}

	public static AsyncServer getInstance(RemoteBukkit plugin) throws IOException {
		return getInstance(plugin, DEFAULT_PORT);
	}

	public static AsyncServer getInstance(RemoteBukkit plugin, int port) throws IOException {
		if (mInstance == null) mInstance = new AsyncServer(port);
		mInstance.plugin = plugin;

		return mInstance;
	}

	public void run() {
		try {
			plugin.getLogger().info(TAG + "listening on " + serverChannel.getLocalAddress());
			while (isActive) connect(serverChannel.accept());
		} catch (IOException e) {

		}
	}

	public static void connect(SocketChannel channel) {
		try {
			Channel client = new Channel(mInstance, channel);
			plugin.getLogger().info(TAG + "client connected on " + client.getLongName());
			connected.add(client);
			client.start();
		} catch (IOException e) {
			plugin.getLogger().warning(TAG + "something went wrong while connecting a client.");
		}
	}

	public static void disconnect(Channel channel) {
		try {
			plugin.getLogger().info(TAG + "client disconnected from " + channel.getLongName());
			connected.remove(channel);
			channel.getChannel().close();
		} catch (IOException e) {

		}
	}

	public void stopServer() {
		for (Channel channel : getConnected()) channel.close();

		this.connected.clear();
		this.isActive = false;
		this.interrupt();
		
		plugin.getLogger().info(TAG + "shutting down.");
	}

	/*public static TokenPair generateTokenPair(String consumer_name) {
		if (TOKEN_CACHE.containsKey(consumer_name)) return TOKEN_CACHE.get(consumer_name);
		String token = DigestUtils.md5Hex(consumer_name + System.nanoTime());
		String secret = DigestUtils.md5Hex(consumer_name + System.nanoTime() + token);
		
		TokenPair tokenPair = new TokenPair(token, secret);
		writeTokenToDisk(consumer_name, tokenPair);
		this.TOKEN_CACHE.put(consumer_name, tokenPair);
		return tokenPair;
	}*/

	public static List<Channel> getConnected() {
		return connected;
	}

	public static void log(String message) {
		plugin.getLogger().info(TAG + message);
	}
}