package camelpoc;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class RouteTest extends CamelBlueprintTestSupport {

    @EndpointInject(uri = "mock:result")
    MockEndpoint mockEndpoint;

    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/blueprint.xml";
    }

    @Test
    public void shouldReceiveAggregatedMessage() throws Exception {
        // Given
        mockEndpoint.expectedMinimumMessageCount(1);
        mockEndpoint.message(0).body().isEqualTo("someBody");

        // Then
        assertMockEndpointsSatisfied();
    }

}