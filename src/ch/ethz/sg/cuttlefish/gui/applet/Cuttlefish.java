package ch.ethz.sg.cuttlefish.gui.applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.JsonNetwork;

public class Cuttlefish extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BrowsableNetwork network = null;
	private NetworkPanel networkPanel = null;
	private CuttlefishMenu menu = null;
	private CuttlefishToolbars toolbars = null;
	
    public void init() {
    	try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) { 
            System.err.println("createGUI didn't complete successfully");
        }          
    }

    public void start() {
    	this.setVisible(true);
    }

    public void stop() {
    }

    public void destroy() {
    }
    
    private void createGUI() {

    	this.setSize(1000, 700);
    	
    	JPanel container = new JPanel();
    	container.setSize(1000, 700);
    	container.setLayout(new BorderLayout() );
    	JPanel controlContainer = new JPanel();
    	controlContainer.setLayout(new BoxLayout(controlContainer, BoxLayout.Y_AXIS));
    	controlContainer.add(getMenu());
    	controlContainer.add(getToolbars());
    	container.add(controlContainer, BorderLayout.NORTH);
    	container.add(getNetworkPanel(), BorderLayout.CENTER);
    	this.add(container);
    		
		toolbars.getSimulationToolbar().addObserver(getMenu().getViewMenu());
		toolbars.getMouseToolbar().addObserver(getMenu().getViewMenu());
		toolbars.getZoomToolbar().addObserver(getMenu().getViewMenu());
		toolbars.getDBToolbar().addObserver(getMenu().getViewMenu());
		
		toolbars.getSimulationToolbar().setVisible(true);
		toolbars.getMouseToolbar().setVisible(true);
		toolbars.getZoomToolbar().setVisible(true);
		toolbars.getDBToolbar().setVisible(true);
		networkPanel.addObserver(getMenu().getLayoutMenu());
    }
    
    private NetworkPanel getNetworkPanel() {
    	if(networkPanel == null) {
    		networkPanel = new NetworkPanel();
    		networkPanel.setNetwork(getNetwork());
    	}
    	return networkPanel;
    }
    
    private BrowsableNetwork getNetwork() {
    	if(network == null) {    		
    		network = new JsonNetwork(sampleJsonData);
    	}
    	return network;
    }
    
    private CuttlefishToolbars getToolbars() {
    	if(toolbars == null) {
    		toolbars = new CuttlefishToolbars(getNetworkPanel());
    	}
    	return toolbars;
    }
    
    private CuttlefishMenu getMenu() {
    	if(menu == null) {
    		menu = new CuttlefishMenu(getNetworkPanel(), getToolbars());
    	}
    	return menu;
    	
    }
    
    private String sampleJsonData = "[ { \"adjacencies\": [ 9,15,16,27,34,39,41,43,48,58,60,65,69,70,71,89,98,105,107,112,116,122,124,127,128,138,139,144 ],      \"data\": \"null\",\"id\": 0,\"name\": \"A12\" },{ \"adjacencies\": [ 0,11,18,22,23,25,30,38,44,49,55,56,62,65,67,69,71,80,82,87,95,98,106,108,110,114,127,128 ],\"data\": \"null\",\"id\": 1,\"name\": \"B2\" },{ \"adjacencies\": [ 0,1,4,6,11,14,15,16, 19,22,25,27,37,40,49,54,56,70,73,77,80,83,94,100,107,108,122,124,135,144 ],\"data\": \"null\",\"id\": 2,\"name\": \"C3\" },{ \"adjacencies\": [ 4,8,11,13,19,23,31,39,40,42,43,44,49,51,55,56,57,70,78,79,80,94,95,96,99,113,121,126,135,137 ],\"data\":  \"null\",\"id\": 3,\"name\": \"ABA\" },{ \"adjacencies\": [ 1,6,25,27,34,39,41,42,52,57,71,77,81,82,83,89,104,106,107,113,   115,117,119,121,122,130,136,147 ],\"data\": \"null\",\"id\": 4,\"name\": \"FOO\" },{ \"adjacencies\": [ 0,3,4,6,15,24,28,31, 34,38,41,44,51,53,56,58,65,73,78,79,81,84,85,86,94,98,102,121,124,126,129,135,142,144,147 ],\"data\": \"null\",\"id\": 5,    \"name\": \"BAR\" },{ \"adjacencies\": [ 7,20,25,30,32,37,38,48,51,52,58,68,69,71,80,86,93,95,110,112,116,121,127,130,131,   135 ],\"data\": \"null\",\"id\": 6,\"name\": \"GM\" },{ \"adjacencies\": [ 1,2,8,11,15,19,24,32,37,52,57,67,68,70,73,79,82,  83,85,89,93,94,98,99,100,117,122,126,128,131,143 ],\"data\": \"null\",\"id\": 7,\"name\": \"GA\" },{ \"adjacencies\": [ 1,2, 10,14,15,25,32,37,39,41,43,47,48,49,52,55,60,67,68,80,83,84,85,86,94,104,112,129,135 ],\"data\": \"null\",\"id\": 8,         \"name\": \"AAA\" },{ \"adjacencies\": [ 1,2,5,6,7,8,11,13,16,25,34,39,40,48,53,56,62,65,67,69,73,78,82,83,85,87,94,98,100,  106,114 ],\"data\": \"null\",\"id\": 9,\"name\": \"AABB\" },{ \"adjacencies\": [ 9,23,27,34,49,56,66,69,83,84,86,87,89,98,99,100,104,110,112,119,131,135 ],\"data\": \"null\",\"id\": 10,\"name\": \"MM\" },{ \"adjacencies\": [ 8,19,20,24,25,34,51,52,  55,56,57,66,73,86,89,94,98,122,124,137 ],\"data\": \"null\",\"id\": 11,\"name\": \"BRB\" },{ \"adjacencies\": [ 0,2,4,15,20, 30,41,42,44,50,52,55,66,71,72,80,86,87,96,98,100,106,115,117,123,130,131,137,142,143 ],\"data\": \"null\",\"id\": 13,        \"name\": \"PBS\" },{ \"adjacencies\": [ 1,7,9,10,11,13,19,44,48,49,53,54,62,65,69,72,82,83,93,94,98,100,103,114,117,121,127,129,131,135,137,142,143,144 ],\"data\": \"null\",\"id\": 14,\"name\": \"ALL\" } ]";

}
