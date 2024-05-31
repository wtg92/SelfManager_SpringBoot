package manager.exception;

import manager.system.SMError;

public class LogicException extends SMException{

	private static final long serialVersionUID = 8456552022232062450L;
	public LogicException(SMError error,Object... params) {
		super(error, params);
	}

	public LogicException(String msg) {
		super(SMError.COMMON, msg);
	}
}
