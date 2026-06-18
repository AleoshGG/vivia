package aleosh.online.vivia.features.users.lessor.domain.repositories;

import aleosh.online.vivia.features.users.lessor.domain.entities.Lessor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ILessorRepository {
    Lessor save(Lessor Lessor);
    Optional<Lessor> getById(UUID id);
    void deleteById(UUID id);
}
