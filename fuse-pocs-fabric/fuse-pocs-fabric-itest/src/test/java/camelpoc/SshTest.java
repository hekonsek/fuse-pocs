/**
 * Copyright (C) FuseSource, Inc.
 * http://fusesource.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package camelpoc;

import io.fabric8.api.Container;
import io.fabric8.itests.paxexam.support.ContainerBuilder;
import io.fabric8.itests.paxexam.support.FabricTestSupport;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

import java.util.Set;

import static io.fabric8.itests.paxexam.support.ContainerBuilder.create;
import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class SshTest extends FabricTestSupport {

    @Configuration
    public Option[] config() {
        return new Option[]{
                new DefaultCompositeOption(fabricDistributionConfiguration())
        };
    }

    Set<Container> containers;

    @After
    public void tearDown() throws InterruptedException {
        ContainerBuilder.destroy(containers);
    }

    @Test
    public void shouldListBundleOnRemoteMachine() throws Exception {
        // Given
        System.err.println(executeCommand("fabric:create -n"));
        System.err.println(executeCommand("fabric:profile-create --parents feature-camel netty-http-server"));
        System.err.println(executeCommand("fabric:profile-edit --features camel-netty-http netty-http-server"));
        System.err.println(executeCommand("fabric:profile-edit --bundles mvn:fuse-pocs/fuse-pocs-fabric-bundle/1.0-SNAPSHOT netty-http-server"));
        containers = create().withName("router-container").withProfiles("netty-http-server").
                assertProvisioningResult().build();
        Container container = containers.iterator().next();
        String[] containerSshUrl = container.getSshUrl().split(":");
        String containerHost = containerSshUrl[0];
        String containerPort = containerSshUrl[1];

        // When
        String bundlesOnContainer = executeCommand(format(
                "ssh -l %s -P %s -p %s %s osgi:list",
                "admin", "admin", containerPort, containerHost
        ));

        // Then
        assertTrue(bundlesOnContainer.contains("camel-netty-http"));
    }

}