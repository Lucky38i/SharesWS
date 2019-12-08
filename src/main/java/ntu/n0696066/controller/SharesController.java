package ntu.n0696066.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ntu.n0696066.Application;
import ntu.n0696066.repository.SharesRepository;
import ntu.n0696066.model.SharePrice;
import ntu.n0696066.model.Shares;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * Purchase shares in a new stock
     * @param shares the stock to purchase
     * @return Returns status
     */
    @PostMapping("/purchasestock")
    @ResponseStatus(HttpStatus.CREATED)
    public String purchaseShare(@RequestBody Shares shares) {
        shareRepo.save(shares);
        return "Success";
    }

    /**
     * Updatess the share object when a client decides to sell or purchase more shares
     * @param shareToUpdate CurrentShares object containing current shares to be added to current stock
     * @return Returns status
     */
    @PutMapping("/updateshares")
    public String updateShares(@RequestBody Shares shareToUpdate){
        shareRepo.save(shareToUpdate);
        return "Success";
    }

    /**
     * Used as a search function that will return stock details based on share symbol
     * @param shareSymbol The symbol used to help find stocks
     * @return Returns JSON Object
     */
    @RequestMapping("/liststock")
    public JsonNode listStock(@RequestParam(value="sharesymbol") String shareSymbol){
        JsonNode foundShare = null;
        try {
            Object[] apiObjects = {shareSymbol, Application.apiKey};
            ObjectMapper mapper = new ObjectMapper();

            foundShare = mapper.readValue(new URL(symbolSearch.format(apiObjects)), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foundShare;
    }

    /**
     * Retrieves the specified stock item based on the given symbol
     * @param shareSymbol The symbol with which to retrieve the stock item
     * @return Returns new stock item
     */
    @RequestMapping("retrievestock")
    public Shares retrieveStock(@RequestParam String shareSymbol) {
        Shares tempShare = new Shares();
        try {
            Object[] apiObjects = {shareSymbol, Application.apiKey};
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
            tempShare.setCompanySymbol(shareSymbol);
            tempShare.setSharesAmount(0);
            tempShare.setSharePrice(tempPrice);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.REQUEST_TIMEOUT, "Server Unavailable", e);
        }
        return tempShare;
    }
}
