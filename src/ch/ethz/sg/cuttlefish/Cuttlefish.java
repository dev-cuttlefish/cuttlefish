package ch.ethz.sg.cuttlefish;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.layout.api.LayoutController;
import org.gephi.layout.spi.Layout;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;

import sun.misc.Signal;
import sun.misc.SignalHandler;
import ch.ethz.sg.cuttlefish.exporter.NetworkExportController;
import ch.ethz.sg.cuttlefish.layout.LayoutLoader;
import ch.ethz.sg.cuttlefish.layout.kcore.WeightedKCoreLayout;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.GraphMLNetwork;
import ch.ethz.sg.cuttlefish.networks.JsonNetwork;
import ch.ethz.sg.cuttlefish.networks.PajekNetwork;
import ch.ethz.sg.cuttlefish.testing.NetworkStatistics;

public class Cuttlefish {

	public static Options options = new Options();
	private static Calendar cal = Calendar.getInstance();
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyy-MM-dd HH:mm:ss");
	private static BrowsableNetwork network = null;
	private static CommandLine opts;

	// New fields
	private static org.gephi.layout.spi.Layout layout = null;

	private static boolean shouldStop = false;

	public static final boolean VERBOSE_LAYOUT = false;

	/**
	 * Initialize a Gephi Toolkit project.
	 */
	private static void initProject() {
		ProjectController pc = Lookup.getDefault().lookup(
				ProjectController.class);
		pc.newProject();
		pc.getCurrentWorkspace();
	}

	private static void exit(int code, String msg) {
		if (msg != null && !msg.isEmpty()) {
			out(msg);
		}

		ProjectController pc = Lookup.getDefault().lookup(
				ProjectController.class);
		pc.closeCurrentWorkspace();
		pc.closeCurrentProject();

		out("Exit!");
		System.exit(code);
	}

	public static void main(String[] args) {
		initProject();

		if (args.length != 0) {
			initOptions();
			CommandLineParser parser = new GnuParser();

			try {
				// parse the command line arguments
				opts = parser.parse(options, args);
			} catch (ParseException e) {
				// something went wrong..
				e.printStackTrace();
			}

			if (opts.hasOption("help") || !opts.hasOption("input")) {
				printUsage();
				exit(0, "");
			}

			// Add a handler to Ctrl+C (SIGINT)
			Signal ctrlC = new Signal("INT");
			Signal.handle(ctrlC, new SignalHandler() {

				@Override
				public void handle(Signal sig) {
					shouldStop = true;

					// inserts a new line after the "^C" character
					System.out.println();
				}
			});

			parseOptions(args);

		} else {
			startGui();
		}
	}

	public static void debug(Object caller, String msg) {
		System.out.println("[DEBUG] " + caller.getClass().getSimpleName()
				+ ": " + msg);
	}

	public static void out(String s) {
		System.out.println("[" + sdf.format(cal.getTime()) + "] " + s);
	}

	private static void parseOptions(String[] args) {

		if (opts.hasOption("help")) {
			printUsage();
			exit(0, "");
		}

		if (opts.hasOption("gui")) {
			startGui();
		} else {
			startCmd();
			exit(0, "");
		}
	}

	private static void startGui() {
		new ch.ethz.sg.cuttlefish.gui.Cuttlefish();
	}

	private static void startCmd() {

		if (opts.hasOption("stats")) {
			stats();
		}

		if (opts.hasOption("output")) {
			outputNetwork();
		} else {
			out("No output selecting, nothing to do.");
		}
	}

	private static BrowsableNetwork getNetwork() {
		if (network == null)
			getInputNetwork();
		return network;
	}

	private static Layout getLayout() {
		if (layout == null)
			loadSelectedLayout();
		return layout;
	}

	private static void loadSelectedLayout() {
		LayoutLoader loader = LayoutLoader.getInstance();
		String layoutName = opts.getOptionValue("layout", "fixed");
		Layout layout = loader.getLayout(layoutName);

		if (layout == null) {
			exit(-200, "Unsupported layout: " + layoutName);
		} else {
			setLayoutParameters(layout);
			setLayout(layout);
		}
	}

	private static void setLayoutParameters(Layout layout) {
		String paramString = opts.getOptionValue("layout-params");
		String[] layoutParams = null;
		if (paramString != null) {
			layoutParams = paramString.split(",");
		}

		if (layout instanceof WeightedKCoreLayout && layoutParams != null) {
			double alpha = Double.parseDouble(layoutParams[0]);
			double beta = Double.parseDouble(layoutParams[1]);
			((WeightedKCoreLayout) layout).setAlpha(alpha);
			((WeightedKCoreLayout) layout).setBeta(beta);
		}
	}

	private static void setLayout(Layout newLayout) {
		LayoutController layoutController = Lookup.getDefault().lookup(
				LayoutController.class);
		layoutController.setLayout(newLayout);

		out("Setting layout: " + newLayout.getBuilder().getName()
				+ " (stop with Ctrl+C)");

		if (!layoutController.canExecute())
			exit(-202, "Layout " + newLayout.getBuilder().getName()
					+ " could not be started.");

		try {
			layoutController.executeLayout();
			while (layoutController.canStop()) {
				if (shouldStop)
					layoutController.stopLayout();
				Thread.sleep(100);
			}

			if (shouldStop)
				out("Layout was interrupted before converging!");
			else
				out("Layout was set after convergence!");

		} catch (InterruptedException e) {
			exit(-201, e.getLocalizedMessage());
		} catch (Exception e) {
			exit(-201, "Layout " + getLayout().getBuilder().getName()
					+ " failed! Reason: " + e.getLocalizedMessage());
		}

		layout = newLayout;
	}

	private static void getInputNetwork() {
		String file = opts.getOptionValue("input");
		String format = opts.getOptionValue("in-format", "cxf");
		out("Opening network: " + file);

		try {
			if (format.equalsIgnoreCase("cxf")) {
				network = new CxfNetwork(new File(file));
			} else if (format.equalsIgnoreCase("graphml")) {
				network = new GraphMLNetwork(new File(file));
			} else if (format.equalsIgnoreCase("pajek")) {
				network = new PajekNetwork(new File(file));
			} else if (format.equalsIgnoreCase("json")) {
				network = new JsonNetwork(new File(file));
			} else {
				out("Unsupported input format: " + format);
				printUsage();
				exit(-101, "");
			}
		} catch (FileNotFoundException e) {
			exit(-102, "File not found: " + file);
		} catch (IOException e) {
			exit(-103, "File could not be read: " + file);
		}

		out("Opened a network with " + network.getVertexCount() + " nodes and "
				+ network.getEdgeCount() + " edges");
	}

	private static void outputNetwork() {
		getNetwork(); // load network from input
		getLayout(); // compute layout

		String format = opts.getOptionValue("out-format", "tikz");
		String outfile = opts.getOptionValue("output");

		if (format.equalsIgnoreCase("all")) {
			debug(Cuttlefish.class, "Debugging ALL output formats");
			exportAllFormats();
			exit(0, "Debug finished!");
		}

		try {
			out("Selected output format: " + format);
			Exporter exporter = NetworkExportController.getExporter(format);
			NetworkExportController.export(getNetwork(), outfile, exporter);
			out("Network exported to file: " + outfile);

		} catch (Exception e) {
			exit(-300, "Error while exporting network to " + format
					+ "\nError: " + e.getLocalizedMessage());
		}
	}

	private static void exportAllFormats() {
		String outfile = "/home/ilias/Desktop/export_test/output.";
		for (String format : NetworkExportController.getExporterList()) {
			String name = outfile + format.toLowerCase();
			try {
				// out("Network output format: " + format);
				Exporter exporter = NetworkExportController.getExporter(format);
				NetworkExportController.export(getNetwork(), name, exporter);
				out("Network exported to file: " + name);
			} catch (Exception ex) {
				String msg = ex.getLocalizedMessage().toString();
				out(msg);
				// exit(-300, msg);
			}
		}
	}

	@SuppressWarnings("static-access")
	private static void initOptions() {
		Option help = OptionBuilder.withLongOpt("help")
				.withDescription("prints this message").create("h");
		Option input = OptionBuilder.withDescription("input file")
				.withValueSeparator().withLongOpt("input")
				.withArgName("input file").hasArg().create("i");
		Option inputFormat = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"input format: cxf (default), json, graphml, pajek")
				.withLongOpt("in-format").withArgName("input format").hasArg()
				.create();
		Option output = OptionBuilder.withValueSeparator()
				.withDescription("output file").withLongOpt("output")
				.withArgName("output file").hasArg().create("o");
		Option outputFormat = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"output format: tikz (default), cxf, applet, "
								+ "svg, json, cmx, graphml, jpg")
				.withLongOpt("out-format").withArgName("format").hasArg()
				.create();
		Option imgSize = OptionBuilder.withValueSeparator()
				.withDescription("image size: e.g. 1000x1000")
				.withArgName("<width>x<height>").withLongOpt("out-size")
				.hasArg().create();
		Option gui = OptionBuilder
				.withDescription("start the graphical interface")
				.withLongOpt("gui").create("g");
		Option layout = OptionBuilder
				.withValueSeparator()
				.withDescription("network layout: " + LayoutLoader.getKeyList())
				.withLongOpt("layout").hasArg().withArgName("layout")
				.create("l");
		Option params = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"layout parameters in a comma separated list, "
								+ "e.g. \"--layout-params 3.14,4\"")
				.withArgName("x,y,...").withLongOpt("layout-params").hasArg()
				.create();
		Option stats = OptionBuilder.withLongOpt("stats")
				.withDescription("prints network statistics report")
				.create("s");

		options.addOption(help);
		options.addOption(input);
		options.addOption(inputFormat);
		options.addOption(output);
		options.addOption(outputFormat);
		options.addOption(gui);
		options.addOption(layout);
		options.addOption(params);
		options.addOption(imgSize);
		options.addOption(stats);
	}

	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar cuttlefish.jar [options]", options);
	}

	private static void stats() {
		getNetwork();

		out("Network Statistics Report Beta");
		NetworkStatistics stats = new NetworkStatistics();
		boolean ready = stats.execute();

		if (ready) {
			System.out.println(stats.getReport() + "\n");
		} else {
			out("Network is empty!");
		}
	}
}
