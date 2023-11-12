package org.mongodb.utility.util;

import org.mongodb.utility.constants.Constants;

public class HelperUtil {
    private static String OS = null;

    /**
     *
     * @return
     */
    public static String detectOS() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }

        if (OS.contains("win")) return Constants.OS_WINX;
        else if (OS.contains("mac")) return Constants.OS_MACOSX;
        else return OS;
    }
}
