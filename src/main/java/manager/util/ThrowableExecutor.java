package manager.util;

@FunctionalInterface
public interface ThrowableExecutor<E extends Exception> {
	void execute() throws E;
}
