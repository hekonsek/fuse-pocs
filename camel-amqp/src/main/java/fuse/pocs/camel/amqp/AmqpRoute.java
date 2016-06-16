package fuse.pocs.camel.amqp;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPComponent;

import static java.lang.Integer.parseInt;
import static org.apache.camel.component.amqp.AMQPComponent.amqpComponent;

public class AmqpRoute extends RouteBuilder {

    public void configure() throws Exception {
        int port = parseInt(System.getProperty("AMQP_SERVICE_PORT", "5672"));
        String connectionURI = "failover:(amqp://localhost:" + port + ")";
        AMQPComponent amqpComponent = amqpComponent(connectionURI);
        getContext().addComponent("amqp", amqpComponent);

        from("amqp:queue").to("mock:test");
    }

}
