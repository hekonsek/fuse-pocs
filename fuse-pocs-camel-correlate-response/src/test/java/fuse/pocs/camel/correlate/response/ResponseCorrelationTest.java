package fuse.pocs.camel.correlate.response;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ResponseCorrelationTest extends CamelTestSupport {

    RandomService randomService = mock(RandomService.class);

    @EndpointInject(uri = "mock:test")
    MockEndpoint mockEndpoint;

    @Produce(uri = "direct:test")
    ProducerTemplate producerTemplate;

    @Test
    public void shouldCorrelateResponses() throws InterruptedException {
        // Given
        given(randomService.randomValue()).willReturn("1", "2");
        mockEndpoint.expectedBodiesReceivedInAnyOrder("11", "22");

        // When
        producerTemplate.asyncSendBody("direct:test", "1");
        SECONDS.sleep(5);
        producerTemplate.asyncSendBody("direct:test", "2");

        // Then
        assertMockEndpointsSatisfied();
    }

    @Test
    public void shouldCorrelateNotOrderedResponses() throws InterruptedException {
        // Given
        given(randomService.randomValue()).willReturn("2", "1");
        mockEndpoint.expectedBodiesReceivedInAnyOrder("11", "22");

        // When
        producerTemplate.asyncSendBody("direct:test", "1");
        SECONDS.sleep(5);
        producerTemplate.asyncSendBody("direct:test", "2");

        // Then
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:test").
                        setProperty(CorrelationProcessor.PROPERTY_INCOMING_REQUEST, body()).
                        beanRef("randomService", "randomValue").
                        process(new CorrelationProcessor()).
                        setBody().simple("${body}${property.incomingRequest}").
                        to(mockEndpoint);
            }


        };
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("randomService", randomService);
        return registry;
    }
}