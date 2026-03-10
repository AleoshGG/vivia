package aleosh.online.vivia.features.users.lessee.data.repositories;

import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.data.mappers.LesseeMapper;
import aleosh.online.vivia.features.users.lessee.domain.entities.Lessee;
import aleosh.online.vivia.features.users.lessee.domain.repositories.ILesseeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class LesseeRepositoryAdapter implements ILesseeRepository {

    private final LesseeRepository lesseeRepository;
    private final LesseeMapper lesseeMapper;

    public LesseeRepositoryAdapter(
            LesseeRepository lesseeRepository,
            @org.springframework.beans.factory.annotation.Qualifier("lesseeDataMapper") LesseeMapper lesseeMapper
    ) {
        this.lesseeRepository = lesseeRepository;
        this.lesseeMapper = lesseeMapper;
    }

    @Override
    public Lessee save(Lessee lessee) {
        LesseeEntity lesseeEntity = lesseeMapper.toEntity(lessee);
        LesseeEntity savedLesseeEntity = lesseeRepository.save(lesseeEntity);
        return lesseeMapper.toDomain(savedLesseeEntity);
    }

    @Override
    public Optional<Lessee> getByUsername(String username) {
        Optional<LesseeEntity> lesseeEntity = lesseeRepository.findByUsername(username);
        return lesseeEntity.map(lesseeMapper::toDomain);
    }

    @Override
    public Optional<Lessee> getByEmail(String email) {
        Optional<LesseeEntity> lesseeEntity = lesseeRepository.findByEmail(email);
        return lesseeEntity.map(lesseeMapper::toDomain);
    }

    @Override
    public List<Lessee> getAllLessees() {
        return lesseeRepository.findAll().stream()
                .map(lesseeMapper::toDomain)
                .collect(Collectors.toList());
    }
}