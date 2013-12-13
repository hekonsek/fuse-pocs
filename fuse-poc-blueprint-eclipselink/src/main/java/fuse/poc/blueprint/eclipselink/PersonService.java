package fuse.poc.blueprint.eclipselink;

public interface PersonService {

    Person getPerson(String name);

    void savePerson(Person person);

}