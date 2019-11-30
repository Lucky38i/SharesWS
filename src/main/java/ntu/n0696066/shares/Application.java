package ntu.n0696066.shares;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alexmcbean
 */
@SpringBootApplication
public class Application {
    public static String apiKey;
    public static void main(String[] args) throws IOException{

        // Pull in properties from property file
        try(InputStream in = new FileInputStream("src/main/resources/application.properties")){
            Properties prop = new Properties();
            prop.load(in);

            apiKey = prop.getProperty("alphavantage.apikey");
        }
        SpringApplication.run(Application.class,args);

    }
}
