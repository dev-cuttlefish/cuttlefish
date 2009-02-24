package ch.ethz.sg.cuttlefish.misc;

import org.apache.commons.collections15.Factory;

public class EdgeFactory implements Factory<Edge> {
	public Edge create() {
		return new Edge();
	}
}