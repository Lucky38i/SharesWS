package ntu.n0696066.controller;

import ntu.n0696066.model.User;
import ntu.n0696066.payload.ApiResponse;
import ntu.n0696066.repository.UserRepository;
import ntu.n0696066.security.CurrentUser;
import ntu.n0696066.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/getusershares")
    @ResponseBody
    public ResponseEntity<?> currentUser(@CurrentUser UserPrincipal currentUser) {
        User tempUser = userRepository.findById(currentUser.getId()).orElse(null);
        assert tempUser != null;
        return ResponseEntity.ok(tempUser);
    }
}
