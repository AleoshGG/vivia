package aleosh.online.vivia.features.properties.draft.services;

import aleosh.online.vivia.features.properties.draft.data.dtos.request.CreatePropertyDraftRequestDto;
import aleosh.online.vivia.features.properties.draft.data.dtos.response.CreatePropertyDraftResponseDto;

import java.util.UUID;

public interface IPropertyDraftService {

    CreatePropertyDraftResponseDto createDraft(CreatePropertyDraftRequestDto request, UUID lessorId);
}
