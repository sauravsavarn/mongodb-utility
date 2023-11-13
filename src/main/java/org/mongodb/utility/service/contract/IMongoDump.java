package org.mongodb.utility.service.contract;

import org.mongodb.utility.domain.BackupConfiguration;
import org.mongodb.utility.domain.RestoreConfiguration;
import org.mongodb.utility.domain.exception.BackupException;
import org.mongodb.utility.domain.exception.RestoreException;

import java.io.IOException;

public interface IMongoDump {
    String execute(String command, BackupConfiguration backupConfiguration, RestoreConfiguration restoreConfiguration) throws BackupException, RestoreException, IOException, InterruptedException;
//    String backup(BackupConfiguration backupConf) throws BackupException;
//    void restore(RestoreConfiguration restoreConf) throws RestoreException;
}
