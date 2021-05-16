package de.bacnetz.conversion;

public interface Converter<S, T> {

    T convert(S source);

    void convert(S source, T target);

}
