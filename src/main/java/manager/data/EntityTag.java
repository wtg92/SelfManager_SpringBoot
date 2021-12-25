package manager.data;

public class EntityTag implements Cloneable{
	
	public String name;
	public Boolean createdBySystem;
	@Override
	public String toString() {
		return String.format(" {name:'%s', createdBySystem:'%s}", name, createdBySystem);
	}
	
	public EntityTag() {}

	public EntityTag(String name, Boolean createdBySystem) {
		super();
		this.name = name;
		this.createdBySystem = createdBySystem;
	}
	
	@Override
	public EntityTag clone() {
		try {
			return (EntityTag) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
