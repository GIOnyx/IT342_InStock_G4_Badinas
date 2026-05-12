package edu.cit.badinas.instock.users;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Handles file persistence for user avatar images.
 *
 * <p>Files are stored under {@code ./uploads/avatars/} relative to the working
 * directory.  The returned URL is a server-relative path that Spring serves
 * from its static-resource handler (configured in {@code application.properties}).
 *
 * <h3>Validation</h3>
 * Only JPEG, PNG, GIF and WebP content-types are accepted.
 * Violations throw a {@link RuntimeException} with the SDD error code
 * {@code FILE-001: Invalid File Type}.
 */
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5 MB

    private final Path uploadDir;

    public FileStorageService() {
        this.uploadDir = Paths.get("uploads", "avatars").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create avatar upload directory: " + ex.getMessage());
        }
    }

    /**
     * Persists {@code file} to disk and returns the public URL path.
     *
     * @param file     the incoming multipart file
     * @param userId   owner's user ID (used to namespace the filename)
     * @return server-relative URL, e.g. {@code /uploads/avatars/42_a1b2c3.jpg}
     */
    public String store(MultipartFile file, Long userId) {
        validateFile(file);

        String extension = resolveExtension(file.getContentType());
        String filename = userId + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + extension;
        Path target = uploadDir.resolve(filename);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store avatar: " + ex.getMessage());
        }

        return "/uploads/avatars/" + filename;
    }

    // ── Private helpers ──────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("FILE-001: No file provided");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new RuntimeException("FILE-001: File exceeds the 5 MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("FILE-001: Invalid File Type — only JPEG, PNG, GIF and WebP are accepted");
        }
    }

    private String resolveExtension(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/gif"  -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };
    }
}
