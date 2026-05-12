package edu.cit.badinas.instock.users;

import edu.cit.badinas.instock.auth.AuthResponse;
import edu.cit.badinas.instock.core.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for user-specific operations that live outside the auth flow.
 *
 * <p>Currently exposes only the avatar-upload endpoint required by the SDD:
 * {@code POST /api/v1/users/avatar} (multipart/form-data, field: {@code file}).
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    /**
     * Uploads a profile picture for the authenticated user.
     *
     * <p>Accepts {@code multipart/form-data} with a single field named {@code file}.
     * The server persists the image and stores its URL in the {@code avatar_url} column.
     *
     * @param currentUser the user resolved from the JWT
     * @param file        the uploaded image file
     * @return updated user data including the new {@code avatarUrl}
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AuthResponse>> uploadAvatar(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") MultipartFile file) {

        String avatarUrl = fileStorageService.store(file, currentUser.getId());

        currentUser.setAvatarUrl(avatarUrl);
        User saved = userRepository.save(currentUser);

        AuthResponse response = AuthResponse.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .role(saved.getRole().name())
                .avatarUrl(saved.getAvatarUrl())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Avatar updated", response));
    }
}
