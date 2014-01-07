package fuse.poc.blueprint.eclipselink;

import fuse.pocs.blueprint.eclipselink.CustomRollbackException;
import fuse.pocs.blueprint.eclipselink.Person;
import fuse.pocs.blueprint.eclipselink.PersonService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.BundleContext;

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

    @Inject
    BundleContext context;

    @Configuration
    public Option[] commonOptions() {

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
                configureConsole().ignoreLocalConsole(),
                logLevel(LogLevel.INFO),

                features(
                        maven().groupId("org.apache.karaf.assemblies.features").artifactId("enterprise").type("xml")
                                .classifier("features").version("2.3.3"), "transaction", "jndi", "jpa"),

                mavenBundle("org.hsqldb", "hsqldb", "2.3.1"),
                mavenBundle("org.apache.commons", "commons-lang3", "3.1"),
                mavenBundle("com.google.guava", "guava", "15.0"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.asm", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.antlr", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa.jpql", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.core", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa", "2.4.2"),

//                provision("file:///NotBackedUp/hkonsek/labs/apache-karaf-2.3.3/deploy/blueprint.xml"),

////
                mavenBundle("com.github.lburgazzoli", "karaf-examples-jpa-common", "1.0.0.SNAPSHOT"),
                mavenBundle("com.github.lburgazzoli", "karaf-examples-jpa-eclipselink-adapter", "1.0.0.SNAPSHOT"),
                mavenBundle("fuse-pocs", "fuse-pocs-blueprint-eclipselink-bundle", "0.1-SNAPSHOT"),

                junitBundles(),
//                debugConfiguration("8889", true)

        };
    }

    @Test
    public void shouldSavePerson() {
        // Given
        Person person = new Person("John");

        // When
        personService.save(person);

        // Then
        Person loadedPerson = personService.findByName(person.getName());
        assertEquals(person.getName(), loadedPerson.getName());
    }

    @Test
    public void shouldRollbackSave() {
        // Given
        Person person = new Person("Henry");

        // When
        try {
            personService.saveAndRollback(person);
        } catch (CustomRollbackException e) {
        }

        // Then
        Person loadedPerson = personService.findByName(person.getName());
        assertNull(loadedPerson);
    }

}