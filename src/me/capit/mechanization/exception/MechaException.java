package me.capit.mechanization.exception;

import me.capit.mechanization.Mechanized;

public class MechaException extends Exception {
	private static final long serialVersionUID = 5188890591462592532L;
	
	public MechaException(Mechanized mechanized, String msg){
		super(mechanized.getName()+": "+msg);
	}
}
