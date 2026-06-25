package aleosh.online.vivia.features.properties.likes.data.entities;

import aleosh.online.vivia.features.properties.properties.data.entities.PropertyEntity;
import aleosh.online.vivia.features.users.users.data.entities.UserEntity;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "property_likes")
public class PropertyLikeEntity {

    @EmbeddedId
    private PropertyLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("propertyId")
    @JoinColumn(name = "property_id")
    private PropertyEntity property;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public PropertyLikeEntity() {}

    public PropertyLikeId getId() { return id; }
    public void setId(PropertyLikeId id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public PropertyEntity getProperty() { return property; }
    public void setProperty(PropertyEntity property) { this.property = property; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
