package fuse.poc.blueprint.eclipselink;

import fuse.pocs.blueprint.eclipselink.bundle1.Person;
import fuse.pocs.blueprint.eclipselink.bundle1.PersonService;
import fuse.pocs.blueprint.eclipselink.bundle2.Address;
import fuse.pocs.blueprint.eclipselink.bundle2.AddressService;
import fuse.pocs.blueprint.eclipselink.composedbundle.ComposedService;
import fuse.pocs.blueprint.eclipselink.composedbundle.CustomRollbackException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;
import java.io.File;

import static java.util.concurrent.TimeUnit.SECONDS;
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
    ComposedService composedService;

    @Inject
    PersonService personService;

    @Inject
    AddressService addressService;

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

                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.asm", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.antlr", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa.jpql", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.core", "2.4.2"),
                mavenBundle("org.eclipse.persistence", "org.eclipse.persistence.jpa", "2.4.2"),

                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-blueprint-eclipselink-interbundletx-adapter").versionAsInProject(),
                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-blueprint-eclipselink-interbundletx-bundle1").versionAsInProject(),
                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-blueprint-eclipselink-interbundletx-bundle2").versionAsInProject(),
                mavenBundle().groupId("fuse-pocs").artifactId("fuse-pocs-blueprint-eclipselink-interbundletx-composedbundle").versionAsInProject()
        };
    }

    @Test
    public void shouldRollbackPersonAndAddress() {
        // Given
        Person person = new Person("Henry");
        Address address = new Address("Wall street 1");

        // When
        try {
            composedService.rollbackAll(person, address);
        } catch (CustomRollbackException e) {
            // Then
            Person loadedPerson = personService.findByName(person.getName());
            Address loadedAddress = addressService.findByStreet(address.getStreet());
            assertNull(loadedPerson);
            assertNull(loadedAddress);
            return;
        }
        fail("Should throw rollback exception.");
    }

    @Test
    public void shouldSavePersonAndAddress() {
        // Given
        Person person = new Person("John");
        Address address = new Address("Wall street 2");

        // When
        composedService.saveAll(person, address);

        // Then
        Person loadedPerson = personService.findByName(person.getName());
        Address loadedAddress = addressService.findByStreet(address.getStreet());
        assertNotNull(loadedPerson);
        assertNotNull(loadedAddress);
    }

    @Test
    public void shouldNotSavePersonBeforeCommit() throws InterruptedException {
        // Given
        final Person person = new Person("Fred");
        final Address address = new Address("Wall street 3");

        // When
        new Thread() {
            @Override
            public void run() {
                composedService.saveDelayed(person, address);
            }
        }.start();

        // Then
        SECONDS.sleep(5);
        Person loadedPerson = personService.findByName(person.getName());
        Address loadedAddress = addressService.findByStreet(address.getStreet());
        assertNull(loadedPerson);
        assertNull(loadedAddress);

        do {
            loadedPerson = personService.findByName(person.getName());
            SECONDS.sleep(1);
        } while (loadedPerson == null);
        loadedAddress = addressService.findByStreet(address.getStreet());
        assertNotNull(loadedAddress);
    }


}