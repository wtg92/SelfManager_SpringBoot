package manager.exception;

import manager.system.SMError;

public class LogicException extends SMException{

	private static final long serialVersionUID = 8456552022232062450L;

	public LogicException(Object mes) {
		super(mes);
	}
	
	public LogicException(Object mes, SMError error, boolean mesAppend) {
		super(mes,error,mesAppend);
	}
	
	public LogicException(SMError error) {
		super(error);
	}
	
	public LogicException(SMError error,Object mes) {
		super(error, mes);
	}
}
