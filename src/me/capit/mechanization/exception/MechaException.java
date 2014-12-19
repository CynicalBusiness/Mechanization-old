package me.capit.mechanization.exception;

public class MechaException extends Exception {
	private static final long serialVersionUID = 5188890591462592532L;
	
	public MechaException(String msg){
		super(msg);
	}
	public MechaException(){
		super("An exception with Mechanization occured:");
	}
	
	public class InvalidElementException extends MechaException {
		private static final long serialVersionUID = 6003698563802556281L;
		final String expected,got;
		public InvalidElementException(String expected, String got){
			this.expected=expected; this.got=got;
		}
		@Override
		public String getLocalizedMessage(){
			return "Got bad element: Expected '"+expected+"' but got '"+got+"'!";
		}
	}
	
	public class MechaNameNullException extends MechaException {
		private static final long serialVersionUID = 8523328478693236410L;
	}
	
	public class MechaAttributeInvalidException extends MechaException {
		private static final long serialVersionUID = -7959227343391758901L;
		public MechaAttributeInvalidException(String msg){super(msg);}
	}
	
}
