package com.jseb.remotebukkit.network;

import java.lang.StringBuilder;
import java.nio.channels.SocketChannel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;

import com.jseb.remotebukkit.json.*;

public class Channel extends Thread {
	private static String TAG = "[RB CHANNEL] ";
	private AsyncServer server;
	private SocketChannel channel;
	private OutputStream out;
	private InputStream in;
	private BufferedReader rd; 
	private BufferedWriter wr;
	private boolean isActive;
	private boolean isAuthenticated;
	private AsyncServerHandler handler;

	// attributes
	private String ip;
	private int port;
	private String hostname;

	public Channel(AsyncServer server, SocketChannel channel) throws IOException {
		this.server = server;
		this.channel = channel;
		this.out = channel.socket().getOutputStream();
		this.in = channel.socket().getInputStream();
		this.rd = new BufferedReader(new InputStreamReader(this.in));
		this.wr = new BufferedWriter(new OutputStreamWriter(this.out));
		this.handler = new AsyncServerHandler(server);

		this.ip = ((InetSocketAddress) getChannel().getRemoteAddress()).getAddress().toString();
		this.port = ((InetSocketAddress) getChannel().getRemoteAddress()).getPort();
		this.hostname = ((InetSocketAddress) getChannel().getRemoteAddress()).getHostName();
	}

	public void run() {
		this.isActive = true;
		listen();
	}

	private void listen() {
		try {
			while (this.isActive) {
				handler.onReceive(this, new JSONObject(rd.readLine()));
				if (!this.isActive) server.disconnect(this);
			}
		} catch (IOException e) {

		} catch (NullPointerException e) {
			server.disconnect(this);
		}
	}

	public Socket getSocket() {
		return this.getChannel().socket();
	}

	public SocketChannel getChannel() {
		return this.channel;
	}

	public String getHostName() {
		return this.hostname;
	}

	public String getIP() {
		return this.ip;
	}

	public int getPort() {
		return this.port;
	}

	public String getLongName() {
		return "[" + getIP() + ", " + getPort() + "]";
	}

	public void close() {
		this.isActive = false;
		this.interrupt();
		server.disconnect(this);
	}

	public boolean getActive() {
		return this.isActive;	
	}

	public void authenticate() {
		this.isAuthenticated = true;
	}

	public boolean isAuthenticated() {
		return this.isAuthenticated;
	}

	public void write(JSONObject object) {
		try {
			wr.write(object.toString());
			wr.newLine();
            wr.flush();
		} catch (IOException e) {
			
		}
	}
}