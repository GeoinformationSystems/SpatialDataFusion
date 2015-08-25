package de.tudresden.gis.fusion.operation;

public class ProcessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ProcessException(ExceptionKey exceptionKey, String message, Throwable e) {
		super(exceptionKey.getMessage() + ": " + message, e);
	}
	
	public ProcessException(ExceptionKey exceptionKey, String message) {
		super(exceptionKey.getMessage() + ": " + message);
	}
	
	public static enum ExceptionKey {
		
		PROCESS_EXCEPTION("An exception occurred during process execution"),
		INPUT_MISSING("Mandatory input is missing"),
		INPUT_NOT_ACCESSIBLE("Input is not accessible"),
		INPUT_NOT_APPLICABLE("Input is not applicable")
		;
		
		String msg;
		
		private ExceptionKey(String msg){
			this.msg = msg;
		}
		
		public String getMessage(){
			return msg;
		}
		
	}

}
