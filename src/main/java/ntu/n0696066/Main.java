package ntu.n0696066;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ntu.n0696066.shares.CurrentShares;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alexmcbean
 */
public class Main
{
    public static void main(String[] args) {
        try {
            CurrentShares quickXML;
            String apiKey = "RS7AKRXL27P6VEFX";
            quickXML = new CurrentShares();
            CurrentShares.SharePrice sharePrice = new CurrentShares.SharePrice();
            ZoneId zId = ZoneId.of("Europe/London");
            ObjectMapper mapper = new ObjectMapper();
            URL apiCall = new URL("https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=BA&apikey=RS7AKRXL27P6VEFX");

            JsonNode receivedJSON = mapper.readValue(apiCall, JsonNode.class);
            for (int i = 0; i < receivedJSON.get("bestMatches").size(); i++){

                System.out.println(receivedJSON.get("bestMatches").get(i).get("2. name"));
            }
            System.out.println(receivedJSON.get("bestMatches"));


            /**
            sharePrice.setCurrency("GBR");
            sharePrice.setValue(BigDecimal.valueOf(1700));


            quickXML.setCompanyName("Amazon");
            quickXML.setCompanySymbol("AMZN");
            quickXML.setSharePrice(sharePrice);
            quickXML.setSharesAmount(BigInteger.valueOf(250));

            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(quickXML.getClass().getPackage().getName());
                javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(quickXML, System.out);
            } catch (javax.xml.bind.JAXBException ex) {
                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            }**/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
