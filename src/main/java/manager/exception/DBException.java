package manager.exception;

import manager.system.SelfXErrors;

public class DBException extends SMException{
	private static final long serialVersionUID = -5767088328193131363L;
	public DBException(SelfXErrors error, Object ...mes) {
		super(error, mes);
	}
}
