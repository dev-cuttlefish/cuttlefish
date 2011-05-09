package ch.ethz.sg.cuttlefish.gui2;

import java.awt.ActiveEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import ch.ethz.sg.cuttlefish.gui2.toolbars.DBToolbar;
import ch.ethz.sg.cuttlefish.gui2.toolbars.MouseToolbar;
import ch.ethz.sg.cuttlefish.gui2.toolbars.SimulationToolbar;
import ch.ethz.sg.cuttlefish.gui2.toolbars.ZoomToolbar;

public class CuttlefishToolbars extends JPanel {

	private MouseToolbar mouseToolbar;
	private ZoomToolbar zoomToolbar;
	private SimulationToolbar simulationToolbar;
	private DBToolbar dbToolbar;
	private static final long serialVersionUID = 1L;

	public CuttlefishToolbars(NetworkPanel networkPanel) {
		super();
		this.setLayout(new FlowLayout());
		mouseToolbar = new MouseToolbar(networkPanel);
		zoomToolbar = new ZoomToolbar(networkPanel);
		simulationToolbar = new SimulationToolbar(networkPanel);
		dbToolbar = new DBToolbar(networkPanel);
		this.add(mouseToolbar);
		this.add(zoomToolbar);
		this.add(simulationToolbar);
		this.add(dbToolbar);
		simulationToolbar.setVisible(true);
		dbToolbar.setVisible(true);
	}
	
	public MouseToolbar getMouseToolbar() {
		return mouseToolbar;
	}
	
	public ZoomToolbar getZoomToolbar() {
		return zoomToolbar;
	}
	
	public SimulationToolbar getSimulationToolbar() {
		return simulationToolbar;
	}
	
	public DBToolbar getDBToolbar() {
		return dbToolbar;
	}
}
