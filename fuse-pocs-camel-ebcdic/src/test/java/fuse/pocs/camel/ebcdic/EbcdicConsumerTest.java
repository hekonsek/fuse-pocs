package fuse.pocs.camel.ebcdic;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.InputStream;


public class EbcdicConsumerTest extends CamelTestSupport {

    // Routing fixtures

    @Produce(uri = "direct:test")
    ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:test")
    MockEndpoint mockEndpoint;

    /**
     * Charsets supported by Oracle JVM - http://docs.oracle.com/javase/6/docs/technotes/guides/intl/encoding.doc.html
     */
    String ebcdicCharsetName = "Cp1047";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:test").
                        unmarshal().string(ebcdicCharsetName).
                        to(mockEndpoint);
            }
        };
    }

    @Test
    public void shouldDecodeEbcdic() throws Exception {
        // Given
        InputStream ebcdicMessage = getClass().getResourceAsStream("ebcdicMessage");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.message(0).body().contains("order-items");

        // When
        producerTemplate.sendBody(ebcdicMessage);

        // Then
        assertMockEndpointsSatisfied();
    }

}