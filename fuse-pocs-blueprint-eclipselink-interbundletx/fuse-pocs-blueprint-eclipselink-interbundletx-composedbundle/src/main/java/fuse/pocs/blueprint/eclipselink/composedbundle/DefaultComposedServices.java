package fuse.pocs.blueprint.eclipselink.composedbundle;

import fuse.pocs.blueprint.eclipselink.bundle1.Person;
import fuse.pocs.blueprint.eclipselink.bundle1.PersonService;
import fuse.pocs.blueprint.eclipselink.bundle2.Address;
import fuse.pocs.blueprint.eclipselink.bundle2.AddressService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class DefaultComposedServices implements ComposedService {

    private final PersonService personService;

    private final AddressService addressService;

    public DefaultComposedServices(PersonService personService, AddressService addressService) {
        this.personService = personService;
        this.addressService = addressService;
    }

    @Override
    public void saveAll(Person person, Address address) {
        personService.save(person);
        addressService.save(address);
    }

    @Override
    public void rollbackAll(Person person, Address address) {
        personService.save(person);
        addressService.save(address);
        throw new CustomRollbackException();
    }

    @Override
    public void saveDelayed(Person person, Address address) {
        personService.save(person);
        try {
            SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        addressService.save(address);
    }

}