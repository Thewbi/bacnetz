package de.bacnetz.server.resources.devices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bacnetz.devices.DeviceDto;
import de.bacnetz.devices.DeviceFacade;

@Path("/device")
public class DeviceResource {

    private final static Logger LOG = LoggerFactory.getLogger(DeviceResource.class);

    @Autowired
    private DeviceFacade deviceFacade;

    @GET
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<DeviceDto> all() {
        return deviceFacade.getDevices();
    }

    /**
     * http://127.0.0.1:8182/bacnetz/device/toggle/100
     * 
     * @param uid
     */
    @POST
    @Path("/toggle/{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void toggle(@PathParam("uid") final long uid) {
        LOG.info("toggle: uid={}", uid);
    }

}
