package ch.ethz.sg.cuttlefish.gui2.tasks;

import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import edu.uci.ics.jung.algorithms.layout.Layout;

public class DumpToDBWorker extends SwingWorker<Object, Object> {
		private NetworkPanel networkPanel;
		private CxfNetwork network;
		private Connection conn;
		private String nodesTableName;
		private String linksTableName;
		private Layout<Vertex, Edge> layout;
		private Map<String,String> linkAttrType;
		private List<String> linkAttrs;
		private Map<String,String> nodeAttrType;
		private List<String> nodeAttrs;
		private boolean dumpFinished = false;
		private boolean replaceTables;

		public DumpToDBWorker(NetworkPanel networkPanel, CxfNetwork network, Layout<Vertex, Edge> layout, String nodesTableName, String linksTableName, Connection conn, List<String> nodeAttrs, List<String> linkAttrs, boolean replaceTables) {
			this.conn = conn;
			this.networkPanel = networkPanel;
			this.network = network;
			this.nodesTableName = nodesTableName;
			this.linksTableName = linksTableName;
			this.layout = layout;
			this.replaceTables = replaceTables;
			
			linkAttrType = new HashMap<String, String>();
			this.linkAttrs= linkAttrs;
			nodeAttrType = new HashMap<String, String>();
			this.nodeAttrs = nodeAttrs;

			nodeAttrType.put("id", "INT PRIMARY KEY NOT NULL");
			nodeAttrType.put("label", "VARCHAR(255)");
			nodeAttrType.put("color", "VARCHAR(255)");
			nodeAttrType.put("borderColor", "VARCHAR(255)");
			nodeAttrType.put("size", "INT");
			nodeAttrType.put("shape", "VARCHAR(10)");
			nodeAttrType.put("width", "INT");
			nodeAttrType.put("hide", "BOOLEAN");
			nodeAttrType.put("var1", "VARCHAR(255)");
			nodeAttrType.put("var2", "VARCHAR(255)");
			nodeAttrType.put("x", "FLOAT");
			nodeAttrType.put("y", "FLOAT");
			nodeAttrType.put("fixed", "BOOLEAN");
			
			linkAttrType.put("id_origin", "INT");
			linkAttrType.put("id_dest", "INT");
			linkAttrType.put("weight", "INT");
			linkAttrType.put("label", "VARCHAR(255)");
			linkAttrType.put("width", "INT");
			linkAttrType.put("color", "VARCHAR(255)");
			linkAttrType.put("var1", "VARCHAR(255)");
			linkAttrType.put("var2", "VARCHAR(255)");
			linkAttrType.put("hide", "BOOLEAN");
										
		}
		
		private boolean createTables() throws Exception {
			DatabaseMetaData dbmeta = conn.getMetaData();
			ResultSet rs;
			rs = dbmeta.getTables(null, null, nodesTableName, null);
			while(rs.next() ) {
				if( nodesTableName.equalsIgnoreCase( rs.getString("TABLE_NAME") ) ) {
					if(replaceTables) {
						dropTable(nodesTableName);
					} else {
						JOptionPane.showMessageDialog(networkPanel, "Table " + nodesTableName + " already exists in the database.", "Table exists", JOptionPane.ERROR_MESSAGE, null);
						return false;
					}
				}
			}
			String query = "CREATE TABLE " + nodesTableName + '(';
			for(int i = 0; i < nodeAttrs.size(); ++i) {
				if( i > 0)
					query += ", ";
				query += nodeAttrs.get(i) + ' ' + nodeAttrType.get(nodeAttrs.get(i));
			}			
			query += ");";
			Statement st = conn.createStatement();
			st.execute(query);
			
			rs = dbmeta.getTables(null, null, linksTableName, null);
			while(rs.next() ) {
				if( linksTableName.equalsIgnoreCase( rs.getString("TABLE_NAME") ) ) {
					if(replaceTables) {
						dropTable(linksTableName);
					} else {
						JOptionPane.showMessageDialog(networkPanel, "Table " + linksTableName + " already exists in the database.", "Table exists", JOptionPane.ERROR_MESSAGE, null);
						return false;
					}
				}
			}						
			query = "CREATE TABLE " + linksTableName + '(';
			for(int i = 0; i < linkAttrs.size(); ++i) {
				if( i > 0)
					query += ", ";
				query += linkAttrs.get(i) + ' ' + linkAttrType.get(linkAttrs.get(i));
			}			
			query += ",PRIMARY KEY(id_origin, id_dest),FOREIGN KEY(id_origin) REFERENCES " + nodesTableName + "(id),FOREIGN KEY(id_dest) REFERENCES " + nodesTableName + "(id));";
			st.execute(query);
			return true;
		}
		
		private void dropTable(String tableName) throws Exception {
			/* If table exists and user has chosen to replace existing tables,
			 * drop table first and then create it.
			 */
			conn.createStatement().execute("DROP TABLE "+tableName+";");
		}
		
		private void setNodeAttr(Vertex n, String attr, PreparedStatement st, int index) throws SQLException {
			if(attr.compareToIgnoreCase("id") == 0) {
				st.setInt(index, n.getId());
			} else if(attr.compareToIgnoreCase("label") == 0) {
				st.setString(index, n.getLabel());
			} else if(attr.compareToIgnoreCase("color") == 0) {
				st.setString(index, "" + ((double)n.getFillColor().getRed()/256.d)
					+","+((double)n.getFillColor().getGreen()/256.d)
					+","+((double)n.getFillColor().getBlue()/256.d));
			} else if(attr.compareToIgnoreCase("borderColor") == 0) {
				st.setString(index, "" + ((double)n.getColor().getRed()/256.d)
					+","+((double)n.getColor().getGreen()/256.d)
					+","+((double)n.getColor().getBlue()/256.d));
			} else if(attr.compareToIgnoreCase("size") == 0) {
				st.setDouble(index, n.getSize());
			} else if(attr.compareToIgnoreCase("shape") == 0) {			
				if (n.getShape() instanceof Rectangle2D) {
					st.setString(index, "square");
				} else {
					st.setString(index, "circle");
				}
			} else if(attr.compareToIgnoreCase("width") == 0) {
				st.setInt(index, n.getWidth());
			} else if(attr.compareToIgnoreCase("hide") == 0) {
				st.setBoolean(index, n.isExcluded() );
			} else if(attr.compareToIgnoreCase("var1") == 0) {
				st.setString(index, n.getVar1() );
			} else if(attr.compareToIgnoreCase("var2") == 0) {
				st.setString(index, n.getVar2() );
			} else if(attr.compareToIgnoreCase("x") == 0) {
				st.setFloat(index, (float)(layout.transform(n)).getX() );
			} else if(attr.compareToIgnoreCase("y") == 0) {
				st.setFloat(index, (float)(layout.transform(n)).getY() );
			} else if(attr.compareToIgnoreCase("fixed") == 0) {
				st.setBoolean(index, n.isFixed() );
			}
		}
		
		private void setLinkAttr(Edge e, String attr, PreparedStatement st, int index) throws SQLException {
			if(attr.compareToIgnoreCase("id_origin") == 0) {
				st.setInt(index, network.getSource(e).getId() );
			} else if(attr.compareToIgnoreCase("id_dest") == 0) {
				st.setInt(index, network.getDest(e).getId() );
			} else if(attr.compareToIgnoreCase("weight") == 0) {
				st.setFloat(index, (float)e.getWeight() );
			} else if(attr.compareToIgnoreCase("label") == 0) {
				st.setString(index, e.getLabel() );
			} else if(attr.compareToIgnoreCase("width") == 0) {
				st.setFloat(index, (float)e.getWidth() );
			} else if(attr.compareToIgnoreCase("color") == 0) {
				st.setString(index, "" + ((double)e.getColor().getRed()/256.d)
					+","+((double)e.getColor().getGreen()/256.d)
					+","+((double)e.getColor().getBlue()/256.d));
			} else if(attr.compareToIgnoreCase("var1") == 0) {
				st.setString(index, e.getVar1() );
			} else if(attr.compareToIgnoreCase("var2") == 0) {
				st.setString(index, e.getVar2() );
			} else if(attr.compareToIgnoreCase("hide") == 0) {
				st.setBoolean(index, e.isExcluded() );
			}
		}
		
		@Override
		protected Object doInBackground() throws Exception {
			networkPanel.getStatusBar().setBusyMessage("Dumping the network to a database", this);
			if (!createTables() ) {
				return null;
			}
			String query = "INSERT INTO " + nodesTableName + "(";
			for(int i = 0; i < nodeAttrs.size(); ++i) {
				if( i > 0)
					query += ",";
				query += nodeAttrs.get(i);
			}
			query += ") VALUES (";
			for(int i = 0; i < nodeAttrs.size(); ++i) {
				if( i > 0)
					query += ",";
				query += "?";
			}
			query += ");";
			PreparedStatement insertNode = conn.prepareStatement(query);
			try {
				for(Vertex n : network.getVertices() ) {
					for(int i = 0; i < nodeAttrs.size(); ++i)
						setNodeAttr(n, nodeAttrs.get(i), insertNode, i+1);
					insertNode.execute();
				}
			} catch(SQLException e) {
				JOptionPane.showMessageDialog(networkPanel, "Error while dumping a node to the database", "Database Error", JOptionPane.ERROR_MESSAGE, null);
				e.printStackTrace();
			}

			query = "INSERT INTO " + linksTableName + "(";
			for(int i = 0; i < linkAttrs.size(); ++i) {
				if( i > 0)
					query += ",";
				query += linkAttrs.get(i);
			}
			query += ") VALUES (";
			for(int i = 0; i < linkAttrs.size(); ++i) {
				if( i > 0)
					query += ",";
				query += "?";
			}
			query += ");";
			PreparedStatement insertLink = conn.prepareStatement(query);
			
			try {
				for(Edge e : network.getEdges() ) {
					for(int i = 0; i < linkAttrs.size(); ++i)
						setLinkAttr(e, linkAttrs.get(i), insertLink, i+1);
					insertLink.execute();	
				}
			} catch(SQLException e) {
				JOptionPane.showMessageDialog(networkPanel, "Error while dumping a link to the database", "Database Error", JOptionPane.ERROR_MESSAGE, null);
				e.printStackTrace();
			}
		
			dumpFinished = true;
			
			synchronized (networkPanel) {
				networkPanel.notifyAll();	
			}
			
			return null;
		}
		
		@Override
		protected void done() {
			super.done();
			if(dumpFinished) {
				networkPanel.getStatusBar().setMessage("Done dumping to database");
			} else {
				networkPanel.getStatusBar().setMessage("Dumping to database canceled");
			}
		}
		
	}