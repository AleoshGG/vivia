package aleosh.online.vivia.features.users.admin.data.repositories;

import aleosh.online.vivia.features.users.admin.data.entities.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminRepository extends JpaRepository<AdminEntity, UUID> {
}
