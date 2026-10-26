package com.proximtecproduct.tutor_tuteee.storage;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final Path uploadPath;
    private final String serverPort;

    // Inject the Dotenv bean via constructor
    public FileStorageService(Dotenv dotenv) {
        String uploadDir = dotenv.get("FILE_UPLOAD_DIR", "uploads");
        this.serverPort = dotenv.get("SERVER_PORT", "8080");

        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }
}