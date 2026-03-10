package aleosh.online.vivia.features.users.lessee.domain.repositories;

import aleosh.online.vivia.features.users.lessee.domain.entities.Lessee;
import java.util.List;
import java.util.Optional;

public interface ILesseeRepository {
    Lessee save(Lessee lessee);
    Optional<Lessee> getByUsername(String username);
    Optional<Lessee> getByEmail(String email);
    List<Lessee> getAllLessees();
}