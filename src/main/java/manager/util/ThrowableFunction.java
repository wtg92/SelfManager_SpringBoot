package manager.util;

@FunctionalInterface
public interface ThrowableFunction<T,R,E extends Exception> {
	public R apply(T t) throws E;
}
