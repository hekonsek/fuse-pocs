package fuse.pocs.blueprint.hibernate.raw;

import java.util.List;

public interface PersonService {

    void save(Person person);

    List<Person> findAll();

}
