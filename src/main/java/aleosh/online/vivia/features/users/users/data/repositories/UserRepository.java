package aleosh.online.vivia.features.users.users.data.repositories;

import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.fcmToken = :token WHERE u.id = :userId")
    void updateFcmToken(@Param("userId") UUID userId, @Param("token") String token);

    @Query("SELECT u.fcmToken FROM UserEntity u WHERE u.id = :userId")
    Optional<String> findFcmTokenByUserId(@Param("userId") UUID userId);
}
