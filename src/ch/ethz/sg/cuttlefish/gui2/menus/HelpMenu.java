package ch.ethz.sg.cuttlefish.gui2.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;


import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;

public class HelpMenu extends AbstractMenu {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4251419612908498161L;
	JMenuItem about;
	
	public HelpMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		initialize();
		this.setText("Help");
	}
	
	private void initialize() {
		about = new JMenuItem("About");
		this.add(about);
		about.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				//JOptionPane.showMessageDialog(this, "hellp");
			}
		});
	}

}
