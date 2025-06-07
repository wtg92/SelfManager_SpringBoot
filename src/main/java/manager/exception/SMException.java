package manager.exception;

import manager.system.SelfXErrors;

import java.io.Serializable;

public abstract class SMException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 3135260691229951722L;
	public SelfXErrors type = null;

	public Object[] params = new Object[]{};

	public SMException() {}
	
	public SMException(Object mes) {
		super(mes.toString());
	}
	
	public SMException(SelfXErrors error, Object... mes) {
		this.params = mes;
		this.type = error;
	}
}
