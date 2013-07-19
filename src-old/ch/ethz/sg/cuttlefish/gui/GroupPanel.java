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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Extension of JPanel that contains a widget to be added to a tab
 */
public class GroupPanel extends JPanel implements ItemSelectable{

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private JPanel labelPanel = null;
	private JPanel contentPanel = null;
	private Color highlightColor = Color.orange;  //  @jve:decl-index=0:
	private BrowserWidget widget=null;
	private ArrayList<ItemListener> listeners = new ArrayList<ItemListener>();
	/**
	 * This is the default constructor
	 */
	public GroupPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this with the default user interface options
	 * @return void
	 */
	private void initialize() {
		jLabel = new JLabel();
		jLabel.setText("JLabel");
		jLabel.setBackground(Color.gray);
		jLabel.setForeground(highlightColor);
		jLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.setSize(300, 200);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		this.add(getLabelPanel(), BorderLayout.SOUTH);
		this.add(getContentPanel(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes labelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLabelPanel() {
		if (labelPanel == null) {
			labelPanel = new JPanel();
			labelPanel.setLayout(new GridBagLayout());
			labelPanel.setBackground(Color.gray);
			labelPanel.add(jLabel, new GridBagConstraints());
			labelPanel.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					setActivated(!widget.isActive());
				}
			});
		}
		return labelPanel;
	}

	/**
	 * This method initializes contentPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(new BorderLayout());
			
		}
		return contentPanel;
	}
	
	/**
	 * This method sets the widget represented in this GroupPanel
	 * @param widget created and ready to display
	 * @param id identifier of the panel
	 */
	public  void setBrowserWidget(BrowserWidget widget, String id){
		if(this.widget==null){
			contentPanel.add((Component) widget, BorderLayout.CENTER);
			widget.setId(id);
			this.widget = widget;
			widget.setGroupPanel(this);
			
		}
	}
	
	/**
	 * Sets the label of the panel
	 * @param label
	 */
	public void setLabel(String label){
		jLabel.setText(label);
	}
	/**
	 * Gives the current label of the panel
	 * @return String label
	 */
	public String getLabel(){
		return jLabel.getText();
	}

	/**
	 * returns whether the widget is active or not
	 * @return boolean true if the widget was active, false otherwise
	 */
	public boolean isActivated() {
		return widget.isActive();
	}

	/**
	 * activates or deactivates the widget in the panel
	 * @param activated boolean true to activate and false to deactivate
	 */
	public void setActivated(boolean activated) {
		if(widget!=null && widget.isClickable()){
			widget.setActive(activated);
			if(activated){
				labelPanel.setBackground(highlightColor);
				jLabel.setForeground(Color.GRAY);
			}else{
				labelPanel.setBackground(Color.GRAY);
				jLabel.setForeground(highlightColor);
			}
			if(widget!=null){
				widget.onActiveChanged();
			}
			//notify the listeners over the widet the state change event
			for(ItemListener listener: listeners){
				ItemEvent e = new ItemEvent(this,0,widget, isActivated() ? ItemEvent.SELECTED : ItemEvent.DESELECTED);
				listener.itemStateChanged(e);
			}
		}
	}

	/**
	 * checks whether the widget can be clicked or not
	 * @return boolean
	 */
	public boolean isClickable() {
		return widget.isClickable();
	}

	/**
	 * getter for the higlighting color of the panel
	 * @return Color
	 */
	public Color getHighlightColor() {
		return highlightColor;
	}

	/**
	 * Setter for the highlight color
	 * @param highlightColor
	 */
	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
	}

   /**
    * adds a new itemListener to the listener list
    * @param ItemListener to be added
    */
	public void addItemListener(ItemListener arg0) {
		listeners.add(arg0);
	}

	/**
	 * Gets a list of the active objects (just the widget here)
	 * @return array with the objects
	 */
	public Object[] getSelectedObjects() {
	
		if(isActivated()){
			return new Object[]{widget};
		}
		return null;
	}

	/**
	 * Listener remover
	 * @param ItemListener to remove
	 */
	public void removeItemListener(ItemListener arg0) {
		listeners.remove(arg0);
		
	}

}
