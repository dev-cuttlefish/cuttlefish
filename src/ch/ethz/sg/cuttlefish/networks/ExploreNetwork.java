package ch.ethz.sg.cuttlefish.networks;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.misc.NetworkToSQLite;

public class ExploreNetwork extends DBNetwork {
	
	public ExploreNetwork() {
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * This method "connects" to a cxfFile, i.e., it reads the cxfFile and dumps
	 * the network to a SQLite in-memory database.
	 * 
	 * @param cxfFile
	 * @return
	 */
	public boolean connect(BrowsableNetwork network) {
		boolean connected = true;
		try {
			conn = NetworkToSQLite.networkToSQLiteDump(network);
		} catch (SQLException e) {
			connected = false;
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		if(connected) {
			this.setNetwork("CFEdges");
    		this.setNodeTable("CFNodes");
			getNetworkNames(null);			
		}
		
		return connected;
	}
	
	
}
