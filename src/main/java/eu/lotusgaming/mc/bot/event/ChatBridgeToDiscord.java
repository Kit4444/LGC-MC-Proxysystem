//Created by Chris Wille at 09.02.2024
package eu.lotusgaming.mc.bot.event;

import eu.lotusgaming.mc.main.LotusController;
import eu.lotusgaming.mc.misc.ChatBridgeUtils;
import eu.lotusgaming.mc.misc.Playerdata;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatBridgeToDiscord implements Listener{
	
	JDA jda;
	public ChatBridgeToDiscord(JDA jda) {
		this.jda = jda;
	}
	
	@EventHandler
	public void onPlayerChat(ChatEvent event) {
		if(event.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) event.getSender();
			long channelId = ChatBridgeUtils.translateStringToLong(player.getServer().getInfo().getName());
			String fancyName = ChatBridgeUtils.translateBCKeyToFancyName(player.getServer().getInfo().getName());
			if(channelId == 0) {
				
			}else {
				if(!event.isCommand()) {
					if(event.getMessage().startsWith("@")) {
						if(player.hasPermission("lgc.event.useStaffchat")) {
							event.setCancelled(true);
							String message = event.getMessage().substring(1);
							if(!message.isBlank()) {
								sendStaffchat(player, message);
								for(ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
									all.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacy("§7[§cStaffchat§7] §a" + fancyName + " §7- §6" + player.getName() + "§7: " + event.getMessage().substring(1)));
								}
							}
						}else {
							event.setCancelled(false);
						}
					}else {
						sendChat(player, event.getMessage(), channelId);
					}
				}
			}
		}
	}
	
	void sendChat(ProxiedPlayer sender, String message, long channelId) {
		Guild guild = jda.getGuildById(ChatBridgeUtils.public_guild);
		TextChannel channel = guild.getTextChannelById(channelId);
		LotusController lc = new LotusController();
		long timestamp = (System.currentTimeMillis() / 1000);
		String userId = lc.getPlayerData(sender, Playerdata.LotusChangeID);
		String role = lc.getPlayerData(sender, Playerdata.PlayerGroup);
		String res = "**[**" + role + " **|** <t:" + timestamp + ":f> **]** " + sender.getName() + " (" + userId + "): " + message;
		channel.sendMessage(res).queue();
	}
	
	void sendStaffchat(ProxiedPlayer sender, String message) {
		Guild guild = jda.getGuildById(ChatBridgeUtils.staff_guild);
		TextChannel channel = guild.getTextChannelById(ChatBridgeUtils.staff_chat);
		LotusController lc = new LotusController();
		long timestamp = (System.currentTimeMillis() / 1000);
		String userId = lc.getPlayerData(sender, Playerdata.LotusChangeID);
		String role = lc.getPlayerData(sender, Playerdata.PlayerGroup);
		String server = ChatBridgeUtils.translateBCKeyToFancyName(sender.getServer().getInfo().getName());
		String res = "**[**" + role + " **|** <t:" + timestamp + ":f> **|** " + server + "**]** " + sender.getName() + " (" + userId + "): " + message;
		channel.sendMessage(res).queue();
	}
}