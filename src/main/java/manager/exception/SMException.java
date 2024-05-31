package manager.exception;

import manager.system.SMError;

public abstract class SMException extends RuntimeException{

	private static final long serialVersionUID = 3135260691229951722L;
	public SMError type = null;

	public Object[] params = new Object[]{};

	public SMException() {}
	
	public SMException(Object mes) {
		super(mes.toString());
	}
	
	public SMException(SMError error,Object... mes) {
		this.params = mes;
		this.type = error;
	}
}
