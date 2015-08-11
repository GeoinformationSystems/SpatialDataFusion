package de.tudresden.gis.fusion.operation;

public class ProcessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ProcessException(ExceptionKey exceptionKey, String message, Throwable e) {
		super(exceptionKey.getMessage() + ": " + message, e);
	}
	
	public static enum ExceptionKey {
		
		PROCESS_EXCEPTION("An exception occurred during process execution"),
		INPUT_MISSING("At least one mandatory input is missing"),
		INPUT_NOT_ACCESSIBLE("At least one input is not accessible"),
		INPUT_NOT_APPLICABLE("At least one input is not applicable")
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
