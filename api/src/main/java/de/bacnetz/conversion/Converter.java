package de.bacnetz.conversion;

public interface Converter<S, T> {

	void convert(S source, T target);

	T convert(S source);

}
