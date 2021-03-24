package de.bacnetz.configuration;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.utils.NetworkUtils;

public class DefaultConfigurationManager implements ConfigurationManager {

    private static final Logger LOG = LogManager.getLogger(DefaultConfigurationManager.class);

    private final Map<String, Object> properties = new HashMap<>();

    /**
     * ctor
     */
    public DefaultConfigurationManager() {

        // add local_ip default value
        properties.computeIfAbsent(LOCAL_IP_CONFIG_KEY, k -> {
            try {
                return NetworkUtils.retrieveLocalIP();
            } catch (UnknownHostException | SocketException e) {
                LOG.error(e.getMessage(), e);
            }
            return "error";
        });

        // add multicast_ip default value
        properties.computeIfAbsent(MULTICAST_IP_CONFIG_KEY, k -> {
            return BACNET_MULTICAST_IP_DEFAULT_VALUE;
        });

        // add port default value
        properties.computeIfAbsent(PORT_CONFIG_KEY, k -> {
            return BACNET_PORT_DEFAULT_VALUE;
        });
    }

    @Override
    public void setProperty(final String key, final String value) {
        properties.put(key, value);
    }

    @Override
    public String getPropertyAsString(final String key) {
        final Object value = properties.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    @Override
    public int getPropertyAsInt(final String key) {
        final Object value = properties.get(key);
        if (value == null) {
            throw new RuntimeException("Configuration property '" + key + "' not present!");
        }
        return ((Integer) value).intValue();
    }

    public void updateWithCommandLine(final CommandLine commandLine) {

        if (commandLine.hasOption(LOCAL_IP_CONFIG_KEY)) {
            properties.put(LOCAL_IP_CONFIG_KEY, commandLine.getOptionValue(LOCAL_IP_CONFIG_KEY));
        }
        if (commandLine.hasOption(MULTICAST_IP_CONFIG_KEY)) {
            properties.put(MULTICAST_IP_CONFIG_KEY, commandLine.getOptionValue(MULTICAST_IP_CONFIG_KEY));
        }
    }

    @Override
    public void dumpOptions() {
        LOG.info("---------------------------------------------------------------------------------");
        LOG.info("Properties are:");
        LOG.info("---------------------------------------------------------------------------------");
        properties.entrySet().stream().forEach(entry -> {
            LOG.info(entry.getKey() + ": " + entry.getValue());
        });
        LOG.info("---------------------------------------------------------------------------------");
    }

}
