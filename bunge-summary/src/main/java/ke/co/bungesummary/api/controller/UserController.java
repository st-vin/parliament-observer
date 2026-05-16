package ke.co.bungesummary.api.controller;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import ke.co.bungesummary.api.dto.AuthRequest;
import ke.co.bungesummary.api.dto.LoginResponse;
import ke.co.bungesummary.api.dto.RegisterResponse;
import ke.co.bungesummary.api.dto.VerifyEmailRequest;
import ke.co.bungesummary.domain.entity.User;
import ke.co.bungesummary.domain.repository.UserRepository;
import ke.co.bungesummary.security.JwtService;
import ke.co.bungesummary.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@Profile("api")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody AuthRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEmailVerified(false);

        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(saved.getId(), saved.getEmail(), saved.isEmailVerified()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.email().trim().toLowerCase(), request.password()));
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtService.generateToken(principal.getId(), principal.getUsername());
            return ResponseEntity.ok(
                    new LoginResponse(token, principal.getId(), principal.getUsername()));
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

  /** Stub: marks user verified when token matches user id (real email flow in later sprint). */
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {
        UUID userId;
        try {
            userId = UUID.fromString(request.token());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token");
        }

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "User not found"));

        user.setEmailVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("verified", true, "userId", user.getId()));
    }
}
