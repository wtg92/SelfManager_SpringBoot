package manager.exception;

import manager.system.SMError;

public class DBException extends SMException{

	private static final long serialVersionUID = -5767088328193131363L;
	
	public DBException(Object mes) {
		super(mes);
	}
	
	public DBException(Object mes, SMError error, boolean mesAppend) {
		super(mes,error,mesAppend);
	}
	
	public DBException(SMError error) {
		super(error);
	}
	
	public DBException(SMError error,Object mes) {
		super(error, mes);
	}
}
