package de.bacnetz.factory;

public interface Factory<T> {

	T create(Object... args);

}
