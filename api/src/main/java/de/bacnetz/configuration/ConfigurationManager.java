package de.bacnetz.configuration;

public interface ConfigurationManager {

    static final String LOCAL_IP_CONFIG_KEY = "local_ip";

    static final String PORT_CONFIG_KEY = "port";

    static final String MULTICAST_IP_CONFIG_KEY = "multicast_ip";

    // broadcast ip, has to match the subnet that the bacnet communication partners
    // are contained in
    static final String BACNET_MULTICAST_IP_DEFAULT_VALUE = "192.168.0.255";

    // default BACnet port
    // 0xBAC0 == 47808d
    // this value is overriden by the command line parameter 'port'
    static final int BACNET_PORT_DEFAULT_VALUE = 0xBAC0;

    void setProperty(String configKey, String value);

    String getPropertyAsString(String configKey);

    int getPropertyAsInt(String configKey);

    void dumpOptions();

}
