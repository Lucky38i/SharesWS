package ntu.n0696066.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ntu.n0696066.Application;
import ntu.n0696066.model.User;
import ntu.n0696066.payload.ApiResponse;
import ntu.n0696066.repository.StockRepository;
import ntu.n0696066.repository.SharesRepository;
import ntu.n0696066.model.Stock;
import ntu.n0696066.model.Shares;
import ntu.n0696066.repository.UserRepository;
import ntu.n0696066.security.CurrentUser;
import ntu.n0696066.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/shares")
public class SharesController {

    MessageFormat globalQuote = new MessageFormat(
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={0}&apikey={1}");
    MessageFormat symbolSearch = new MessageFormat(
            "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=\"{0}\"&apikey=\"{1}\"");

    @Autowired
    SharesRepository shareRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    StockRepository stockRepo;

    @Autowired
    ObjectMapper mapper = JsonMapper.builder().build();

    Logger logger = LoggerFactory.getLogger(SharesController.class);

    /**
     * Finds and returns the currently held shares of the currently logged in user
     * @param currentUser Retrieve the currently logged in user
     * @return Returns a list of shares owned by the user
     */
    @RequestMapping("/getshares")
    public ResponseEntity<?> retrieveShares(@CurrentUser UserPrincipal currentUser) {
        User tempUser = userRepo.findById(currentUser.getId()).orElse(null);
        if (tempUser != null ) return ResponseEntity.ok(tempUser.getOwnedShares());
        else return ResponseEntity.notFound().build();
    }

    @PostMapping("/purchasestock")
    public ResponseEntity<?> purchaseStock(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody Shares shares) {

        User tempUser = userRepo.findById(currentUser.getId()).orElse(null);

        if (tempUser != null) {
            tempUser.getOwnedShares().add(shares);
            userRepo.save(tempUser);
            return ResponseEntity.ok(new ApiResponse(true, "Stock purchased"));
        }else {
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Updates the share object when a client decides to sell or purchase more shares
     * @param shareToUpdate CurrentShares object containing current shares to be added to current stock
     * @return Returns status
     */
    @PutMapping("/updateshares")
    public ResponseEntity<?> updateShares(@CurrentUser UserPrincipal currentUser,
                                          @Valid @RequestBody Shares shareToUpdate){
        User tempUser = userRepo.findById(currentUser.getId()).orElse(null);

        if (tempUser != null) {
            // Hashset does not keep duplicates so adding just updates the existing share
            tempUser.getOwnedShares().add(shareToUpdate);
            userRepo.save(tempUser);
            return ResponseEntity.ok(new ApiResponse(true, "Stock purchased"));
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Used as a search function that will return stock details based on share symbol
     * @param shareSymbol The symbol used to help find stocks
     * @return Returns JSON Object
     */
    @RequestMapping("/liststock")
    public ResponseEntity<?> listStock(@RequestParam(value="sharesymbol") String shareSymbol){
        JsonNode foundShare = null;
        try {
            Object[] apiObjects = {shareSymbol, Application.apiKey};
            foundShare = mapper.readValue(new URL(symbolSearch.format(apiObjects)), JsonNode.class);
        } catch (IOException e) {
            logger.warn("Alphavantage is down");
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Alphavantage is down", e);
        }
        if (foundShare != null) return ResponseEntity.ok(foundShare);
        else return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves the specified stock item based on the given symbol
     * @param shareSymbol The symbol with which to retrieve the stock item
     * @return Returns new stock item
     */
    @RequestMapping("retrievestock")
    public ResponseEntity<?> retrieveStock(@RequestParam(name = "sharesymbol") String shareSymbol) {
        Shares tempShare = new Shares();
        Stock tempStock = new Stock();

        try {

            Object[] apiObjects = {shareSymbol, Application.apiKey};
            JsonNode searchResults = mapper.readValue(new URL(symbolSearch.format(apiObjects)),
                    JsonNode.class);

            // If share price exists then retrieve from DB
            if (stockRepo.findByShareSymbol(shareSymbol).isPresent()) {
                tempStock = stockRepo.findByShareSymbol(shareSymbol).get();
            }

            JsonNode quoteResults = mapper.readValue(new URL(globalQuote.format(apiObjects)),
                    JsonNode.class);


            LocalDate tempDate = LocalDate.parse(
                    quoteResults.get("Global Quote").get("07. latest trading day").asText());

            tempStock.setShareSymbol(searchResults.get("bestMatches").get(0).get("2. name").textValue());
            tempStock.setCurrency(searchResults.get("bestMatches").get(0).get("8. currency").textValue());
            tempStock.setValue(Float.parseFloat(quoteResults.get("Global Quote").get("05. price").textValue()));
            tempStock.setCurrentShares(Long.parseLong(quoteResults.get("Global Quote").get("06. volume").textValue()));
            tempStock.setLastUpdate(tempDate);

            // Deduct the amount of current shares from held shares by users
            for (Shares i: tempStock.getUserShares()) {
                tempStock.setCurrentShares(tempStock.getCurrentShares() - i.getOwnedShares());
            }

            // Update existing item if it exists otherwise create new
            stockRepo.save(tempStock);

            tempShare.setCompanyName(searchResults.get("bestMatches").get(0).get("2. name").textValue());
            tempShare.setCompanySymbol(shareSymbol);
            tempShare.setOwnedShares(0);

            tempShare.setStock(tempStock);

        } catch (IOException e) {
            logger.warn("Alphvantage Server down");
            throw new ResponseStatusException(
                    HttpStatus.REQUEST_TIMEOUT, "Server Unavailable", e);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "Malformed Share Symbol", e);
        }
        return ResponseEntity.ok(tempShare);
    }
}
