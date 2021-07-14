package manager.util;

@FunctionalInterface
public interface ThrowableSupplier<T,E extends Exception> {
	public T get() throws E;
}
