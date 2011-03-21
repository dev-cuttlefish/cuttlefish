package ch.ethz.sg.cuttlefish.gui;

import java.util.Hashtable;

import javax.swing.JMenu;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

public abstract class BrowserMenu extends JMenu {

	private static final long serialVersionUID = 1L;
	private String condition;
	protected Hashtable<String, String> arguments = new Hashtable<String, String>();
	
	private INetworkBrowser browser;
	
	public BrowserMenu(String condition) {
		this.condition = condition;
	}
	
	/**
	 * Method to query the value of one of the arguments of the menu
	 * @param name of the parameter
	 * @return String with the value of the parameter
	 */
	public final String getArgument(String name) {
		return arguments.get(name);
	}

	/**
	 * Method that sets the arguments table
	 * @param arguments Hashtable with the arguments mapping
	 */
	protected final void setArgument(String key, String value) {
		arguments.put(key, value);
	}
	
	/**
	 * Initializer method
	 */
	public abstract void init();
	
	/**
	 * Getter of the networkBrowser interface that the menu is related to
	 * @return INetworkBrowser
	 */
	public INetworkBrowser getBrowser() {
		return browser;
	}
	
	/**
	 * Setter for the networkBrowser of the menu
	 * @param browser
	 */
	public void setBrowser(INetworkBrowser browser) {
		this.browser = browser;
	}
	
	/**
	 * Method that checks that the network fulfills the condition of being the proper class for the tab
	 * @param network that cuttlefish is treating
	 * @return
	 */
	
	public boolean checkCondition(BrowsableNetwork network){
		if(condition==null  || condition.equals("*")){
   			return true;
   		}
   		try{
   			Class<?> clazz = Class.forName(condition);
   			clazz.cast(network);
   			return true;
	    
   		}catch(Exception e){
   			return false;
   		}
   	}
}
