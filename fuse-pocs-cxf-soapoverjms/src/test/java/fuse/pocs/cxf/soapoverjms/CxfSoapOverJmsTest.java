package fuse.pocs.cxf.soapoverjms;

import org.apache.activemq.broker.BrokerService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.jms.spec.JMSSpecConstants;
import org.junit.Assert;
import org.junit.Test;

public class CxfSoapOverJmsTest extends Assert {

    @Test
    public void shouldInvokeServiceOverJms() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61500");
        broker.start();

        String address = "jms:jndi:dynamicQueues/test.cxf.jmstransport.queue3"
                + "?jndiInitialContextFactory"
                + "=org.apache.activemq.jndi.ActiveMQInitialContextFactory"
                + "&jndiConnectionFactoryName=ConnectionFactory&jndiURL=tcp://localhost:61500";

        JaxWsServerFactoryBean serviceFactory = new JaxWsServerFactoryBean();
        serviceFactory.setServiceClass(HelloService.class);
        serviceFactory.setAddress(address);
        serviceFactory.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);
        serviceFactory.setServiceBean(new HelloServiceImpl());
        serviceFactory.create();

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);
        factory.setServiceClass(HelloService.class);
        factory.setAddress(address);
        HelloService client = (HelloService) factory.create();
        String reply = client.sayHello();
        assertEquals(HelloServiceImpl.RESPONSE, reply);
    }

}
