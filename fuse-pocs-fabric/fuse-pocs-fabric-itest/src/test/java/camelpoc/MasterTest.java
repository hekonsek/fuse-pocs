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
import io.fabric8.api.FabricService;
import io.fabric8.api.ServiceProxy;
import io.fabric8.itests.paxexam.support.ContainerBuilder;
import io.fabric8.itests.paxexam.support.FabricTestSupport;
import org.apache.commons.io.IOUtils;
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

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class MasterTest extends FabricTestSupport {

    @Test
    public void shouldKeepSingletonNetty() throws Exception {
        ServiceProxy<FabricService> fabricService = ServiceProxy.createServiceProxy(bundleContext, FabricService.class);

        System.err.println(executeCommand("fabric:create -n"));
        System.err.println(executeCommand("fabric:profile-create --parents feature-camel foo"));
        System.err.println(executeCommand("fabric:profile-edit --features camel-netty-http foo"));
        System.err.println(executeCommand("fabric:profile-edit --bundles mvn:fuse-pocs/fuse-pocs-fabric-bundle/1.0-SNAPSHOT foo"));

        Container master = (Container) ContainerBuilder.create(fabricService).withName("master").withProfiles("foo").assertProvisioningResult().build().iterator().next();

        InputStream inputStream = new URL("http://localhost:18081/").openStream();
        String response = IOUtils.toString(inputStream);
        assertEquals("master", response);

        Container slave = (Container) ContainerBuilder.create(fabricService).withName("slave").withProfiles("foo").assertProvisioningResult().build().iterator().next();
        master.destroy();

        inputStream = new URL("http://localhost:18081/").openStream();
        response = IOUtils.toString(inputStream);
        assertEquals("master", response);

        slave.destroy();
    }

    @Configuration
    public Option[] config() {
        return new Option[]{
                new DefaultCompositeOption(fabricDistributionConfiguration()),
                mavenBundle("commons-io", "commons-io").versionAsInProject()
        };
    }

}