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

import javax.swing.JPanel;

import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

public abstract class BrowserWidget extends JPanel{

	private static final long serialVersionUID = 1L;
	private boolean isActive=false;
	private INetworkBrowser browser;
	private GroupPanel groupPanel = null;
	private BrowsableNetwork network = null;
	private String id;
	private boolean clickable=false;
	private Class<?> networkClass = null;
	private Hashtable<String, String> arguments = new Hashtable<String, String>();
	
	public final String getId() {
		return id;
	}
	public final void setId(String id) {
		this.id = id;
	}
	
	public final String getArgument(String name) {
		return arguments.get(name);
	}
	
	

	protected final void setArguments(Hashtable<String, String> arguments) {
		this.arguments = arguments;
	}
	
	
	public final void setNetwork(BrowsableNetwork network){
	

	//System.out.println();
		if(networkClass == null || networkClass.isInstance(network)){
			try {
				this.network = network;
				
				this.setVisible(true);
				onNetworkSet();
			//	System.out.println(getId() + "\t X");
				
			} catch (Exception e) {
				this.setVisible(false);
				this.network = null;
				System.err.println(e);
				System.out.println(getId() + "\t");
			}
		}else{
			this.setVisible(false);
			this.network = null;
		}
		
	}
	
	public final BrowsableNetwork getNetwork(){
		return network;
	}
	
	protected void onNetworkSet(){
	}
	
	public final boolean isActive() {
		return isActive;
	}
	
	public final void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public INetworkBrowser getBrowser() {
		return browser;
	}
	public void setBrowser(INetworkBrowser browser) {
		this.browser = browser;
	}
	
	protected void onActiveChanged(){}
	
	public final boolean isClickable() {
		return clickable;
	}
	
	public void updateAnnotations(){}
	
	public abstract void init();
	
	public final GroupPanel getGroupPanel() {
		return groupPanel;
	}
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
	
	public final Class<?> getNetworkClass() {
		return networkClass;
	}
	protected final void setNetworkClass(Class<?> networkClass) {
		this.networkClass = networkClass;
	}
	protected final void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	
	

	
	
}
