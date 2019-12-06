package ntu.n0696066.controller;

import ntu.n0696066.dao.UserRepository;
import ntu.n0696066.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    UserRepository userRepo;
    final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    @ResponseBody
    public String registerUser(User user){
        User temp = userRepo.findByUsername(user.getUsername());
        System.out.println(temp);
        return "Success";
    }


    @RequestMapping("/register")
    public String registerTemp(@RequestParam String username) {
        String returnVal = "";
        try {
            log.info("Username: " + username);
            User temp = userRepo.findByUsername(username);
            returnVal = "Success";
        } catch (NullPointerException e) {
            log.info("User doesn't exist");
            returnVal = "Failure";
        }
        return returnVal;
    }
}
