package aleosh.online.vivia.features.users.lessor.data.repositories;


import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.mappers.LessorMapper;
import aleosh.online.vivia.features.users.lessor.domain.entities.Lessor;
import aleosh.online.vivia.features.users.lessor.domain.repositories.ILessorRepository;
import aleosh.online.vivia.features.users.users.domain.exceptions.UserNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class LessorRepositoryAdapter implements ILessorRepository {

    private final LessorRepository lessorRepository;
    private final LessorMapper  lessorMapper;

    public LessorRepositoryAdapter(
            LessorRepository lessorRepository,
            @org.springframework.beans.factory.annotation.Qualifier("lessorDataMapper") LessorMapper lessorMapper
    ) {
        this.lessorRepository = lessorRepository;
        this.lessorMapper = lessorMapper;
    }

    @Override
    public Lessor save(Lessor lessor) {
        LessorEntity lessorEntity = lessorMapper.toEntity(lessor);
        LessorEntity savedLessorEntity = lessorRepository.save(lessorEntity);
        return lessorMapper.toDomain(savedLessorEntity);
    }

    @Override
    public Optional<Lessor> getById(UUID id) {
        Optional<LessorEntity> lessorEntity = lessorRepository.findById(id);
        return lessorEntity.map(lessorMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        if (!lessorRepository.existsById(id)) {
            throw new UserNotFoundException("User lessor not found.");
        }
        lessorRepository.deleteById(id);
    }
}
