package aleosh.online.vivia.features.users.lessor.domain.repositories;

import aleosh.online.vivia.features.users.lessor.domain.entities.Lessor;
import java.util.List;
import java.util.Optional;

public interface ILessorRepository {
    Lessor save(Lessor Lessor);
    Optional<Lessor> getByUsername(String Username);
    Optional<Lessor> getByCompanyName(String CompanyName);
    List<Lessor> getAllLessors();

}
