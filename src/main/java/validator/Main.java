package validator;

// import org.apache.commons.cli.*;

import validator.IO.DataFileParser;
import validator.IO.ResultLogger;
import validator.Redis.RedisManager;
import validator.Redis.ValidationResult;
import validator.Util.CliParser;
import validator.Util.ProgressBar;

import com.google.common.base.Stopwatch;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.concurrent.TimeUnit;

public class Main 
{
    public static void main( String[] args )
    {
        CliParser cli = new CliParser();
        cli.parse(args);

        RedisManager rm = new RedisManager();
        rm.init(cli.getRedisHost(), cli.getRedisPort());
        rm.updateClusterMetadata();

        DataFileParser dataParser = new DataFileParser(cli.getDataFilename());
        ResultLogger rlogger = new ResultLogger();

        ProgressBar progressBar = new ProgressBar();

        // Timer for the duration of the validation process
        Stopwatch stopwatch = Stopwatch.createStarted();

        KeyValue kv;
        int kv_counter = 0;
        int bytes_read = 0;
        int slotMovedErrorCount = 0;
        int slotUnknownErrorCount = 0;
        int valueMissmatchErrorCount = 0;
        String extraMsg = "";
        System.out.println("Start data valiation from file " + dataParser.getDataFilename());
        System.out.println("Progress: ");
        while ( (kv = dataParser.readKV()) != null) {
            kv_counter++;
            bytes_read += kv.size();
            ValidationResult vresult = rm.validateKey(kv.getKey(), kv.getValue());

            switch (vresult.getStatus()) {
                case SLOT_MOVED:
                    slotMovedErrorCount++;
                    rlogger.log(vresult.getMessage());
                    break;
                case VALUE_MISSMATCH:
                    valueMissmatchErrorCount++;
                    rlogger.log(vresult.getMessage());
                    break;
                case SLOT_UNKNOWN:
                    slotUnknownErrorCount++;
                    rlogger.log(vresult.getMessage());
                    break;
                case OK:
                    // Everything is fine. No need to report anything
                    break;
                default:
                    assert(false);
            }

            extraMsg = "Slots moved: " + slotMovedErrorCount + 
                       " Value missmatch: " + valueMissmatchErrorCount +
                       " Slots unknown: " + slotUnknownErrorCount;
            progressBar.printProgress(bytes_read, dataParser.getFileSizeBytes(), extraMsg);
        }
        
        stopwatch.stop();
        assert (bytes_read == dataParser.getFileSizeBytes());
        
        
        int totalErrors = slotMovedErrorCount + valueMissmatchErrorCount + slotUnknownErrorCount;
        String resultSummary = "Data file: " + dataParser.getDataFilename() + 
                                " (" + dataParser.getFileSizeBytes() + " bytes)\n" +
                                "KVs checked: " + kv_counter + "\n" +
                                "Errors: " + totalErrors + "\n" +
                                "\t" + extraMsg;

        System.out.println(resultSummary); 
        System.out.println("Detailed error results in file validationResults.out");
        rlogger.log(resultSummary);
        
        dataParser.close();
        rlogger.close();
        rm.shutdown();
    }
}
