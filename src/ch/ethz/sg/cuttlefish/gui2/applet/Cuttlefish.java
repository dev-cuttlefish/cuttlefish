package ch.ethz.sg.cuttlefish.gui2.applet;

import java.applet.Applet;
import java.awt.BorderLayout;



import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.ExploreNetwork;
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

	private String json = "";
	private String sourceNodeID = "";
	private String distance = "";
	private int width = 1000;
	private int height = 800;
	
    public void init() {    	
    	json = getParameter("data");
    	System.out.println(json);
    	sourceNodeID = getParameter("source_node");
    	distance = getParameter("distance");
    	if(getParameter("width") != null)
    		width = Integer.parseInt(getParameter("width"));
    	if(getParameter("height") != null)
    		height = Integer.parseInt(getParameter("height"));
    	
    	try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) { 
            System.err.println("createGUI didn't complete successfully");
        }
        getToolbars().getDBToolbar().exploreNodeFrame().setNodeField(sourceNodeID);
        getToolbars().getDBToolbar().exploreNodeFrame().setDistanceField(distance);
        getToolbars().getDBToolbar().exploreNodeFrame().clickOk();
    }
    

    public void start() {
    	this.setVisible(true);
    }

    public void stop() {}

    public void destroy() {}
    
    private void createGUI() {
    	this.setSize(width,height);  
    	
    	JPanel container = new JPanel();
    	container.setLayout(new BorderLayout() );    	   
    	
    	JPanel controlContainer = new JPanel();    	
    			
    	controlContainer.setLayout(new BoxLayout(controlContainer, BoxLayout.Y_AXIS));
    	controlContainer.add(getMenu());
    	controlContainer.add(getToolbars());
    	container.add(controlContainer, BorderLayout.NORTH);
    	container.add(getNetworkPanel(), BorderLayout.CENTER);    	
    		
		toolbars.getSimulationToolbar().addObserver(getMenu().getViewMenu());
		toolbars.getMouseToolbar().addObserver(getMenu().getViewMenu());
		toolbars.getZoomToolbar().addObserver(getMenu().getViewMenu());
		toolbars.getDBToolbar().addObserver(getMenu().getViewMenu());
		
		toolbars.getSimulationToolbar().setVisible(true);
		toolbars.getMouseToolbar().setVisible(true);
		toolbars.getZoomToolbar().setVisible(true);
		toolbars.getDBToolbar().setVisible(true);
		networkPanel.addObserver(getMenu().getLayoutMenu());
		
		
		this.add(container);
    }
    
    private NetworkPanel getNetworkPanel() {
    	if(networkPanel == null) {
    		networkPanel = new NetworkPanel(width,height-100);
    		networkPanel.setNetwork(getNetwork());
    	}
    	return networkPanel;
    }
    
    private BrowsableNetwork getNetwork() {
    	if(network == null) { 
 			network = new ExploreNetwork();	 
 			JsonNetwork jsonNetwork = new JsonNetwork( json );
 			((ExploreNetwork)network).connect(jsonNetwork );
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
    /*
    String jsonNodesExample = "[ " +
    		"{ \"id\": 0," +
    		"\"label\": \"node0\"," +
    		"\"color\": \"{1,0,0}\"," +
    		"\"borderColor\": \"{0,1,0}\"," +
    		"\"size\": 10," +
    		"\"shape\": \"square\"," +
    		"\"width\": 3," +
    		"\"x\": 4," +
    		"\"y\": 5," +
    		"\"var1\": \"var1\"," +
    		"\"var2\": \"var2\"," +
    		"\"hide\": \"false\"," +
    		"\"fixed\": \"false\" }," +	
    		"{ \"id\": 1," +
    		"\"label\": \"node1\"," +
    		"\"color\": \"{1,0,1}\"," +
    		"\"borderColor\": \"{1,1,0}\"," +
    		"\"size\": 20," +
    		"\"shape\": \"circle\"," +
    		"\"width\": 3," +
    		"\"x\": 4," +
    		"\"y\": 5," +
    		"\"var1\": \"var1\"," +
    		"\"var2\": \"var2\"," +
    		"\"hide\": \"false\"," +
    		"\"fixed\": \"false\" }" +	
    		" ]";

    String jsonEdgesExample = "[ " +
    		"{ \"id_origin\": 0," +
    		"\"id_dest\": 1," +
    		"\"label\": \"edge01\"," +
    		"\"weight\": 4," +
    		"\"width\": 5," +
    		"\"color\": \"{0,0,1}\"," +
    		"\"var1\": \"var1\"," +
    		"\"var2\": \"var2\"," +
    		"\"hide\": \"false\" }" +		    		
    		" ]";
*/
}
