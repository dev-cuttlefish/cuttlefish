package ch.ethz.sg.cuttlefish.misc;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;

public class TikzDialog extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	NetworkPanel networkPanel;
	private TikzExporter tikzExporter;
	private javax.swing.JButton CancelButton;
	private javax.swing.JButton browseButton;
	private javax.swing.JButton exportButton;
	private javax.swing.JTextField fileTextField;
	private javax.swing.JLabel heightLabel;
	private javax.swing.JTextField heightTextField;
	private javax.swing.JLabel outputLabel;
	private javax.swing.JRadioButton sizeDefaultRButton;
	private javax.swing.JRadioButton sizeFixedRButton;
	private javax.swing.JLabel sizeLabel;
	private javax.swing.JRadioButton style3DRButton;
	private javax.swing.JRadioButton styleCircleRButton;
	private javax.swing.JLabel styleLabel;
	private javax.swing.JLabel widthLabel;
	private javax.swing.JTextField widthTextField;
	
	public TikzDialog(TikzExporter tikzExporter, NetworkPanel networkPanel) {
		this.networkPanel = networkPanel;
		this.tikzExporter = tikzExporter;
		initComponents();
		this.setTitle("Tikz export");
		this.setSize(410, 300);		
	}

	private void initComponents() {

        style3DRButton = new javax.swing.JRadioButton();
        styleCircleRButton = new javax.swing.JRadioButton();
        outputLabel = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        styleLabel = new javax.swing.JLabel();
        sizeLabel = new javax.swing.JLabel();
        sizeDefaultRButton = new javax.swing.JRadioButton();
        sizeFixedRButton = new javax.swing.JRadioButton();
        widthLabel = new javax.swing.JLabel();
        heightLabel = new javax.swing.JLabel();
        widthTextField = new javax.swing.JTextField();
        heightTextField = new javax.swing.JTextField();
        exportButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();

        style3DRButton.setText("3D ball");
        style3DRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	style3DRButtonActionPerformed(evt);
            }			
        });
        
        styleCircleRButton.setText("Circle");
        styleCircleRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleCircleRButtonActionPerformed(evt);
            }
        });

        outputLabel.setText("Output file");

        fileTextField.setText("");
        fileTextField.setMinimumSize(new Dimension(200, 19));
        fileTextField.setPreferredSize(new Dimension(200, 19));
        fileTextField.setMaximumSize(new Dimension(200, 19));
        fileTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileTextFieldActionPerformed(evt);
            }
        });
        fileTextField.addKeyListener(new KeyListener() {			
			@Override
			public void keyTyped(KeyEvent e) {}			
			@Override
			public void keyReleased(KeyEvent e) {
				if(fileTextField.getText().length() > 0) {
					exportButton.setEnabled(true);
				} else {
					exportButton.setEnabled(false);
				}
			}			
			@Override
			public void keyPressed(KeyEvent e) {}
		});

        browseButton.setText("Browse...");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        styleLabel.setText("Nodes style");

        sizeLabel.setText("Size");

        sizeDefaultRButton.setText("Default");
        sizeDefaultRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeDefaultRButtonActionPerformed(evt);
            }
        });

        sizeFixedRButton.setText("Fixed");
        sizeFixedRButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				sizeFixedRButtonActionPerformed(e);
			}			
		});

        widthLabel.setText("width");

        heightLabel.setText("height");
        calculateHeightAndWidth();
        widthTextField.setEnabled(false);        
        heightTextField.setEnabled(false);
        widthTextField.addFocusListener(new FocusListener() {			
			@Override
			public void focusLost(FocusEvent e) {
				int width, height;
				width = Integer.parseInt(widthTextField.getText());
				height = Integer.parseInt(heightTextField.getText());
				tikzExporter.setSize(width, height);
			}			
			@Override
			public void focusGained(FocusEvent e) {}
		});
        
        heightTextField.addFocusListener(new FocusListener() {			
			@Override
			public void focusLost(FocusEvent e) {
				int width, height;
				width = Integer.parseInt(widthTextField.getText());
				height = Integer.parseInt(heightTextField.getText());
				tikzExporter.setSize(width, height);
			}			
			@Override
			public void focusGained(FocusEvent e) {}
		});

        exportButton.setText("Export");
        exportButton.setEnabled(false);
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });
        
        ButtonGroup sizeButtons = new ButtonGroup();
        sizeButtons.add(sizeDefaultRButton);
        sizeButtons.add(sizeFixedRButton);
        sizeDefaultRButton.setSelected(true);
        
        ButtonGroup styleButtons = new ButtonGroup();
        styleButtons.add(style3DRButton);
        styleButtons.add(styleCircleRButton);
        styleCircleRButton.setSelected(true);
        

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(outputLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fileTextField))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(styleLabel)
                                    .addComponent(sizeLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(sizeDefaultRButton)
                                    .addComponent(style3DRButton))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(styleCircleRButton)
                                    .addComponent(sizeFixedRButton)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(21, 21, 21)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(widthLabel)
                                                .addGap(22, 22, 22))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(heightLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(heightTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                                            .addComponent(widthTextField))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(exportButton)
                        .addGap(18, 18, 18)
                        .addComponent(CancelButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputLabel)
                    .addComponent(fileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(styleLabel)
                    .addComponent(styleCircleRButton)
                    .addComponent(style3DRButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeDefaultRButton)
                    .addComponent(sizeFixedRButton)
                    .addComponent(sizeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(widthLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(heightLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportButton)
                    .addComponent(CancelButton))
                .addGap(33, 33, 33))
        );
    }

	public void calculateHeightAndWidth() {
		double xmin = Double.MAX_VALUE, xmax = Double.MIN_VALUE, ymin = Double.MAX_VALUE, ymax = Double.MIN_VALUE;
        for(Vertex v : networkPanel.getNetwork().getVertices() ) {
        	if ( networkPanel.getNetworkLayout().transform(v).getX() < xmin)
        		xmin = networkPanel.getNetworkLayout().transform(v).getX();
        	if( networkPanel.getNetworkLayout().transform(v).getX() > xmax)
        		xmax = networkPanel.getNetworkLayout().transform(v).getX();
        	if( networkPanel.getNetworkLayout().transform(v).getY() < ymin)
        		ymin = networkPanel.getNetworkLayout().transform(v).getY();
        	if( networkPanel.getNetworkLayout().transform(v).getY() > ymax)
        		ymax = networkPanel.getNetworkLayout().transform(v).getY();
        }
        widthTextField.setText(Integer.toString((int)(xmax-xmin)));
        heightTextField.setText(Integer.toString((int)(ymax-ymin)));
	}

	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		FileChooser fc = new FileChooser();
		fc.setDialogTitle("Output to file...");
		fc.setFileFilter(new FileNameExtensionFilter(".tex files", "tex"));
		int returnVal = fc.showSaveDialog(networkPanel);				
		if (returnVal == JFileChooser.APPROVE_OPTION) {					
			tikzExporter.setOutputFile(fc.getSelectedFile());
			exportButton.setEnabled(true);
			String filePath = "";
			try {
				filePath = fc.getSelectedFile().getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fileTextField.setText(filePath);
		}
		this.toFront();
	}

	private void styleCircleRButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		tikzExporter.setNodeStyle("circle");
	}

	private void sizeDefaultRButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		widthTextField.setEnabled(false);
		heightTextField.setEnabled(false);
		tikzExporter.setFixedSize(false);
	}

	private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {
		tikzExporter.setFixedSize(sizeFixedRButton.isSelected());
		int width, height;
		width = Integer.parseInt(widthTextField.getText());
		height = Integer.parseInt(heightTextField.getText());
		tikzExporter.setSize(width, height);
		if(style3DRButton.isSelected()) {
			tikzExporter.setNodeStyle("ball");
		} else {
			tikzExporter.setNodeStyle("circle");
		}
		tikzExporter.setOutputFile(new File(fileTextField.getText()));
		tikzExporter.exportToTikz(networkPanel.getNetworkLayout());
		this.setVisible(false);
	}

	private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}

	private void fileTextFieldActionPerformed(java.awt.event.ActionEvent evt) {		
	}

	private void sizeFixedRButtonActionPerformed(ActionEvent e) {
		widthTextField.setEnabled(true);
		heightTextField.setEnabled(true);
		tikzExporter.setFixedSize(true);
		int width, height;
		width = Integer.parseInt(widthTextField.getText());
		height = Integer.parseInt(heightTextField.getText());
		tikzExporter.setSize(width, height);
	}
	
	public void setTikzExporter(TikzExporter tikzExporter) {
		this.tikzExporter = tikzExporter;		
	}

	private void style3DRButtonActionPerformed(ActionEvent evt) {
		tikzExporter.setNodeStyle("ball");
	}
}
