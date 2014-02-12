package camelpoc;

import fuse.pocs.fabric.NettyHttpRoute;
import io.fabric8.itests.paxexam.support.ContainerBuilder;
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

    @After
    public void tearDown() throws InterruptedException {
        ContainerBuilder.destroy();
    }

    @Test
    public void shouldCreateCamelRouter() throws Exception {
        // Given
        err.println(executeCommand("fabric:create -n"));
        err.println(executeCommand("fabric:profile-create --parents feature-camel netty-http-server"));
        err.println(executeCommand("fabric:profile-edit --features camel-netty-http netty-http-server"));
        err.println(executeCommand("fabric:profile-edit --bundles mvn:fuse-pocs/fuse-pocs-fabric-bundle/1.0-SNAPSHOT netty-http-server"));
        ContainerBuilder.create().
                withName("router-container").withProfiles("netty-http-server").
                assertProvisioningResult().build();
        InputStream inputStream = new URL("http://localhost:18080/").openStream();

        // When
        String response = IOUtils.toString(inputStream);

        // Then
        assertEquals(NettyHttpRoute.RESPONSE, response);
    }

}