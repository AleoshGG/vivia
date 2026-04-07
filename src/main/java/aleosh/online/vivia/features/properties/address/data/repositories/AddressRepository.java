package aleosh.online.vivia.features.properties.address.data.repositories;

import aleosh.online.vivia.features.properties.address.data.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
}
