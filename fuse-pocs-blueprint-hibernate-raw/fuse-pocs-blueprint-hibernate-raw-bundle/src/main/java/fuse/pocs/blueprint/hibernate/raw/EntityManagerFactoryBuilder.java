package fuse.pocs.blueprint.hibernate.raw;

import org.hibernate.dialect.HSQLDialect;
import org.hsqldb.jdbc.JDBCDriver;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import java.util.Properties;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.PASS;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.URL;
import static org.hibernate.cfg.AvailableSettings.USER;

public class EntityManagerFactoryBuilder {

    public static EntityManagerFactory build(PersistenceProvider persistenceProvider) {
        Properties properties = new Properties();
        properties.setProperty(DIALECT, HSQLDialect.class.getName());
        properties.setProperty(DRIVER, JDBCDriver.class.getName());
        properties.setProperty(URL, "jdbc:hsqldb:mem:testdb");
        properties.setProperty(USER, "sa");
        properties.setProperty(PASS, "");
        properties.setProperty(SHOW_SQL, "true");
        properties.setProperty(HBM2DDL_AUTO, "update");
        return persistenceProvider.createEntityManagerFactory("PERSON", properties);
    }

}
