package fuse.pocs.camel.spring.cxf.async;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("/customerservice/")
public class CustomerService {

    @GET
    @Path("/customers")
    public String getCustomers() {
        return null;
    }

}