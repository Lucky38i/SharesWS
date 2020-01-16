package ntu.n0696066.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ntu.n0696066.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final static MessageFormat rateQuote = new MessageFormat(
            "https://api.exchangeratesapi.io/latest?base={0}&symbols={1}");
    private final static MessageFormat baseCodes = new MessageFormat(
            "https://api.exchangeratesapi.io/latest?base={0}");

    @Autowired
    ObjectMapper mapper = JsonMapper.builder().build();

    Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    @Deprecated
    @RequestMapping("/convert")
    public ResponseEntity<?> convertCurrency(String base, String rate) {
        String returnValue = null;
        try {
            Object[] apiObjects = {base, rate};
            JsonNode searchResults = mapper.readValue(new URL(rateQuote.format(apiObjects)),
                    JsonNode.class);
            returnValue = String.valueOf(searchResults.get("rates").get(rate).doubleValue());
        } catch (IOException e ) {

        }
        assert (returnValue != null);
        return ResponseEntity.ok(returnValue);
    }

    @RequestMapping("/currencyrates")
    public ResponseEntity<?> getCodes(String base) {
        JsonNode searchResult;
        try {
            Object[] apiObject = {base};
            searchResult = mapper.readValue(new URL(baseCodes.format(apiObject)),
                    JsonNode.class);
        } catch (IOException e) {
            logger.warn("Unable to connect to Currency Conversion API");
            throw new ResponseStatusException(
                    HttpStatus.REQUEST_TIMEOUT, "Currency Exchange Down", e);
        }
        assert (searchResult != null);
        return ResponseEntity.ok(searchResult);
    }
}
