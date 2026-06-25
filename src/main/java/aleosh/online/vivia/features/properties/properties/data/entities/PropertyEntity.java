package aleosh.online.vivia.features.properties.properties.data.entities;

import aleosh.online.vivia.features.address.address.data.entities.AddressEntity;
import aleosh.online.vivia.features.properties.amenity.data.entities.AmenityEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", length = 50)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LessorEntity lessor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_type_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PropertyTypeEntity propertyType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AddressEntity address;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<PropertyMediaEntity> media = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_amenity",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<AmenityEntity> amenities = new ArrayList<>();

    @Column(name = "is_available_to_rent", nullable = false)
    private boolean isAvailableToRent = false;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "area_m2", nullable = false, precision = 8, scale = 2)
    private BigDecimal areaM2;

    @Column(name = "bedrooms", nullable = false)
    private Integer bedrooms;

    @Column(name = "bathrooms", nullable = false, precision = 3, scale = 1)
    private BigDecimal bathrooms;

    @Column(name = "parking_spaces")
    private Integer parkingSpaces;

    @Column(name = "construction_year")
    private Integer constructionYear;

    @Column(name = "is_condominium", nullable = false)
    private boolean isCondominium = false;

    @Column(name = "listed_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal listedPrice;

    @Column(name = "price_per_m2", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerM2;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
