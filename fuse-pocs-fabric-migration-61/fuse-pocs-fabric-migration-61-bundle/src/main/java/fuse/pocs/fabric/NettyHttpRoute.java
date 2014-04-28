package fuse.pocs.fabric;

import org.apache.camel.builder.RouteBuilder;

public class NettyHttpRoute extends RouteBuilder {

    public static final String RESPONSE = "Hello world!";

    @Override
    public void configure() throws Exception {
        from("netty-http:http://localhost:18080/").
                setBody().constant(RESPONSE);
    }

}
