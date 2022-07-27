package com.erdi.unsignedmessages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = "UnsignedMessages", version = "1.0.0")
@Description("A lightweight plugin which disables chat reporting")
@Author("Erdi__")
@ApiVersion(ApiVersion.Target.v1_19)
@Commands(
		@Command(name = "unsignedmessagesreload", permission = "unsignedmessages.reload", usage = "/<command>")
)
@Permissions(
		@Permission(name = "unsignedmessages.reload", desc = "Permission for /unsignedmessagesreload")
)
public class UnsignedMessages extends JavaPlugin implements Listener, CommandExecutor {
	private boolean explicitlyDisabled = false;
	private String format = "";
	private boolean blankFormat = true;
	
	@Override
	public void onLoad() {
		if(!serverIsValid()) {
			getLogger().warning("Unsupported server version: " + getServerVersion());
			getLogger().warning("This plugin was created for versions 1.19.1 and above");
			
			explicitlyDisabled = true;
		} else
			getLogger().info("Detected server version: " + getServerVersion());
	}
	
	@Override
	public void onEnable() {
		if(explicitlyDisabled) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		getServer().getPluginManager().registerEvents(this, this);
		
		saveDefaultConfig();
		
		getCommand("unsignedmessagesreload").setExecutor(this);;
		
		reload();
	}
	
	@Override
	public void onDisable() {
		if(explicitlyDisabled)
			return;
	}
	
	public void reload() {
		reloadConfig();

		format = getConfig().getString("chat-format");
		blankFormat = format.isEmpty();
		
		getLogger().info("Format: " + (blankFormat ? "blank" : "`" + format.replaceAll("`", "'") + "`"));
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		if(!blankFormat)
			event.setFormat(format);
		else
			event.setMessage(ChatColor.RESET + event.getMessage());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		getLogger().info("Reloading config.yml...");
		
		reload();

		getLogger().info("Reloaded config.yml");
		sender.sendMessage(ChatColor.GREEN + "Reloaded config.yml!");
		
		return true;
	}
	
	public boolean serverIsValid() {
		try {
			String version = getServerVersion();
			int major = Integer.parseInt(version.split("\\.")[1]);
			int minor;
			
			try {
				minor = Integer.parseInt(version.split("\\.")[2]);
			} catch(Exception e) {
				minor = 0;
			}
			
			return (major == 19 && minor >= 1) || major > 19;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getServerVersion() {
		String version = Bukkit.getBukkitVersion();
		
		try {
			version = version.split("-")[0];
		} catch(Exception e) {}
		
		return version;
	}
}
