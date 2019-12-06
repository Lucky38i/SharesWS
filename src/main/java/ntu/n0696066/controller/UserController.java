package ntu.n0696066.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ntu.n0696066.Application;
import ntu.n0696066.dao.SharesRepository;
import ntu.n0696066.dao.UserRepository;
import ntu.n0696066.model.SharePrice;
import ntu.n0696066.model.Shares;
import ntu.n0696066.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;

@RestController
public class UserController {

    MessageFormat globalQuote = new MessageFormat(
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={0}&apikey={1}");
    MessageFormat symbolSearch = new MessageFormat(
            "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=\"{0}\"&apikey=\"{1}\"");

    @Autowired
    UserRepository userRepo;
    @Autowired
    SharesRepository shareRepo;
    final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

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

    @RequestMapping("/tempuser")
    public User tempUser(@RequestParam String username, @RequestParam String password) {
        User tempUser = new User(username, password);
        try {
            Shares tempShare = new Shares();
            Object[] apiObjects = {"AMZN", Application.apiKey};
            ObjectMapper mapper = new ObjectMapper();
            JsonNode quoteResults = mapper.readValue(new URL(globalQuote.format(apiObjects)),
                    JsonNode.class);
            JsonNode searchResults = mapper.readValue(new URL(symbolSearch.format(apiObjects)),
                    JsonNode.class);
            SharePrice tempPrice = new SharePrice();
            LocalDate tempDate = LocalDate.parse(
                    quoteResults.get("Global Quote").get("07. latest trading day").asText());

            tempPrice.setCurrency(searchResults.get("bestMatches").get(0).get("8. currency").textValue());
            tempPrice.setValue(Float.parseFloat(quoteResults.get("Global Quote").get("05. price").textValue()));
            tempPrice.setLastUpdate(tempDate);

            tempShare.setCompanyName(searchResults.get("bestMatches").get(0).get("2. name").textValue());
            tempShare.setCompanySymbol("AMZN");
            tempShare.setSharesAmount(0);
            tempShare.setSharePrice(tempPrice);
            tempShare.setUser(tempUser);

            tempUser.getOwnedShares().add(tempShare);

            userRepo.save(tempUser);
            shareRepo.save(tempShare);


        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.REQUEST_TIMEOUT, "Server Unavailable", e);
        }
        return userRepo.findByUsername(tempUser.getUsername());
    }
}
