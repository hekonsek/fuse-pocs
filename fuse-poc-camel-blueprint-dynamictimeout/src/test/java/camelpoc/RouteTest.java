package camelpoc;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class RouteTest extends CamelBlueprintTestSupport {

    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/blueprint.xml";
    }

    @Test
    public void shouldReceiveAggregatedMessage() throws Exception {
        // Given
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);
        getMockEndpoint("mock:result").message(0).body().isEqualTo("someBody");

        // Then
        assertMockEndpointsSatisfied();
    }

}