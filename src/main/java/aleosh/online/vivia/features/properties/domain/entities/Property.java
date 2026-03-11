package aleosh.online.vivia.features.properties.domain.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Property {
    private final UUID id;
    private final String city;
    private final String state;
    private final String neighborhood;
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
        this.city = builder.city;
        this.state = builder.state;
        this.neighborhood = builder.neighborhood;
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
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getNeighborhood() { return neighborhood; }
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
        private String city;
        private String state;
        private String neighborhood;
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

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder neighborhood(String neighborhood) {
            this.neighborhood = neighborhood;
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