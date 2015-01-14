package de.tudresden.gis.fusion.operation;

public class ProcessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ProcessException(ExceptionKey exceptionKey, Throwable e) {
		super(exceptionKey.getMessage(), e);
	}
	
	public ProcessException(ExceptionKey exceptionKey) {
		super(exceptionKey.getMessage());
	}
	
	public ProcessException(ExceptionKey exceptionKey, String message) {
		super(exceptionKey.getMessage() + ": " + message);
	}
	
	public static enum ExceptionKey {
		
		GENERAL_EXCEPTION(""),
		ACCESS_RESTRICTION(""),
		NO_APPLICABLE_INPUT("");
		
		private String message;
		private ExceptionKey(String message){
			this.message = message;
		}
		public String getMessage(){
			return message;
		}
	}

}
