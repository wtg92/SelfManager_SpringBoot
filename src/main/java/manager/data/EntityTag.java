package manager.data;

import java.util.Objects;

public class EntityTag implements Cloneable{
	
	public String name;
	public Boolean createdBySystem;
	@Override
	public String toString() {
		return String.format(" {name:'%s', createdBySystem:'%s}", name, createdBySystem);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EntityTag entityTag)) return false;
        return Objects.equals(name, entityTag.name) && Objects.equals(createdBySystem, entityTag.createdBySystem);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, createdBySystem);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getCreatedBySystem() {
		return createdBySystem;
	}

	public void setCreatedBySystem(Boolean createdBySystem) {
		this.createdBySystem = createdBySystem;
	}
}
