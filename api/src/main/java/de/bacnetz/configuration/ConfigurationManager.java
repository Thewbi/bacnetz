package de.bacnetz.configuration;

public interface ConfigurationManager {

    static final String LOCAL_IP_CONFIG_KEY = "local_ip";

    static final String PORT_CONFIG_KEY = "port";

    String getPropertyAsString(String configKey);

    int getPropertyAsInt(String configKey);

    void dumpOptions();

}
