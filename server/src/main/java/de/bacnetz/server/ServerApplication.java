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
 * <h1>Build using Gradle</h1>
 * Make sure you do have unrestricted access to the internet because Gradle needs to 
 * download dependencies.
 * Run the Gradle build task for all projects in the order outlined above.
 * In eclipse, you open the "Gradle Tasks" view and run build tasks from there.
 * 
 * After the server was build, the result is the file:
 * C:\aaa_se\bacnetz\server\build\libs\server-0.0.1-SNAPSHOT.jar.
 * 
 * <h1>Running the server</h1>
 * <pre>
 * java -jar server-0.0.1-SNAPSHOT.jar
 * java -jar server-0.0.1-SNAPSHOT.jar --server.address=192.168.0.11 --bind.ip=192.168.0.11 --multicast.ip=192.168.0.255
 * java -jar server-0.0.1-SNAPSHOT.jar --server.address=192.168.0.108 --bind.ip=192.168.0.108 --multicast.ip=192.168.0.255
 * </pre>
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
 * <h1>Running the server</h1>
 * Edit src/main/resources/application.properties and set your local IP address into
 * both of the two properties: server.address and bind.ip
 * 
 * <h1>Runnable jar</h1>
 * The gradle spring boot plugin is imported into the gradle file to build the runnable fat jar. 
 * The jar is placed into the folder: C:\aaa_se\bacnetz\server\build\libs. It is called server-0.0.1-SNAPSHOT.jar.
 * Running: java -jar server-0.0.1-SNAPSHOT.jar
 * java -jar server-0.0.1-SNAPSHOT.jar --server.address=192.168.0.11 --bind.ip=192.168.0.11 --multicast.ip=192.168.0.255
 * 
 * 
 * ERROR:
 * <pre>
 * java.lang.NoSuchMethodError: javax.ws.rs.core.Application.getProperties()Ljava/util/Map;
 * </pre>
 * If you get this error in the server, when you open the angular app using your browser,
 * then you have two versions of jersey loaded by the JVM. In order to get rid of the old 
 * version, go to C:\Users\<your_user>\.gradle\caches\modules-2\files-2.1\javax.ws.rs and delete
 * all old versions from here. After that the error is gone! 
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
