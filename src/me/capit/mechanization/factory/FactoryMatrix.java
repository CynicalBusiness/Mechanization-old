package me.capit.mechanization.factory;

import java.util.ArrayList;
import java.util.List;

import me.capit.eapi.data.DataModel;
import me.capit.eapi.math.Vector3;
import me.capit.mechanization.exception.MechaException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FactoryMatrix {
	private final Vector3 dims,chestLoc;
	private final DataModel[][][] matrix;
	
	public FactoryMatrix(DataModel model) throws NullPointerException, IllegalArgumentException, MechaException{
		if (!model.getName().equals("matrix")) throw new MechaException().new InvalidElementException("matrix", model.getName());
		
		dims = new Vector3(
				Double.parseDouble(model.getAttribute("width").getValueString()),
				Double.parseDouble(model.getAttribute("height").getValueString()),
				Double.parseDouble(model.getAttribute("depth").getValueString()));
		chestLoc = new Vector3(
				Double.parseDouble(model.getAttribute("chestX").getValueString()),
				Double.parseDouble(model.getAttribute("chestY").getValueString()),
				Double.parseDouble(model.getAttribute("chestZ").getValueString()));
		
		try {
			matrix = new DataModel[(int) dims.y][(int) dims.z][(int) dims.x];
			for (int j = 0; j<dims.y; j++) for (int k = 0; k<dims.z; k++) for (int l = 0; l<dims.x; l++) 
				matrix[j][k][l] = (DataModel) ((DataModel) ((DataModel) model.getChildren().get(j)).getChildren().get(k)).getChildren().get(l);
			
			DataModel chest = matrix[(int) chestLoc.y][(int) chestLoc.z][(int) chestLoc.x];
			if (!chest.getAttribute("material").getValueString().equals("CHEST")) throw null;

			if (glitchInMatrix()) throw new ArrayIndexOutOfBoundsException();
		} catch (ArrayIndexOutOfBoundsException e){
			throw new MechaException("Format did not match dims.");
		}
	}
	
	private boolean glitchInMatrix(){ // Huehuehue.
		for (DataModel[][] e2 : matrix) for (DataModel[] e1 : e2) for (DataModel e : e1) if (e==null) return true;
		return false;
	}
	
	public boolean elementAtPositionRequriesData(Vector3 pos){
		DataModel e = getElementAtPosition(pos);
		return e.getAttribute("data")!=null;
	}
	
	public DataModel getElementAtPosition(Vector3 pos){
		return matrix[(int) pos.y][(int) pos.z][(int) pos.x];
	}
	
	public Material getMaterialAtPosition(Vector3 pos){
		DataModel ise = getElementAtPosition(pos);
		try {
			return Material.valueOf(ise.getAttribute("material").getValueString());
		} catch (NullPointerException e){
			return null;
		} catch (IllegalArgumentException e){
			e.printStackTrace();
			return Material.AIR;
		}
	}
	
	public short getDurabilityAtPosition(Vector3 pos){
		DataModel ise = getElementAtPosition(pos);
		return elementAtPositionRequriesData(pos) ? Short.parseShort(ise.getAttribute("data").getValueString()) : -1;
	}
	
	public ItemStack getItemStackAtPosition(Vector3 pos){
		try {
			ItemStack is = new ItemStack(getMaterialAtPosition(pos));
			if (getDurabilityAtPosition(pos)>-1) is.setDurability(getDurabilityAtPosition(pos));
			return is;
		} catch (NullPointerException e){
			return null;
		}
	}
	
	public List<Vector3> getLocationsOfMaterial(Material mat, int data){
		List<Vector3> ps = new ArrayList<Vector3>();
		for (int y = 0; y<dims.y; y++){ for (int z = 0; z<dims.z; z++){ for (int x = 0; x<dims.x; x++){
			Vector3 curpos = new Vector3(x,y,z);
			if (getMaterialAtPosition(curpos)!=mat) continue;
			if (getDurabilityAtPosition(curpos)>-1 && data==getDurabilityAtPosition(curpos)) continue;
			ps.add(curpos);
		}}}
		return ps;
	}
	
	public Vector3 getDims(){
		return dims;
	}
	
	public Vector3 getChestLocation(){
		return chestLoc;
	}
}
