package aleosh.online.vivia.features.properties.data.entities;

import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String neighborhood;

    @Column(name = "department_type")
    private String departmentType;

    private Double area;

    @Column(name = "rooms_number")
    private int roomsNumber;

    @Column(name = "bathrooms_number")
    private int bathroomsNumber;

    @Column(name = "parking_number")
    private int parkingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessor_id", nullable = false)
    private LessorEntity lessor;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PropertyImageEntity> images = new ArrayList<>();

    public void addImage(PropertyImageEntity image) {
        images.add(image);
        image.setProperty(this);
    }

    public void removeImage(PropertyImageEntity image) {
        images.remove(image);
        image.setProperty(null);
    }
}
