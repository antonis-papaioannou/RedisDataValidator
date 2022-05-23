package antonis.Util;

import org.apache.commons.cli.*;

/**
 * Command line parameter parser
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */
public class CliParser {
	private String redisHost;
	private String redisPort;
	private String datafile;

	private Options options;
	
	public CliParser() 
	{
		this.redisHost = "127.0.0.1";	//default host is the localhos
		this.redisPort = "6379";		//default redis port
		this.datafile = null;			//this is a required argument
		options = buildOptions();
	}


	private Options buildOptions() 
	{
		Options opts = new Options();

		Option OptRedisHost = Option.builder("h")
				.longOpt("host")
				.argName("Redis_IP")
				.hasArg()
				.required(false)
				.desc("Target Redis host (default 127.0.0.1)").build();
		opts.addOption(OptRedisHost);
	
		Option OptRedisPort = Option.builder("p")
				.longOpt("port")
				.argName("port")
				.hasArg()
				.required(false)
				.desc("Target Redis port (Default 6379)").build();
		opts.addOption(OptRedisPort);
	
		Option OptDataFile = Option.builder("d")
				.longOpt("datafile")
				.argName("file")
				.hasArg()
				.required(true)
				.desc("Data file").build();
		opts.addOption(OptDataFile);

		return opts;
	}

	public void parse(String [] args)
    {
        CommandLine cmd = null;
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                redisHost = cmd.getOptionValue("h");
                // System.out.println("Redis target host: " + redisHost);
            }
            
            if (cmd.hasOption("p")) {
                redisPort = cmd.getOptionValue("p");
                // System.out.println("Redis target host: " + redisPort);
            }
            
            if (cmd.hasOption("d")) {
                datafile = cmd.getOptionValue("d");
                // System.out.println("datafile: " + datafile);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        }
    }

	public String getRedisHost() 
	{
		return this.redisHost;
	}

	public String getRedisPort()
	{
		return this.redisPort;
	}

	public String getDataFilename() 
	{
		return this.datafile;
	}
}
