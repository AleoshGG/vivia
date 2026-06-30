package aleosh.online.vivia.features.properties.draft.services;

import aleosh.online.vivia.features.properties.draft.data.dtos.response.PublishedPropertySseDto;
import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;

public interface IPropertyPublicationService {

    PublishedPropertySseDto publish(PropertyDraft draft);
}
