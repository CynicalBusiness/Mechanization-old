package me.capit.mechanization;

import me.capit.mechanization.factory.WorldFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class MechaDataController implements Listener, CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
		return false;
	}
	
	@EventHandler
	public void activateFactory(PlayerInteractEvent e){
		Block b = e.getClickedBlock();
		if (b instanceof Chest){
			Bukkit.getServer().getLogger().info("Player clicked chest!");
			Chest chest = (Chest) b;
			WorldFactory wf = WorldFactory.getFactoryAtChest(e.getItem(), chest);
			if (wf!=null && wf.valid()){
				e.getPlayer().sendMessage(ChatColor.GREEN+"Factory started!");
				if (wf.getFactory().doesCaptureEvents()) e.setCancelled(true);
			}
		}
	}
	
}
