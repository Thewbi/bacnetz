package de.bacnetz.server.resources.system;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;

@Path("/sysinfo")
//@Api(value = "/sysinfo")
public class SystemInformationResource {

    /**
     * http://127.0.0.1:8182/bacnetz/sysinfo/version
     * 
     * @return the version of this application.
     */
//    @ApiOperation("Get app version.")
//    @ApiResponses({ @ApiResponse(code = 200, message = "OK") })
    @GET
    @Path("/version")
    public String getImageVersion() {
        return "0.1";
    }

}
