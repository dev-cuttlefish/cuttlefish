package ch.ethz.sg.cuttlefish.misc;

import java.io.File;

import javax.swing.JFileChooser;

public class FileChooser extends JFileChooser {
	
	/***
	 * This file chooser is customized to remember the last
	 * selected directory by the user
	 */
	
	public FileChooser() {
		super();
		if(ch.ethz.sg.cuttlefish.gui2.Cuttlefish.currentDirectory != null) {
			setCurrentDirectory(ch.ethz.sg.cuttlefish.gui2.Cuttlefish.currentDirectory);
		} else {
			setCurrentDirectory(new File(System.getProperty("user.dir") ) );
		}
	}
	
	@Override
	public void approveSelection() {
		ch.ethz.sg.cuttlefish.gui2.Cuttlefish.currentDirectory = getCurrentDirectory();
		super.approveSelection();
	}
}
