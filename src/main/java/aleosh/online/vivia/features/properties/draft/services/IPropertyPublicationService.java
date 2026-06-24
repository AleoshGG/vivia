package aleosh.online.vivia.features.properties.draft.services;

import aleosh.online.vivia.features.properties.draft.domain.entities.PropertyDraft;

public interface IPropertyPublicationService {

    void publish(PropertyDraft draft);
}
