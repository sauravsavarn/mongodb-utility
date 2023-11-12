package org.mongodb.utility.service.contract;

import org.mongodb.utility.domain.BackupConfiguration;
import org.mongodb.utility.domain.RestoreConfiguration;
import org.mongodb.utility.domain.exception.BackupException;
import org.mongodb.utility.domain.exception.RestoreException;

public interface IMongoDump {
    String backup(BackupConfiguration backupConf) throws BackupException;
    void restore(RestoreConfiguration restoreConf) throws RestoreException;
}
