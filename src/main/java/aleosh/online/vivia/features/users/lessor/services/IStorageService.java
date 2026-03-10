package aleosh.online.vivia.features.users.lessor.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IStorageService {

    String uploadFile(MultipartFile file) throws IOException;
    String getFileUrl(String key);
    void deleteFile(String key);

}
