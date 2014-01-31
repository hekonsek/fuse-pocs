package fuse.pocs.blueprint.openjpa.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long>, PersonRepositoryCustom {

    Person findByName(String name);

}
