package camelpoc;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class RouteTest extends CamelBlueprintTestSupport {

//    @Override
//    protected String getBlueprintDescriptor() {
//        return "/OSGI-INF/blueprint/blueprint.xml";
//    }

    @Test
    public void testRoute() throws Exception {
        // the route is timer based, so every 5th second a message is send
        // we should then expect at least one message
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);
        getMockEndpoint("mock:result").message(0).body().isEqualTo("someBody");

        // assert expectations
        assertMockEndpointsSatisfied();
    }

}
