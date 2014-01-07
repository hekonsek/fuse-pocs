package fuse.pocs.blueprint.eclipselink;

public interface PersonService {

    void save(Person person);

    void saveAndRollback(Person person);

    Person findByName(String name);

}
