package manager.exception;

import manager.system.SMError;

public abstract class SMException extends RuntimeException{

	private static final long serialVersionUID = 3135260691229951722L;
	public SMError type = null;
	
	
	public SMException() {}
	
	public SMException(Object mes) {
		super(mes.toString());
	}
	
	public SMException(SMError error,Object mes) {
		this(mes.toString(), error,true);
	}
	
	public SMException(Object mes, SMError error, boolean mesAppend) {
		this(mesAppend ? error.getDescription() +"   "+ mes : mes + "   "+ error.getDescription());
		this.type = error;
	}
	
	public SMException(SMError error) {
		this(error.getDescription());
		this.type = error;
	}

}
