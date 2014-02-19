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
import io.fabric8.itests.paxexam.support.FabricTestSupport;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.UrlReference;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import java.util.Set;

import static io.fabric8.itests.paxexam.support.ContainerBuilder.create;
import static io.fabric8.itests.paxexam.support.ContainerBuilder.destroy;
import static junit.framework.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.scanFeatures;

@RunWith(JUnit4TestRunner.class)
//@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class AmqBrokerTest extends FabricTestSupport {

    Set<Container> containers;

    @Configuration
    public Option[] config() {
        return new Option[]{
                new DefaultCompositeOption(fabricDistributionConfiguration()),
                scanFeatures(getKarafFeatureUrl(), "spring-jms"),
                scanFeatures(getFabricFeatureUrl(), "mq-fabric", "mq-fabric-camel"),
        };
    }

    public static UrlReference getKarafFeatureUrl() {
        String type = "xml/features";
        return mavenBundle("org.apache.karaf.assemblies.features", "standard", "2.3.0").type(type);
    }

    public static UrlReference getFabricFeatureUrl() {
        String type = "xml/features";
        return mavenBundle("io.fabric8", "fabric8-karaf", "1.0.0.redhat-346").type(type);
    }

    @After
    public void tearDown() throws InterruptedException {
        destroy(containers);
    }

    @Test
    public void shouldCreateAmqBroker() throws Exception {
        // Given
        String message = "message";
        String queue = "queue";
        System.err.println(executeCommand("fabric:create -n"));
        containers = create().withName("router-container").withProfiles("mq-amq").
                assertProvisioningResult().build();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", "discovery:(fabric:default)");
        JmsTemplate jms = new JmsTemplate(connectionFactory);

        // When
        jms.convertAndSend(queue, message);
        String receivedMessage = (String) jms.receiveAndConvert(queue);

        // Then
        assertEquals(message, receivedMessage);
    }

}