package manager.util;

@FunctionalInterface
public interface ThrowablePredicate<T,E extends Exception> {
	public boolean test​(T t) throws E;
}
