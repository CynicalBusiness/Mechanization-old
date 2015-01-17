package me.capit.mechanization;

import me.capit.eapi.data.DataModel;
import me.capit.mechanization.parser.MetaParser;

public interface Mechanized {
	
	/**
	 * Gets the name registered as a <b>unique</b> identifier for the Mechanized object.
	 * @return The unique name.
	 */
	public String getName();
	
	/**
	 * Gets the <i>display</i> name of the Mechanized object to display to the user.
	 * @return The display name.
	 */
	public default String getDisplayName(){
		return getMeta().getName();
	}
	
	public MetaParser getMeta();
	
	public DataModel getModel();
}
