package manager.exception;

import manager.system.SelfXErrors;

public class LogicException extends SMException{

	private static final long serialVersionUID = 8456552022232062450L;
	public LogicException(SelfXErrors error, Object... params) {
		super(error, params);
	}

	public LogicException(String msg) {
		super(SelfXErrors.COMMON, msg);
	}
}
