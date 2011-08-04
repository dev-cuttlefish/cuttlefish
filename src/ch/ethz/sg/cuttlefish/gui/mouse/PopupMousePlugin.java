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

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.JPopupMenu;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;

public class PopupMousePlugin<V, E> extends AbstractPopupGraphMousePlugin {
    private JPopupMenu edgePopup, vertexPopup;
    private NetworkPanel networkPanel;
    
    public PopupMousePlugin(NetworkPanel networkPanel) {
        this(MouseEvent.BUTTON3_MASK);
        this.networkPanel = networkPanel;
    }
    
    public PopupMousePlugin(int modifiers) {
        super(modifiers);
    }
    
    @SuppressWarnings("unchecked")
	protected void handlePopup(MouseEvent e) {
        final VisualizationViewer<V,E> vv =
                (VisualizationViewer<V,E>)e.getSource();
        Point2D p = e.getPoint();
        
        GraphElementAccessor<V,E> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            final V v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            if(v != null) {
                updateVertexMenu(v, networkPanel, p);
                vertexPopup.show(vv, e.getX(), e.getY());
            } else {
                final E edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
                if(edge != null) {
                    updateEdgeMenu(edge, networkPanel, p);
                    edgePopup.show(vv, e.getX(), e.getY());              
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	private void updateVertexMenu(V v, NetworkPanel networkPanel, Point2D point) {
        if (vertexPopup == null) return;
        Component[] menuComps = vertexPopup.getComponents();
        for (Component comp: menuComps) {
            if (comp instanceof VertexListener) {
                ((VertexListener<V>)comp).setVertexView(v, networkPanel);
            }
            if (comp instanceof MenuPointListener) {
                ((MenuPointListener)comp).setPoint(point);
            }
        }
        
    }
    
    public JPopupMenu getEdgePopup() {
        return edgePopup;
    }
    
    public void setEdgePopup(JPopupMenu edgePopup) {
        this.edgePopup = edgePopup;
    }
    
    public JPopupMenu getVertexPopup() {
        return vertexPopup;
    }
    
    public void setVertexPopup(JPopupMenu vertexPopup) {
        this.vertexPopup = vertexPopup;
    }
    
    @SuppressWarnings("unchecked")
	private void updateEdgeMenu(E edge, NetworkPanel networkPanel, Point2D point) {
        if (edgePopup == null) return;
        Component[] menuComps = edgePopup.getComponents();
        for (Component comp: menuComps) {
            if (comp instanceof EdgeListener) {
                ((EdgeListener<E>)comp).setEdgeView(edge, networkPanel);
            }
            if (comp instanceof MenuPointListener) {
                ((MenuPointListener)comp).setPoint(point);
            }
        }
    }
    
}

