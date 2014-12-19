package me.capit.mechanization;

public interface Mechanized {
	
	/**
	 * Gets the name registered as a <b>unique</b> identifier for the Mechanized object.
	 * @return The unique name.
	 */
	public abstract String getName();
	
	/**
	 * Gets the <i>display</i> name of the Mechanized object to display to the user.
	 * @return The display name.
	 */
	public abstract String getDisplayName();
	
}
