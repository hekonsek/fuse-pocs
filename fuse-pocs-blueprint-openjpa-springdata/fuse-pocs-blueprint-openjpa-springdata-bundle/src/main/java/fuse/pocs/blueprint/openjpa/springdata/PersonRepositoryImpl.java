package fuse.pocs.blueprint.openjpa.springdata;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class PersonRepositoryImpl implements PersonRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void rollbackAfterSave(Person person) {
        entityManager.persist(person);
        throw new CustomRollbackException();
    }

}