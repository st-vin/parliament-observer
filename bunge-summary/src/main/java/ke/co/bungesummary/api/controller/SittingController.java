package ke.co.bungesummary.api.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sittings")
@Profile("api")
public class SittingController {

    /** Public read stub — full implementation in T-020. */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listSittings() {
        return ResponseEntity.ok(
                Map.of(
                        "content",
                        List.of(),
                        "page",
                        Map.of("number", 0, "size", 20, "totalElements", 0, "totalPages", 0)));
    }
}
