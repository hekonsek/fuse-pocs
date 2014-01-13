package fuse.pocs.blueprint.eclipselink.composedbundle;

import fuse.pocs.blueprint.eclipselink.bundle1.Person;
import fuse.pocs.blueprint.eclipselink.bundle2.Address;

public interface ComposedService {

    void saveAll(Person person, Address address);

    void rollbackAll(Person person, Address address);

    void saveDelayed(Person person, Address address);

}
