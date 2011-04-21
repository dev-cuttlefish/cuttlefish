package ch.ethz.sg.cuttlefish.gui2;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Cuttlefish extends JFrame {
	private CuttlefishMenu mainMenu = null;
	private NetworkPanel networkPanel = null;
	
	public Cuttlefish() {
		super();
		initialize();
	}   
	

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setSize(1000, 700);  //The initial size of the user interface is slightly smaller than 1024x768
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getMainMenu());
		this.setContentPane(getNetworkPanel());
		this.setTitle("Cuttlefish 2.0");
	}
	
	private CuttlefishMenu getMainMenu() {
		if(mainMenu == null) {
			mainMenu = new CuttlefishMenu(getNetworkPanel());
		}
		return mainMenu;
	}
	

	private NetworkPanel getNetworkPanel() {
		if (networkPanel == null) {
			networkPanel = new NetworkPanel();
		}
		return networkPanel;
	}
	
	public static void main(String[] args) {
		JFrame cuttlefishWindow = new Cuttlefish();		
		cuttlefishWindow.setVisible(true);
	} 
}
