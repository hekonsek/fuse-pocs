package fuse.pocs.camel.spring.cxf.async;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class AsynchronousProcessor implements Processor {

    static final String RESPONSE = "RESPONSE";

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setBody(RESPONSE);
    }

}