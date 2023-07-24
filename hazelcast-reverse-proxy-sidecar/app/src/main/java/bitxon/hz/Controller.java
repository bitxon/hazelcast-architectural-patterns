package bitxon.hz;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/{type}/{key}")
    public String get(@PathVariable("type") String type, @PathVariable("key") String key) {
        return "You missed a proxy cache and get value from service";
    }

    @PutMapping("/{type}/{key}/{value}")
    public String put(@PathVariable("type") String type, @PathVariable("key") String key, @PathVariable("value") String value) {
        return value;
    }
}
