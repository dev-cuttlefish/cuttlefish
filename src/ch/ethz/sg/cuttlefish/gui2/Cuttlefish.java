package ch.ethz.sg.cuttlefish.gui2;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Cuttlefish extends JFrame {
	private JMenuBar mainMenu = null;
	private NetworkPanel networkPanel = null;
	
	public Cuttlefish() {
		super();
		initialize();
		JMenuBar m = new JMenuBar();
		JMenu m1 = new JMenu();
		m1.add(new JMenuItem("menu"));
		m.add(m1);
		this.add(m, BorderLayout.SOUTH);
	}   
	
	public static void main(String[] args) {
		JFrame cuttlefishWindow = new Cuttlefish();
	    cuttlefishWindow = new Cuttlefish();		
		cuttlefishWindow.setVisible(true);
	} 
	
	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(1000, 700);  //The initial size of the user interface is slightly smaller than 1024x768
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().add(getMainMenu(), BorderLayout.NORTH);
		this.getContentPane().add(getNetworkPanel(), BorderLayout.CENTER);
		this.setTitle("Cuttlefish 2.0");
	}
	
	private JMenuBar getMainMenu() {
		if(mainMenu == null) {
			mainMenu = new JMenuBar();
			mainMenu.add(new ImportMenu(getNetworkPanel()));
		}
		return mainMenu;
	}
	

	private NetworkPanel getNetworkPanel() {
		if (networkPanel == null) {
			networkPanel = new NetworkPanel();
		}
		return networkPanel;
	}
}
