package com.jseb.remotebukkit.network;

import org.bukkit.Server;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.jseb.remotebukkit.json.JSONObject;
import com.jseb.remotebukkit.json.JSONArray;
import com.jseb.remotebukkit.utils.*;

public class AsyncServerHandler {
	private static String TAG = "[RB SERVER HNDLR] ";
	private AsyncServer server;

	public AsyncServerHandler(AsyncServer server) {
		this.server = server;
	}

	public void onReceive(Channel channel, JSONObject request) {
		switch(request.getInt("type")) {
			case Constants.REQUEST_AUTHENTICATION: 
				server.plugin.getLogger().info(TAG + "authentication request from " + channel.getLongName());
				channel.write(requestAuthentication(channel, request));
				break;
			case Constants.REQUEST_DATA_REQUEST: 
				server.plugin.getLogger().info(TAG + "data request from " + channel.getLongName());
				channel.write(requestData(channel, request));
				break;
			case Constants.REQUEST_COMMAND_REQUEST: 
				server.plugin.getLogger().info(TAG + "command request from " + channel.getLongName());
				channel.write(requestCommand(channel, request));
				break;
			case Constants.REQUEST_PLAYER_INFO: 
				server.plugin.getLogger().info(TAG + "player info request from " + channel.getLongName());
				channel.write(requestPlayerInfo(channel, request));
				break;
			case Constants.REQUEST_ADMIN_MAIL:
				server.plugin.getLogger().info(TAG + "chat (read) request from " + channel.getLongName());
				channel.write(requestChatHistory(channel, request));
				break;
			case Constants.REQUEST_WRITE_ADMIN_MAIL:
				server.plugin.getLogger().info(TAG + "chat (write) request from " + channel.getLongName());
				channel.write(requestSendChatMessage(channel, request));
				break;
			default:
				server.plugin.getLogger().info(TAG + "unknown request from " + channel.getLongName());
				channel.write(handleError(Constants.ERROR_UNKNOWN_REQUEST));
				break;
		}
	} 

	public JSONObject requestAuthentication(Channel channel, JSONObject request) {
		if (request.getString("pin").equals(server.plugin.getPIN())) {
			channel.authenticate();
			return new JSONObject().put("type", Constants.REQUEST_RESPONSE)
								   .put("result", Constants.RESULT_SUCCESS);
		} else return handleError(Constants.ERROR_INCORRECT_PIN);
	} 

	public JSONObject requestData(Channel channel, JSONObject request) {
		if (!channel.isAuthenticated()) return handleError(Constants.ERROR_AUTHENTICATION);	

		Server bukkitServer = server.plugin.getServer();

		return new JSONObject().put("type", Constants.REQUEST_RESPONSE)
							   .put("result", Constants.RESULT_SUCCESS)
							   .put("server-ip", bukkitServer.getIp())
							   .put("server-port", bukkitServer.getPort())
							   .put("server-name", bukkitServer.getServerName())
							   .put("players", bukkitServer.getOnlinePlayers().length)
							   .put("player-history", Utils.getPlayerHistory())
							   .put("player-names", Utils.getPlayers());
	}

	public JSONObject requestCommand(Channel channel, JSONObject request) {
		if (!channel.isAuthenticated()) return handleError(Constants.ERROR_AUTHENTICATION);			
		
		boolean success = server.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), request.getString("command"));
		return new JSONObject().put("type", Constants.REQUEST_RESPONSE)
							   .put("result", success ? Constants.RESULT_SUCCESS : Constants.RESULT_FAILURE)
							   .put("extra", success ? "none" : "command failed");
	} 

	public JSONObject requestPlayerInfo(Channel channel, JSONObject request) {
		if (!channel.isAuthenticated()) return handleError(Constants.ERROR_AUTHENTICATION);

		Player player = Bukkit.getServer().getPlayer(request.getString("player"));
		if (player == null) return handleError(Constants.ERROR_PLAYER_NOT_FOUND);

		return new JSONObject().put("type", Constants.REQUEST_RESPONSE)
							   .put("result", Constants.RESULT_SUCCESS)
							   .put("player-name", player.getName())
							   .put("player-address", player.getAddress())
							   .put("player-level", player.getLevel())
							   .put("player-playtime", player.getPlayerTime())
							   .put("player-gm", player.getGameMode())
							   .put("player-holding", player.getItemInHand())
							   .put("player-health", player.getHealth());
	}

	public JSONObject requestSendChatMessage(Channel channel, JSONObject request) {
		if (!channel.isAuthenticated()) return handleError(Constants.ERROR_AUTHENTICATION);

		Utils.writeChat(request.getString("message"), "RBCLIENT");
		return new JSONObject().put("type", Constants.REQUEST_RESPONSE)
							   .put("result", Constants.RESULT_SUCCESS);
	}

	public JSONObject requestChatHistory(Channel channel, JSONObject request) {
		if (!channel.isAuthenticated()) return handleError(Constants.ERROR_AUTHENTICATION);

		return new JSONObject().put("type", Constants.REQUEST_RESPONSE)
							   .put("result", Constants.RESULT_SUCCESS)
							   .put("data", Utils.getChatHistory());
	}

	/*public JSONObject broadcast(Channel channel, JSONObject request) {
		if (!channel.isAuthenticated()) return handleError(Constants.ERROR_AUTHENTICATION);

		JSONObject message = new JSONObject().put("type", Constants.REQUEST_BROADCAST)
										     .put("sender", channel.getHostName())
										     .put("message", request.getString("message"))
										     .put("time", System.currentTimeMillis());

		for (Channel mChannel : server.getConnected()) mChannel.write(message);
		return new JSONObject().put("type", REQUEST_RESPONSE).put("result", "success");
	}*/

	public JSONObject handleError(int error) {
		JSONObject response = new JSONObject().put("type", Constants.REQUEST_RESPONSE)
											  .put("result", Constants.RESULT_FAILURE);

		switch (error) {
			case Constants.ERROR_AUTHENTICATION: 
				response.put("extra", "authenticate with \"type : 1\" and \"pin : [PIN]\"");
		 	case Constants.ERROR_INCORRECT_PIN:
		 		response.put("extra", "incorrect pin");
		 	case Constants.ERROR_UNKNOWN_REQUEST:
		 		response.put("extra", "unknown request");
	 		case Constants.ERROR_PLAYER_NOT_FOUND:
		 		response.put("extra", "player not found");
		 	default:
		 		response.put("extra", "generic failure");
		}

		return response;
	}
}