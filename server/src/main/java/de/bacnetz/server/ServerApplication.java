package de.bacnetz.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <h1>Build order</h1>
 * <ol>
 * <li />common
 * <li />api
 * <li />bacnetz
 * <li />bacnetzmstp
 * <li />jsonrpc
 * <li />fatclient
 * <li />server (Takes very, very long to build because it packages the angular
 * app. It takes about 15 minutes.)
 * </ol>
 * 
 * <h1>Wireshark display filter bacnet ip</h1>
 * 
 * <pre>
 * -- bacnet without spam:
 * (bacnet || bvlc || bacapp) && !(bacapp.unconfirmed_service == 8) && !(bacapp.unconfirmed_service == 0)
 * 
 * -- only bacnet traffic
 * bacnet || bvlc || bacapp
 * 
 * -- https://www.thes4group.com/articles/tool-box-essentials-using-wireshark-to-troubleshoot-bacnet-ip-issues
 * 
 * -- filter out who-is
 * (bacnet || bvlc || bacapp) && !(bacapp.unconfirmed_service == 8)
 * 
 * -- filter out i-am
 * (bacnet || bvlc || bacapp) && !(bacapp.unconfirmed_service == 0)
 * </pre>
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "de.bacnetz")
@EnableAutoConfiguration
@EnableScheduling
public class ServerApplication {

    /**
     * <pre>
     * npm i
     * ng serve
     * http://localhost:4200/
     * </pre>
     * 
     * Gradle:
     * Uses the maven repository for some reason!
     * 
     * <pre>
     * gradle clean build
     * gradle publishToMavenLocal
     * gradle run
     * </pre>
     * 
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
