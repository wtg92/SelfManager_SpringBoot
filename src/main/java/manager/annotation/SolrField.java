package manager.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value=ElementType.FIELD)
public @interface SolrField {
	
}
