package org.mongodb.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.mongodb.utility.config.AppConfig;
import org.mongodb.utility.config.DBConfig;
import org.mongodb.utility.config.FMIConfig;
import org.mongodb.utility.constants.Constants;
import org.mongodb.utility.domain.dbcollection.DBCollection;
import org.mongodb.utility.domain.hostconf.MongoServerDefaultHostConfiguration;
import org.mongodb.utility.domain.model.DatabaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
//@EnableConfigurationProperties(value = DBConfig.class)
//@PropertySource("classpath:application.yml")
@Log4j2
public class MongodbUtilityApplication implements CommandLineRunner {

    @Autowired
    private DBConfig dbConfig;
    @Autowired
    private FMIConfig fmiConfig;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private MongoServerDefaultHostConfiguration mongoHostConfig;

    @Value("${home.mongodb.mac}")
    private String locBinaryMongoDBMacOSX;
    @Autowired
    private DBCollection dbCollection;

    private ObjectMapper objectMapper;

    /**
     * Here args denotes as below:
     * args[0] : denotes "env" from where to take backup like sit, uat, pte and so on.
     * args[1] : denotes "regions" like apac, emea or nam where any particular fmi lies into.
     * args[2] : denotes "FMI" like TH, SG or so on.
     * args[3] : denotes location of output directory where the backup has to be created.
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(MongodbUtilityApplication.class, args);

        System.out.println("args[0] : " + args[0] + " - args[1] : " + args[1] + " - args[2] : " + args[2] + " - args[3] : " + args[3]);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Printing value of args:=> " + args);
        System.out.println("dbConfig : " + dbConfig);
        System.out.println("dbConfig.env : " + dbConfig.getEnv());
        System.out.println("dbConfig.name : " + dbConfig.getName());
        System.out.println("dbConfig.sit.apac.read : " + ((dbConfig.getEnv().get("sit")).get("apac").get("read")));
        System.out.println("dbConfig.sit.apac.read.hostname : " + ( (dbConfig.getEnv().get("sit")).get("apac").get("read")).values());
        System.out.println("dbConfig.sit.apac.read.hostname : " + (( (dbConfig.getEnv().get("sit")).get("apac").get("read")).entrySet()).getClass());

        DatabaseModel result = (DatabaseModel) ( (dbConfig.getEnv().get("sit")).get("apac").get("read")).values().toArray()[0];
        System.out.println("result => " + result.getHostname());

//        System.out.println("fmi-config => " + fmiConfig.getRegion());
        System.out.println("fmi-config.apac.TH.read => " + fmiConfig.getRegion().get("apac").get("TH").get("read"));
        System.out.println("fmi-config.apac.SG.write => " + fmiConfig.getRegion().get("apac").get("SG").get("write"));

        log.trace("[[TRACE]] : fmi-config.apac.SG.write => " + fmiConfig.getRegion().get("apac").get("SG").get("write"));


        appConfig.addToCache(Constants.ENV, args[0]);
        appConfig.addToCache(Constants.REGION, args[1]);
        appConfig.addToCache(Constants.FMI, args[2]);
        appConfig.addToCache(Constants.LOCATION, args[3]);
        appConfig.addToCache(Constants.READ_DB_CONFIG, (DatabaseModel) ( (dbConfig.getEnv().get(args[0])).get(args[1]).get("read")).values().toArray()[0]);
        appConfig.addToCache(Constants.WRITE_DB_CONFIG, (DatabaseModel) ( (dbConfig.getEnv().get(args[0])).get(args[1]).get("write")).values().toArray()[0]);
        appConfig.addToCache(Constants.READ_DB_NAME, fmiConfig.getRegion().get(args[1]).get(args[2]).get("write"));
        appConfig.addToCache(Constants.WRITE_DB_NAME, fmiConfig.getRegion().get(args[1]).get(args[2]).get("write"));

        /* ** printing the app-config cache ** */
        System.out.println("<<<<<< FINALLY PRINTING ITEMS IN APP-CACHE >>>>>>");
        appConfig.getAppCache().forEach( (key, value) -> {
            System.out.println("item.KEYS : " + key + " - item.VALUES : " + value);
        });

        System.out.println("Printing location of MongoDB Binaries for MacOSX : " + locBinaryMongoDBMacOSX);

        System.out.println("MongoDBServerHostConfiguration : " + mongoHostConfig.getMongoDumpBinAbsolutePath());

        System.out.println("DB-Collections : " + dbCollection.getDbcollections());
    }
}
