package fuse.pocs.camel.netty.http.params;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static fuse.pocs.camel.netty.http.params.NettyTestConfig.DECODED_HTTP_QUERY_PARAMS;
import static fuse.pocs.camel.netty.http.params.NettyTestConfig.HTTP_QUERY_PARAMS;
import static fuse.pocs.camel.netty.http.params.NettyTestConfig.RAW_HTTP_QUERY_PARAMS_DECODED;
import static org.apache.camel.Exchange.HTTP_QUERY;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = NettyTestConfig.class,
        loader = CamelSpringDelegatingTestContextLoader.class
)
public class NettyHttpParametersEncodingTest extends Assert {

    @Produce(uri = "direct:test")
    ProducerTemplate producerTemplate;

    @Autowired
    MockHttpServer mockHttpServer;

    @Before
    public void setUp() {
        mockHttpServer.reset();
    }

    @Test
    public void shouldNotDecodeParameters() {
        producerTemplate.sendBody("direct:test", "msg");
        assertEquals(HTTP_QUERY_PARAMS, mockHttpServer.lastRequestUri().getRawQuery());
        assertEquals(DECODED_HTTP_QUERY_PARAMS, mockHttpServer.lastRequestUri().getQuery());
    }

    @Test
    public void shouldNotDecodeParameters_overrideWithHeader() {
        producerTemplate.sendBodyAndHeader("direct:test", "msg", HTTP_QUERY, HTTP_QUERY_PARAMS);
        assertEquals(HTTP_QUERY_PARAMS, mockHttpServer.lastRequestUri().getRawQuery());
        assertEquals(DECODED_HTTP_QUERY_PARAMS, mockHttpServer.lastRequestUri().getQuery());
    }

    @Test
    public void shouldNotDecodeRawParameters() {
        producerTemplate.sendBody("direct:test-raw", "msg");
        assertEquals(RAW_HTTP_QUERY_PARAMS_DECODED, mockHttpServer.lastRequestUri().getRawQuery());
        assertEquals(RAW_HTTP_QUERY_PARAMS_DECODED, mockHttpServer.lastRequestUri().getQuery());
    }

    @Ignore // This one should not pass the same way, as the test above
    @Test
    public void shouldNotDecodeRawParameters_overrideWithHeader() {
        producerTemplate.sendBodyAndHeader("direct:test-raw", "msg", HTTP_QUERY, HTTP_QUERY_PARAMS);
        assertEquals(RAW_HTTP_QUERY_PARAMS_DECODED, mockHttpServer.lastRequestUri().getRawQuery());
        assertEquals(RAW_HTTP_QUERY_PARAMS_DECODED, mockHttpServer.lastRequestUri().getQuery());
    }

}

@Configuration
class NettyTestConfig extends SingleRouteCamelConfiguration {

    // Routing fixtures

    static final int HTTP_PORT = 11000;

    static final String HTTP_QUERY_PARAMS = "param=x1%2526y%253D2";

    static final String DECODED_HTTP_QUERY_PARAMS = "param=x1%26y%3D2";

    static final String REQUEST_URL = "http://localhost:" + HTTP_PORT + "/uri?" + HTTP_QUERY_PARAMS;

    static final String NETTY_URI = "netty-http:" + REQUEST_URL;

    static final String RAW_HTTP_QUERY_PARAMS = "param=x1%26y%3D2";

    static final String RAW_HTTP_QUERY_PARAMS_DECODED = "param=x1&y=2";

    static final String RAW_REQUEST_URL = "http://localhost:" + HTTP_PORT + "/uri?" + RAW_HTTP_QUERY_PARAMS;

    static final String RAW_NETTY_URI = "netty-http:" + RAW_REQUEST_URL;

    @Override
    public RouteBuilder route() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:test").to(NETTY_URI);

                from("direct:test-raw").to(RAW_NETTY_URI);
            }
        };
    }

    // Spring dependencies

    @Bean(initMethod = "start", destroyMethod = "stop")
    MockHttpServer mockHttpServer() {
        return new MockHttpServer(HTTP_PORT);
    }

}