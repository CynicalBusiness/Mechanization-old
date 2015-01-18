package me.capit.mechanization.factory;

import java.io.Serializable;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.capit.eapi.data.DataModel;
import me.capit.eapi.math.Vector3;
import me.capit.mechanization.Mechanized;
import me.capit.mechanization.exception.MechaException;
import me.capit.mechanization.parser.FactoryDataParser;
import me.capit.mechanization.parser.MetaParser;
import me.capit.mechanization.recipe.MechaFactoryRecipe;

public class MechaFactory implements Mechanized, Serializable {
	private static final long serialVersionUID = -6430047152764487993L;
	private String name = "invalid";
	private final DataModel model;
	private final MetaParser meta;
	private final FactoryDataParser data;
	private final FactoryMatrix matrix;
	
	public MechaFactory(DataModel model) throws MechaException {
		if (!model.getName().equals("factory")) throw new MechaException(this, "Model name is not 'factory'!");
		if (!model.hasAttribute("name")) throw new MechaException(this, "Name attribute is missing.");
		name = model.getAttribute("name").getValueString();
		this.model = model;

		meta = new MetaParser(this);
		data = new FactoryDataParser(this);
		matrix = new FactoryMatrix(this);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public MetaParser getMeta(){
		return meta;
	}
	
	public FactoryDataParser getData(){
		return data;
	}
	
	@Override
	public DataModel getModel(){
		return model;
	}
	
	public FactoryMatrix getMatrix(){
		return matrix;
	}
	
	public boolean validActivator(ItemStack activator){
		return data.getActivator().isInput(activator, true);
	}
	
	public boolean applyActivatorEffects(ItemStack activator){
		if (!validActivator(activator) || activator.getAmount()<data.getActivatorConsumption()) return false;
		activator.setDurability((short) (activator.getDurability()+data.getActivatorDamage()));
		if (activator.getAmount()==data.getActivatorConsumption()) activator.setType(Material.AIR);
		else activator.setAmount(activator.getAmount()-data.getActivatorConsumption());
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public boolean blockMatchesAtLocation(Block b, Vector3 pos){
		return matrix.isValid(b.getType(), b.getData(), pos);
	}
	
	public boolean validForLocation(Location origin, Vector3 relativity){
		for (int x=0; x<matrix.getDims().x; x++){
			for (int y=0; y<matrix.getDims().y; y++){
				for (int z=0; z<matrix.getDims().z; z++){
					Vector3 pos = new Vector3(x,y,z);
					Vector3 relpos = pos.multiply(relativity);
					Block b = origin.getWorld().getBlockAt(
							origin.getBlockX()+(int) relpos.x, 
							origin.getBlockY()+(int) relpos.y, 
							origin.getBlockZ()+(int) relpos.z);
					if (!blockMatchesAtLocation(b,pos)) return false;
				}
			}
		}
		return true;
	}
	
	public MechaFactoryRecipe getRecipeFromInput(Inventory input){
		for (MechaFactoryRecipe recipe : data.getRecipes()){
			if (recipe!=null && recipe.inventoryMatchesInput(input)) return recipe;
		}
		return null;
	}
	
	public List<Vector3> getFurnaceLocations(){
		return matrix.getLocationsOfMaterial(Material.FURNACE, 0);
	}
}
