package manager.util;

/* 个人认为是java的问题，不是我的问题，导致出现这个类 */
@FunctionalInterface
public interface BiThrowableSupplier<T,E extends Exception,M extends Exception> {
	public T get() throws E,M;
}
