package camelpoc;

import fuse.pocs.fabric.NettyHttpRoute;
import io.fabric8.api.Container;
import io.fabric8.api.FabricService;
import io.fabric8.api.ServiceProxy;
import io.fabric8.itests.paxexam.support.ContainerBuilder;
import io.fabric8.itests.paxexam.support.ContainerProxy;
import io.fabric8.itests.paxexam.support.FabricTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import static io.fabric8.api.ServiceProxy.createServiceProxy;
import static java.lang.System.err;
import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class SimpleFabricTest extends FabricTestSupport {

    @Configuration
    public Option[] config() {
        return new Option[]{
                new DefaultCompositeOption(fabricDistributionConfiguration()),
                mavenBundle("commons-io", "commons-io").versionAsInProject()
        };
    }

    Set<ContainerProxy> containers;


    @After
    public void tearDown() throws InterruptedException {
        for (Container container : containers) {
            container.stop(true);
        }
    }

    @Test
    public void shouldCreateCamelRouter() throws Exception {
        // Helper available in - https://github.com/fabric8io/fabric8/pull/1212
        ServiceProxy<FabricService> fabricService = createServiceProxy(bundleContext, FabricService.class);

        // Given
        err.println(executeCommand("fabric:create -n"));
        err.println(executeCommand("fabric:profile-create --parents feature-camel netty-http-server"));
        err.println(executeCommand("fabric:profile-edit --features camel-netty-http netty-http-server"));
        err.println(executeCommand("fabric:profile-edit --bundles mvn:fuse-pocs/fuse-pocs-fabric-migration-61-bundle/1.0-SNAPSHOT netty-http-server"));
        containers = ContainerBuilder.create(fabricService).
                withName("router-container").withProfiles("netty-http-server").
                assertProvisioningResult().build();
        InputStream inputStream = new URL("http://localhost:18080/").openStream();

        // When
        String response = IOUtils.toString(inputStream);

        // Then
        assertEquals(NettyHttpRoute.RESPONSE, response);
        err.println(executeCommand("fabric:profile-list"));

    }

}