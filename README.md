# bacnetz
BACNET Test Implementation

Tests for getting to know the BACNET specification.

## What is bacnetz?
bacnetz is written in Java. It uses Java-UDP-Sockets and only adds a BACNET stack on top of the UDP infrastructure of the host operating system. 
It basically is a test for the BACNET over IP subset of the BACNET standard.
At this point, bacnetz starts a Spring Boot server and waits for BACnet WHO-IS messages to which all simulated devices will answer with their respective I-AM response.
The devices are functional in that the can return lists of their properties.
Some of the properties also respsond to COV-Subscriptions and send updates via the COV feature when their state changes.

## Things to keep in mind
It is not possible to run more than one BACnet stack on a single machine in the default configuration!

The reason is that the BACnet standard mandates that all BACnet stacks bind to the port 0xBAC0 (47808d).
So running two stacks on one machine cannot bind to the same port.
Therefor a test setup has to use two machines connected via a network.

## Tools
A very good tool (on Windows) for interacting with BACnet devices is YABE (Yet Another BACnet Explorer). 
It is a C# application which is available in source. It runs on windows. I have never tried in on Linux using Mono.

Another great tool is called VTS. Although it is targeted at BACnet veterans and power users it can be usefull to beginners. 
It allows the construction of individual BACnet messages and can repeatedly send those messages which allows for code and 
fix learning sessions (Never do code and fix! It is not a proper software engineering practice!)

If YABE or VTS is not working for your, you can simulate a BACNET device using BACNET4j (https://github.com/infiniteautomation/BACnet4J). 
The very usefull BACNET4J wrapper (https://github.com/Code-House/bacnet4j-wrapper) 
is a great library that contains good examples on how to use the BACNET4J library.

When working with BACNET4J, you have to be aware of the fact that it is not possible 
to start two devices on the same host machine because BACNET seems to define the port 
0xBAC0 as the default port on which all bacnet devices send and receive messages. 
The easiest way to work with BACNET4J as a beginner is to use two host machines on the 
same network and to start a device on each of the machines. Those devices are then able to talk to each other.

bacnetz is developed against a device that is started using BACNET4J and the BACNET4J wrapper.

Wireshark is the tool of choice to inspect the network traffic and all packets that are send back and forth. 
Using the filter "bacnet" or "port 47808", wireshark will show BACNET packets only. (47808 = 0xBAC0).

The first bacnetz test binds a Java DatagramSocket (for UDP) on the port 0xBAC0. 
This port is then used to broadcast a Who-Is UDP packet over the broadcast address 
that is configured on the first ethernet apdater of the host operating system. 
The broadcast is sent to the port 0xBAC0 of all systems in the local network. 
If a system hosts a BACNET device, it will listen on that port and it will respond with a I-Am answer.
