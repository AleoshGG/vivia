package aleosh.online.vivia.features.properties.properties.domain.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Property {
    private final UUID id;
    private final String address_id;
    private final String departmentType;
    private final Double price;
    private final Double area;
    private final int roomsNumber;
    private final int bathroomsNumber;
    private final int parkingNumber;
    private final String title;
    private final String description;
    private final UUID lessorId; // Relación con el arrendador en el dominio
    private final List<PropertyImage> images; // Relación con las imágenes

    // Constructor privado que recibe el Builder
    private Property(Builder builder) {
        this.id = builder.id;
        this.address_id = builder.address_id;
        this.departmentType = builder.departmentType;
        this.price = builder.price;
        this.area = builder.area;
        this.roomsNumber = builder.roomsNumber;
        this.bathroomsNumber = builder.bathroomsNumber;
        this.parkingNumber = builder.parkingNumber;
        this.title = builder.title;
        this.description = builder.description;
        this.lessorId = builder.lessorId;
        this.images = builder.images != null ? new ArrayList<>(builder.images) : new ArrayList<>();
    }

    // --- Getters ---
    public UUID getId() { return id; }
    public String getAddress_id() { return address_id; }
    public String getDepartmentType() { return departmentType; }
    public Double getPrice() { return price; }
    public Double getArea() { return area; }
    public int getRoomsNumber() { return roomsNumber; }
    public int getBathroomsNumber() { return bathroomsNumber; }
    public int getParkingNumber() { return parkingNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public UUID getLessorId() { return lessorId; }
    public List<PropertyImage> getImages() { return Collections.unmodifiableList(images); }

    // --- Clase Builder Estática ---
    public static class Builder {
        private UUID id;
        private String address_id;
        private String departmentType;
        private Double price;
        private Double area;
        private int roomsNumber;
        private int bathroomsNumber;
        private int parkingNumber;
        private String title;
        private String description;
        private UUID lessorId;
        private List<PropertyImage> images = new ArrayList<>();

        // Métodos del builder que retornan 'this' para el encadenamiento
        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder address_id(String address_id) {
            this.address_id = address_id;
            return this;
        }

        public Builder departmentType(String departmentType) {
            this.departmentType = departmentType;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder area(Double area) {
            this.area = area;
            return this;
        }

        public Builder roomsNumber(int roomsNumber) {
            this.roomsNumber = roomsNumber;
            return this;
        }

        public Builder bathroomsNumber(int bathroomsNumber) {
            this.bathroomsNumber = bathroomsNumber;
            return this;
        }

        public Builder parkingNumber(int parkingNumber) {
            this.parkingNumber = parkingNumber;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder lessorId(UUID lessorId) {
            this.lessorId = lessorId;
            return this;
        }

        public Builder addImage(PropertyImage image) {
            this.images.add(image);
            return this;
        }

        public Builder images(List<PropertyImage> images) {
            this.images = images;
            return this;
        }

        // Método final que construye y retorna la entidad
        public Property build() {
            // Aquí puedes agregar validaciones de dominio (ej. requireNonNull)
            // antes de instanciar el objeto.
            return new Property(this);
        }
    }
}