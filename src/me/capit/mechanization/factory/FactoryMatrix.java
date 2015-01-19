package me.capit.mechanization.factory;

import java.util.ArrayList;
import java.util.List;

import me.capit.eapi.data.Child;
import me.capit.eapi.data.DataModel;
import me.capit.eapi.item.MaterialParser;
import me.capit.eapi.math.Vector3;
import me.capit.mechanization.exception.MechaException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FactoryMatrix {
	private final Vector3 dims,chestLoc;
	private final DataModel[][][] matrix;
	
	public FactoryMatrix(MechaFactory factory) throws MechaException {
		Child child = factory.getModel().findFirstChild("matrix");
		if (child==null || !(child instanceof DataModel)) throw new MechaException(factory, "Matrix element is missing or not of correct type.");
		DataModel model = (DataModel) child;
		
		try {
			dims = new Vector3(
					Double.parseDouble(model.getAttribute("width").getValueString()),
					Double.parseDouble(model.getAttribute("height").getValueString()),
					Double.parseDouble(model.getAttribute("depth").getValueString()));
			chestLoc = new Vector3(
					Double.parseDouble(model.getAttribute("chestX").getValueString()),
					Double.parseDouble(model.getAttribute("chestY").getValueString()),
					Double.parseDouble(model.getAttribute("chestZ").getValueString()));
		} catch (NullPointerException | IllegalArgumentException e){
			throw new MechaException(factory, "Matrix definition is not valid.");
		}
		
		MechaException matrixErr = new MechaException(factory, "Matrix is not of correct size: "+dims.x+","+dims.y+","+dims.z);
		try {
			matrix = new DataModel[(int) dims.y][(int) dims.z][(int) dims.x];
			//for (int j = 0; j<dims.y; j++) for (int k = 0; k<dims.z; k++) for (int l = 0; l<dims.x; l++) 
			//	matrix[j][k][l] = (DataModel) ((DataModel) ((DataModel) model.getChildren().get(j)).getChildren().get(k)).getChildren().get(l);
			
			for (int j=0; j<dims.y; j++){
				DataModel[][] y = new DataModel[(int) dims.z][(int) dims.x];
				DataModel cy = (DataModel) model.getChildren().get(j);
				for (int k=0; k<dims.z; k++){
					DataModel[] z = new DataModel[(int) dims.x];
					DataModel cz = (DataModel) cy.getChildren().get(k);
					for (int l=0; l<dims.x; l++){
						DataModel cx = (DataModel) cz.getChildren().get(l);
						z[l] = cx;
					}
					y[k] = z;
				}
				matrix[j] = y;
			}
				
			if (!isValid(Material.CHEST, (byte) 0, chestLoc)) throw new MechaException(factory, "Chest is not in the correct location.");
		} catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e){
			throw matrixErr;
		}
		if (glitchInMatrix()) throw matrixErr;

	}
	
	private boolean glitchInMatrix(){ // Huehuehue.
		for (DataModel[][] e2 : matrix) for (DataModel[] e1 : e2) for (DataModel e : e1) if (e==null) return true;
		return false;
	}
	
	public DataModel getElementAtPosition(Vector3 pos){
		return matrix[(int) pos.y][(int) pos.z][(int) pos.x];
	}
	
	public MaterialParser getParserAtPosition(Vector3 pos){
		DataModel ise = getElementAtPosition(pos);
		return new MaterialParser(ise.hasAttribute("material") ? ise.getAttribute("material").getValueString() : null);
	}
	
	public ItemStack[] getStacksAtPosition(Vector3 pos){
		return getParserAtPosition(pos).getStacks();
	}
	
	public boolean isValid(ItemStack stack, Vector3 pos){
		return getParserAtPosition(pos).isInput(stack, true);
	}
	public boolean isValid(Material mat, byte data, Vector3 pos){
		return isValid(new ItemStack(mat, 1, data), pos);
	}
	
	public List<Vector3> getLocationsOfMaterial(Material mat, int data){
		List<Vector3> ps = new ArrayList<Vector3>();
		for (int y = 0; y<dims.y; y++){ for (int z = 0; z<dims.z; z++){ for (int x = 0; x<dims.x; x++){
			Vector3 curpos = new Vector3(x,y,z);
			MaterialParser parser = getParserAtPosition(curpos);
			if (parser.size()==1 && parser.getStacks()[0].getType()==mat) ps.add(curpos);
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
