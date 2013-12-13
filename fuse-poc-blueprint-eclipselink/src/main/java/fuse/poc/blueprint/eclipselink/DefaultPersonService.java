package fuse.poc.blueprint.eclipselink;

import javax.persistence.EntityManager;

/**
 * Created by hkonsek on 12/12/13.
 */
public class DefaultPersonService implements PersonService {

    private EntityManager em;

    @Override
    public Person getPerson(String name) {
        return (Person) getEm().createQuery("select p from Person p").getSingleResult();
    }

    @Override
    public void savePerson(Person person) {
        getEm().persist(person);
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
