package aleosh.online.vivia.features.address.address.domain.repositories;

import aleosh.online.vivia.features.address.address.domain.entities.Address;
import java.util.UUID;

public interface IAddressRepository {
    Address save(Address address);
    void deleteById(UUID id);
}
