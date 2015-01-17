package me.capit.mechanization.parser;

import java.util.Random;

import me.capit.eapi.item.GameItem;
import me.capit.eapi.item.ItemHandler;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialParser {
	
	private String[] mats;
	private short[] dmgs;
	
	public MaterialParser(String attr){
		String[] raw = attr.split("\\|");
		mats = new String[raw.length];
		dmgs = new short[raw.length];
		for (int i=0; i<raw.length; i++){
			String[] data = raw[i].split("\\:");
			mats[i] = data[0];
			try {
				dmgs[i] = data.length>1 ? Short.parseShort(data[1]) : -1;
			} catch (NumberFormatException e){
				dmgs[i] = -1;
			}
		}
	}
	
	public int size(){
		return mats.length;
	}
	
	public short[] getData(){
		return dmgs;
	}
	
	public ItemStack[] getStacks(int[] amounts) throws IllegalArgumentException {
		if (amounts.length!=size()) throw new IllegalArgumentException("Amounts array be if length "+size()+"!");
		ItemStack[] stacks = new ItemStack[size()];
		for (int i=0; i<mats.length; i++){
			if (mats[i].startsWith("!")){
				GameItem item = ItemHandler.getItem(mats[i]);
				stacks[i] = item!=null ? item.getItemStack(amounts[i], dmgs[i]) : null;
			} else {
				try {
					stacks[i] = new ItemStack(Material.valueOf(mats[i]), amounts[i], dmgs[i]);
				} catch (IllegalArgumentException e){
					stacks[i] = null;
				}
			}
		}
		return stacks;
	}
	
	public ItemStack[] getStacks(int amount){
		int[] amounts = new int[size()];
		for (int i=0; i<amounts.length; i++){
			amounts[i] = amount;
		}
		return getStacks(amounts);
	}
	
	public ItemStack[] getStacks(){
		return getStacks(1);
	}
	
	public Material[] getMaterials(){
		ItemStack[] stacks = getStacks();
		Material[] mats = new Material[stacks.length];
		for (int i=0; i<stacks.length; i++){
			mats[i] = stacks[i].getType();
		}
		return mats;
	}
	
	public boolean inputValid(ItemStack stack, boolean ignoreAmount){
		ItemStack[] stacks = ignoreAmount ? getStacks() : getStacks(stack.getAmount());
		for (int i=0; i<stacks.length; i++){
			if (ItemHandler.stackEquals(stacks[i], stack, ignoreAmount, dmgs[i]<0)) return true;
		}
		return false;
	}
	
	public ItemStack getOutput(int amount){
		ItemStack[] stacks = getStacks(amount);
		Random rand = new Random();
		return stacks[rand.nextInt(stacks.length)];
	}
}
