package aleosh.online.vivia.features.address.address.data.repositories;

import aleosh.online.vivia.features.address.address.data.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
}
