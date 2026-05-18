package ke.co.bungesummary.web;

import ke.co.bungesummary.security.UserPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "ke.co.bungesummary.web")
@Profile("api")
public class WebModelAdvice {

    @ModelAttribute("currentUser")
    public UserPrincipal currentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return principal;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(@AuthenticationPrincipal UserPrincipal principal) {
        return principal != null;
    }
}
