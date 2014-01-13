package fuse.pocs.blueprint.eclipselink.bundle2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = AUTO)
    private long id;

    private String street;

    public Address() {
    }

    public Address(String street) {
        this.street = street;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

}