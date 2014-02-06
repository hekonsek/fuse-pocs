package fuse.pocs.camel.xml.normalizer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {Config.class},
        loader = CamelSpringDelegatingTestContextLoader.class
)
public class JmsConsumerTest {

    @Produce(uri = "jms:test")
    ProducerTemplate testProducer;

    @EndpointInject(uri = "mock:test")
    MockEndpoint mockEndpoint;

    @Autowired
    CamelContext camelContext;

    @Test
    public void shouldReadMessages() throws Exception {
        final int messages = 10000;
        mockEndpoint.expectedMessageCount(messages);
        mockEndpoint.setRetainLast(10);

        for (int i = 0; i < messages; i++) {
            testProducer.sendBody("seda:test", i);
        }

        assertIsSatisfied(5, MINUTES, mockEndpoint);
    }

}

@Configuration
class Config extends SingleRouteCamelConfiguration {

    @Bean
    ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }

    @Bean
    ConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(connectionFactory());
    }

    @Bean
    PlatformTransactionManager platformTransactionManager() {
        return new JmsTransactionManager(cachingConnectionFactory());
    }

    @Override
    public RouteBuilder route() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:test?concurrentConsumers=5").to("jms:test");

                from("jms:test?concurrentConsumers=5&cacheLevelName=CACHE_CONSUMER")
                        .transacted()
                        .to("mock:test");
            }
        };
    }

}