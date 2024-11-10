package manager.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Target(value=ElementType.TYPE)
public @interface SolrEntity {

	String[] name();
	
	/**
	 * 默认为true  除类名之外的
	 */
	boolean isAdditional() default true;
	
}
