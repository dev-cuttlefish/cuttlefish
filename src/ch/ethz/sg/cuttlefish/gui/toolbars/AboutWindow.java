package ch.ethz.sg.cuttlefish.gui.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ch.ethz.sg.cuttlefish.gui.NetworkPanel;

public class AboutWindow extends JFrame {		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static String logoIcon = "icons/CF1.gif";

		private JLabel logo;
		private JLabel bla;
		private JLabel authors;
		private JLabel info;
		private JButton close;
		
		public AboutWindow(NetworkPanel networkPanel) {
			initialize();
			this.setTitle("About");
			setLocationRelativeTo(networkPanel);
			this.setTitle("About");
			this.setSize(450, 323);
			this.setResizable(false);
		}
		
		private void initialize() {
			initComponents();

			close.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
		}
		private void initComponents() {

	        logo = new javax.swing.JLabel();
	        info = new javax.swing.JLabel();
	        close = new javax.swing.JButton();
	        bla = new javax.swing.JLabel();
	        authors = new javax.swing.JLabel();

	        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

	        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource(logoIcon)));
	        logo.setName("logo");

	        info.setFont(new java.awt.Font("Dialog", 0, 12));
	        info.setText("<html>For more information visit <a href=\"http://cuttlefish.sourceforge.net/\">http://cuttlefish.sourceforge.net/</a></html>");
	        info.setName("info");

	        close.setText("Close");
	        close.setName("close");

	        bla.setFont(new java.awt.Font("Dialog", 0, 12));
	        bla.setText("<html>Cuttlefish is a project maintained by the Chair of Systems Design<br>at ETH Zürich (<a href=\"http://www.sg.ethz.ch\">http://www.sg.ethz.ch</a>).<br>Cuttlefish is free software: you can redistribute it and/or modify it <br>under the terms of the GNU General Public License as published <br>by the Free Software Foundation, either version 3 of the License, or<br> (at your option) any later version.</html>");
	        bla.setName("bla");

	        authors.setFont(new java.awt.Font("Dialog", 0, 12));
	        authors.setText("Authors: David Garcia, Markus Geipel, Petar Tsankov");
	        authors.setName("authors");

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGap(0, 424, Short.MAX_VALUE)
	            .addGroup(layout.createSequentialGroup()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addContainerGap()
	                        .addComponent(logo))
	                    .addGroup(layout.createSequentialGroup()
	                        .addContainerGap()
	                        .addComponent(bla, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
	                    .addGroup(layout.createSequentialGroup()
	                        .addContainerGap()
	                        .addComponent(info, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addContainerGap())
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                .addContainerGap(178, Short.MAX_VALUE)
	                .addComponent(close)
	                .addGap(177, 177, 177))
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(authors)
	                .addContainerGap(105, Short.MAX_VALUE))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGap(0, 303, Short.MAX_VALUE)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(logo)
	                .addGap(18, 18, 18)
	                .addComponent(bla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(12, 12, 12)
	                .addComponent(authors)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(info, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
	                .addComponent(close)
	                .addContainerGap())
	        );

	        pack();
	    }
	}