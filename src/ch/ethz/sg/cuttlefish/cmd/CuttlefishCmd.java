package ch.ethz.sg.cuttlefish.cmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

import ch.ethz.sg.cuttlefish.Cuttlefish;

public class CuttlefishCmd {
	
	public CuttlefishCmd() {
		
	}
	
	public static void main(String[] args) {
		System.out.println("Cuttlefish command line interface");
		
		CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( Cuttlefish.options, args );
	        if( !line.hasOption("input") || !line.hasOption("output")) {
	        	printUsage();
	        	System.exit(0);
	        }
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
	}
	
	private static void printUsage() {
		System.out.println("You need to specify an input and an output file!");
	}
}
