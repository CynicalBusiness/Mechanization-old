package me.capit.mechanization;

import me.capit.mechanization.factory.WorldFactory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MechaDataController implements Listener, CommandExecutor {
	Mechanization plugin;
	public MechaDataController(Mechanization plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
		return false;
	}
	
	@EventHandler
	public void activateFactory(PlayerInteractEvent e){
		if (e.getAction()!=Action.LEFT_CLICK_BLOCK) return;
		Block b = e.getClickedBlock();
		if (b.getType()==Material.CHEST){
			Chest chest = (Chest) b.getState();
			WorldFactory wf = WorldFactory.getFactoryAtChest(e.getItem(), chest);
			if (wf!=null && wf.valid() && wf.getInputRecipe()!=null && !wf.isRunning()
					&& wf.validFurnacesForRecipe(wf.getInputRecipe()) && wf.getFactory().applyActivatorEffects(e.getItem())){
				e.getPlayer().sendMessage(wf.getFactory().getColor()+wf.getFactory().getDisplayName()+ChatColor.GREEN+" started!");
				wf.activate();
				if (wf.getFactory().doesCaptureEvents()) e.setCancelled(true);
			}
		}
	}
	
}
