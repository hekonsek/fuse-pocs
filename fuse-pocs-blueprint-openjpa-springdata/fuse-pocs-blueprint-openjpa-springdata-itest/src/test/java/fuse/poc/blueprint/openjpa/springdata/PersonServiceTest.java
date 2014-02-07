package fuse.poc.blueprint.openjpa.springdata;

import fuse.pocs.blueprint.openjpa.springdata.CustomRollbackException;
import fuse.pocs.blueprint.openjpa.springdata.Person;
import fuse.pocs.blueprint.openjpa.springdata.PersonRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;

@RunWith(PaxExam.class)
public class PersonServiceTest extends Assert {

    // Fixtures

    @Inject
    PersonRepository personService;

    @Configuration
    public Option[] commonOptions() {
        switchPlatformEncodingToUTF8();
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
                vmOption("-Dfile.encoding=UTF-8"),
                configureConsole().ignoreLocalConsole(),
                logLevel(LogLevel.INFO),

                features(
                        maven().groupId("org.apache.karaf.assemblies.features").artifactId("enterprise").type("xml")
                                .classifier("features").version("2.3.3"),
                        "transaction", "jndi", "jpa"),

                features(
                        maven().groupId("org.apache.camel.karaf").artifactId("apache-camel").
                                type("xml").classifier("features").version("2.12.2"),
                        "camel-spring", "spring-orm"),


                mavenBundle().groupId("org.hsqldb").artifactId("hsqldb").versionAsInProject(),

                // OpenJPA
                bundle("mvn:commons-lang/commons-lang/2.6"),
                bundle("mvn:commons-pool/commons-pool/1.6"),
                bundle("mvn:org.apache.geronimo.specs/geronimo-servlet_2.5_spec/1.2"),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.commons-dbcp/1.4_3"),
                bundle("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.serp/1.14.1_1"),
                mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.commons-collections").versionAsInProject(),
                mavenBundle().groupId("org.apache.xbean").artifactId("xbean-asm4-shaded").versionAsInProject(),
                mavenBundle().groupId("org.apache.openjpa").artifactId("openjpa").versionAsInProject(),

                // Spring Data JPA pseudo-feature
                mavenBundle().groupId("org.springframework.data").artifactId("spring-data-commons").versionAsInProject(),
                mavenBundle().groupId("org.springframework.data").artifactId("spring-data-jpa").versionAsInProject(),

                // Business bundle
                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-blueprint-openjpa-springdata-bundle").versionAsInProject()
        };
    }

    // Tests

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
            personService.rollbackAfterSave(person);
        } catch (CustomRollbackException e) {
        }

        // Then
        Person loadedPerson = personService.findByName(person.getName());
        assertNull(loadedPerson);
    }

    @Test
    public void shouldCommitPerson() {
        // Given
        Person person = new Person("Fred");

        // When
        personService.commitAfterSave(person);

        // Then
        Person loadedPerson = personService.findByName(person.getName());
        assertNotNull(loadedPerson);
        assertEquals(person.getName(), loadedPerson.getName());
    }

    // Helpers

    /**
     * Workaround for PAXEXAM-595 .
     */
    private void switchPlatformEncodingToUTF8() {
        try {
            System.setProperty("file.encoding", "UTF-8");
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}