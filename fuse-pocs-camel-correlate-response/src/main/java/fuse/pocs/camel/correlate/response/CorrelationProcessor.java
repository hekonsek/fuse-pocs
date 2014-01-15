package fuse.pocs.camel.correlate.response;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Duration.ONE_MINUTE;
import static java.util.concurrent.TimeUnit.MINUTES;

public class CorrelationProcessor implements Processor {

    public static final String PROPERTY_INCOMING_REQUEST = "incomingRequest";

    private final Cache<String, String> correlationRepository = CacheBuilder.newBuilder().
            expireAfterWrite(15, MINUTES).build();

    @Override
    public void process(Exchange exchange) throws Exception {
        String request = exchange.getProperty(PROPERTY_INCOMING_REQUEST, String.class);
        String response = exchange.getIn().getBody(String.class);

        // For simplicity: correlation ID == body
        String requestCorrelationId = request;
        String responseCorrelationId = response;

        // Response matches request - return immediately
        if (requestCorrelationId.equals(responseCorrelationId)) {
            return;
        }

        // Service returned response with invalid identifier - remember it for different request
        correlationRepository.put(responseCorrelationId, response);

        // Wait minute (at most) until response you wait for arrives
        await().atMost(ONE_MINUTE).until(responseArrived(requestCorrelationId));
        String savedResponse = correlationRepository.getIfPresent(requestCorrelationId);
        exchange.getIn().setBody(savedResponse);
        correlationRepository.invalidate(requestCorrelationId);
    }

    private Callable<Boolean> responseArrived(final String correlationId) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return correlationRepository.getIfPresent(correlationId) != null;
            }
        };
    }

}
