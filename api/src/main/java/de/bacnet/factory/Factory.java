package de.bacnet.factory;

public interface Factory<T> {

	T create(Object... args);

}
