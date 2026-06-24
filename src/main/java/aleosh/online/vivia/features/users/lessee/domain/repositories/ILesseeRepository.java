package aleosh.online.vivia.features.users.lessee.domain.repositories;

import aleosh.online.vivia.features.users.lessee.domain.entities.Lessee;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ILesseeRepository {
    Lessee save(Lessee lessee);
    Optional<Lessee> getById(UUID id);
    void delteById(UUID id);
}