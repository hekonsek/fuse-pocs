package fuse.pocs.blueprint.eclipselink.bundle2;

public interface AddressService {

    void save(Address address);

    Address findByStreet(String street);

}
