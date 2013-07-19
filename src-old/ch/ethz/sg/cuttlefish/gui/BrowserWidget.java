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


package ch.ethz.sg.cuttlefish.gui;

import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

/**
 * JPanel that stores a general widget for cuttlefish
 */
public abstract class BrowserWidget extends JPanel{

	private static final long serialVersionUID = 1L;
	private boolean isActive=false;
	private boolean clickable=false;

	private String id;
	private Hashtable<String, String> arguments = new Hashtable<String, String>();
	
	private INetworkBrowser browser;
	private GroupPanel groupPanel = null;
	
	private BrowsableNetwork network = null;
	private Class<?> networkClass = null;
	
	/**
	 * Getter for the Id of the widget
	 * @return String id of the widget
	 */
	public final String getId() {
		return id;
	}
	/**
	 * Setter for the id of the widget
	 * @param id to set
	 */
	public final void setId(String id) {
		this.id = id;
	}

	/**
	 * Method to query the value of one of the arguments of the widget
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
	protected final void setArguments(Hashtable<String, String> arguments) {
		this.arguments = arguments;
	}
	
	
	/**
	 * Method that sets the network that the widget is related to
	 * @param network
	 */
	public final void setNetwork(BrowsableNetwork network){
	
		if(networkClass == null || networkClass.isInstance(network)){
			try {
				this.network = network;
				
				this.setVisible(true);
				onNetworkSet();
		
			} catch (Exception e) {
				this.setVisible(false);
				this.network = null;
				JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				System.err.println("Error setting the network for a widget");
				e.printStackTrace();
				System.out.println(getId() + "\t");
			}
		}
		else {
			this.setVisible(false);
			this.network = null;
		}
		
	}
	
	/**
	 * Getter for the network that the widget refers to
	 * @return network
	 */
	public final BrowsableNetwork getNetwork(){
		return network;
	}
	
	/**
	 * Method to be called when the network is set
	 */
	protected void onNetworkSet(){
	}
	
	/**
	 * Check method to know whether the widget is active
	 * @return boolean
	 */
	public final boolean isActive() {
		return isActive;
	}
	
	/**
	 * Method to activate or deactivate the widget
	 * @param isActive
	 */
	public final void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * Getter of the networkBrowser interface that the widget is related to
	 * @return INetworkBrowser
	 */
	public INetworkBrowser getBrowser() {
		return browser;
	}
	
	/**
	 * Setter for the networkBrowser of the widget
	 * @param browser
	 */
	public void setBrowser(INetworkBrowser browser) {
		this.browser = browser;
	}
	
	/**
	 * Method that should be called when a change has taken place
	 */
	protected void onActiveChanged(){}
	
	/**
	 * Check method for clickability of the widget
	 * @return boolean 
	 */
	public final boolean isClickable() {
		return clickable;
	}
	
	/**
	 * Updater for any annotations existing in the widget
	 */
	public void updateAnnotations(){}
	
	/**
	 * Initializer method
	 */
	public abstract void init();
	
	/**
	 * Getter for the gropuPanel where the widget is displayed
	 * @return GroupPanel
	 */
	public final GroupPanel getGroupPanel() {
		return groupPanel;
	}
	
	/**
	 * Setter for the groupPanel where the widget is displayed
	 * @param groupPanel
	 */
	public final void setGroupPanel(GroupPanel groupPanel) {
		this.groupPanel = groupPanel;
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if(getGroupPanel()!=null){
			getGroupPanel().setVisible(aFlag);
		}
		super.setVisible(aFlag);
		onActiveChanged();
	}
	
	/**
	 * Getter for the class of the network that is associated to the widget
	 * @return Class that instantiates the network
	 */ 
	public final Class<?> getNetworkClass() {
		return networkClass;
	}
	
	/**
	 * setter for the class of the network associated to the widget
	 * @param networkClass
	 */
	protected final void setNetworkClass(Class<?> networkClass) {
		this.networkClass = networkClass;
	}
	
	/**
	 * setter for the clickability of the widget
	 * @param clickable
	 */
	protected final void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	
	

	
	
}
