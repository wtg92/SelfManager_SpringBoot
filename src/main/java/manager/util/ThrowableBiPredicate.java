package manager.util;

@FunctionalInterface
public interface ThrowableBiPredicate<T,M,E extends Exception> {
	public boolean test​(T t,M m) throws E;
}
