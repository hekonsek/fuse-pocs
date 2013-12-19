package fuse.poc.blueprint.eclipselink;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.io.File;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
//import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;
//import static org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel.INFO;

@RunWith(PaxExam.class)
public class PersonServiceTest extends Assert {

    @Inject
    PersonService personService;

    Person person = new Person("Henry");

    @Configuration
    public Option[] configure() {
//        throw new RuntimeException("xxx");
        return commonOptions();
    }

    public Option[] commonOptions() {

        return new Option[]{
//                mavenBundle( "org.apache.aries.blueprint", "org.apache.aries.blueprint", "1.0.0" ),
//                mavenBundle( "org.apache.aries", "org.apache.aries.util", "1.0.0" ),
//                mavenBundle( "org.apache.aries.proxy", "org.apache.aries.proxy", "1.0.0" ),

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
                // It is really nice if the container sticks around after the test so you can check the contents
                // of the data directory when things go wrong.
                keepRuntimeFolder(),
                // Don't bother with local console output as it just ends up cluttering the logs
                configureConsole().ignoreLocalConsole(),
                // Force the log level to INFO so we have more details during the test.  It defaults to WARN.
                logLevel(LogLevel.INFO),

                // Provision the container with some features: jndi, transaction, jpa
                features(
                        maven().groupId("org.apache.karaf.assemblies.features").artifactId("standard").type("xml")
                                .classifier("features").version("2.3.3"), "jndi", "transaction", "jpa"),

                mavenBundle("org.hsqldb", "hsqldb", "2.3.1"),
                mavenBundle("org.apache.commons", "commons-lang3", "3.1"),
                mavenBundle("com.google.guava", "guava", "15.0"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.asm", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.antlr", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa.jpql", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.core", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa", "2.4.2"),
//                mavenBundle("org.eclipse.persistence","org.eclipse.persistence.weaving","2.4.2"),

//
//                                                                        mavenBundle("com.github.lburgazzoli","lb-karaf-common","1.0.0.SNAPSHOT"),
                mavenBundle("com.github.lburgazzoli", "karaf-examples-jpa-common", "1.0.0.SNAPSHOT"),
                mavenBundle("com.github.lburgazzoli", "karaf-examples-jpa-eclipselink-adapter", "1.0.0.SNAPSHOT"),
//                                                                                                mavenBundle("com.github.lburgazzoli","karaf-examples-jpa-eclipselink","1.0.0.SNAPSHOT"),
                mavenBundle().groupId("camelpoc").artifactId("fuse-poc-blueprint-eclipselink").version("1.0-SNAPSHOT"),

                junitBundles(),
//                debugConfiguration("8889", true)

        };
    }

    @Test
    public void shouldSavePerson2() {
        // When
        personService.savePerson(person);
        Person loadedPerson = personService.getPerson(person.getName());

        // When
        assertNotNull(loadedPerson);
    }

}
