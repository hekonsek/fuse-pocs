package fuse.pocs.camel.xml.normalizer;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

public class XmlNormalizationTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:test")
    MockEndpoint mockEndpoint;

    @Test
    public void shouldRemoveNewlinesFromElement() throws InterruptedException, IOException {
        // Given
        String normalizedMessage = IOUtils.toString(getClass().getResourceAsStream("normalizedAndTokenizedMessage.xml"));
        mockEndpoint.expectedMinimumMessageCount(1);
        mockEndpoint.message(0).body().isEqualTo(normalizedMessage);

        // When
        sendBody("direct:test", getClass().getResourceAsStream("message.xml"));

        // Then
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:test").
                        bean(XmlElementNormalizationTransformer.class).split().tokenizeXML("Child").
                        to("mock:test");
            }
        };
    }
}
