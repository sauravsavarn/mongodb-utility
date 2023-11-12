package org.mongodb.utility.domain;

import lombok.Data;
import org.mongodb.utility.constants.Constants;
import org.mongodb.utility.domain.hostconf.MongoServerDefaultHostConfiguration;
import org.mongodb.utility.util.HelperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.regex.Pattern;

@Data
@Configuration
public class BackupConfiguration {

    private String detectedOS = null;
    String backupName = null;
    String dbName = null;
    String collectionName = null;
    String backupDirectory = null;
    String backupRemoteDirectory = "/";
    @Autowired
    private Environment environment;

    private BackupConfiguration() {
    }

    ;

    public static BackupConfiguration getInstance(String dbName, String backupName) {
        BackupConfiguration conf = new BackupConfiguration();
        conf.dbName = dbName;
        conf.backupName = backupName;
        return conf;
    }

    public static BackupConfiguration getInstance(String dbName) {
        return getInstance(dbName, dbName);
    }

    public static BackupConfiguration getInstance(String dbName, String collectionName, String backupDirectory) {
        BackupConfiguration conf = BackupConfiguration.getInstance(dbName);
        conf.collectionName = collectionName;
        if (backupDirectory != null) {
            conf.backupDirectory = backupDirectory;
        }
        return conf;
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
     * @return
     */
    public String toString() {
        return String.format("BackupConfiguration[db:%s %s dir:%s file:%s.zip]",
                dbName, collectionName != null ? "coll:" + collectionName : "", backupDirectory, backupName);
    }
}
