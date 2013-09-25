package ch.ethz.sg.cuttlefish.testing;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class creates a filter on an existing PrintStream, by checking if a line
 * printed with 'println' starts with a specific filter. If so, the line is not
 * printed.
 * 
 * @author irinis
 * 
 */
public class FilteredErrorStream extends PrintStream {

	private PrintStream stream;
	private List<String> filterOut;

	public FilteredErrorStream(PrintStream out) {
		super(out);

		stream = out;
		filterOut = new ArrayList<String>();
	}

	public void addFilter(String s) {
		if (!filterOut.contains(s))
			filterOut.add(s);
	}

	public void removeFilter(String s) {
		if (filterOut.contains(s))
			filterOut.remove(s);
	}

	@Override
	public void println(String s) {

		for (String filter : filterOut) {
			if (s.toLowerCase().startsWith(filter.toLowerCase())) {
				return;
			}
		}

		stream.println(s);
	}

}
