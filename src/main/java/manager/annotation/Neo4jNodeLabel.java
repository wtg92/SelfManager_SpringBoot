package manager.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 先不用 默认的Label就是类名
 * @author 王天戈
 *
 */
@Target(value=ElementType.TYPE)
public @interface Neo4jNodeLabel {

	String[] name();
	
	/**
	 * 默认为true  除类名之外的
	 */
	boolean isAddtional() default true;
	
}
