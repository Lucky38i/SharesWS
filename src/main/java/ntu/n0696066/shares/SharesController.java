package ntu.n0696066.shares;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.converter.BigDecimalStringConverter;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;

@RestController
public class SharesController {

    MessageFormat globalQuote = new MessageFormat(
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={0}&apikey={1}");
    MessageFormat symbolSearch = new MessageFormat(
            "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=\"{0}\"&apikey=\"{1}\"");


    /**
     * Purchase shares in a new stock
     * @param newPurchase The share object used to make a new stock purchase
     * @return Returns status
     */
    @PostMapping("/purchasestock")
    public @ResponseBody String purchaseShare(@RequestBody CurrentShares newPurchase) {
        try {
            JAXBContext context = JAXBContext.newInstance(CurrentShares.class);
            Marshaller m = context.createMarshaller();
            m.marshal(newPurchase, new File("/" + newPurchase.getCompanySymbol() + ".xml"));
        }catch (JAXBException e) {
            e.printStackTrace();
        }
        return "Success";
    }

    /**
     * Purchase more shares for an existing held share
     * @param shareToUpdate CurrentShares object containing current shares to be added to current stock
     * @return Returns status
     */
    @PutMapping("/purchaseshares")
    public String updateShares(@RequestBody CurrentShares shareToUpdate){
        File shareXml = new File("/" + shareToUpdate.getCompanySymbol() + "xml");

        if (shareXml.isFile()) {
            try {
                CurrentShares tempShare = retrieveStock(shareToUpdate.companySymbol);
                JAXBContext context = JAXBContext.newInstance(CurrentShares.class);
                Marshaller m = context.createMarshaller();

                tempShare.setSharesAmount(tempShare.getSharesAmount().add(shareToUpdate.sharesAmount));
                m.marshal(tempShare, shareXml);
            }catch(JAXBException e){
                e.printStackTrace();
            }
        }
        return "Success";
    }

    /**
     * Sell Shares from a currently held stock
     * @param sharesToSell CurrentShares Object containing the subtracting amount from the currently held stock
     * @return Returns status
     */
    @PutMapping("/sellshares")
    public String sellShares(@RequestBody CurrentShares sharesToSell) {
        File shareXml = new File("/" + sharesToSell.getCompanySymbol() + ".xml");
        try {
            CurrentShares tempShare = retrieveStock(sharesToSell.companySymbol);

            JAXBContext context = JAXBContext.newInstance(CurrentShares.class);
            Marshaller m = context.createMarshaller();

            tempShare.setSharesAmount(tempShare.getSharesAmount().subtract(sharesToSell.sharesAmount));

            m.marshal(tempShare, shareXml);
        } catch(JAXBException e) {
            e.printStackTrace();
        }
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
            Object[] apiObjects = {shareSymbol,Application.apiKey};
            ObjectMapper mapper = new ObjectMapper();

            JsonNode searchResults = mapper.readValue(new URL(symbolSearch.format(apiObjects)), JsonNode.class);
            foundShare = searchResults;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foundShare;
    }

    /**
     * Retrieves the specified stock item based on the given symbol
     * @param shareSymbol The symbol with which to retrieve the stock item
     * @return Returns either an existing held stock or build a new one
     */
    @RequestMapping("retrievestock")
    public CurrentShares retrieveStock(@RequestParam(value="sharesymbol") String shareSymbol) {
        CurrentShares tempShare = new CurrentShares();
        File fileXml = new File("/" + shareSymbol + ".xml");
        try {
            if (fileXml.isFile()){
                JAXBContext context = JAXBContext.newInstance(CurrentShares.class);
                Unmarshaller um = context.createUnmarshaller();
                tempShare = (CurrentShares) um.unmarshal(fileXml);
                // TODO Implement way to update shares price by day
            }
            else {
                Object[] apiObjects = {shareSymbol, Application.apiKey};
                ObjectMapper mapper = new ObjectMapper();
                JsonNode quoteResults = mapper.readValue(new URL(globalQuote.format(apiObjects)),
                        JsonNode.class);
                JsonNode searchResults = mapper.readValue(new URL(symbolSearch.format(apiObjects)),
                        JsonNode.class);
                CurrentShares.SharePrice tempPrice = new CurrentShares.SharePrice();
                LocalDate tempDate = LocalDate.parse(
                        quoteResults.get("bestMatches").get(0).get("07. latest trading day").asText());
                GregorianCalendar gCal = GregorianCalendar.from(tempDate.atStartOfDay(ZoneId.systemDefault()));
                XMLGregorianCalendar calendarXml = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCal);

                tempPrice.setCurrency(searchResults.get("bestMatches").get(0).get("8. currency").textValue());
                tempPrice.setValue(new BigDecimalStringConverter().fromString(
                        quoteResults.get("Global Quote").get("05. price").textValue()));
                tempPrice.setLastUpdate(calendarXml);

                tempShare.setCompanyName(searchResults.get("bestMatches").get(0).get("2. name").textValue());
                tempShare.setCompanySymbol(shareSymbol);
                tempShare.setSharesAmount(BigInteger.valueOf(0));
                tempShare.setSharePrice(tempPrice);
            }

        } catch (IOException | JAXBException | DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return tempShare;
    }
}
