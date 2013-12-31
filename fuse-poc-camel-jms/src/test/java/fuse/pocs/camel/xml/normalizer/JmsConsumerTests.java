package fuse.pocs.camel.xml.normalizer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
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
import org.junit.Assert;
import org.junit.BeforeClass;
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
import java.util.ArrayList;
import java.util.List;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {Config.class},
        loader = CamelSpringDelegatingTestContextLoader.class
)
public class JmsConsumerTests {

    @Produce(uri = "jms:queue:jobQueue")
    protected ProducerTemplate testProducer;

    @EndpointInject(uri = "mock:xxx")
    MockEndpoint mockEndpoint;

    @Autowired
    CamelContext camelContext;

    static int count = 100;

    static List<String> messages = new ArrayList<String>(count);

    static BrokerService broker;

    @BeforeClass
    static public void setup() throws Exception {
        for (int i = 0; i < count; i++) {
            messages.add("m" + i);
        }

//        Runtime.getRuntime().exec(new String[]{"rm", "-rf", "/home/hkonsek/projects/fuse-pocs/activemq-data/localhost/KahaDB"}).waitFor();
//        broker = new BrokerService();
//        broker.setBrokerName("localhost");
//        broker.setPersistent(false);
////        broker.addConnector("tcp://localhost:61616");
//        broker.start();
    }

    @AfterClass
    static public void teardown() throws Exception {
//        broker.stop();
//        broker.waitUntilStopped();
    }


    @Autowired
    ConsumerTemplate consumerTemplate;

    @Test
    public void should() throws Exception {

        mockEndpoint.expectedMessageCount(messages.size());
        for (int i = 0; i < messages.size(); i++) {
            mockEndpoint.message(i).body().isEqualTo(messages.get(i));
        }


//        for(int i = 0; i < messages.size();i++) {
//            testProducer.sendBody("jms:queue:destinationQueue", messages.get(i));
//
//        }

//                testProducer.sendBody("direct:start", "xxx");
        consumerTemplate = camelContext.createConsumerTemplate();
        testProducer.sendBody("jms:queue:jobQueue", "xxx");

        int m = 0;
        while (m < count) {
//            consumerTemplate.receiveBody("jms:queue:destinationQueue");
            Object msg = consumerTemplate.receiveBodyNoWait("jms:queue:destinationQueue");
            if (msg != null) {
                Assert.assertEquals(messages.get(m), msg);
                m++;
            }
//            Thread.sleep(50);
        }

//        mockEndpoint.assertIsSatisfied(TimeUnit.MINUTES.toMillis(5));
    }

}

@Configuration
class Config extends SingleRouteCamelConfiguration {

    @Autowired
    CamelContext camelContext;

    @Bean
    ConsumerTemplate consumerTemplate() throws Exception {
        ConsumerTemplate consumerTemplate = camelContext.createConsumerTemplate();
        consumerTemplate.start();
        return consumerTemplate;
    }

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
                from("jms:queue:jobQueue")
                        .transacted()
                        .setBody().constant(JmsConsumerTests.messages).
                        split(body())
                        .to("jms:queue:destinationQueue");

                from("direct:start").setBody().constant(JmsConsumerTests.messages).
                        split(body()).to("jms:queue:destinationQueue");

//                from("jms:queue:destinationQueue").to("mock:xxx");
            }
        };
    }

}