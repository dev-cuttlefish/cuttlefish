package ch.ethz.sg.cuttlefish.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.Edge;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class CxfToCmx {

	private static boolean writeToNodeFile(CxfNetwork network, File file) {
		try {
			FileWriter out = new FileWriter(file);
			out.write("nodeID;networkid;AliasID;Detail1;Detail2;Detail3;Detail4;Detail5\n");
			for(Vertex v : network.getVertices()) {
				out.write(Integer.toString(v.getId()));
				out.write(";1;0;");
				if(v.getLabel() != null) {
					out.write(v.getLabel());
				} else {
					out.write(Integer.toString(v.getId()));
				}
				out.write(";;;;\n");
			}
			out.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	private static boolean writeToLinkEventFiles(CxfNetwork network, File linkEventFile, File linkEventParentFile, File linkEventRecipientFile, File linkEventSenderFile) {
		try {
			FileWriter linkEventWriter = new FileWriter(linkEventFile);
			FileWriter linkEventParentWriter = new FileWriter(linkEventParentFile);
			FileWriter linkEventSenderWriter = new FileWriter(linkEventSenderFile);
			FileWriter linkEventRecipientWriter = new FileWriter(linkEventRecipientFile);
			linkEventWriter.write("linkeventID;networkid;linkeventDate;Subject;Content;Detail1;Detail2;Detail3;Detail4;Detail5\n");
			linkEventSenderWriter.write("linkeventID;senderNodeID;networkidca\n");
			linkEventRecipientWriter.write("linkeventID;recipientNodeID;networkid\n");
			linkEventParentWriter.write("linkeventID;parentLinkeventID;networkid\n");
			int eventId = 1;
			for(Edge e : network.getEdges()) {
				linkEventWriter.write("" + eventId + ";1;" + e.getVar1() + ";s" + eventId + ";c" + eventId + ";" + "d1;d2;d3;d4;" + e.getVar2() + "\n");				
				linkEventSenderWriter.write("" + eventId + ';' + e.getSource().getId() + ";1\n" );
				linkEventRecipientWriter.write("" + eventId + ';' + e.getTarget().getId() + ";1\n" );				
				eventId++;
			}				
			linkEventWriter.close();
			linkEventParentWriter.close();
			linkEventSenderWriter.close();
			linkEventRecipientWriter.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean cxfToCmx(CxfNetwork network, File linkEventFile, File linkEventParentFile, File linkEventRecipientFile, File linkEventSenderFile, File nodeFile) {
		File[] files = {linkEventFile, linkEventParentFile, linkEventRecipientFile, linkEventSenderFile, nodeFile};
		for(File f : files) {
			if(f.exists()) {
				int retval = JOptionPane.showConfirmDialog(null, "File " + f.getAbsolutePath() + " exists and will be overwritten. Proceed?", "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
				if(retval == JOptionPane.YES_OPTION) {
					f.delete();
				} else {
					return false;
				}
			}
		}
		boolean success = true;
		success = writeToNodeFile(network, nodeFile);
		success = writeToLinkEventFiles(network, linkEventFile, linkEventParentFile, linkEventRecipientFile, linkEventSenderFile);		
		return success;
	}
	
}
