package be.ifosup.learning.webservice.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User controller
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public String userPage() {
        return "user/index";
    }
}
