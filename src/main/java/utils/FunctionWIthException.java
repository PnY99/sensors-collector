package utils;

@FunctionalInterface
public interface FunctionWIthException<T, V, E extends Exception> {
    V apply(T t) throws E;
}
