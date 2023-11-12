package org.mongodb.utility.service.impl;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mongodb.utility.domain.BackupConfiguration;
import org.mongodb.utility.domain.RestoreConfiguration;
import org.mongodb.utility.domain.exception.BackupException;
import org.mongodb.utility.domain.exception.RestoreException;
import org.mongodb.utility.domain.hostconf.IMongoServerHostConfiguration;
import org.mongodb.utility.service.contract.IMongoDump;

@Log4j2
@Data
public class MongoDump implements IMongoDump {
    private final IMongoServerHostConfiguration hostConfig;

    /**
     * Constructor
     * @param hostConf host configuration
     */
    public MongoDump(IMongoServerHostConfiguration hostConf) {
        this.hostConfig = hostConf;
    }


    private void execute() {

    }
    
    @Override
    public String backup(BackupConfiguration backupConf) throws BackupException {
        return null;
    }

    @Override
    public void restore(RestoreConfiguration restoreConf) throws RestoreException {

    }
}
