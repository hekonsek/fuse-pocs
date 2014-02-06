package camelpoc;

import org.apache.camel.spi.ComponentResolver;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class ComponentResolverTest extends CamelBlueprintTestSupport {

    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/blueprint.xml";
    }

    @Test
    public void shouldReceiveComponentResolver() throws Exception {
        // When
        ComponentResolver nettyComponentResolver = context.getRegistry().lookupByNameAndType("nettyComponentResolver", ComponentResolver.class);

        // Then
        assertNotNull(nettyComponentResolver);
    }

    @Test
    public void shouldResolveNettyComponent() throws Exception {
        // When
        ComponentResolver nettyComponentResolver = context.getRegistry().lookupByNameAndType("nettyComponentResolver", ComponentResolver.class);

        // Then
        assertNotNull(nettyComponentResolver.resolveComponent("netty", context));
    }

    @Test
    public void shouldNotResolveSedaComponent() throws Exception {
        // When
        ComponentResolver nettyComponentResolver = context.getRegistry().lookupByNameAndType("nettyComponentResolver", ComponentResolver.class);

        // Then
        assertNull(nettyComponentResolver.resolveComponent("seda", context));
    }


}