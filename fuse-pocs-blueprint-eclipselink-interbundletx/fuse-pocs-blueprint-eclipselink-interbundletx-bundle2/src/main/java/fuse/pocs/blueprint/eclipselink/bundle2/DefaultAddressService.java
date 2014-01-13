package fuse.pocs.blueprint.eclipselink.bundle2;

import javax.persistence.EntityManager;
import java.util.List;

public class DefaultAddressService implements AddressService {

    private final EntityManager entityManager;

    public DefaultAddressService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Address address) {
        entityManager.persist(address);
    }

    @Override
    public Address findByStreet(String street) {
        List<Address> addresses = entityManager.
                createQuery("select p from Address p where p.street = :street", Address.class).
                setParameter("street", street).
                getResultList();
        if (addresses.isEmpty()) {
            return null;
        }
        return addresses.get(0);
    }

}
