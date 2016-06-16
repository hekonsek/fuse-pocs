package fuse.pocs.camel.amqp;

import org.apache.activemq.broker.BrokerService;
import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class AmqpRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:test")
    MockEndpoint mock;

    @Override
    protected void doPreSetup() throws Exception {
        int amqpPort = AvailablePortFinder.getNextAvailable();
        System.setProperty("AMQP_SERVICE_PORT", amqpPort + "");

        BrokerService broker = new BrokerService();
        broker.setPersistent(false);
        broker.addConnector("amqp://localhost:" + amqpPort);
        broker.start();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new AmqpRoute();
    }

    // Tests

    @Test
    public void shouldSendMessageToQueue() throws InterruptedException {
        // Given
        String message = "foo";
        mock.expectedBodiesReceived(message);

        // When
        template.sendBody("amqp:queue", message);

        // Then
        mock.assertIsSatisfied();
    }

}
