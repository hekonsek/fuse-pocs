package fuse.pocs.blueprint.openjpa;

import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

public class OsgiEntityManagerWrapperFactoryBean extends AbstractEntityManagerFactoryBean {

    private final EntityManagerFactory entityManagerFactory;

    public OsgiEntityManagerWrapperFactoryBean(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
        return entityManagerFactory;
    }

}