package de.bacnetz.fatclient;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * This class allows the usage of spring context beans within classes that are
 * themselves not created from within the spring context. One such example is
 * the Apach Pivot FatClient app class that uses button handlers that are
 * contained in the spring application context.
 */
@Component
public class ApplicationContextProvider {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(final ApplicationContext applicationContext) {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

}
