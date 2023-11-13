package org.mongodb.utility.service.impl;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.bcel.Const;
import org.mongodb.utility.constants.Constants;
import org.mongodb.utility.domain.BackupConfiguration;
import org.mongodb.utility.domain.RestoreConfiguration;
import org.mongodb.utility.domain.dbcollection.DBCollection;
import org.mongodb.utility.domain.exception.BackupException;
import org.mongodb.utility.domain.exception.RestoreException;
import org.mongodb.utility.domain.hostconf.IMongoServerHostConfiguration;
import org.mongodb.utility.domain.model.DatabaseModel;
import org.mongodb.utility.service.contract.IMongoDump;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Data
@Component
public class MongoDump implements IMongoDump {
    private final IMongoServerHostConfiguration hostConfig;

    /**
     * Constructor
     *
     * @param hostConf mongodb host configuration
     */
    public MongoDump(IMongoServerHostConfiguration hostConf) {
        this.hostConfig = hostConf;
    }


    /**
     *
     * @param command - denotes the Intent which can be either backup or restore as of now.
     * @param backupConfiguration
     * @param restoreConfiguration
     * @return
     * @throws BackupException
     * @throws RestoreException
     * @throws IOException
     * @throws InterruptedException
     */
    public String execute(String command, BackupConfiguration backupConfiguration, RestoreConfiguration restoreConfiguration) throws BackupException, RestoreException, IOException, InterruptedException {
        if (command == Constants.OPS_BACKUP) return backup(backupConfiguration);
        else if (command == Constants.OPS_RESTORE) return restore(restoreConfiguration);
        else return null;
    }

    /**
     *
     * @param backupConf
     * @return
     * @throws BackupException
     * @throws IOException
     * @throws InterruptedException
     */
    private String backup(BackupConfiguration backupConf) throws BackupException, IOException, InterruptedException {
        if (backupConf.getAppConfig().getAppCache().isEmpty())
            return "Database backup cannot proceed due No Configuration Found!!!";

        // ArrayList<DatabaseModel> databaseModels = new ArrayList<>();
        ArrayList<Database> databases = new ArrayList<>();
        /* ** Iff both the read-db-configuration and read-db-name exists as per the configuration in yaml - proceed to take BackUP of read-db ** */
        if (backupConf.getAppConfig().getAppCache().get(Constants.READ_DB_CONFIG) != null && backupConf.getAppConfig().getAppCache().get(Constants.READ_DB_NAME) != null) {
            //databaseModels.add( (DatabaseModel) backupConf.getAppConfig().getAppCache().get(Constants.READ_DB_CONFIG));
            databases.add(new Database(backupConf.getMongoHostConfig().getMongoDumpBinAbsolutePath(), backupConf.getBackupDirectory(), backupConf.getWorkingDirectory(), (DatabaseModel) backupConf.getAppConfig().getAppCache().get(Constants.READ_DB_CONFIG), (String) backupConf.getAppConfig().getAppCache().get(Constants.READ_DB_NAME), backupConf.getDbCollection()));
        }

        /* ** Iff both the read-db-configuration and read-db-name exists as per the configuration in yaml - proceed to take BackUP of read-db ** */
        if (backupConf.getAppConfig().getAppCache().get(Constants.WRITE_DB_CONFIG) != null && backupConf.getAppConfig().getAppCache().get(Constants.WRITE_DB_NAME) != null) {
            //databaseModels.add( (DatabaseModel) backupConf.getAppConfig().getAppCache().get(Constants.WRITE_DB_CONFIG));
            databases.add(new Database(backupConf.getMongoHostConfig().getMongoDumpBinAbsolutePath(), backupConf.getBackupDirectory(), backupConf.getWorkingDirectory(), (DatabaseModel) backupConf.getAppConfig().getAppCache().get(Constants.WRITE_DB_CONFIG), (String) backupConf.getAppConfig().getAppCache().get(Constants.WRITE_DB_NAME), backupConf.getDbCollection()));
        }

        /* ***************************************************************************************************** */
        /* ** Using Sequential Streams iff there is only either of read or write database operations required ** */
        /* ** Using Parallel Streams iff both read and write database operations required ** */
        /* ***************************************************************************************************** */
        if (!databases.isEmpty() && databases.size() == 2) { /* ** parallel execution ** */
            databases.parallelStream().forEach(database -> {
                System.out.println(database + " " + Thread.currentThread().getName());

//                /* ** ** */
//                List<String> command = Arrays.asList(
//                        database.getAbspathMongoBinary(),
//                        "--db", database.getDbName(), //NOT NECESSARY IF YOU DUMP ALL DBs
////                        "--collection", "my-pretty-collection", // NOT NECESSARY IF YOU DUMP ALL Collections in the db
////                        "--port", "28017", //NOT NECESSARY IF DEFAULT 27017
//                        "--username", database.getDatabaseModel().getUsername(), //NOT NECESSARY IF NO AUTH
//                        "--authenticationDatabase", "admin", //NOT NECESSARY IF NO AUTH
//                        "--password", database.getDatabaseModel().getPassword(), //NOT NECESSARY IF NO AUTH
////                        "--query", "{\"customer\":\"John Doe\")}", // USED FOR ONLY DUMPING RESULTS OF A QUERY
//                        "--out", database.getLocBackupDirectory() // DEFAULTS TO working directory
//                );

                /* ** ** */
                database.getDbCollection().getDbcollections().forEach(dbCollection -> {
                    // ########### per collection as specified ######## //
                    List<String> command = Arrays.asList(
                            database.getAbspathMongoBinary(),
                            "--db", database.getDbName(), //NOT NECESSARY IF YOU DUMP ALL DBs
                            "--collection", dbCollection, // NOT NECESSARY IF YOU DUMP ALL Collections in the db
//                        "--port", "28017", //NOT NECESSARY IF DEFAULT 27017
                            "--username", database.getDatabaseModel().getUsername(), //NOT NECESSARY IF NO AUTH
                            "--authenticationDatabase", "admin", //NOT NECESSARY IF NO AUTH
                            "--password", database.getDatabaseModel().getPassword(), //NOT NECESSARY IF NO AUTH
//                        "--query", "{\"customer\":\"John Doe\")}", // USED FOR ONLY DUMPING RESULTS OF A QUERY
//                            "--out", database.getLocBackupDirectory()/+`dbCollection.json` // DEFAULTS TO working directory
                            "--out", database.getLocBackupDirectory()+"/"+dbCollection+".json", // DEFAULTS TO working directory
                            "--pretty"
                    );

                    /* ** ** */
                    //ProcessBuilder pb = new ProcessBuilder(command).directory(new File("/Volumes/Macintosh-User/workspace/OUTPUT_FILES/tmp/")); //YOU CAN CHANGE THE WORKING DIRECTORY AS NECESSARY
                    ProcessBuilder pb = new ProcessBuilder(command).directory(new File(database.getLocWorkingDirectory())); //YOU CAN CHANGE THE WORKING DIRECTORY AS NECESSARY
                    //ProcessBuilder pb = new ProcessBuilder(command);
                    System.out.println(pb.command());
                    //Process process = pb.start();
                    Process process = null;

                    try {
                        process = pb.start();
                        List<String> results = readOutputHelper(process.getInputStream());
                        List<String> err = readOutputHelper(process.getErrorStream());

                        System.out.println(results);
                        System.out.println(err);

                        //WAITING FOR A RETURN FROM THE PROCESS WE STARTED
                        int exitCode = process.waitFor();

                        System.out.println("exitCode:" + exitCode);
                    } catch (Exception exception) {
                        System.out.println("[MongoDump - backup ] : Exception - " + exception.getMessage());
                        exception.printStackTrace();
                    }
                    // ################################################ //
                });

            });
        } else { /* ** sequential execution ** */
            System.out.println(databases.get(0) + " " + Thread.currentThread().getName());

            // ################################################ //
            if(!databases.isEmpty()) {
                databases.forEach(database -> {
                    System.out.println(database + " " + Thread.currentThread().getName());

                    /* ** ** */
                    database.getDbCollection().getDbcollections().parallelStream().forEach(dbCollection -> {
                        // ########### per collection as specified ######## //
                        List<String> command = Arrays.asList(
                                database.getAbspathMongoBinary(),
                                "--db", database.getDbName(), //NOT NECESSARY IF YOU DUMP ALL DBs
                                "--collection", dbCollection, // NOT NECESSARY IF YOU DUMP ALL Collections in the db
//                        "--port", "28017", //NOT NECESSARY IF DEFAULT 27017
                                "--username", database.getDatabaseModel().getUsername(), //NOT NECESSARY IF NO AUTH
                                "--authenticationDatabase", "admin", //NOT NECESSARY IF NO AUTH
                                "--password", database.getDatabaseModel().getPassword(), //NOT NECESSARY IF NO AUTH
//                        "--query", "{\"customer\":\"John Doe\")}", // USED FOR ONLY DUMPING RESULTS OF A QUERY
//                            "--out", database.getLocBackupDirectory()/+`dbCollection.json` // DEFAULTS TO working directory
                                "--out", database.getLocBackupDirectory()+"/"+dbCollection+".json", // DEFAULTS TO working directory
                                "--pretty"
                        );

                        /* ** ** */
                        ProcessBuilder pb = new ProcessBuilder(command).directory(new File(database.getLocWorkingDirectory())); //YOU CAN CHANGE THE WORKING DIRECTORY AS NECESSARY
                        System.out.println(pb.command());
                        //Process process = pb.start();
                        Process process = null;

                        try {
                            process = pb.start();
                            List<String> results = readOutputHelper(process.getInputStream());
                            List<String> err = readOutputHelper(process.getErrorStream());

                            System.out.println(results);
                            System.out.println(err);

                            //WAITING FOR A RETURN FROM THE PROCESS WE STARTED
                            int exitCode = process.waitFor();

                            System.out.println("exitCode:" + exitCode);
                        } catch (Exception exception) {
                            System.out.println("[MongoDump - backup ] : Exception - " + exception.getMessage());
                            exception.printStackTrace();
                        }
                        // ################################################ //
                    });

                });
            }
            // ################################################ //
        }

        /* ** ** */
        return null;
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private List<String> readOutputHelper(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }

    /**
     * @param restoreConf
     * @return
     * @throws RestoreException
     */
    private String restore(RestoreConfiguration restoreConf) throws RestoreException {
        return null;
    }
}

/**
 *
 */
@Data
class Database {
    private DatabaseModel databaseModel;
    private String dbName;
    private DBCollection dbCollection;
    private String abspathMongoBinary; //absolute path of responsible mongo binary required for the job/task.
    private String locBackupDirectory; //absolute path/location of backup directory.
    private String locWorkingDirectory; //absolute path/location of working directory.

    /**
     * @param databaseModel
     * @param dbName
     */
    public Database(String abspathMongoBinary, String abspathBackupDir, String abspathWorkingDir, DatabaseModel databaseModel, String dbName, DBCollection dbCollection) {
        this.abspathMongoBinary = abspathMongoBinary;
        this.locBackupDirectory = abspathBackupDir;
        this.locWorkingDirectory = abspathWorkingDir;
        this.databaseModel = databaseModel;
        this.dbName = dbName;
        this.dbCollection = dbCollection;
    }
}