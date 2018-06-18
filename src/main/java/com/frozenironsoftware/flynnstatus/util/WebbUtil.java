package com.frozenironsoftware.flynnstatus.util;

import com.frozenironsoftware.flynnstatus.data.Constants;
import com.goebl.david.Webb;

public class WebbUtil {

    /**
     * Get a webb instance with the user agent populated
     * @return webb
     */
    public static Webb getWebb() {
        Webb webb = Webb.create();
        webb.setDefaultHeader("User-Agent",
                String.format("%s/%s (Java/%s)",
                        Constants.NAME, Constants.VERSION, System.getProperty("java.version")));
        return webb;
    }
}
