package camelpoc;

import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.io.File;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;

@RunWith(PaxExam.class)
public class SpringBatchRepositoryTest extends Assert {

    @Inject
    CamelContext camelContext;

    @Configuration
    public Option[] configuration() {
        return new Option[]{
                karafDistributionConfiguration()
                        .frameworkUrl(
                                maven().groupId("org.apache.karaf")
                                        .artifactId("apache-karaf")
                                        .type("zip")
                                        .versionAsInProject())
                        .karafVersion("2.3.3")
                        .name("Apache Karaf")
                        .unpackDirectory(new File("target/pax"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                configureConsole().ignoreLocalConsole().ignoreRemoteShell(),

                logLevel(LogLevel.INFO),

                features(
                        maven().groupId("org.apache.camel.karaf").artifactId("apache-camel").
                                type("xml").classifier("features").versionAsInProject(),
                        "camel-spring-batch"),

                // Tested module
                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-springdm-springbatch-bundle").versionAsInProject(),
        };
    }

    @Test
    @Ignore
    public void shouldNotThrowClassNotFoundException() throws InterruptedException {
        MockEndpoint mockEndpoint = camelContext.getEndpoint("mock:test", MockEndpoint.class);
        mockEndpoint.expectedMinimumMessageCount(5);
        mockEndpoint.assertIsSatisfied();
    }

}
