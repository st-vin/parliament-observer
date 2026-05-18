package ke.co.bungesummary.web.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ke.co.bungesummary.api.dto.AuthRequest;
import ke.co.bungesummary.domain.entity.User;
import ke.co.bungesummary.domain.repository.UserRepository;
import ke.co.bungesummary.security.AuthCookies;
import ke.co.bungesummary.security.JwtService;
import ke.co.bungesummary.security.UserPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Profile("api")
public class AuthWebController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthWebController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("title", "Login");
        model.addAttribute("authRequest", new AuthRequest("", ""));
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("authRequest") AuthRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.email().trim().toLowerCase(), request.password()));
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtService.generateToken(principal.getId(), principal.getUsername());
            setTokenCookie(response, token);
            return "redirect:/";
        } catch (BadCredentialsException ex) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("title", "Sign Up");
        model.addAttribute("authRequest", new AuthRequest("", ""));
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("authRequest") AuthRequest request,
            RedirectAttributes redirectAttributes) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            redirectAttributes.addFlashAttribute("error", "Email already registered");
            return "redirect:/register";
        }
        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEmailVerified(false);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute(
                "message", "Account created. You can log in now (email verification coming soon).");
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(AuthCookies.JWT_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(AuthCookies.JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        long secondsUntilExpiry =
                (jwtService.parseToken(token).getExpiration().getTime() - System.currentTimeMillis())
                        / 1000L;
        cookie.setMaxAge((int) Math.max(secondsUntilExpiry, 0));
        response.addCookie(cookie);
    }
}
