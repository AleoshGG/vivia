package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import java.util.List;

public class PropertyDetailResponseDto {

    private final PropertyDetailContentDto content;
    private final List<PropertyMediaResponseDto> contentMedia;

    public PropertyDetailResponseDto(PropertyDetailContentDto content, List<PropertyMediaResponseDto> contentMedia) {
        this.content = content;
        this.contentMedia = contentMedia;
    }

    public PropertyDetailContentDto getContent() { return content; }
    public List<PropertyMediaResponseDto> getContentMedia() { return contentMedia; }
}
