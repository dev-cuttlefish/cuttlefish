package ch.ethz.sg.cuttlefish.gui2;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;


public class Cuttlefish extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2232589699110179555L;
	public static File currentDirectory = null;
	private CuttlefishMenu mainMenu = null;
	private CuttlefishToolbars toolbars = null;
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
		this.setLayout(new BorderLayout());
		CuttlefishToolbars toolbars = getToolbars();
		this.add(toolbars, BorderLayout.PAGE_START);
		toolbars.getSimulationToolbar().addObserver(mainMenu.getViewMenu());
		toolbars.getMouseToolbar().addObserver(mainMenu.getViewMenu());
		toolbars.getZoomToolbar().addObserver(mainMenu.getViewMenu());
		toolbars.getDBToolbar().addObserver(mainMenu.getViewMenu());
		
		toolbars.getSimulationToolbar().setVisible(true);
		toolbars.getMouseToolbar().setVisible(true);
		toolbars.getZoomToolbar().setVisible(true);
		toolbars.getDBToolbar().setVisible(true);
		this.add(getNetworkPanel(), BorderLayout.CENTER);
		this.setTitle("Cuttlefish 2.0");
	}
	
	private CuttlefishToolbars getToolbars() {
		if(toolbars == null) {
			toolbars = new CuttlefishToolbars(getNetworkPanel());
		}
		return toolbars;
	}
	
	private CuttlefishMenu getMainMenu() {
		if(mainMenu == null) {
			mainMenu = new CuttlefishMenu(getNetworkPanel(), getToolbars());
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
