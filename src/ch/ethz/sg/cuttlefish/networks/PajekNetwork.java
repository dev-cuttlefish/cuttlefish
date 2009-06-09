/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

	This file is part of Cuttlefish.
	
 	Cuttlefish is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
*/

package ch.ethz.sg.cuttlefish.networks;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.EdgeFactory;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.misc.VertexFactory;
import edu.uci.ics.jung.io.PajekNetReader;

public class PajekNetwork extends BrowsableNetwork {

	private static final long serialVersionUID = 1L;
	
	public void load(File netFile){
		VertexFactory vertexFactory = new VertexFactory();
		EdgeFactory edgeFactory = new EdgeFactory();
		
		PajekNetReader<PajekNetwork, Vertex, Edge> pReader = new PajekNetReader<PajekNetwork, Vertex, Edge>(vertexFactory, edgeFactory);
		try {
			pReader.load(netFile.getAbsolutePath(), this);
		} catch (IOException ioEx) {
			JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Input error in Pajek file");
			ioEx.printStackTrace();
		}
		
		for (Vertex v : getVertices())
			v.setLabel(pReader.getVertexLabeller().transform(v));

		if (pReader.getEdgeWeightTransformer() != null)
			for (Edge e : getEdges())
				if (pReader.getEdgeWeightTransformer().transform(e) != null)
					e.setWeight(pReader.getEdgeWeightTransformer().transform(e).doubleValue());
	}
}