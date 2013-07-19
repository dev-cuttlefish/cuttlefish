package ch.ethz.sg.cuttlefish.misc;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.sg.cuttlefish.exporter.TikzExporter;
import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.networks.Vertex;

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
	private javax.swing.JRadioButton sizeScaledRButton;
	private javax.swing.JLabel sizeLabel;
	private javax.swing.JRadioButton style3DRButton;
	private javax.swing.JRadioButton styleCircleRButton;
	private javax.swing.JLabel styleLabel;
	private javax.swing.JLabel widthLabel;
	private javax.swing.JTextField widthTextField;
	private JTextField coordTextField;
	private JTextField nodeTextField;
	private JTextField edgeTextField;
	
	public TikzDialog(TikzExporter tikzExporter, NetworkPanel networkPanel) {
		setResizable(false);
		this.networkPanel = networkPanel;
		this.tikzExporter = tikzExporter;
		initComponents();
		this.setTitle("Tikz export");
		this.setSize(431, 377);		
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
        sizeScaledRButton = new JRadioButton("Scaled");
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
        
        sizeScaledRButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				sizeScaledRButtonActionPerformed(e);
			}			
		});

        widthLabel.setText("width");
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

        heightLabel.setText("height");
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
        sizeButtons.add(sizeScaledRButton);
        
        ButtonGroup styleButtons = new ButtonGroup();
        styleButtons.add(style3DRButton);
        styleButtons.add(styleCircleRButton);
        styleCircleRButton.setSelected(true);
        
        JLabel nodeFactorLabel = new JLabel("Node factor");
        nodeTextField = new JTextField();
        nodeTextField.setColumns(10);
        
        JLabel edgeFactorLabel = new JLabel("Edge Factor");
        edgeTextField = new JTextField();
        edgeTextField.setColumns(10);
        
        JLabel coordinatesFactorLabel = new JLabel("Coordinates Factor");
        coordTextField = new JTextField();
        coordTextField.setColumns(10);
        
        calculateHeightAndWidth();
        getScalingFactors();
        
		sizeDefaultRButton.setSelected(true);
        widthTextField.setEnabled(false);        
        heightTextField.setEnabled(false);
        coordTextField.setEnabled(false);
        nodeTextField.setEnabled(false);
        edgeTextField.setEnabled(false);
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addContainerGap()
        					.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
        						.addGroup(layout.createSequentialGroup()
        							.addGap(5)
        							.addComponent(outputLabel)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(fileTextField)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(browseButton))
        						.addGroup(layout.createSequentialGroup()
        							.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        								.addComponent(styleLabel)
        								.addComponent(sizeLabel))
        							.addPreferredGap(ComponentPlacement.UNRELATED)
        							.addGroup(layout.createParallelGroup(Alignment.LEADING)
        								.addComponent(sizeDefaultRButton)
        								.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        									.addGroup(layout.createSequentialGroup()
        										.addGroup(layout.createParallelGroup(Alignment.LEADING)
        											.addComponent(style3DRButton)
        											.addComponent(sizeFixedRButton))
        										.addGap(7)
        										.addGroup(layout.createParallelGroup(Alignment.LEADING)
        											.addComponent(widthLabel)
        											.addComponent(heightLabel))
        										.addPreferredGap(ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
        										.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        											.addComponent(styleCircleRButton)
        											.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
        												.addComponent(heightTextField, Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
        												.addComponent(widthTextField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))))
        									.addGroup(Alignment.LEADING, layout.createSequentialGroup()
        										.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        											.addGroup(layout.createSequentialGroup()
        												.addComponent(sizeScaledRButton)
        												.addPreferredGap(ComponentPlacement.RELATED)
        												.addComponent(nodeFactorLabel)
        												.addGap(65))
        											.addGroup(layout.createSequentialGroup()
        												.addGroup(layout.createParallelGroup(Alignment.LEADING)
        													.addComponent(edgeFactorLabel)
        													.addComponent(coordinatesFactorLabel))
        												.addPreferredGap(ComponentPlacement.RELATED)))
        										.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
        											.addComponent(nodeTextField, 0, 0, Short.MAX_VALUE)
        											.addComponent(edgeTextField, 0, 0, Short.MAX_VALUE)
        											.addComponent(coordTextField, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)))))
        							.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE))))
        				.addGroup(layout.createSequentialGroup()
        					.addGap(133)
        					.addComponent(exportButton)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(CancelButton)))
        			.addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGap(22)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(outputLabel)
        				.addComponent(fileTextField, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
        				.addComponent(browseButton))
        			.addGap(18)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(styleLabel)
        				.addComponent(styleCircleRButton)
        				.addComponent(style3DRButton))
        			.addGap(18)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(sizeDefaultRButton)
        				.addComponent(sizeLabel))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        					.addComponent(sizeFixedRButton)
        					.addComponent(widthLabel)
        					.addComponent(widthTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        				.addGroup(layout.createSequentialGroup()
        					.addGap(25)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(heightLabel)
        						.addComponent(heightTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(sizeScaledRButton)
        				.addComponent(nodeFactorLabel)
        				.addComponent(nodeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(edgeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(edgeFactorLabel))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(coordTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(coordinatesFactorLabel))
        			.addPreferredGap(ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(exportButton)
        				.addComponent(CancelButton))
        			.addGap(33))
        );
        getContentPane().setLayout(layout);
    }

	private void getScalingFactors() {
		coordTextField.setText(tikzExporter.getCoordinatesFactor()+"");
        nodeTextField.setText(tikzExporter.getNodeSizeFactor()+"");
        edgeTextField.setText(tikzExporter.getEdgeSizeFactor()+"");
	}

	private void calculateHeightAndWidth() {
		double xmin = Double.MAX_VALUE, xmax = Double.MIN_VALUE, ymin = Double.MAX_VALUE, ymax = Double.MIN_VALUE;
        for(Vertex v : networkPanel.getNetwork().getVertices() ) {
        	if (v.getPosition().getX() < xmin)
        		xmin = v.getPosition().getX();
        	if (v.getPosition().getX() > xmax)
        		xmax = v.getPosition().getX();
        	if (v.getPosition().getY() < ymin)
        		ymin = v.getPosition().getY();
        	if (v.getPosition().getY() > ymax)
        		ymax = v.getPosition().getY();
        }
        
        widthTextField.setText(java.lang.Math.round(xmax-xmin)+"");
        heightTextField.setText(java.lang.Math.round(ymax-ymin)+"");
	}

	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		FileChooser fc = new FileChooser();
		fc.setDialogTitle("Output to file...");
		fc.setFileFilter(new FileNameExtensionFilter(".tex files", "tex"));
		int returnVal = fc.showSaveDialog(networkPanel);

		try {
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				tikzExporter.setWriter(new BufferedWriter(new FileWriter(fc
						.getSelectedFile())));
				exportButton.setEnabled(true);
				String filePath = "";
				filePath = fc.getSelectedFile().getCanonicalPath();
				fileTextField.setText(filePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
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
		
		nodeTextField.setEnabled(false);
		edgeTextField.setEnabled(false);
		coordTextField.setEnabled(false);
	}

	private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {
		tikzExporter.setFixedSize(sizeFixedRButton.isSelected());
		
		if(sizeFixedRButton.isSelected()) {
			int width, height;
			width = Integer.parseInt(widthTextField.getText());
			height = Integer.parseInt(heightTextField.getText());
			tikzExporter.setSize(width, height);
			
		} else if(sizeScaledRButton.isSelected()) {
			double node, edge, coord;
			node = Double.parseDouble(nodeTextField.getText());
			edge = Double.parseDouble(edgeTextField.getText());
			coord = Double.parseDouble(coordTextField.getText());
			tikzExporter.setScalingFactors(node, edge, coord);
			
		} else if(sizeDefaultRButton.isSelected()) {
			// Select default scaling factors
			tikzExporter.setDefaultFactors();
			getScalingFactors();
			calculateHeightAndWidth();
			
		}
		
		if(style3DRButton.isSelected()) {
			tikzExporter.setNodeStyle("ball");
		} else {
			tikzExporter.setNodeStyle("circle");
		}
		
		try {
			tikzExporter.setWriter(new BufferedWriter(new FileWriter(new File(fileTextField.getText()))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tikzExporter.execute();
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
		
		nodeTextField.setEnabled(false);
		edgeTextField.setEnabled(false);
		coordTextField.setEnabled(false);
	}
	
	private void sizeScaledRButtonActionPerformed(ActionEvent e) {
		nodeTextField.setEnabled(true);
		edgeTextField.setEnabled(true);
		coordTextField.setEnabled(true);
		
		widthTextField.setEnabled(false);
		heightTextField.setEnabled(false);
		tikzExporter.setFixedSize(false);
	}
	
	public void setTikzExporter(TikzExporter tikzExporter) {
		this.tikzExporter = tikzExporter;		
	}

	private void style3DRButtonActionPerformed(ActionEvent evt) {
		tikzExporter.setNodeStyle("ball");
	}
}
