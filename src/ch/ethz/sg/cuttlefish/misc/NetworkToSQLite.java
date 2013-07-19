package ch.ethz.sg.cuttlefish.misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class NetworkToSQLite {

	/**
	 * This method reads an CXF network and stores it in a an SQLite in-memory
	 * database. Return the connection to the SQLite database.
	 * 
	 * @param cxfFile
	 * @return
	 * @throws SQLException 
	 */
	public static Connection networkToSQLiteDump(BrowsableNetwork network) throws SQLException {
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}

		connection = DriverManager.getConnection("jdbc:sqlite::memory:");
		Statement statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE IF EXISTS CFNodes");
		statement.executeUpdate("DROP TABLE IF EXISTS CFEdges");
		statement.executeUpdate("DROP TABLE IF EXISTS Directed");
		statement
				.executeUpdate("CREATE TABLE CFNodes(id INT(11) PRIMARY KEY NOT NULL, label VARCHAR(255), color VARCHAR(255), borderColor VARCHAR(255), size INT(3), shape VARCHAR(10), width INT(3), hide BOOLEAN, var1 VARCHAR(255), var2 VARCHAR(255), x FLOAT, y FLOAT, fixed BOOLEAN)");
		statement
				.executeUpdate("CREATE TABLE CFEdges(id_origin INT(11), id_dest INT(11), weight INT(3), label VARCHAR(255), width INT(3), color VARCHAR(255), var1 VARCHAR(255), var2 VARCHAR(255), hide BOOLEAN, PRIMARY KEY(id_origin, id_dest), FOREIGN KEY(id_origin) REFERENCES CFNodes(id), FOREIGN KEY(id_dest) REFERENCES CFNodes(id))");

		//CxfNetwork cxfNetwork = new CxfNetwork(cxfFile);
		PreparedStatement stAddNodes = connection
				.prepareStatement("INSERT INTO CFNodes(id, label, color, borderColor, size, shape, width, hide, var1, var2, x, y, fixed) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

		// By default the network is directed
		if (network instanceof CxfNetwork && !((CxfNetwork)network).isDirected() )
			statement.executeUpdate("CREATE TABLE Directed(FALSE)");
		else
			statement.executeUpdate("CREATE TABLE Directed(TRUE)");
		
		statement.executeUpdate("INSERT INTO Directed VALUES (1)");

		for (Vertex v : network.getVertices()) {
			stAddNodes.setInt(1, v.getId());
			stAddNodes.setString(2, v.getLabel());
			stAddNodes.setString(3, ""
					+ ((double) v.getFillColor().getRed() / 256d) + ","
					+ ((double) v.getFillColor().getGreen() / 256d) + ","
					+ ((double) v.getFillColor().getBlue() / 256d));
			stAddNodes.setString(4, ""
					+ ((double) v.getBorderColor().getRed() / 256d) + ","
					+ ((double) v.getBorderColor().getGreen() / 256d) + ","
					+ ((double) v.getBorderColor().getBlue() / 256d));
			stAddNodes.setDouble(5, v.getSize());
			stAddNodes.setString(6, v.getShape());
			stAddNodes.setInt(7, v.getWidth());
			stAddNodes.setBoolean(8, v.isExcluded());
			stAddNodes.setString(9, v.getVar1());
			stAddNodes.setString(10, v.getVar2());
			stAddNodes.setDouble(11, v.getPosition() != null ? v.getPosition()
					.getX() : 0);
			stAddNodes.setDouble(12, v.getPosition() != null ? v.getPosition()
					.getY() : 0);
			stAddNodes.setBoolean(13, v.isFixed());
			stAddNodes.addBatch();
		}
		connection.setAutoCommit(false);
		stAddNodes.executeBatch();
		connection.setAutoCommit(true);

		PreparedStatement stAddEdges = connection
				.prepareStatement("INSERT INTO CFEdges(id_origin, id_dest , weight , label , width , color , var1 , var2 , hide) VALUES (?,?,?,?,?,?,?,?,?)");
		for (Edge e : network.getEdges()) {			
			stAddEdges.setInt(1, e.getSource() != null ? e.getSource().getId() : network.getEndpoints(e)
					.getFirst().getId());
			stAddEdges.setInt(2, e.getSource() != null ? e.getTarget().getId() : network.getEndpoints(e)
					.getSecond().getId());
			stAddEdges.setDouble(3, e.getWeight());
			stAddEdges.setString(4, e.getLabel());
			stAddEdges.setDouble(5, e.getWidth());
			stAddEdges.setString(6, ""
					+ ((double) e.getColor().getRed() / 256d) + ","
					+ ((double) e.getColor().getGreen() / 256d) + ","
					+ ((double) e.getColor().getBlue() / 256d));
			stAddEdges.setString(7, e.getVar1());
			stAddEdges.setString(8, e.getVar2());
			stAddEdges.setBoolean(9, e.isExcluded());
			stAddEdges.addBatch();
		}

		connection.setAutoCommit(false);
		stAddEdges.executeBatch();
		connection.setAutoCommit(true);
		return connection;
	}

}
