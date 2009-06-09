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
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.EdgeFactory;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.misc.VertexFactory;
import edu.uci.ics.jung.io.GraphMLReader;

public class GraphMLNetwork extends BrowsableNetwork {

	private static final long serialVersionUID = 1L;
	
	public void load(File netFile){
		VertexFactory vertexFactory = new VertexFactory();
		EdgeFactory edgeFactory = new EdgeFactory();
		
		GraphMLReader<GraphMLNetwork, Vertex, Edge> pReader;
			try {
				pReader = new GraphMLReader<GraphMLNetwork, Vertex, Edge>(vertexFactory, edgeFactory);
				pReader.load(netFile.getAbsolutePath(), this);
			} catch (ParserConfigurationException pcEx) {
				JOptionPane.showMessageDialog(null,pcEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				System.err.println("Parsing error in GraphML");
				pcEx.printStackTrace();
			} catch (SAXException saxEx) {
				JOptionPane.showMessageDialog(null,saxEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				System.err.println("Parsing error in GraphML");
				saxEx.printStackTrace();
			} catch (IOException ioEx) {
				JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				System.err.println("Input error in GraphML");
				ioEx.printStackTrace();
			}			
	}
}