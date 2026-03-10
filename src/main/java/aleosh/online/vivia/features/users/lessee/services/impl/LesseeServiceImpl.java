package aleosh.online.vivia.features.users.lessee.services.impl;

import aleosh.online.vivia.features.users.lessee.data.dtos.request.CreateLesseeDto;
import aleosh.online.vivia.features.users.lessee.data.dtos.response.LesseeResponseDto;
import aleosh.online.vivia.features.users.lessee.data.entities.LesseeEntity;
import aleosh.online.vivia.features.users.lessee.data.repositories.LesseeRepository;
import aleosh.online.vivia.features.users.lessee.services.ILesseeService;
import aleosh.online.vivia.features.users.lessee.services.mappers.LesseeMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LesseeServiceImpl implements ILesseeService {

    private final LesseeRepository lesseeRepository;
    private final LesseeMapper lesseeMapper;

    public LesseeServiceImpl(
            LesseeRepository lesseeRepository,
            @org.springframework.beans.factory.annotation.Qualifier("lesseeServiceMapper") LesseeMapper lesseeMapper
    ) {
        this.lesseeRepository = lesseeRepository;
        this.lesseeMapper = lesseeMapper;
    }

    @Override
    public LesseeResponseDto createLessee(CreateLesseeDto createLesseeDto) {
        LesseeEntity lesseeEntity = new LesseeEntity();
        lesseeEntity.setUsername(createLesseeDto.getUsername());
        lesseeEntity.setEmail(createLesseeDto.getEmail());

        LesseeEntity savedLesseeEntity = lesseeRepository.save(lesseeEntity);
        return lesseeMapper.toLesseeResponseDto(savedLesseeEntity);
    }

    @Override
    public LesseeResponseDto getLesseeByUsername(String username) {
        return lesseeRepository.findByUsername(username)
                .map(lesseeMapper::toLesseeResponseDto)
                .orElseThrow(() -> new RuntimeException("No existe el arrendatario"));
    }

    @Override
    public LesseeResponseDto getLesseeByEmail(String email) {
        return lesseeRepository.findByEmail(email)
                .map(lesseeMapper::toLesseeResponseDto)
                .orElseThrow(() -> new RuntimeException("No existe el arrendatario"));
    }

    @Override
    public List<LesseeResponseDto> getAllLessees() {
        return lesseeRepository.findAll().stream()
                .map(lesseeMapper::toLesseeResponseDto)
                .collect(Collectors.toList());
    }
}