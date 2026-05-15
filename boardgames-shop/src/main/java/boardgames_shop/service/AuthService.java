package boardgames_shop.service;

import boardgames_shop.dto.auth.LoginRequest;
import boardgames_shop.dto.auth.LoginResponse;
import boardgames_shop.dto.auth.RegisterRequest;
import boardgames_shop.entity.*;
import boardgames_shop.repository.ClientRepository;
import boardgames_shop.repository.RoleRepository;
import boardgames_shop.repository.UserRepository;
import boardgames_shop.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       ClientRepository clientRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request) {

        Role role = roleRepository.findByName("BUYER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        user = userRepository.save(user);

        Client client = new Client();
        client.setUser(user);
        client.setFullName(request.getName());
        client.setPhone(request.getPhone());
        client.setBirthDate(request.getBirthDate());

        clientRepository.save(client);
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getRole().getName()
        );

        return new LoginResponse(
                user.getId(),
                user.getRole().getName(),
                token
        );
    }
}