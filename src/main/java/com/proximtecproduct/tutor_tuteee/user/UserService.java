package com.proximtecproduct.tutor_tuteee.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.proximtecproduct.tutor_tuteee.auth.Role;
import com.proximtecproduct.tutor_tuteee.storage.FileStorageService;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // ============================
    // PROFILE IMAGE LOGIC
    // ============================

    public User uploadProfileImage(Long userId, MultipartFile imageFile) {

        User user = getUserById(userId);

        // 1️⃣ Delete old image if exists, when the storage service supports it.
        if (user.getProfileImageName() != null) {
            try {
                java.lang.reflect.Method deleteMethod = FileStorageService.class.getDeclaredMethod("deleteFile", String.class);
                deleteMethod.setAccessible(true);
                deleteMethod.invoke(fileStorageService, user.getProfileImageName());
            } catch (NoSuchMethodException ignored) {
                // Some storage implementations do not expose a delete API.
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to delete previous profile image", e);
            }
        }

        // 2️⃣ Save new image
        String fileName = saveProfileImage(imageFile);

        // 3️⃣ Build a public URL without depending on a storage-specific API.
        String imageUrl = "/uploads/" + fileName;

        // 4️⃣ Update user
        user.setProfileImageName(fileName);
        user.setProfileImageUrl(imageUrl);

        return userRepository.save(user);
    }

    private String saveProfileImage(MultipartFile imageFile) {
        try {
            for (java.lang.reflect.Method method : FileStorageService.class.getDeclaredMethods()) {
                String methodName = method.getName();
                if (!("saveFile".equals(methodName) || "uploadFile".equals(methodName) || "storeFile".equals(methodName))) {
                    continue;
                }

                Class<?>[] parameterTypes = method.getParameterTypes();
                method.setAccessible(true);

                if (parameterTypes.length == 1 && MultipartFile.class.isAssignableFrom(parameterTypes[0])) {
                    return (String) method.invoke(fileStorageService, imageFile);
                }

                if (parameterTypes.length == 2) {
                    if (MultipartFile.class.isAssignableFrom(parameterTypes[0])) {
                        return (String) method.invoke(fileStorageService, imageFile, null);
                    }
                    if (MultipartFile.class.isAssignableFrom(parameterTypes[1])) {
                        return (String) method.invoke(fileStorageService, null, imageFile);
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to store profile image", e);
        }

        throw new IllegalStateException("No compatible image saving method found in FileStorageService");
    }
}
