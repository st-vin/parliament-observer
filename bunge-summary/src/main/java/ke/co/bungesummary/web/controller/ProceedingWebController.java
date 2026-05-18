package ke.co.bungesummary.web.controller;

import java.util.UUID;
import ke.co.bungesummary.api.dto.ProceedingDetailResponse;
import ke.co.bungesummary.api.service.ProceedingService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Profile("api")
public class ProceedingWebController {

    private final ProceedingService proceedingService;

    public ProceedingWebController(ProceedingService proceedingService) {
        this.proceedingService = proceedingService;
    }

    @GetMapping("/proceedings/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        ProceedingDetailResponse proceeding = proceedingService.getById(id);
        model.addAttribute("title", proceeding.title());
        model.addAttribute("proceeding", proceeding);
        return "proceeding-detail";
    }
}
