package ntu.n0696066.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ntu.n0696066.Application;
import ntu.n0696066.model.Shares;
import ntu.n0696066.model.Stock;
import ntu.n0696066.model.User;
import ntu.n0696066.repository.SharesRepository;
import ntu.n0696066.repository.StockRepository;
import ntu.n0696066.repository.UserRepository;
import ntu.n0696066.security.CurrentUser;
import ntu.n0696066.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/shares")
public class SharesController {

    private final static MessageFormat globalQuote = new MessageFormat(
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={0}&apikey={1}");
    private final static MessageFormat symbolSearch = new MessageFormat(
            "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords={0}&apikey={1}");

    @Autowired
    SharesRepository shareRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    StockRepository stockRepo;

    @Autowired
    ObjectMapper mapper = JsonMapper.builder().build();

    Logger logger = LoggerFactory.getLogger(SharesController.class);

    @Transactional
    @PostMapping("/sellshare")
    public ResponseEntity<?> sellShare(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody Shares shares) {
        User tempUser = userRepo.findById(currentUser.getId()).orElse(null);
        Shares tempShare;

        if (shareRepo.findByCompanySymbolAndUser(shares.getCompanySymbol(), tempUser).isPresent()) {
            tempShare = shareRepo.findByCompanySymbolAndUser(shares.getCompanySymbol(), tempUser).get();
            if (shares.getOwnedShares()== 0) {
                shareRepo.delete(tempShare);
            }
            else {
                tempShare.setOwnedShares(shares.getOwnedShares());
                shareRepo.saveAndFlush(tempShare);
            }
        }
        tempUser = userRepo.findById(currentUser.getId()).orElse(null);
        assert tempUser != null;
        return ResponseEntity.ok(tempUser);
    }

    @Transactional
    @PostMapping("/buyshare")
    public ResponseEntity<?> purchaseStock(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody Shares shares) {

        User tempUser = userRepo.findById(currentUser.getId()).orElse(null);
        Shares tempShare = shares;
        Stock tempStock = null;

        // Retrieve existing stock
        if (stockRepo.findByShareSymbol(tempShare.getCompanySymbol()).isPresent()) {
            tempStock = stockRepo.findByShareSymbol(tempShare.getCompanySymbol()).get();
        }
        // Retrieve existing share if purchasing more shares
        if (shareRepo.findByCompanySymbolAndUser(shares.getCompanySymbol(), tempUser).isPresent()) {
            tempShare = shareRepo.findByCompanySymbolAndUser(shares.getCompanySymbol(), tempUser).get();
            tempShare.setOwnedShares(tempShare.getOwnedShares() + shares.getOwnedShares());
            shareRepo.saveAndFlush(tempShare);
        } else {    // Add new shares to the user and save to DB
            assert tempStock != null;
            tempShare.setStock(tempStock);
            tempShare.setUser(tempUser);
            shareRepo.saveAndFlush(tempShare);
            assert tempUser != null;
            tempUser.getOwnedShares().add(tempShare);
            userRepo.saveAndFlush(tempUser);
        }

        // Update shares held in stock
        assert(tempStock != null);      // This should never be false
        tempStock.getUserShares().add(tempShare);
        tempStock.setCurrentShares(tempStock.getCurrentShares() - tempShare.getOwnedShares());
        stockRepo.saveAndFlush(tempStock);


        tempUser = userRepo.findById(currentUser.getId()).orElse(null);
        assert tempUser != null;
        return ResponseEntity.ok(tempUser);
    }


    /**
     * Used as a search function that will return stock details based on share symbol
     * @param shareSymbol The symbol used to help find stocks
     * @return Returns JSON Object
     */
    @RequestMapping("/liststock")
    public ResponseEntity<?> listStock( @RequestParam(value="sharesymbol") String shareSymbol){
        JsonNode foundShare;
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
    @RequestMapping(value = "/retrievestock")
    public ResponseEntity<?> retrieveStock(@CurrentUser UserPrincipal currentUser, @RequestParam(name = "sharesymbol") String shareSymbol) {
        Shares tempShare = new Shares();
        Stock tempStock = new Stock();
        User tempUser = userRepo.findById(currentUser.getId()).orElse(null);
        try {

            Object[] apiObjects = {shareSymbol, Application.apiKey};
            JsonNode searchResults = mapper.readValue(new URL(symbolSearch.format(apiObjects)),
                    JsonNode.class);


            JsonNode quoteResults = mapper.readValue(new URL(globalQuote.format(apiObjects)),
                    JsonNode.class);

            //API Limit Reached return out-of-date share
            if (quoteResults.get("Note") != null || searchResults.get("Note") != null){
                logger.warn(quoteResults.get("Note").textValue());
                logger.info("Responding with out-of-date share");
                if (shareRepo.findByCompanySymbol(shareSymbol).isPresent()) {
                    tempShare = shareRepo.findByCompanySymbol(shareSymbol).get();
                }
                else {
                    throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "API Limit Reached", null);
                }
            }

            //Malformed Share Symbol
            else if (quoteResults.get("Error Message") != null) {
                logger.warn(quoteResults.get("Error Message").textValue());
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Malformed Share Symbol", null);
            }
            else if (searchResults.get("Error Message") != null ) {
                logger.warn(searchResults.get("Error Message").textValue());
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Malformed Share Symbol", null);
            }
            else {
                LocalDate tempDate = LocalDate.parse(
                        quoteResults.get("Global Quote").get("07. latest trading day").asText());

                // If stock exists then retrieve from DB
                if (stockRepo.findByShareSymbol(shareSymbol).isPresent()) {
                    tempStock = stockRepo.findByShareSymbol(shareSymbol).get();
                }

                tempStock.setShareSymbol(quoteResults.get("Global Quote").get("01. symbol").textValue());
                tempStock.setCurrency(searchResults.get("bestMatches").get(0).get("8. currency").textValue());
                tempStock.setValue(Float.parseFloat(quoteResults.get("Global Quote").get("05. price").textValue()));
                tempStock.setCurrentShares(Long.parseLong(quoteResults.get("Global Quote").get("06. volume").textValue()));
                tempStock.setLastUpdate(tempDate);

                // Deduct the amount of current shares from held shares by users
                for (Shares i : tempStock.getUserShares()) {
                    tempStock.setCurrentShares(tempStock.getCurrentShares() - i.getOwnedShares());
                }

                // Update existing item if it exists otherwise create new
                stockRepo.save(tempStock);

                // If Share exists then retrieve from DB
                if (shareRepo.findByCompanySymbolAndUser(shareSymbol, tempUser).isPresent()) {
                    tempShare = shareRepo.findByCompanySymbolAndUser(shareSymbol, tempUser).get();
                }
                else {

                    tempShare.setCompanyName(searchResults.get("bestMatches").get(0).get("2. name").textValue());
                    tempShare.setCompanySymbol(shareSymbol);
                    tempShare.setOwnedShares(0);
                    tempShare.setStock(tempStock);
                }
            }

        } catch (IOException e) {
            logger.warn("Unable to connect to Alphavantage API");
            // Return existing share with out of date details
            if (shareRepo.findByCompanySymbol(shareSymbol).isPresent()) {
                logger.info("Responding with out-of-date share");
                return ResponseEntity.ok(shareRepo.findByCompanySymbol(shareSymbol).get());
            }
            else {
                throw new ResponseStatusException(
                        HttpStatus.REQUEST_TIMEOUT, "Server Unavailable", e);
            }
        }
        return ResponseEntity.ok(tempShare);
    }
}
