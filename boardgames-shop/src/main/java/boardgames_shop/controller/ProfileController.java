package boardgames_shop.controller;

import boardgames_shop.dto.profile.ProfileResponse;
import boardgames_shop.dto.profile.UpdateProfileRequest;
import boardgames_shop.service.ProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileResponse getProfile() {
        return profileService.getProfile();
    }

    @PutMapping
    public void updateProfile(
            @RequestBody UpdateProfileRequest request
    ) {
        profileService.updateProfile(request);
    }

    @DeleteMapping
    public void deleteProfile() {
        profileService.deleteProfile();
    }
}