package ch.ethz.sg.cuttlefish;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ch.ethz.sg.cuttlefish.cmd.CuttlefishCmd;

public class Cuttlefish {
	
	public static Options options = new Options();

	public static void main(String[] args) {
		initOptions();

		CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        if( line.hasOption( "help" ) ) {
	            printUsage();
	            System.exit(0);
	        }
	        if( line.hasOption("cmd")) {
	        	startCmd(args);
	        } else {
	        	startGui(args);
	        }
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
	}
	
	private static void startGui(String[] args) {
		Cuttlefish.main(args);
	}
	
	private static void startCmd(String[] args) {
		CuttlefishCmd.main(args);
	}
	
	private static void initOptions() {
		Option help = new Option( "help", "print this message" );
		Option gui = new Option("gui", "start graphical interface (default)");
		Option cmd = new Option("cmd", "start command line interface");
		OptionBuilder.withArgName( "file" );
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("input file" );
		Option inputFile = OptionBuilder.create( "input" );
		OptionBuilder.withArgName( "format" );
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("input file format\nsupported formats: cxf, graphml, pajek" );
		Option inputFormat = OptionBuilder.create( "informat" );
		OptionBuilder.withArgName( "file" );
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("output file" );
		Option outputFile = OptionBuilder.create( "output" );
		OptionBuilder.withArgName( "format" );
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("output file format\nsupported formats: tex, jpg, cxf" );
		Option outputFormat = OptionBuilder.create( "outformat" );
		options.addOption( inputFile );
		options.addOption( inputFormat );
		options.addOption( outputFile );
		options.addOption( outputFormat );
		options.addOption( help );				
		options.addOption( gui );
		options.addOption( cmd );
	}
	
	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "java -jar cuttlefish.jar [options]", options );
	}
}
