package ch.ethz.sg.cuttlefish.gui2;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JMenu;
import javax.swing.JToolBar;

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
