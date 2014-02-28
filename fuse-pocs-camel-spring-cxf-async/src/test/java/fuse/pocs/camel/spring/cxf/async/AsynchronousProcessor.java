package fuse.pocs.camel.spring.cxf.async;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class AsynchronousProcessor implements Processor {

    static final String RESPONSE = "RESPONSE";

    static private final Logger LOG = LoggerFactory.getLogger(AsynchronousProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        LOG.info("Sleeping...");
        TimeUnit.SECONDS.sleep(10);
        exchange.getIn().setBody(RESPONSE);
    }

}