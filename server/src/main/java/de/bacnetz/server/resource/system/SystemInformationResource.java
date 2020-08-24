package de.bacnetz.server.resource.system;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/sysinfo")
public class SystemInformationResource {

    /**
     * http://127.0.0.1:8182/bacnetz/sysinfo/version
     * 
     * @return the version of this application.
     */
    @GET
    @Path("/version")
    public String getImageVersion() {
        return "0.1";
    }

}
