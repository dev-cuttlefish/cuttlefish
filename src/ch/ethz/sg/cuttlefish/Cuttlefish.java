package ch.ethz.sg.cuttlefish;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ch.ethz.sg.cuttlefish.layout.ARF2Layout;
import ch.ethz.sg.cuttlefish.layout.FixedLayout;
import ch.ethz.sg.cuttlefish.layout.KCoreLayout;
import ch.ethz.sg.cuttlefish.layout.WeightedARF2Layout;
import ch.ethz.sg.cuttlefish.misc.AppletExporter;
import ch.ethz.sg.cuttlefish.misc.CxfSaver;
import ch.ethz.sg.cuttlefish.misc.CxfToCmx;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.GraphMLExporter;
import ch.ethz.sg.cuttlefish.misc.SVGExporter;
import ch.ethz.sg.cuttlefish.misc.TikzExporter;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.BrowsableForestNetwork;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.GraphMLNetwork;
import ch.ethz.sg.cuttlefish.networks.JsonNetwork;
import ch.ethz.sg.cuttlefish.networks.PajekNetwork;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class Cuttlefish {

	public static Options options = new Options();
	private static Calendar cal = Calendar.getInstance();
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyy-MM-dd HH:mm:ss");
	private static BrowsableNetwork network = null;
	private static Layout<Vertex, Edge> layout = null;
	private static CommandLine opts;
	private static VisualizationViewer<Vertex, Edge> vv;
	private static boolean done = false;

	
	
	public static void main(String[] args) {
		if(args.length != 0) {			
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
				System.exit(0);
			}
			
			// this helps us to press Ctrl+C in order to stop iterative layouts
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() {
			       done = true;
			       outputNetwork();
			    }
			 });
			
			parseOptions(args);
		} else {
			startGui();
		}

	}

	public static void out(String s) {
		System.out.println("[" + sdf.format(cal.getTime()) + "] " + s);
	}

	private static void parseOptions(String[] args) {
		
		if (opts.hasOption("help")) {
			printUsage();
			System.exit(0);
		}
		if (opts.hasOption("gui")) {
			startGui();
		} else {
			startCmd();
			System.exit(0);
		}
	}

	private static void startGui() {
		new ch.ethz.sg.cuttlefish.gui2.Cuttlefish();
	}

	private static void startCmd() {

		if (opts.hasOption("output")) {
			outputNetwork();
		} else {
			out("No output selecting, nothing to do. Exit.");
		}
	}

	private static BrowsableNetwork getNetwork() {
		if (network == null)
			getInputNetwork();
		return network;
	}

	private static Layout<Vertex, Edge> getLayout() {
		if (layout == null)
			getSelectedLayout();
		return layout;
	}

	private static void getSelectedLayout() {
		String l = opts.getOptionValue("layout", "fixed");
		if (l.compareToIgnoreCase("arf") == 0) {
			layout = new ARF2Layout<Vertex, Edge>(getNetwork(), true,
					Integer.MAX_VALUE);
		} else if (l.compareToIgnoreCase("weighted-arf") == 0) {
			layout = new WeightedARF2Layout<Vertex, Edge>(getNetwork(), true,
					Integer.MAX_VALUE);
		//} else if (l.compareToIgnoreCase("spring") == 0) {
		//	layout = new SpringLayout<Vertex, Edge>(getNetwork());
		} else if (l.compareToIgnoreCase("kamada-kawai") == 0) {
			layout = new KKLayout<Vertex, Edge>(getNetwork());
		} else if (l.compareToIgnoreCase("fruchterman-reingold") == 0) {
			layout = new FRLayout<Vertex, Edge>(getNetwork());
		} else if (l.compareToIgnoreCase("k-core") == 0) {
			layout = new KCoreLayout<Vertex, Edge>(getNetwork(), null);
		} else if (l.compareToIgnoreCase("iso-m") == 0) {
			layout = new ISOMLayout<Vertex, Edge>(getNetwork());
		} else if (l.compareToIgnoreCase("circle") == 0) {
			layout = new CircleLayout<Vertex, Edge>(getNetwork());
		} else if (l.compareToIgnoreCase("tree") == 0) {
			BrowsableForestNetwork tree = new BrowsableForestNetwork((BrowsableNetwork)getNetwork());
			layout = new TreeLayout<Vertex, Edge>(tree);
		} else if (l.compareToIgnoreCase("radial-tree") == 0) {
			BrowsableForestNetwork tree = new BrowsableForestNetwork((BrowsableNetwork)getNetwork());
			layout = new RadialTreeLayout<Vertex, Edge>(tree);
		} else if (l.compareToIgnoreCase("k-core") == 0) {

		} else if (l.compareToIgnoreCase("fixed") == 0) {
			layout = new FixedLayout<Vertex, Edge>(getNetwork(), new CircleLayout<Vertex, Edge>(getNetwork()));
		} else {
			System.out.println("Unspoported layout: " + l);
			System.exit(0);
		}
		// We need to create a visualization viewer, otherwise JUNG
		// won't fire up the layout algorithm...
		vv = new VisualizationViewer<Vertex, Edge>(layout);
		out("Setting layout: " + l);
		if (layout instanceof IterativeContext) {
			out("Iterative layout, waiting on the layout to converge...");
			out("The layout will automatically stop when the change is less than 10");
			out("you can always manually stop the layout by pressing Ctrl+C");
			done = false;
			double change = 0;
			while (!done) {
				done = ((IterativeContext) layout).done();				
				if (layout instanceof ARF2Layout) {
					change = Math.abs(((ARF2Layout<Vertex, Edge>) layout).getChange());					
				}
				if (layout instanceof WeightedARF2Layout) {
					change = Math.abs(((WeightedARF2Layout<Vertex, Edge>) layout).getChange());
				}				
				System.out.print("[" + sdf.format(cal.getTime()) + "] Layout change: " + change + "\r");
				done = change < 10;				
				// since the layout won't tell us when it finishes,
				// we have no choice but to pull its status
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}


			}
			out("Iterative layout finished");
		}
		out("Layout set");
	}

	private static void outputNetwork() {
		out("Outputing the network");
		String format = opts.getOptionValue("out-format", "tikz");
		if (format.compareToIgnoreCase("cxf") == 0) {
			out("Exporting to cxf");
			CxfSaver s = new CxfSaver(getNetwork(), getLayout());
			s.save(new File(opts.getOptionValue("output")));
		} else if (format.compareToIgnoreCase("tikz") == 0) {
			out("Exporting to tikz");
			TikzExporter t = new TikzExporter(getNetwork());
			t.setOutputFile(new File(opts.getOptionValue("output")));
			t.exportToTikz(getLayout());
		} else if (format.compareToIgnoreCase("applet") == 0) {
			out("Exporting to applet");
			AppletExporter a = new AppletExporter(getNetwork(), getLayout());
			a.exportToApplet(new File(opts.getOptionValue("output")));
		} else if (format.compareToIgnoreCase("svg") == 0) {
			out("Exporting to interactive svg");
			SVGExporter e = new SVGExporter(getNetwork(), getLayout());
			int height=1000, width=1000;
			e.toSVG(new File(opts.getOptionValue("output")), height, width);
		} else if (format.compareToIgnoreCase("cmx") == 0) {
			out("Exporting to CMX");
			if(getNetwork() instanceof CxfNetwork) {
				String file = opts.getOptionValue("output");
				CxfToCmx.cxfToCmx((CxfNetwork)getNetwork(), new File(file+"_linkevent.csv"), new File(file+"_linkeventparent.csv"), new File(file+"_linkeventrecipient.csv"), new File(file+"_linkeventsender.csv"), new File(file+"_node.csv"));	
			}
			else {
				out("You can convert only CXF networks!");
			}
						
		} else if (format.compareToIgnoreCase("jpeg") == 0) {
			int width = 1000, height = 1000;
			out("Exporting to jpeg");
			getLayout();
			Dimension size = vv.getSize();
			BufferedImage img = new BufferedImage(size.width, size.height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = img.createGraphics();
			JFrame frame = new JFrame();
			frame.setSize(width,height);
			frame.add(vv);
			vv.setSize(width, height);
			frame.setVisible(true);
			vv.paintImmediately(new Rectangle(width,height));
			g2.draw(new Rectangle(300, 300));
			g2.drawImage(vv.createImage(1000, 1000), 0, 0, vv);
			OutputStream out;
			try {
				out = new FileOutputStream(opts.getOptionValue("output"));
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(img);
				param.setQuality(1.0f, true);
				encoder.setJPEGEncodeParam(param);
				encoder.encode(img, param);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ImageFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		} else if(format.compareToIgnoreCase("json") == 0) {
			String file = opts.getOptionValue("output");
			AppletExporter exporter = new AppletExporter(getNetwork(), getLayout());
			try {
				exporter.exportJsonData(new PrintStream(new File(file)));
			} catch (FileNotFoundException e) {
				out("File not found");
				e.printStackTrace();
			}
		} else if(format.compareToIgnoreCase("graphml") == 0){
			String filename = opts.getOptionValue("output");
			
			// Updated version of GraphML I/O
			GraphMLExporter exporter = new GraphMLExporter(getNetwork(), getLayout());
			exporter.export(new File(filename));
		} else {
			System.out.println("Unsupported output format '" + format + "'\n");
			printUsage();
			System.exit(0);
		}
		out("Done");
	}

	private static void getInputNetwork() {
		out("Opening network: " + opts.getOptionValue("input"));
		String file = opts.getOptionValue("input");
		String format = opts.getOptionValue("in-format", "cxf");
		if (format.compareToIgnoreCase("cxf") == 0) {
			network = new CxfNetwork(new File(file));
		} else if (format.compareToIgnoreCase("graphml") == 0) {
			GraphMLNetwork n = new GraphMLNetwork();
			n.load(new File(file));
			network = n;
		} else if (format.compareToIgnoreCase("pajek") == 0) {
			PajekNetwork n = new PajekNetwork();
			n.load(new File(file));
			network = n;
		} else if (format.compareToIgnoreCase("json") == 0 ) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = null;
				StringBuilder stringBuilder = new StringBuilder();
				String ls = System.getProperty("line.separator");
				while( ( line = reader.readLine() ) != null ) {
			        stringBuilder.append( line );
			        stringBuilder.append( ls );
			    }
				JsonNetwork n = new JsonNetwork(stringBuilder.toString());
				network = n;
			} catch (FileNotFoundException e) {
				out("File not found " + file);
				e.printStackTrace();
			} catch (IOException e) {
				out("Error while reading file");
				e.printStackTrace();
			}			
		} else {
			
			System.out.println("Unsupported input format '" + format + "'\n");
			printUsage();
			System.exit(0);
		}
		out("Opened a network with " + network.getVertexCount() + " nodes and "
				+ network.getEdgeCount() + " edges");
	}

	@SuppressWarnings("static-access")
	private static void initOptions() {
		Option help = OptionBuilder.withLongOpt("help")
				.withDescription("prints this message").create("h");
		Option input = OptionBuilder.withDescription("input file")
				.withValueSeparator().withLongOpt("input")
				.withArgName("input file").hasArg().create("i");
		Option inputFormat = OptionBuilder.withValueSeparator()
				.withDescription("input format: cxf (default), json, graphml, pajek")
				.withLongOpt("in-format").withArgName("input format").hasArg()
				.create();
		Option output = OptionBuilder.withValueSeparator()
				.withDescription("output file").withLongOpt("output")
				.withArgName("output file").hasArg().create("o");
		Option outputFormat = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"output format: tikz (default), cxf, applet, svg, json, cmx, graphml")
				.withLongOpt("out-format").withArgName("input format").hasArg()
				.create();
		Option gui = OptionBuilder
				.withDescription("start the graphical interface")
				.withLongOpt("gui").create("g");
		Option layout = OptionBuilder
				.withValueSeparator()
				.withDescription(
						"network layout: arf, fixed (default), weighted-arf, k-core, kamada-kawai, fruchterman-reingold, iso-m, circle, tree, radial-tree")
				.withLongOpt("layout").hasArg().withArgName("layout")
				.create("l");
		options.addOption(help);
		options.addOption(input);
		options.addOption(inputFormat);
		options.addOption(output);
		options.addOption(outputFormat);
		options.addOption(gui);
		options.addOption(layout);
	}

	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar cuttlefish.jar [options]", options);
	}
}
