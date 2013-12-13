package fuse.poc.blueprint.eclipselink;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by hkonsek on 12/12/13.
 */
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
