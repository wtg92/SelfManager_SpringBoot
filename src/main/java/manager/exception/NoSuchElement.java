package manager.exception;

import manager.system.NoSuchElementType;

public class NoSuchElement extends SMException {

	private static final long serialVersionUID = -3480603015770152898L;
	public NoSuchElementType type = NoSuchElementType.UNDECIDED;
	
	public NoSuchElement(){};
	public NoSuchElement(NoSuchElementType type) {
		this.type = type;
	};
}
