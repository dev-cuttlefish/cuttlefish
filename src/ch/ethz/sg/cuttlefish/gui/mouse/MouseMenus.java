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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.uci.ics.jung.visualization.VisualizationViewer;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;


public class MouseMenus {
    
    public static class EdgeMenu extends JPopupMenu {         
        public EdgeMenu(final JFrame frame) {
            super("Edge Menu");
            this.add(new EdgeLabelDisplay());
            this.add(new WeightDisplay());
            this.add(new WidthDisplay());
            this.addSeparator(); 
            this.add(new EdgeSetLabel());
            this.add(new EdgeSetWeight());
            this.add(new EdgeSetWidth());
            this.add(new SetColor("edgeColor"));
            this.addSeparator();
            this.add(new EdgeDelete());
        }
        
    }
        
    public static class EdgeLabelDisplay extends JMenuItem implements EdgeListener<Edge> {
    	private static final long serialVersionUID = 1L;
		public void setEdgeView(Edge e, NetworkPanel networkPanel) {
			if(e.getLabel() != null && e.getLabel().length() > 0)
				this.setText("Label: " + e.getLabel());
			else
				this.setText("Label: no label");
        }
    }
    
    public static class WeightDisplay extends JMenuItem implements EdgeListener<Edge> {
    	private static final long serialVersionUID = 1L;
		public void setEdgeView(Edge e, NetworkPanel networkPanel) {
            this.setText("Weight: " + e.getWeight());
        }
    }    
    
    public static class WidthDisplay extends JMenuItem implements EdgeListener<Edge> {
    	private static final long serialVersionUID = 1L;
		public void setEdgeView(Edge e, NetworkPanel networkPanel) {
            this.setText("Width: " + e.getWidth());
        }
    }
    
    public static class EdgeSetLabel extends JMenuItem implements EdgeListener<Edge> {
    	private Edge edge;
    	private NetworkPanel networkPanel;
    	public EdgeSetLabel() {
    		this.setText("Set label...");
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					String newLabel = JOptionPane.showInputDialog(networkPanel, "Enter new label", "Set link label", JOptionPane.QUESTION_MESSAGE);
					edge.setLabel(newLabel);
				}
			});
    	}
		@Override
		public void setEdgeView(Edge edge, NetworkPanel networkPanel) {
			this.edge = edge;
			this.networkPanel = networkPanel;			
		}    	
    }
    
    public static class EdgeDelete extends JMenuItem implements EdgeListener<Edge> {
    	private Edge edge;
    	private NetworkPanel networkPanel;
    	public EdgeDelete() {
    		this.setText("Delete");
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					networkPanel.getNetwork().removeEdge(edge);
				}
			});
    	}
		@Override
		public void setEdgeView(Edge edge, NetworkPanel networkPanel) {
			this.edge = edge;
			this.networkPanel = networkPanel;
		}    	
    }
    
    public static class EdgeSetWidth extends JMenuItem implements EdgeListener<Edge> {
    	private Edge edge;
    	private NetworkPanel networkPanel;
    	public EdgeSetWidth() {
    		this.setText("Set width...");
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					String newWidthStr = JOptionPane.showInputDialog(networkPanel, "Enter width", "Set link width", JOptionPane.QUESTION_MESSAGE);
					double newWidth;
					if(newWidthStr != null) {
						try {
							newWidth = Double.parseDouble(newWidthStr);
							edge.setWidth(newWidth);
						} catch (NumberFormatException ex){
							JOptionPane.showMessageDialog(null, "You did not enter a number!", "Not a number error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
    	}
		@Override
		public void setEdgeView(Edge edge, NetworkPanel networkPanel) {
			this.edge = edge;
			this.networkPanel = networkPanel;
		}    	
    }
    
    public static class EdgeSetWeight extends JMenuItem implements EdgeListener<Edge> {
    	private Edge edge;
    	private NetworkPanel networkPanel;
    	public EdgeSetWeight() {
    		this.setText("Set weight...");
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					String newWeightStr = JOptionPane.showInputDialog(networkPanel, "Enter weight", "Set link weight", JOptionPane.QUESTION_MESSAGE);
					double newWeight;
					if(newWeightStr != null) {
						try {
							newWeight = Double.parseDouble(newWeightStr);
							edge.setWeight(newWeight);
						} catch (NumberFormatException ex){
							JOptionPane.showMessageDialog(null, "You did not enter a number!", "Not a number error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
    	}
		@Override
		public void setEdgeView(Edge edge, NetworkPanel networkPanel) {
			this.edge = edge;
			this.networkPanel = networkPanel;
		}    	
    }
    
    public static class EdgeVar1Display extends JMenuItem implements EdgeListener<Edge> {
    	private static final long serialVersionUID = 1L;
    	public void setEdgeView(Edge e, NetworkPanel networkPanel) {
        	if (e.getVar1() != null)
        		this.setText("var1 = " + e.getVar1());
        }
    }

    public static class EdgeVar2Display extends JMenuItem implements EdgeListener<Edge> {
    	private static final long serialVersionUID = 1L;
    	public void setEdgeView(Edge e, NetworkPanel networkPanel) {
        	if (e.getVar2() != null)
        		this.setText("var2 = " + e.getVar2());
        }
    }

    
    public static class VertexMenu extends JPopupMenu {
        public VertexMenu() {
            super("Vertex Menu");
            this.add(new VertexIdDisplay());
            this.add(new VertexLabelDisplay());
            this.add(new VertexSizeDisplay());
            this.addSeparator();
            this.add(new VertexSetLabel());
            this.add(new VertexSetSize());
            this.add(new SetColor("fill"));
            this.add(new SetColor("border"));
            this.add(new VertexSetBorderWidth());
            this.add(new VertexSetShape());
            this.addSeparator();
            this.add(new VertexDelete());
        }
    }
    
    public static class VertexSetSize extends JMenuItem implements VertexListener<Vertex> {				
    	private Vertex vertex;
    	private NetworkPanel networkPanel;
    	public VertexSetSize() {
    		this.setText("Set size...");
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					String newSizeStr = JOptionPane.showInputDialog(networkPanel, "Enter new size", "Set node size", JOptionPane.QUESTION_MESSAGE);
					double newSize;
					if(newSizeStr != null) {
						try {
							newSize = Double.parseDouble(newSizeStr);
							vertex.setSize(newSize);
						} catch (NumberFormatException ex){
							JOptionPane.showMessageDialog(null, "You did not enter a number!", "Not a number error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
    	}
    	@Override
		public void setVertexView(Vertex vertex,
			NetworkPanel networkPanel) {
			this.vertex = vertex;			
			this.networkPanel = networkPanel;
		}
    }
    
    public static class VertexSetBorderWidth extends JMenuItem implements VertexListener<Vertex> {				
    	private Vertex vertex;
    	private NetworkPanel networkPanel;
    	public VertexSetBorderWidth() {
    		this.setText("Set border width...");
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					String newWidthStr = JOptionPane.showInputDialog(networkPanel, "Enter new size", "Set node size", JOptionPane.QUESTION_MESSAGE);
					int newWidth;
					if(newWidthStr != null) {
						try {
							newWidth = Integer.parseInt(newWidthStr);
							vertex.setWidth(newWidth);
						} catch (NumberFormatException ex){
							JOptionPane.showMessageDialog(null, "You did not enter a number!", "Not a number error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
    	}
    	@Override
		public void setVertexView(Vertex vertex,
			NetworkPanel networkPanel) {
			this.vertex = vertex;			
			this.networkPanel = networkPanel;
		}
    }
    
    public static class VertexSetLabel extends JMenuItem implements VertexListener<Vertex> {				
    	private Vertex vertex;
    	private NetworkPanel networkPanel;
    	public VertexSetLabel() {
    		this.setText("Set label...");
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					String newLabel = JOptionPane.showInputDialog(networkPanel, "Enter new label", "Set node label", JOptionPane.QUESTION_MESSAGE);
					if(newLabel != null) {
						vertex.setLabel(newLabel);
					}
				}
			});
    	}
    	@Override
		public void setVertexView(Vertex vertex,
			NetworkPanel networkPanel) {
			this.vertex = vertex;			
			this.networkPanel = networkPanel;
		}
    }
    
    public static class VertexDelete extends JMenuItem implements VertexListener<Vertex> {				
    	private Vertex vertex;
    	private NetworkPanel networkPanel;
    	public VertexDelete() {
    		this.setText("Delete node");
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					networkPanel.getNetwork().removeVertex(vertex);
				}
			});
    	}
    	@Override
		public void setVertexView(Vertex vertex,
			NetworkPanel networkPanel) {
			this.vertex = vertex;
			this.networkPanel = networkPanel;
		}
    }
    
    public static class VertexSetShape extends JMenuItem implements VertexListener<Vertex> {				
    	private Vertex vertex;
    	private NetworkPanel networkPanel;
    	public VertexSetShape() {
    		this.setText("Set shape...");
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					String newShape = (String)JOptionPane.showInputDialog(networkPanel, "Choose node shape", "Set node shape", JOptionPane.QUESTION_MESSAGE, null, new String[]{"circle", "square"}, "circle");
					if(newShape != null) {
						vertex.setShape(newShape);
					}
				}
			});
    	}
    	@Override
		public void setVertexView(Vertex vertex,
			NetworkPanel networkPanel) {
    		this.networkPanel = networkPanel;
			this.vertex = vertex;			
		}
    }
    
    public static class SetColor extends JMenuItem implements VertexListener<Vertex>, EdgeListener<Edge> {				
    	private Vertex vertex;
    	private Edge edge;
    	private JFrame colorPanel;
    	private JButton ok;
    	private JButton cancel;
    	final String attribute;
    	private JColorChooser colorChooser;
    	private NetworkPanel networkPanel;
    	public SetColor(final String attribute) {
    		this.attribute = attribute;
    		if(attribute == "fill")
    			this.setText("Set fill color...");
    		else if (attribute == "border")
    			this.setText("Set border color...");
    		else if (attribute == "edgeColor")
    			this.setText("Set color...");
    		colorPanel = new JFrame();
    		ok = new JButton("OK");
    		cancel = new JButton("Cancel");
    		colorChooser = new JColorChooser();
    		colorPanel.setSize(450, 420);    		
    		colorPanel.setVisible(false);
    		colorPanel.setLayout(new FlowLayout());
    		colorPanel.add(colorChooser);
    		colorPanel.add(ok);
    		colorPanel.add(cancel);
    		ok.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(attribute == "fill")
						vertex.setFillColor(colorChooser.getColor());
					else if(attribute == "border")
						vertex.setColor(colorChooser.getColor());
					else if(attribute == "edgeColor")
						edge.setColor(colorChooser.getColor());
					colorPanel.setVisible(false);
				}
			});
    		cancel.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {					
					colorPanel.setVisible(false);
				}
			});
    		this.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(attribute == "fill" && vertex.getFillColor() != null)
						colorChooser.setColor(vertex.getFillColor());
					else if(attribute == "border" && vertex.getColor() != null)
						colorChooser.setColor(vertex.getColor());
					else if(attribute == "edge" && edge.getColor() != null)
						colorChooser.setColor(edge.getColor());
					colorPanel.setVisible(true);
				}
			});
    	}
    	@Override
		public void setVertexView(Vertex vertex,
			NetworkPanel networkPanel) {
			this.vertex = vertex;			
		}
		@Override
		public void setEdgeView(Edge edge, NetworkPanel networkPanel) {
			this.edge = edge;
			this.networkPanel = networkPanel;
		}
    }
    
    public static class VertexLabelDisplay extends JMenuItem implements VertexListener<Vertex> {    	
    	private static final long serialVersionUID = 1L;
		@SuppressWarnings("unchecked")
		public void setVertexView(Vertex v, NetworkPanel networkPanel) {
			if(v.getLabel() != null && v.getLabel().length() > 0)
				this.setText("Label: " + v.getLabel());
			else
				this.setText("Label: no label");			
        }
    }
    
    
    
    public static class VertexSizeDisplay extends JMenuItem implements VertexListener<Vertex> {    	
    	private static final long serialVersionUID = 1L;
		@SuppressWarnings("unchecked")
		public void setVertexView(Vertex v, NetworkPanel networkPanel) {
			this.setText("Size: " + v.getSize());
        }
    }
    
    public static class VertexIdDisplay extends JMenuItem implements VertexListener<Vertex> {    	
    	private static final long serialVersionUID = 1L;
		@SuppressWarnings("unchecked")
		public void setVertexView(Vertex v, NetworkPanel networkPanel) {
			this.setText("Id: " + v.getId());
        }
    }
    
    public static class VertexVar1Display extends JMenuItem implements VertexListener<Vertex> {
    	private static final long serialVersionUID = 1L;
    	public void setVertexView(Vertex v, NetworkPanel networkPanel) {
        	if (v.getVar1() != null)
        		this.setText("var1 = " + v.getVar1());
        	else
        		this.setText("");
        }
    }

    public static class VertexVar2Display extends JMenuItem implements VertexListener<Vertex> {
    	private static final long serialVersionUID = 1L;
    	public void setVertexView(Vertex v, NetworkPanel networkPanel) {
        	if (v.getVar2() != null)
        		this.setText("var2 = " + v.getVar2());
        	else
        		this.setText("");
        }
    }
    
}
