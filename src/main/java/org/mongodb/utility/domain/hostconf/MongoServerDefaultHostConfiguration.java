package org.mongodb.utility.domain.hostconf;

import lombok.Data;
import org.mongodb.utility.constants.Constants;
import org.mongodb.utility.util.HelperUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Component
public class MongoServerDefaultHostConfiguration implements IMongoServerHostConfiguration {

    @Value("${home.mongodb.binaries.mac}")
    private String binMongoHomeMacOSX;
    @Value("${home.mongodb.binaries.windows}")
    private String binMongoHomeWinX;
    @Value("${home.mongodb.binaries.linux.rhel}")
    private String binMongoHomeLinuxRHEL;
    @Value("${home.mongodb.binaries.linux.centos}")
    private String binMongoHomeLinuxCENTOS;
    @Value("${home.mongodb.binaries.linux.debian}")
    private String binMongoHomeLinuxDEBIAN;

    String mongodumpBin = "mongodump";
    String mongorestoreBin = "mongorestore";
    private boolean forceMongoHome = false;
    private String detectedOS = null;

    /**
     * constructor using default value
     */
    public MongoServerDefaultHostConfiguration() {

    }

    /**
     *
     * @param command
     * @return
     */
    private String getMongoCommandAbsolutePath(String command) {
        detectedOS = HelperUtil.detectOS().toLowerCase();
//        Pattern pattern = Pattern.compile(Constants.OS_MACOSX.toLowerCase());
//        Matcher matcher = pattern.matcher(detectedOS);
//        boolean matchStatus = matcher.matches();


        if ( (Pattern.compile(Constants.OS_MACOSX.toLowerCase())).matcher(detectedOS).matches() )  {
            return String.format("%s%s%s%s%s", binMongoHomeMacOSX, File.separator, "bin", File.separator, command);
        } else if ( (Pattern.compile(Constants.OS_WINX.toLowerCase())).matcher(detectedOS).matches() ) {
            return String.format("%s%s%s%s%s.exe", binMongoHomeWinX, File.separator, "bin", File.separator, command);
        } else {
            String binMongoDB = (detectedOS.contains("red") ? binMongoHomeLinuxRHEL : (detectedOS.contains("centos") ? binMongoHomeLinuxCENTOS : (detectedOS.contains("debian") ? binMongoHomeLinuxDEBIAN : "NA")));
            return String.format("%s%s%s%s%s.sh", binMongoDB, File.separator, "bin", File.separator, command);
        }
    }

    /**
     *
     * @return
     */
    public String getMongoDumpBinAbsolutePath() {
        return getMongoCommandAbsolutePath(mongodumpBin);
    }

    /**
     *
     * @return
     */
    public String getMongoRestoreBinAbsolutePath() {
        return getMongoCommandAbsolutePath(mongorestoreBin);
    }
}
