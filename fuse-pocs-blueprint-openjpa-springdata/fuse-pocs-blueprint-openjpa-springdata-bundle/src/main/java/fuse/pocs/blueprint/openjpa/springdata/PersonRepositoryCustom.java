package fuse.pocs.blueprint.openjpa.springdata;

public interface PersonRepositoryCustom {

    void rollbackAfterSave(Person person);

}