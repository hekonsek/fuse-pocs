package fuse.pocs.blueprint.openjpa.springdata;

import fuse.pocs.blueprint.openjpa.Person;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PersonRepositoryCustom {

    void rollbackAfterSave(Person person);

    void commitAfterSave(Person person);

}