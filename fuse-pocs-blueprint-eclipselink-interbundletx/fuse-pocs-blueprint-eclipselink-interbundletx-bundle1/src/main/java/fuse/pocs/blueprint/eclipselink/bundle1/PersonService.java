package fuse.pocs.blueprint.eclipselink.bundle1;

public interface PersonService {

    void save(Person person);

    Person findByName(String name);

}
