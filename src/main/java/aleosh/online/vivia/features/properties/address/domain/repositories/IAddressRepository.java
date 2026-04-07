package aleosh.online.vivia.features.properties.address.domain.repositories;

import aleosh.online.vivia.features.properties.address.domain.entities.Address;

public interface IAddressRepository {
    Address save(Address address);
    void delete(String id);
}
