package de.bacnetz.server.configuration;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.stereotype.Component;

import de.bacnetz.server.cors.CorsFilter;
import de.bacnetz.server.resource.system.SystemInformationResource;
import de.bacnetz.server.resources.devices.DeviceResource;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

@Component
@ApplicationPath("/bacnetz/api")
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

    @PostConstruct
    public void init() {
        // Register components where DI is needed
        this.configureSwagger();
    }

    /**
     * https://stackoverflow.com/questions/35966204/how-to-integrate-swagger-with-jersey-spring-boot
     * https://stackoverflow.com/questions/37640863/springfox-swagger-no-api-docs-with-spring-boot-jersey-and-gradle/42228055#42228055
     *
     * Test: http://localhost:8080/basic/api/swagger.json
     */
    private void configureSwagger() {

        // Available at localhost:port/swagger.json
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        final BeanConfig config = new BeanConfig();
        config.setConfigId("JerseyConfiguration");
        config.setTitle("JerseyConfiguration");
        config.setVersion("v1");
        config.setContact("Me");
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath("/bacnetz/api");

        // this package path has to be the top-level java package of the Jersey
        // resources that you
        // want to appear in the swagger.json
        config.setResourcePackage("de.bacnetz.server");
        config.setPrettyPrint(true);
        config.setScan(true);
    }

}
