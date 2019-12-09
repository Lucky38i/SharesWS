package ntu.n0696066.controller;

import ntu.n0696066.security.CurrentUser;
import ntu.n0696066.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @RequestMapping(value = "/username")
    @ResponseBody
    public String currentUser(@CurrentUser UserPrincipal currentUser) {
        return currentUser.getUsername();
    }
}
