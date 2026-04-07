package aleosh.online.vivia.features.properties.address.domain.entities;

import java.util.UUID;

public class Address {
    private final UUID id;
    private final String address;
    private final String city;
    private final String state;
    private final String neighborhood;

    private Address(Builder builder) {
        this.id = builder.id;
        this.address = builder.address;
        this.city =  builder.city;
        this.state = builder.state;
        this.neighborhood = builder.neighborhood;
    }

    public UUID getId() { return id; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getNeighborhood() { return neighborhood; }

    public static class Builder {
        private UUID id;
        private String address;
        private String city;
        private String state;
        private String neighborhood;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
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

        public Address build() {
            return new Address(this);
        }
    }

}
