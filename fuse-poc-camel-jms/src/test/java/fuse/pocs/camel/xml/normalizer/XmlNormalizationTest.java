package fuse.pocs.camel.xml.normalizer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.Consume;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;
import java.util.ArrayList;
import java.util.List;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {Config.class},
        loader = CamelSpringDelegatingTestContextLoader.class
)
public class XmlNormalizationTest {

    @Produce(uri = "jms:queue:jobQueue")
    protected ProducerTemplate testProducer;

    @Consume
    protected ConsumerTemplate consumer;

    @EndpointInject(uri = "mock:xxx")
    MockEndpoint mockEndpoint;

    static int count = 100;

    static List<String> messages = new ArrayList<String>(count);

    static BrokerService broker;

    @BeforeClass
    static public void setup() throws Exception {
        for (int i = 0; i < count; i++) {
            messages.add("m" + i);
        }

        broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.start();
    }

    @AfterClass
    static public void teardown() throws Exception {
        broker.stop();
    }

    @Test
    public void should() throws InterruptedException {
//        mockEndpoint.expectedMessageCount(messages.size());
//        for(int i = 0; i < messages.size();i++) {
//            mockEndpoint.message(i).body().isEqualTo(messages.get(i));
//        }
        ConsumerTemplate consumer = testProducer.getCamelContext().createConsumerTemplate();
        testProducer.sendBody("msg");
//        mockEndpoint.assertIsSatisfied(TimeUnit.MINUTES.toMillis(2));
        for (int i = 0; i < 1000000; i++) {
            Object o = consumer.receiveBodyNoWait("jms:queue:destinationQueue");
            if (o != null) {
                System.out.println("XXX" + o);
            }
        }
    }

}

@Configuration
class Config extends SingleRouteCamelConfiguration {

    @Bean
    ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory();
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
                from("jms:queue:jobQueue")
//                        .transacted()
                        .setBody().constant(XmlNormalizationTest.messages).
                        split(body())
                        .to("jms:queue:destinationQueue");

//                from("jms:queue:destinationQueue").to("mock:xxx");
            }
        };
    }

}