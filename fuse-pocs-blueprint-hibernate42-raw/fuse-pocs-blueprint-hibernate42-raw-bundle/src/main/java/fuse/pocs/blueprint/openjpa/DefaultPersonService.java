package fuse.pocs.blueprint.openjpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.List;

public class DefaultPersonService implements PersonService {

    private final EntityManagerFactory entityManagerFactory;

    public DefaultPersonService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Person person) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(person);
            transaction.commit();
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public void saveAndRollback(Person person) {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(person);
            throw new CustomRollbackException();
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public Person findByName(String name) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            List<Person> people = entityManager.
                    createQuery("select p from Person p where p.name = :name", Person.class).
                    setParameter("name", name).
                    getResultList();
            if (people.isEmpty()) {
                return null;
            }
            return people.get(0);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

}
