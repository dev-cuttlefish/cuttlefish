package ch.ethz.sg.cuttlefish.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import ch.ethz.sg.cuttlefish.networks.GraphMLNetwork;
import ch.ethz.sg.cuttlefish.networks.InteractiveCxfNetwork;
import ch.ethz.sg.cuttlefish.networks.PajekNetwork;
import ch.ethz.sg.cuttlefish.networks.UserNetwork;

public class NetworkInitializer {
	
	public NetworkInitializer() {}
	
	public void initBrowsableNetwork(BrowsableNetwork network) {}
	
	public void initCxfNetwork(CxfNetwork cxfNetwork) {
		 JFileChooser fc = new JFileChooser();
		 fc.setDialogTitle("Select a CXF file");
		 fc.setFileFilter(new FileNameExtensionFilter(".cxf files", "cxf"));
		    fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
			int returnVal = fc.showOpenDialog(null);

         if (returnVal == JFileChooser.APPROVE_OPTION) {
             File file = fc.getSelectedFile();
             cxfNetwork.load(file);
         } else {
             System.out.println("Input cancelled by user");
         }
	}
	
	public void initPajekNetwork(PajekNetwork pajekNetwork) {
		 JFileChooser fc = new JFileChooser();
		 fc.setDialogTitle("Select a Pajek file");		 
		 fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
		 int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            pajekNetwork.load(file);
        } else {
            System.out.println("Input cancelled by user");
        }
	}

	public void initInteractiveCxfNetwork(InteractiveCxfNetwork interactiveCxfNetwork) {
		initCxfNetwork(interactiveCxfNetwork);
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select a CEF file");
		fc.setFileFilter(new FileNameExtensionFilter(".cef files", "cef"));
	    fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
		int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            interactiveCxfNetwork.loadInstructions(file);            
        } else {
            System.out.println("Input cancelled by user");
        }
	}
	
	public void initGraphMLNetwork(GraphMLNetwork graphmlNetwork) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select a GraphML file");
		fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
		fc.setFileFilter(new FileNameExtensionFilter(".graphml files", "graphml"));
		int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
		    fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
            graphmlNetwork.load(file);
        } else {
            System.out.println("Input cancelled by user");
        }
	}
	
	public void initUserNetwork(UserNetwork userNetwork) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select a CFF file");
	    fc.setCurrentDirectory( new File(System.getProperty("user.dir")));
	    fc.setFileFilter(new FileNameExtensionFilter(".cff files", "cff"));
		int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            userNetwork.load(file);
        } else {
            System.out.println("Input cancelled by user");
        }
	}
}
