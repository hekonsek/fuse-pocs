package fuse.pocs.blueprint.hibernate.raw;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class DefaultPersonService implements PersonService {

    private final EntityManagerFactory entityManagerFactory;

    public DefaultPersonService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Person person) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(person);
        entityManager.getTransaction().commit();

        entityManager.close();
    }

    @Override
    public List<Person> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            return entityManager.createQuery("select i from Person i", Person.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

}