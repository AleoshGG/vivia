package aleosh.online.vivia.features.address.address.data.repositories;

import aleosh.online.vivia.features.address.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.address.address.data.mappers.AddressMapper;
import aleosh.online.vivia.features.address.address.domain.entities.Address;
import aleosh.online.vivia.features.address.address.domain.exceptions.AddressNotFoundException;
import aleosh.online.vivia.features.address.address.domain.repositories.IAddressRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class AddressRepositoryAdapter implements IAddressRepository {

    private final AddressRepository repository;
    private final AddressMapper mapper;

    public AddressRepositoryAdapter(
            AddressRepository repository,
            @Qualifier("addressDataMapper") AddressMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Address save(Address address) {
        AddressEntity entity = mapper.toEntity(address);
        AddressEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new AddressNotFoundException("Address not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
