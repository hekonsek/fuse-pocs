package fuse.pocs.fabric;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class MasterRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("master:netty-master:netty-http:http://localhost:18081/").
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody("master");
                    }
                });
    }

}
