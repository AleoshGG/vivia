package aleosh.online.vivia.features.users.lessor.services.impl;

import aleosh.online.vivia.features.users.lessor.data.dtos.request.CreateLessorDto;
import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
import aleosh.online.vivia.features.users.lessor.data.entities.LessorEntity;
import aleosh.online.vivia.features.users.lessor.data.entities.PasskeyCredentialEntity;
import aleosh.online.vivia.features.users.lessor.data.repositories.LessorRepository;
import aleosh.online.vivia.features.users.lessor.services.ILessorService;
//import aleosh.online.vivia.features.users.lessor.services.IStorageService;
import aleosh.online.vivia.features.users.lessor.services.mappers.LessorMapper;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessorServiceImpl implements ILessorService {

    private final LessorRepository lessorRepository;
    private final LessorMapper lessorMapper;

    /*private final IStorageService storageService;

    @Value("${default.photoUrl}")
    private String defaultPhotoUrl;*/

    public LessorServiceImpl(
            LessorRepository lessorRepository,
            @org.springframework.beans.factory.annotation.Qualifier("lessorServiceMapper")  LessorMapper lessorMapper,
            PasswordEncoder passwordEncoder
            /*,IStorageService storageService*/
    ) {
        this.lessorRepository = lessorRepository;
        this.lessorMapper = lessorMapper;
    }


    @Override
    public LessorResponseDto createLessor(CreateLessorDto createLessorDto /*,MultipartFile file*/) {
        /*String imageKey = null;
        String url = defaultPhotoUrl;

        if (file != null && !file.isEmpty()) {
            try {
                // 1. Subimos la imagen a S3
                imageKey = storageService.uploadFile(file);
                url = storageService.getFileUrl(imageKey);

            } catch (IOException e) {
                throw new RuntimeException("Error al subir la imagen", e);
            }
        }*/

        LessorEntity lessorEntity = new LessorEntity();
        lessorEntity.setFirstName(createLessorDto.getFirstName());
        lessorEntity.setLastName(createLessorDto.getLastName());
        lessorEntity.setCompanyName(createLessorDto.getCompanyName());

        if (createLessorDto.getPasskey() != null) {
            PasskeyCredentialEntity credentialEntity = new PasskeyCredentialEntity();

            // Decodificación del ID de la credencial desde Base64Url a byte[]
            byte[] credentialId = Base64.getUrlDecoder().decode(createLessorDto.getPasskey().getId());
            credentialEntity.setCredentialId(credentialId);

            // ADVERTENCIA DE ARQUITECTURA:
            // El AttestationObject NO es la llave pública cruda. Es un objeto codificado en CBOR.
            // Para propósitos de compilación y estructura inicial, lo decodificamos así,
            // pero esto fallará en un flujo real sin una librería WebAuthn.
            byte[] attestationBytes = Base64.getUrlDecoder().decode(createLessorDto.getPasskey().getAttestationObject());
            credentialEntity.setPublicKey(attestationBytes);

            credentialEntity.setSignCount(0); // Contador inicial en 0

            // Se utiliza el método helper de la entidad para mantener la relación bidireccional
            lessorEntity.addCredential(credentialEntity);
        }

        LessorEntity savedLessorEntity = lessorRepository.save(lessorEntity);
        return lessorMapper.toLessorResponseDto(savedLessorEntity);
    }

    @Override
    public LessorResponseDto getLessorByCompanyName(String companyName) {
        return lessorRepository.findByCompanyName(companyName)
                .map(lessorMapper::toLessorResponseDto)
                .orElseThrow(()-> new RuntimeException("No existe el arrendador"));
    }

    @Override
    public LessorResponseDto getLessorByUsername(String username) {
        return lessorRepository.findByCompanyName(username)
                .map(lessorMapper::toLessorResponseDto)
                .orElseThrow(()-> new RuntimeException("No existe el arrendador"));
    }

    @Override
    public List<LessorResponseDto> getAllLessors() {
        return lessorRepository.findAll().stream()
                .map(lessorMapper::toLessorResponseDto)
                .collect(Collectors.toList());
    }
}
