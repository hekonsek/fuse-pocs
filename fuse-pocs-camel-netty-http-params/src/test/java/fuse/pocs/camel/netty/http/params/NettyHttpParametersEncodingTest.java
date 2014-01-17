package fuse.pocs.camel.netty.http.params;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
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

import java.io.IOException;
import java.net.URL;

import static fuse.pocs.camel.netty.http.params.NettyTestConfig.DECODED_HTTP_QUERY_PARAMS;
import static fuse.pocs.camel.netty.http.params.NettyTestConfig.HTTP_CONSUMER_URL;
import static fuse.pocs.camel.netty.http.params.NettyTestConfig.HTTP_QUERY_PARAMS;
import static fuse.pocs.camel.netty.http.params.NettyTestConfig.RAW_HTTP_QUERY_PARAMS_DECODED;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
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

    @EndpointInject(uri = "mock:consumed")
    MockEndpoint consumingMockEndpoint;

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

    @Test
    public void shouldConsumeEncodedParameter() throws IOException, InterruptedException {
        // Given
        String paramKey = "param";
        String paramValue = "x1%26y%3D2";
        String encodedParamValue = encode(paramValue, UTF_8.name());
        String requestString = format(HTTP_CONSUMER_URL + "?%s=%s", paramKey, encodedParamValue);
        URL request = new URL(requestString);

        consumingMockEndpoint.expectedMessageCount(1);
        consumingMockEndpoint.message(0).header(paramKey).isEqualTo(paramValue);

        // When
        request.openConnection().getInputStream().close();

        // Then
        consumingMockEndpoint.assertIsSatisfied();
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

    // Consumer routing fixtures

    static final String HTTP_CONSUMER_URL = "http://localhost:11001/";

    static final String NETTY_HTTP_CONSUMER_URI = "netty-http:" + HTTP_CONSUMER_URL;

    // Routes fixtures

    @Override
    public RouteBuilder route() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:test").to(NETTY_URI);

                from("direct:test-raw").to(RAW_NETTY_URI);

                from(NETTY_HTTP_CONSUMER_URI).to("mock:consumed");
            }
        };
    }

    // Spring dependencies

    @Bean(initMethod = "start", destroyMethod = "stop")
    MockHttpServer mockHttpServer() {
        return new MockHttpServer(HTTP_PORT);
    }

}