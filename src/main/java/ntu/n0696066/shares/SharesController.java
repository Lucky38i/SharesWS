package ntu.n0696066.shares;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.converter.BigDecimalStringConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.text.MessageFormat;

@RestController
public class SharesController {

    MessageFormat globalQuote = new MessageFormat(
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={0}&apikey={1}");
    // https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=AMZN&apikey=RS7AKRXL27P6VEFX
    MessageFormat symbolSearch = new MessageFormat(
            "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=\"{0}\"&apikey=\"{1}\"");


    @RequestMapping("/listshares")
    public CurrentShares listShares(@RequestParam(value="sharesymbol") String shareSymbol) {
        CurrentShares tempShare = new CurrentShares();
        try {
            Object[] apiObjects = {shareSymbol,Application.apiKey};
            ObjectMapper mapper = new ObjectMapper();

            JsonNode quoteResults = mapper.readValue(new URL(globalQuote.format(apiObjects)),
                    JsonNode.class);
            JsonNode searchResults = mapper.readValue(new URL(symbolSearch.format(apiObjects)),
                    JsonNode.class);

            CurrentShares.SharePrice tempPrice = new CurrentShares.SharePrice();
            tempPrice.setCurrency(searchResults.get("bestMatches").get(0).get("8. currency").textValue());
            tempPrice.setValue(new BigDecimalStringConverter().fromString(
                    quoteResults.get("Global Quote").get("05. price").textValue()));

            tempShare.setCompanyName(searchResults.get("bestMatches").get(0).get("2. name").textValue());
            tempShare.setCompanySymbol(shareSymbol);
            tempShare.setSharesAmount(BigInteger.valueOf(0));
            tempShare.setSharePrice(tempPrice);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempShare;
    }
}
