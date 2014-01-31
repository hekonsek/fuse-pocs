package fuse.pocs.camel.spring.cxf.async;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;
import java.net.URL;


public class AsynchronousProcessingTest extends CamelSpringTestSupport {

    @Test
    public void shouldAsynchronouslyProcessRequest() throws Exception {
        // Given
        InputStream responseStream = new URL("http://localhost:" + 18080 + "/customerservice/customers").openStream();

        // When
        String response = IOUtils.toString(responseStream);

        // Then
        Assert.assertEquals(AsynchronousProcessor.RESPONSE, response);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("classpath:fuse/pocs/camel/spring/cxf/async/camelContext.xml");
    }

}