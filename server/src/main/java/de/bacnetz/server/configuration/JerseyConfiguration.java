package de.bacnetz.server.configuration;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.bacnetz.server.cors.CorsFilter;
import de.bacnetz.server.resources.devices.DeviceResource;
import de.bacnetz.server.resources.system.SystemInformationResource;
//import io.swagger.jaxrs.config.BeanConfig;
//import io.swagger.jaxrs.listing.ApiListingResource;
//import io.swagger.jaxrs.listing.SwaggerSerializers;

@Component
@ApplicationPath("/bacnetz/api")
public class JerseyConfiguration extends ResourceConfig {

    @Value("${spring.jersey.application-path}")
    private String apiPath;

    @Value("${springfox.documentation.swagger.v2.path}")
    private String swagger2Endpoint;

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
    	
    	// the swagger dependency io.swagger:swagger-jaxrs:1.6.4 introduces a transitive dependency
    	// to jsr311-api which conflicts with the newer jersey version 2 used in the rest of this
    	// application so we cannot use it.
//        configureSwagger();
    }

//    /**
 //    * the swagger dependency io.swagger:swagger-jaxrs:1.6.4 introduces a transitive dependency
	// * to jsr311-api which conflicts with the newer jersey version 2 used in the rest of this
	// * application so we cannot use it.
    // *
//     * https://stackoverflow.com/questions/35966204/how-to-integrate-swagger-with-jersey-spring-boot
//     * https://stackoverflow.com/questions/37640863/springfox-swagger-no-api-docs-with-spring-boot-jersey-and-gradle/42228055#42228055
//     *
//     * Test: http://localhost:8080/bacnetz/api/swagger.json
//     */
//    private void configureSwagger() {
//
//        // Available at localhost:port/swagger.json
//        register(ApiListingResource.class);
//        register(SwaggerSerializers.class);
//
//        final BeanConfig config = new BeanConfig();
//        config.setConfigId("Bacnetz");
//        config.setTitle("Bacnetz");
//        config.setVersion("v1");
//        config.setContact("Me");
//        config.setSchemes(new String[] { "http", "https" });
//
//        // this is where the swagger.json file will be served
//        // you have to put this path into index.html of src/main/resource/static/swagger
//        config.setBasePath(this.apiPath);
//        // this package path has to be the top-level java package of the Jersey
//        // resources that you want to appear in the swagger.json
//        config.setResourcePackage("de.bacnetz.server.resources");
//        config.setPrettyPrint(true);
//
//        // this tries to scan the entire angular app and fails for each file!
////        config.setScan(true);
//    }

}
