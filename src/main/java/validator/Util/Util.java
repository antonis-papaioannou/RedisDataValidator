package validator.Util;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Utility functions
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */

public class Util {
    /**
     * Gets the point in code of the caller in a string represenation
     * @return String with <Class>::<functions>::<line>
     */
    public static String pointInCode() {
        String filename = Thread.currentThread().getStackTrace()[2].getFileName();
        String functionName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
        return "[" + filename + ":" + line + " " + functionName + "()]";
    }

    /**
     * The timestamp as a String in the format: yyyy-MM-dd HH:mm:ss
     * @return the timestamp as a string representation
     */
    public static String timestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date = new Date();  

        return formatter.format(date);
    }

}
