package org.mongodb.utility.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mongodb.utility.config.AppConfig;
import org.mongodb.utility.constants.Constants;
import org.mongodb.utility.domain.dbcollection.DBCollection;
import org.mongodb.utility.domain.hostconf.IMongoServerHostConfiguration;
import org.mongodb.utility.domain.model.DatabaseModel;
import org.mongodb.utility.util.HelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@Component
public class BackupConfiguration {

    private String detectedOS = null;
    String backupName = null;
    String dbName = null;
    String collectionName = null;
    List<String> collectionNames = null;
    String backupDirectory = null;
    String backupRemoteDirectory = "/";
    String workingDirectory = null;
    DatabaseModel readDBModel;
    DatabaseModel writeDBModel;
    private AppConfig appConfig;
    private DBCollection dbCollection;
    private IMongoServerHostConfiguration mongoHostConfig;
    @Autowired
    private Environment environment;

    private BackupConfiguration() {
        /* ** call: to get the absolute backup directory location w.r.t the OS where the program is running. ** */
//        this.getAbsoluteBackupName();
    }

    /**
     *
     * @param appConfig
     * @param dbName
     * @param collectionName
     * @param collectionNames
     * @param backupName
     * @param backupDirectory
     */
    public void setInstance(AppConfig appConfig, String dbName, String collectionName, List<String> collectionNames, String backupName, String backupDirectory) {
        this.readDBModel = readDBModel;
        this.writeDBModel = writeDBModel;
        this.backupName = backupName;
        this.dbName = dbName;
        this.collectionName = collectionName;
        this.collectionNames = collectionNames;
        this.backupDirectory = backupDirectory;
    }

    /**
     *
     * @param appConfig
     * @param mongoHostConfig
     * @param dbCollection
     */
    public void setInstance(AppConfig appConfig, IMongoServerHostConfiguration mongoHostConfig, DBCollection dbCollection) {
        this.appConfig = appConfig;
        this.mongoHostConfig = mongoHostConfig;
        this.dbCollection = dbCollection;
//        this.backupDirectory = backupDirectory;
    }

    /**
     * @return
     */
    public String getAbsoluteBackupName() {
        //if backup dir is @NULL, then first check for the backup directory based on the os* as per definition in application.yml
        if (this.backupDirectory == null) {
            /* ** ** */
            this.detectedOS = HelperUtil.detectOS().toLowerCase();
            if ((Pattern.compile(Constants.OS_MACOSX.toLowerCase())).matcher(detectedOS).matches()) {
                this.backupDirectory = environment.getProperty("home.mongodb.backup.path.mac");
            } else if ((Pattern.compile(Constants.OS_WINX.toLowerCase())).matcher(detectedOS).matches()) {
                this.backupDirectory = environment.getProperty("home.mongodb.backup.path.windows");
            } else {
                this.backupDirectory = environment.getProperty("home.mongodb.backup.path.linux");
            }
            /* ** ** */
        }

        //
        String outDir = this.backupDirectory != null ? this.backupDirectory : "./";
        String filename = backupName != null ? backupName : (dbName != null ? dbName : "backup");
        return String.format("%s%s%s.zip", outDir, File.separator, filename);
    }

    /**
     * set application working directory.
     */
    public void getAbsoluteWorkingDirectory() {
        //if backup dir is @NULL, then first check for the backup directory based on the os* as per definition in application.yml
        this.workingDirectory = environment.getProperty("home.app.working.directory");
    }

    /**
     * @return
     */
    public String toString() {
        return String.format("BackupConfiguration[db:%s %s dir:%s file:%s.zip]",
                dbName, collectionName != null ? "coll:" + collectionName : collectionNames.toString(), backupDirectory, backupName);
    }

    @PostConstruct
    private void init() {
        /* ** call: to get the absolute backup directory location w.r.t the OS where the program is running. ** */
        this.getAbsoluteBackupName();
        /* ** call: to get the absolute working directory location w.r.t the OS whre the program is running. ** */
        this.getAbsoluteWorkingDirectory();
    }
}