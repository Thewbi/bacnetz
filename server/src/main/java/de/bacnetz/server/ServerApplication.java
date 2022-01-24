package de.bacnetz.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <h1>Build order</h1>
 * <ol>
 * <li />common  		+ (publishing > publishToMavenLocal)
 * <li />api  			+ (publishing > publishToMavenLocal)
 * <li />bacnetz  		+ (publishing > publishToMavenLocal)
 * <li />bacnetzmstp  	+ (publishing > publishToMavenLocal)
 * <li />jsonrpc  		+ (publishing > publishToMavenLocal)
 * <li />fatclient  	+ (publishing > publishToMavenLocal)
 * <li />server			+ (publishing > publishToMavenLocal)
 * </ol>
 * 
 * <h1>Build order for the server</h1>
 * If you only want to build the server, the build order is:
 * <ol>
 * <li />common 		+ (publishing > publishToMavenLocal)
 * <li />api  			+ (publishing > publishToMavenLocal)
 * <li />bacnetz  		+ (publishing > publishToMavenLocal)
 * <li />server			+ (publishing > publishToMavenLocal)
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
 * In order to sniff BACnet/IP traffic, you have to have a router that allows to configure a monitoring
 * port. When you connect a network cable between that monitoring port and your PC on a second network adapter,
 * you can then open wireshark on that second network adapter. Wireshark will than receive all the 
 * sniffed traffic from the monitoring port and it can dissect the BACNet packets.
 * One router that allows this is the CISCO: ???
 * 
 * It is not possible to just open wireshark on the same ethernet adapter that the BACnetz server communicates over!
 * WireShark will not display any traffic at all! I do not know why!
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
 * Or altenatively, use the command line parameters
 * <pre>
 * --server.address=192.168.0.11 --bind.ip=192.168.0.11 --multicast.ip=192.168.0.255
 * </pre>
 * to override what is configured inside the properties file
 * 
 * <h1>Runnable jar</h1>
 * The gradle spring boot plugin is imported into the gradle file to build the runnable fat jar. 
 * The jar is placed into the folder: C:\aaa_se\bacnetz\server\build\libs. It is called server-0.0.1-SNAPSHOT.jar.
 * The jar is automatically build when the build-goal of the server gradle project is run.
 * 
 * <h1>Running</h1>
 * <pre>
 * java -jar server-0.0.1-SNAPSHOT.jar
 * java -jar server-0.0.1-SNAPSHOT.jar --server.address=192.168.0.11 --bind.ip=192.168.0.11 --multicast.ip=192.168.0.255
 * java -jar server-0.0.1-SNAPSHOT.jar --server.address=192.168.2.11 --bind.ip=192.168.2.11 --multicast.ip=192.168.2.255
 * </pre>
 * 
 * <h1>ERROR: javax.ws.rs.core.Application.getProperties() No such Method Error</h1>
 * <pre>
 * java.lang.NoSuchMethodError: javax.ws.rs.core.Application.getProperties()Ljava/util/Map;
 * </pre>
 * If you get this error in the server, when you open the angular app using your browser,
 * then you have two versions of jersey loaded by the JVM. In order to get rid of the old 
 * version, go to C:\Users\<your_user>\.gradle\caches\modules-2\files-2.1\javax.ws.rs and delete
 * all old versions from here. After that the error is gone!
 * 
 * <h1>COV</h1>
 * The server will only correctly respond to COV subscriptions to the close_state BinaryInput device!
 * No other COV is implemented yet! Use the swagger to trigger the REST API or send a request to the
 * REST API yourself for the /bacnet/api/device/toggle
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
