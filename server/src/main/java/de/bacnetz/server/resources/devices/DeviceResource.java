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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/device")
@Api(value = "/device")
public class DeviceResource {

    private final static Logger LOG = LoggerFactory.getLogger(DeviceResource.class);

    @Autowired
    private DeviceFacade deviceFacade;

    /**
     * http://127.0.0.1:8182/bacnetz/api/device/all
     * 
     * @return
     */
    @ApiOperation("Retrieve all devices")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = List.class) })
    @GET
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<DeviceDto> all() {
        return deviceFacade.getDevices();
    }

    @ApiOperation("Retrieve detailed device information")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = DeviceDto.class) })
    @GET
    @Path("/details/{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DeviceDto details(@PathParam("uid") final long uid) {
        LOG.info("device/details: uid={}", uid);
        return deviceFacade.getDeviceDetails(uid);
    }

    /**
     * http://127.0.0.1:8182/bacnetz/api/device/toggle/100
     * 
     * @param uid
     */
    @ApiOperation("Toggle specific door open state")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK") })
    @POST
    @Path("/toggle/{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void toggle(@PathParam("uid") final long uid) {
        LOG.info("toggle: uid={}", uid);
    }

    @ApiOperation("Toggle all door open states")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK") })
    @POST
    @Path("/toggle")
    @Consumes(MediaType.APPLICATION_JSON)
    public void toggleAll() {
        deviceFacade.toggleAll();
    }

}
