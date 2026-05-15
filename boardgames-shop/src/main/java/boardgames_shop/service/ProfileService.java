package boardgames_shop.service;

import boardgames_shop.entity.Client;
import boardgames_shop.entity.User;
import boardgames_shop.dto.profile.ProfileResponse;
import boardgames_shop.dto.profile.UpdateProfileRequest;
import boardgames_shop.repository.ClientRepository;
import boardgames_shop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProfileService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public ProfileService(
            ClientRepository clientRepository,
            UserRepository userRepository
    ) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public ProfileResponse getProfile() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Client client = clientRepository
                .findByUserId(user.getId())
                .orElseThrow();

        ProfileResponse response = new ProfileResponse();

        response.setId(client.getId());
        response.setName(client.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(client.getPhone());
        response.setBirthDate(client.getBirthDate().toString());

        return response;
    }

    public void updateProfile(UpdateProfileRequest request) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Client client = clientRepository
                .findByUserId(user.getId())
                .orElseThrow();

        client.setFullName(request.getName());
        client.setPhone(request.getPhone());
        client.setBirthDate(java.time.LocalDate.parse(request.getBirthDate()));

        clientRepository.save(client);
    }

    public void deleteProfile() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Client client = clientRepository
                .findByUserId(user.getId())
                .orElseThrow();

        clientRepository.delete(client);
        userRepository.delete(user);
    }
}