package ch.ethz.sg.cuttlefish.gui2.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;

import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui2.Observer;
import ch.ethz.sg.cuttlefish.gui2.Subject;
import ch.ethz.sg.cuttlefish.gui2.toolbars.DBToolbar;
import ch.ethz.sg.cuttlefish.gui2.toolbars.MouseToolbar;
import ch.ethz.sg.cuttlefish.gui2.toolbars.SimulationToolbar;
import ch.ethz.sg.cuttlefish.gui2.toolbars.ZoomToolbar;

public class ViewMenu extends AbstractMenu implements ItemListener, Observer{	
	
	JCheckBoxMenuItem mouseToolbarCheckbox;
	JCheckBoxMenuItem zoomToolbarCheckbox;
	JCheckBoxMenuItem simulationToolbarCheckbox;
	JCheckBoxMenuItem dbToolbarCheckbox;
	
	public ViewMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		initialize();
		this.setText("View");
	}
	
	private void initialize() {
		mouseToolbarCheckbox = new JCheckBoxMenuItem("Mouse toolbar");
		zoomToolbarCheckbox = new JCheckBoxMenuItem("Zoom toolbar");
		simulationToolbarCheckbox = new JCheckBoxMenuItem("Simulation toolbar");
		dbToolbarCheckbox = new JCheckBoxMenuItem("Database toolbar");
		
		mouseToolbarCheckbox.addItemListener(this);
		zoomToolbarCheckbox.addItemListener(this);
		simulationToolbarCheckbox.addItemListener(this);
		dbToolbarCheckbox.addItemListener(this);
		
		this.add(mouseToolbarCheckbox);
		this.add(zoomToolbarCheckbox);
		this.add(simulationToolbarCheckbox);
		this.add(dbToolbarCheckbox);

		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() == mouseToolbarCheckbox) {
			toolbars.getMouseToolbar().setVisible(mouseToolbarCheckbox.getState() );
			System.out.println("Setting mouse toolbar's visibility to " + mouseToolbarCheckbox.getState() );			
		} else if (e.getItem() == zoomToolbarCheckbox) {
			toolbars.getZoomToolbar().setVisible(zoomToolbarCheckbox.getState() );
			System.out.println("Setting zoom toolbar's visibility to " + zoomToolbarCheckbox.getState() );
		} else if (e.getItem() == simulationToolbarCheckbox) {
			toolbars.getSimulationToolbar().setVisible(simulationToolbarCheckbox.getState() );
			System.out.println("Setting simulation toolbar's visibility to " + zoomToolbarCheckbox.getState() );
		} else if (e.getItem() == dbToolbarCheckbox) {
			toolbars.getDBToolbar().setVisible(dbToolbarCheckbox.getState() );
			System.out.println("Setting database toolbar's visibility to " + dbToolbarCheckbox.getState() );
		} else {
			System.out.println("Unknown event");
		}
	}

	@Override
	public void update(Subject o) {
		if(o instanceof MouseToolbar) {
			mouseToolbarCheckbox.setSelected( ((MouseToolbar)o).isVisible() );
		} else if(o instanceof ZoomToolbar) {
			zoomToolbarCheckbox.setSelected( ((ZoomToolbar)o).isVisible() );
		} else if(o instanceof SimulationToolbar) {
			simulationToolbarCheckbox.setSelected( ((SimulationToolbar)o).isVisible() );
			if(((SimulationToolbar)o).isEnabled()) {
				simulationToolbarCheckbox.setEnabled(true);
			} else {
				simulationToolbarCheckbox.setEnabled(false);
			}
		} else if(o instanceof DBToolbar) {
			dbToolbarCheckbox.setSelected( ((DBToolbar)o).isVisible() );
			if(((DBToolbar)o).isEnabled()) {
				dbToolbarCheckbox.setEnabled(true);
			} else {
				dbToolbarCheckbox.setEnabled(false);
			}
		}
	}


}