package fuse.pocs.blueprint.beanvalidator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.io.File;
import java.util.Set;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;

@RunWith(PaxExam.class)
public class BeanValidatorTest extends Assert {

    @Inject
    ValidatorBean validatorBean;

    Event invalidEvent = new Event("foo@gmail.com", "foo@gmail.com", "bar@gmail.com", "baz@gmail.com");

    @Configuration
    public Option[] configuration() {
        return new Option[]{
                karafDistributionConfiguration()
                        .frameworkUrl(
                                maven().groupId("org.apache.karaf")
                                        .artifactId("apache-karaf")
                                        .type("zip")
                                        .version("2.3.3"))
                        .karafVersion("2.3.3")
                        .name("Apache Karaf")
                        .unpackDirectory(new File("target/pax"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                configureConsole().ignoreLocalConsole().ignoreRemoteShell(),

                logLevel(LogLevel.INFO),

                features(
                        maven().groupId("org.apache.camel.karaf").artifactId("apache-camel").
                                type("xml").classifier("features").version("2.12.2"),
                        "camel-bean-validator"),

                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-blueprint-beanvalidator-bundle").versionAsInProject(),
        };
    }

    // Tests

    @Test
    public void shouldInjectSingletonList() {
        // When
        Set<ConstraintViolation<Event>> errors = validatorBean.validateEvent(invalidEvent);

        // Then
        assertEquals("Interpolated message!", errors.iterator().next().getMessage());
    }

}
