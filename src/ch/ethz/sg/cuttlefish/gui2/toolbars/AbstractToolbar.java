package ch.ethz.sg.cuttlefish.gui2.toolbars;


import java.util.LinkedList;

import javax.swing.JToolBar;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;

public abstract class AbstractToolbar extends JToolBar implements Subject {
	/**
	 * 
	 */
	protected LinkedList<Observer> observers;
	
	private static final long serialVersionUID = 1L;
	protected NetworkPanel networkPanel;
	
	public AbstractToolbar(NetworkPanel networkPanel) {
		this.networkPanel = networkPanel;
		this.setFloatable(true);
		observers = new LinkedList<Observer>();
	}
	
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	public void removeObserver(Observer o) {
		observers.remove(o);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		for(Observer o : observers) {
			o.update(this);
		}
	}
	
}
