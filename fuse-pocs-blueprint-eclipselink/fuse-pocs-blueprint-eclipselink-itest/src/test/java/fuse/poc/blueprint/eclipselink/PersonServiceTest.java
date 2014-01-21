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
public class PersonServiceTest extends Assert {

    @Inject
    PersonService personService;

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

                mavenBundle().groupId("org.hsqldb").artifactId("hsqldb").versionAsInProject(),

                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.asm").versionAsInProject(),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.antlr").versionAsInProject(),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.core").versionAsInProject(),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa").versionAsInProject(),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa.jpql").versionAsInProject(),

                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-blueprint-eclipselink-adapter").versionAsInProject(),
                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-blueprint-eclipselink-bundle").versionAsInProject()
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