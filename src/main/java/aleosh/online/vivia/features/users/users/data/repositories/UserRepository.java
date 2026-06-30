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

    @Modifying
    @Query("UPDATE UserEntity u SET u.name = :name, u.paternalSurname = :paternalSurname, u.maternalSurname = :maternalSurname WHERE u.id = :userId")
    void updateName(@Param("userId") UUID userId, @Param("name") String name, @Param("paternalSurname") String paternalSurname, @Param("maternalSurname") String maternalSurname);

    @Modifying
    @Query("UPDATE UserEntity u SET u.email = :email WHERE u.id = :userId")
    void updateEmail(@Param("userId") UUID userId, @Param("email") String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.photoUrl = :photoUrl WHERE u.id = :userId")
    void updatePhotoUrl(@Param("userId") UUID userId, @Param("photoUrl") String photoUrl);
}
