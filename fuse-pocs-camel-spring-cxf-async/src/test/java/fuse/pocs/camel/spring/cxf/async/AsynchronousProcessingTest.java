package fuse.pocs.camel.spring.cxf.async;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class AsynchronousProcessingTest extends CamelSpringTestSupport {

    @Test
    public void shouldAsynchronouslyProcessRequest() throws Exception {
        // When
        String response = queryJetty();

        // Then
        assertEquals(AsynchronousProcessor.RESPONSE, response);
    }

    @Test
    public void shouldHandleDosAttack() throws Exception {
        // Given
        Thread[] clientThreads = new Thread[500];

        // When
        for (int i = 0; i < clientThreads.length; i++) {
            clientThreads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        String response = queryJetty();
                        assertEquals(AsynchronousProcessor.RESPONSE, response);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            clientThreads[i].start();

        }

        // Then
        for (Thread clientThread : clientThreads) {
            clientThread.join();
        }
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("classpath:fuse/pocs/camel/spring/cxf/async/camelContext.xml");
    }

    // Helpers

    private String queryJetty() throws IOException {
        InputStream responseStream = new URL("http://localhost:" + 18080 + "/customerservice/customers").openStream();
        return IOUtils.toString(responseStream);
    }

}