package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import java.util.UUID;

public class PropertyDetailLessorDto {

    private final UUID id;
    private final String name;
    private final String paternalSurname;
    private final String maternalSurname;
    private final String photoUrl;

    public PropertyDetailLessorDto(UUID id, String name, String paternalSurname,
                                   String maternalSurname, String photoUrl) {
        this.id = id;
        this.name = name;
        this.paternalSurname = paternalSurname;
        this.maternalSurname = maternalSurname;
        this.photoUrl = photoUrl;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getPaternalSurname() { return paternalSurname; }
    public String getMaternalSurname() { return maternalSurname; }
    public String getPhotoUrl() { return photoUrl; }
}
