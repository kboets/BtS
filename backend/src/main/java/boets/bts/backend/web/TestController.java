package boets.bts.backend.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/api")
public class TestController {

    @GetMapping("/current")
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
