package de.bacnetz.server.configuration;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.stereotype.Component;

import de.bacnetz.server.cors.CorsFilter;
import de.bacnetz.server.resource.system.SystemInformationResource;
import de.bacnetz.server.resources.devices.DeviceResource;

@Component
@ApplicationPath("/bacnetz")
public class JerseyConfiguration extends ResourceConfig {

    /**
     * ctor
     */
    public JerseyConfiguration() {

        // CORS filter for angular
        register(CorsFilter.class);

        register(SystemInformationResource.class);
        register(DeviceResource.class);

        property(ServletProperties.FILTER_FORWARD_ON_404, true);
    }

}
