package ntu.n0696066;

import ntu.n0696066.shares.CurrentShares;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        CurrentShares quickXML;
        quickXML = new CurrentShares();
        CurrentShares.SharePrice sharePrice = new CurrentShares.SharePrice();
        ZoneId zId = ZoneId.of("Europe/London");

        
        
        sharePrice.setCurrency("GBR");
        sharePrice.setValue(BigDecimal.valueOf(1700));
        
        
        quickXML.setCompanyName("Amazon");
        quickXML.setCompanySymbol("AMZN");
        quickXML.setSharePrice(sharePrice);
        quickXML.setSharesAmount(BigInteger.valueOf(250));
        
        try
        {            
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(quickXML.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(quickXML, System.out);
        } catch (javax.xml.bind.JAXBException ex)
        {
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
    }
}
