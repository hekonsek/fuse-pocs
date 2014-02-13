package fuse.pocs.blueprint.openjpa;

import javax.persistence.EntityManager;
import java.util.List;

public class DefaultPersonService implements PersonService {

    private final EntityManager entityManager;

    public DefaultPersonService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Person person) {
        entityManager.persist(person);
    }

    @Override
    public void saveAndRollback(Person person) {
        save(person);
        throw new CustomRollbackException();
    }

    @Override
    public Person findByName(String name) {
        List<Person> people = entityManager.
                createQuery("select p from Person p where p.name = :name", Person.class).
                setParameter("name", name).
                getResultList();
        if (people.isEmpty()) {
            return null;
        }
        return people.get(0);
    }

}
