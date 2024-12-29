package utils;

@FunctionalInterface
public interface FunctionWithException<T, V, E extends Exception> {
    V apply(T t) throws E;
}
