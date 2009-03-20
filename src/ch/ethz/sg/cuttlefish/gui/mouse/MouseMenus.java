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

package ch.ethz.sg.cuttlefish.gui.mouse;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;


public class MouseMenus {
    
    public static class EdgeMenu extends JPopupMenu {        
        // private JFrame frame; 
        public EdgeMenu(final JFrame frame) {
            super("Edge Menu");
            // this.frame = frame;
            this.add(new EdgeLabelDisplay());
            this.addSeparator();
            this.add(new WeightDisplay());
            this.add(new EdgeVar1Display());
            this.add(new EdgeVar2Display());
//            this.addSeparator();
//           this.add(new EdgePropItem(frame));           
        }
        
    }
    
/*    public static class EdgePropItem extends JMenuItem implements EdgeMenuListener<Samples.MouseMenu.GraphElements.MyEdge>,
            MenuPointListener {
        GraphElements.MyEdge edge;
        VisualizationViewer visComp;
        Point2D point;
        
        public void setEdgeAndView(GraphElements.MyEdge edge, VisualizationViewer visComp) {
            this.edge = edge;
            this.visComp = visComp;
        }

        public void setPoint(Point2D point) {
            this.point = point;
        }
        
        public  EdgePropItem(final JFrame frame) {            
            super("Edit Edge Properties...");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EdgePropertyDialog dialog = new EdgePropertyDialog(frame, edge);
                    dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
                    dialog.setVisible(true);
                }
                
            });
        }
        
    }*/
    
    public static class EdgeLabelDisplay extends JMenuItem implements EdgeListener<Edge> {
    	private static final long serialVersionUID = 1L;
		public void setEdgeView(Edge e, VisualizationViewer visComp) {
            this.setText("label = " + e.getLabel());
        }
    }
    
    public static class WeightDisplay extends JMenuItem implements EdgeListener<Edge> {
    	private static final long serialVersionUID = 1L;
		public void setEdgeView(Edge e, VisualizationViewer visComp) {
            this.setText("weight = " + e.getWeight());
        }
    }
    
    public static class EdgeVar1Display extends JMenuItem implements EdgeListener<Edge> {
    	private static final long serialVersionUID = 1L;
    	public void setEdgeView(Edge e, VisualizationViewer visComp) {
        	if (e.getVar1() != null)
        		this.setText("var1 = " + e.getVar1());
        }
    }

    public static class EdgeVar2Display extends JMenuItem implements EdgeListener<Edge> {
    	private static final long serialVersionUID = 1L;
    	public void setEdgeView(Edge e, VisualizationViewer visComp) {
        	if (e.getVar2() != null)
        		this.setText("var2 = " + e.getVar2());
        }
    }

    
    public static class VertexMenu extends JPopupMenu {
        public VertexMenu() {
            super("Vertex Menu");
            this.addSeparator();
            this.add(new VertexLabelDisplay());
            this.addSeparator();
            this.add(new VertexVar1Display());
            this.add(new VertexVar2Display());
        }
    }
    
    public static class VertexLabelDisplay extends JMenuItem implements VertexListener<Vertex> {
    	private static final long serialVersionUID = 1L;
		@SuppressWarnings("unchecked")
		public void setVertexView(Vertex v, VisualizationViewer visComp) {
            this.setText("label = " + v.getLabel());
        }
    }
    
    public static class VertexVar1Display extends JMenuItem implements VertexListener<Vertex> {
    	private static final long serialVersionUID = 1L;
    	public void setVertexView(Vertex v, VisualizationViewer visComp) {
        	if (v.getVar1() != null)
        		this.setText("var1 = " + v.getVar1());
        }
    }

    public static class VertexVar2Display extends JMenuItem implements VertexListener<Vertex> {
    	private static final long serialVersionUID = 1L;
    	public void setVertexView(Vertex v, VisualizationViewer visComp) {
        	if (v.getVar2() != null)
        		this.setText("var2 = " + v.getVar2());
        }
    }
    
}
