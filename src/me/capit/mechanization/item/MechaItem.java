package me.capit.mechanization.item;

import java.io.Serializable;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jdom2.Element;

import me.capit.mechanization.Mechanized;
import me.capit.mechanization.exception.MechaException;

public class MechaItem implements Mechanized, Serializable {
	private static final long serialVersionUID = 3159299245146020959L;
	private final String name, displayName, lore;
	private final Material baseMaterial;
	private final int baseData;
	
	public MechaItem(Element element) throws MechaException {
		if (!element.getName().equals("item")) throw new MechaException().new InvalidElementException("item", element.getName());
		if (element.getAttribute("name")==null) throw new MechaException().new MechaNameNullException();
		name = element.getAttributeValue("name");
		try {
			Element meta = element.getChild("meta");
			if (meta.getAttribute("display")!=null) displayName = meta.getAttributeValue("display"); else throw null;
			if (meta.getAttribute("lore")!=null) lore = meta.getAttributeValue("lore"); else throw null;
			
			Element data = element.getChild("data");
			if (data.getAttribute("material")!=null) baseMaterial = Material.valueOf(data.getAttributeValue("material")); else throw null;
			baseData = data.getAttribute("data") !=null ? Integer.parseInt(data.getAttributeValue("data")) : 0;
		} catch (NullPointerException | IllegalArgumentException e){
			e.printStackTrace();
			throw new MechaException().new MechaAttributeInvalidException("Null or invalid tag/attribute value for "+name+". ");
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}
	
	public String getLore(){
		return ChatColor.translateAlternateColorCodes('&', lore);
	}
	
	public Material getBaseMaterial(){
		return baseMaterial;
	}
	
	public int getBaseData(){
		return baseData;
	}
	
	public ItemStack getItemStack(){return getItemStack(1);}
	public ItemStack getItemStack(int amount){
		ItemStack is = new ItemStack(getBaseMaterial(), amount);
		is.setDurability((short) getBaseData());
			
		ItemMeta im = is.getItemMeta();
		im.setLore(Arrays.asList(getLore()));
		im.setDisplayName(getDisplayName());
			
		is.setItemMeta(im);
		return is;
	}
}
