package org.skycastle.flowlang;

/**
 *
 */
public interface FuncType<T> {
    public T invoke(Object ... parameters);
}
