package fuse.pocs.camel.xml.normalizer;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

public class XmlNormalizationTest extends CamelTestSupport {

    // Routing fixtures

    @EndpointInject(uri = "mock:test")
    MockEndpoint mockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:test").
                        bean(XmlElementNormalizationTransformer.class).split().tokenizeXML("Child").
                        to(mockEndpoint);
            }
        };
    }

    // Tests

    @Test
    public void shouldRemoveNewlinesFromElement() throws InterruptedException, IOException {
        // Given
        String normalizedMessage = IOUtils.toString(getClass().getResourceAsStream("normalizedAndTokenizedMessage.xml")).replaceAll("\n", "").replaceAll("\\>\\s+?\\<", "><");

        // When
        sendBody("direct:test", getClass().getResourceAsStream("message.xml"));

        // Then
        String receivedXml = mockEndpoint.getExchanges().get(0).getIn().getBody(String.class);
        String normalizedReceivedXml = receivedXml.replaceAll("\n", "").replaceAll("\\>\\s+?\\<", "><");
        assertEquals(normalizedMessage, normalizedReceivedXml);
    }

}
