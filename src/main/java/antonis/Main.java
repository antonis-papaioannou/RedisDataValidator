package antonis;

import org.apache.commons.cli.*;
import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import antonis.IO.DataFileParser;
import antonis.IO.ResultLogger;
import antonis.Redis.RedisManager;
import antonis.Util.CliParser;
import antonis.Util.ProgressBar;
import antonis.Util.CliParser;
import antonis.Redis.ValidationResult;

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
        int validationError_cnt = 0;
        System.out.println("Start data valiation from file " + dataParser.getDataFilename());
        System.out.println("Progress: ");
        while ( (kv = dataParser.readKV()) != null) {
            kv_counter++;
            bytes_read += kv.size();
            // System.out.println(">>> " + kv.toString() + " @ Node " + rm.keyBelongsToNode(kv.getKey()));
            ValidationResult vresult = rm.validateKey(kv.getKey(), kv.getValue());
            if (vresult.getStatus() != ValidationResult.Status.OK) {
                validationError_cnt++;
                // System.out.println(vresult.getMessage());
                rlogger.log(vresult.getMessage());
            }

            progressBar.printProgress(bytes_read, dataParser.getFileSizeBytes(), validationError_cnt);
        }

        stopwatch.stop();
        assert (bytes_read == dataParser.getFileSizeBytes());

        String resultSummary = "Data file: " + dataParser.getDataFilename() + 
                                " (" + dataParser.getFileSizeBytes() + " bytes)\n" +
                                "KVs checked: " + kv_counter + "\n" +
                                "Errors: " + validationError_cnt;

        System.out.println(resultSummary); 
        System.out.println("Detailed error results in file validationResults.out");
        rlogger.log(resultSummary);
        
        dataParser.close();
        rlogger.close();
        rm.shutdown();
    }
}
