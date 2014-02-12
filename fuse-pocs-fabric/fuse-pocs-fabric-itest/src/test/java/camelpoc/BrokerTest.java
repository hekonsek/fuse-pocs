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
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

import javax.jms.ConnectionFactory;

import static io.fabric8.itests.paxexam.support.ContainerBuilder.create;

@Ignore
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class BrokerTest extends FabricTestSupport {

    @Configuration
    public Option[] config() {
        return new Option[]{
                new DefaultCompositeOption(fabricDistributionConfiguration()),
                mavenBundle("org.apache.activemq", "activemq-client", "5.9.0"),
                mavenBundle("org.apache.geronimo.specs", "geronimo-jms_1.1_spec", "1.1.1"),
                mavenBundle("org.apache.geronimo.specs", "geronimo-j2ee-management_1.1_spec", "1.0.1")
        };
    }

    @After
    public void tearDown() throws InterruptedException {
        ContainerBuilder.destroy();
    }

    @Test
    public void shouldListBundleOnRemoteMachine() throws Exception {
        // Given
        System.err.println(executeCommand("fabric:create -n"));
        Container container = (Container) create().withName("router-container").withProfiles("mq").
                assertProvisioningResult().build().iterator().next();

        String[] containerSshUrl = container.getSshUrl().split(":");
        String containerHost = containerSshUrl[0];
        String containerPort = containerSshUrl[1];

        // When
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:PORT");
        connectionFactory.createConnection().close();
    }

}