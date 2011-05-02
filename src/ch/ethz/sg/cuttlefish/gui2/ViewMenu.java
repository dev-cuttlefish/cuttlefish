package ch.ethz.sg.cuttlefish.gui2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;

public class ViewMenu extends AbstractMenu implements ItemListener, Observer{	
	
	JCheckBoxMenuItem mouseToolbarCheckbox;
	JCheckBoxMenuItem zoomToolbarCheckbox;
	JCheckBoxMenuItem simulationToolbarCheckbox;
	
	public ViewMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		initialize();
		this.setText("View");
	}
	
	private void initialize() {
		mouseToolbarCheckbox = new JCheckBoxMenuItem("Mouse toolbar");
		zoomToolbarCheckbox = new JCheckBoxMenuItem("Zoom toolbar");
		simulationToolbarCheckbox = new JCheckBoxMenuItem("Simulation toolbar");
		
		mouseToolbarCheckbox.addItemListener(this);
		zoomToolbarCheckbox.addItemListener(this);
		simulationToolbarCheckbox.addItemListener(this);
		
		this.add(mouseToolbarCheckbox);
		this.add(zoomToolbarCheckbox);
		this.add(simulationToolbarCheckbox);

		
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
		}
	}


}