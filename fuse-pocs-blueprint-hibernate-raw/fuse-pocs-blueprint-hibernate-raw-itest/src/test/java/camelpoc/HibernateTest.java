package camelpoc;

import fuse.pocs.blueprint.hibernate.raw.Person;
import fuse.pocs.blueprint.hibernate.raw.PersonService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;

@RunWith(PaxExam.class)
public class HibernateTest extends Assert {

    @Inject
    PersonService persistenceProvider;

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

                // Test database bundle
                mavenBundle("org.hsqldb", "hsqldb", "2.3.1"),

                // Hibernate pseudo-feature
                bundle("mvn:org.apache.geronimo.specs/geronimo-jta_1.1_spec/1.1.1").startLevel(30),
                bundle("mvn:org.hibernate.javax.persistence/hibernate-jpa-2.1-api/1.0.0.Final").startLevel(30),
                bundle("mvn:org.apache.geronimo.specs/geronimo-servlet_3.0_spec/1.0").startLevel(30),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.antlr/2.7.7_5"),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.ant/1.8.2_2"),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.dom4j/1.6.1_5"),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.serp/1.14.1_1"),
                bundle("mvn:com.fasterxml/classmate/0.9.0"),
                bundle("mvn:org.javassist/javassist/3.18.1-GA"),
                bundle("mvn:org.jboss.spec.javax.security.jacc/jboss-jacc-api_1.4_spec/1.0.2.Final"),
                bundle("wrap:mvn:org.jboss/jandex/1.1.0.Final"),
                bundle("mvn:org.jboss.logging/jboss-logging/3.1.4.GA"),
                bundle("mvn:org.hibernate.common/hibernate-commons-annotations/4.0.4.Final"),
                bundle("mvn:org.hibernate/hibernate-core/4.3.0.Final"),
                bundle("mvn:org.hibernate/hibernate-entitymanager/4.3.0.Final"),
                bundle("mvn:org.hibernate/hibernate-osgi/4.3.0.Final"),

                // Tested module
                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-blueprint-hibernate-raw-bundle").versionAsInProject(),
        };
    }

    @Test
    public void shouldSavePerson() throws InterruptedException {
        // Given
        Person person = new Person("John");

        // When
        persistenceProvider.save(person);

        // Then
        List<Person> people = persistenceProvider.findAll();
        assertEquals(1, people.size());
        assertEquals(person.getName(), people.get(0).getName());
    }

}