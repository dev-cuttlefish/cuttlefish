package ch.ethz.sg.cuttlefish.misc;

import org.apache.commons.collections15.Factory;

public class VertexFactory implements Factory<Vertex> {
	public Vertex create() {
		return new Vertex();
	}
}

