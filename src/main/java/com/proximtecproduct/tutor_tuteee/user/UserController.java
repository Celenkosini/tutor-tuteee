package com.proximtecproduct.tutor_tuteee.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.proximtecproduct.tutor_tuteee.common.ApiResponse;
import com.proximtecproduct.tutor_tuteee.storage.FileStorageService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    // ============================
    // UPLOAD PROFILE IMAGE
    // ============================
    @PostMapping("/{id}/profile-image")
    public ResponseEntity<ApiResponse<User>> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image,
            Authentication auth) {

        // 🔐 Ensure user uploads ONLY their own image
        String email = auth.getName();
        User loggedInUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!loggedInUser.getId().equals(id)) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse<>(false, "You can only update your own profile image", null));
        }

        User updatedUser = userService.uploadProfileImage(id, image);
        return ResponseEntity.ok(ApiResponse.ok(updatedUser));
    }

    // ============================
    // SERVE PROFILE IMAGE
    // ============================
    @GetMapping("/photos/{fileName}")
    public ResponseEntity<Resource> getProfileImage(
            @PathVariable String fileName) {

        Resource resource = loadStoredFile(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    private Resource loadStoredFile(String fileName) {
        try {
            return (Resource) FileStorageService.class.getMethod("loadFile", String.class)
                    .invoke(fileStorageService, fileName);
        } catch (NoSuchMethodException e) {
            try {
                return (Resource) FileStorageService.class.getMethod("loadAsResource", String.class)
                        .invoke(fileStorageService, fileName);
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException("No supported file-loading method found on FileStorageService", ex);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to load profile image", e);
        }
    }
}
