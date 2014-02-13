package fuse.pocs.blueprint.openjpa;

public interface PersonService {

    void save(Person person);

    void saveAndRollback(Person person);

    Person findByName(String name);

}
