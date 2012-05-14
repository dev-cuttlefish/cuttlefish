package ch.ethz.sg.cuttlefish.gui2.menus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.gui2.tasks.DumpToDBWorker;
import ch.ethz.sg.cuttlefish.networks.CxfNetwork;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout;


public class DBDump extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NetworkPanel networkPanel;
	private javax.swing.JToggleButton cancel;
	private javax.swing.JLabel info1;
	private javax.swing.JLabel info2;
	private javax.swing.JComboBox dbType;
	private javax.swing.JLabel dbTypeLabel;
	private javax.swing.JLabel lLabel;
	private javax.swing.JTextField linksField;
	private javax.swing.JTextField nodeField;
	private javax.swing.JLabel nLabel;
	private javax.swing.JToggleButton ok;
	private javax.swing.JTextField passwordField;
	private javax.swing.JLabel passwordLabel;
	private javax.swing.JTextField urlField;
	private javax.swing.JLabel urlLabel;
	private javax.swing.JTextField usernameField;
	private javax.swing.JLabel usernameLabel;
	private javax.swing.JCheckBox replaceTables;
	private JFrame frame;
	
	private javax.swing.JButton attrCancel;
    private javax.swing.JButton attrDump;
    private javax.swing.JButton attrToggle;
    private javax.swing.JLabel attrInfo;
    private javax.swing.JLabel linkAttrLabel;
    private javax.swing.JCheckBox linkColor;
    private javax.swing.JCheckBox linkDest;
    private javax.swing.JCheckBox linkHide;
    private javax.swing.JCheckBox linkLabel;
    private javax.swing.JCheckBox linkOrig;
    private javax.swing.JCheckBox linkVar1;
    private javax.swing.JCheckBox linkVar2;
    private javax.swing.JCheckBox linkWeight;
    private javax.swing.JCheckBox linkWidth;
    private javax.swing.JLabel nodeAttrLabel;
    private javax.swing.JCheckBox nodeBorderColor;
    private javax.swing.JCheckBox nodeColor;
    private javax.swing.JCheckBox nodeFixed;
    private javax.swing.JCheckBox nodeHide;
    private javax.swing.JCheckBox nodeId;
    private javax.swing.JCheckBox nodeLabel;
    private javax.swing.JCheckBox nodeShape;
    private javax.swing.JCheckBox nodeSize;
    private javax.swing.JCheckBox nodeVar1;
    private javax.swing.JCheckBox nodeVar2;
    private javax.swing.JCheckBox nodeWidth;
    private javax.swing.JCheckBox nodeX;
    private javax.swing.JCheckBox nodeY;
	private JFrame selectAttrs;
	
	private Map<String, String> dbTypeToDriverName;
	private Map<String, String> dbTypeToURL;
	private Connection conn;
	
	public DBDump(NetworkPanel networkPanel) {		
		this.networkPanel = networkPanel;
		
		dbTypeToDriverName = new HashMap<String, String>();		
		dbTypeToDriverName.put("MySQL", "com.mysql.jdbc.Driver");
		dbTypeToDriverName.put("PostgreSQL", "org.postgresql.Driver");
		dbTypeToDriverName.put("SQLite", "org.sqlite.JDBC");
		dbTypeToURL = new HashMap<String, String>();
		dbTypeToURL.put("MySQL", "jdbc:mysql://");
		dbTypeToURL.put("PostgreSQL", "jdbc:postgresql://");
		dbTypeToURL.put("SQLite", "jdbc:sqlite::/");
		
		frame = new JFrame();
		initComponents();
		
		selectAttrs = new JFrame();
		initAttrComponents();
		
		selectAttrs.setSize(410,447);
		selectAttrs.setResizable(false);
		selectAttrs.setVisible(false);
		
		frame.setSize(430, 370);
		frame.setResizable(false);		
		frame.setVisible(true);
	}
    

	private void initComponents() {
		info1 = new javax.swing.JLabel();
	    info2 = new javax.swing.JLabel();
	    urlLabel = new javax.swing.JLabel();
	    usernameLabel = new javax.swing.JLabel();
	    passwordLabel = new javax.swing.JLabel();
	    nLabel = new javax.swing.JLabel();
	    lLabel = new javax.swing.JLabel();
	    urlField = new javax.swing.JTextField();
	    nodeField = new javax.swing.JTextField();
	    usernameField = new javax.swing.JTextField();
	    linksField = new javax.swing.JTextField();
	    passwordField = new javax.swing.JPasswordField();
	    ok = new javax.swing.JToggleButton();
	    cancel = new javax.swing.JToggleButton();
	    dbTypeLabel = new javax.swing.JLabel();
	    dbType = new javax.swing.JComboBox();
	    replaceTables = new javax.swing.JCheckBox("Replace tables if they exist");

	    info1.setText("Enter the database connection information and the table");
	    info2.setText("names where the network will be dumped");
	    urlLabel.setText("Database URL");
	    usernameLabel.setText("Username");
	    passwordLabel.setText("Password");
	    nLabel.setText("Nodes table name");
	    lLabel.setText("Links table name");
	    urlField.setText("");
	    nodeField.setText("");
	    usernameField.setText("");
	    linksField.setText("");
	    passwordField.setText("");

	    ok.setText("OK");
	    ok.addActionListener(new java.awt.event.ActionListener() {
	        public void actionPerformed(java.awt.event.ActionEvent evt) {				
	        	frame.setVisible(false);
	        	try {
					Class.forName(dbTypeToDriverName.get(dbType.getSelectedItem())).newInstance();
					conn = DriverManager.getConnection(dbTypeToURL.get(dbType.getSelectedItem())+urlField.getText(), usernameField.getText(), passwordField.getText());
					selectAttrs.setVisible(true);
				} catch (InstantiationException e) {
					JOptionPane.showMessageDialog(networkPanel, "Could not connect to the database", "Could not connect", JOptionPane.ERROR_MESSAGE , null);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					JOptionPane.showMessageDialog(networkPanel, "Could not connect to the database", "Could not connect", JOptionPane.ERROR_MESSAGE , null);
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(networkPanel, "Could not connect to the database", "Could not connect", JOptionPane.ERROR_MESSAGE , null);
					e.printStackTrace();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(networkPanel, "Could not connect to the database", "Could not connect", JOptionPane.ERROR_MESSAGE , null);
					e.printStackTrace();
				}	        	
	        }
	    });

	    cancel.setText("Cancel");
	    cancel.addActionListener(new java.awt.event.ActionListener() {
	        public void actionPerformed(java.awt.event.ActionEvent evt) {
	        	frame.setVisible(false);
	        }
	    });

	    dbTypeLabel.setText("Database type");
	    dbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MySQL", "PostgreSQL", "SQLite" }));
		
	    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(frame.getContentPane());
	    frame.getContentPane().setLayout(layout);	    
	    
	    layout.setHorizontalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup()
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addGroup(layout.createSequentialGroup()
	                    .addContainerGap()
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                        .addComponent(info2)
	                        .addComponent(info1)
	                        .addGroup(layout.createSequentialGroup()
	                            .addComponent(usernameLabel)
	                            .addGap(73, 73, 73)
	                            .addComponent(usernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
	                        .addGroup(layout.createSequentialGroup()
	                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                .addComponent(passwordLabel)
	                                .addComponent(nLabel)
	                                .addComponent(lLabel))
	                            .addGap(23, 23, 23)
	                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                .addComponent(linksField, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
	                                .addComponent(nodeField, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
	                                .addComponent(replaceTables, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
	                                .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)))
	                        .addGroup(layout.createSequentialGroup()
	                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                .addComponent(urlLabel)
	                                .addComponent(dbTypeLabel))
	                            .addGap(46, 46, 46)
	                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                .addComponent(dbType, 0, 227, Short.MAX_VALUE)
	                                .addComponent(urlField, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)))))
	                .addGroup(layout.createSequentialGroup()
	                    .addGap(112, 112, 112)
	                    .addComponent(ok)
	                    .addGap(18, 18, 18)
	                    .addComponent(cancel)))
	            .addContainerGap())
	    );
	    layout.setVerticalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup()
	            .addGap(29, 29, 29)
	            .addComponent(info1)
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	            .addComponent(info2)
	            .addGap(18, 18, 18)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(dbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addComponent(dbTypeLabel))
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(urlLabel)
	                .addComponent(urlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(usernameLabel)
	                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(passwordLabel)
	                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(nLabel)
	                .addComponent(nodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(lLabel)
	                .addComponent(linksField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(replaceTables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(ok)
	                .addComponent(cancel))
	            .addContainerGap(22, Short.MAX_VALUE))
	    );
	}	
	
	private void initAttrComponents() {

        attrInfo = new javax.swing.JLabel();
        nodeAttrLabel = new javax.swing.JLabel();
        linkAttrLabel = new javax.swing.JLabel();
        nodeId = new javax.swing.JCheckBox();
        nodeLabel = new javax.swing.JCheckBox();
        nodeColor = new javax.swing.JCheckBox();
        nodeBorderColor = new javax.swing.JCheckBox();
        nodeSize = new javax.swing.JCheckBox();
        nodeShape = new javax.swing.JCheckBox();
        nodeWidth = new javax.swing.JCheckBox();
        nodeHide = new javax.swing.JCheckBox();
        nodeVar1 = new javax.swing.JCheckBox();
        nodeVar2 = new javax.swing.JCheckBox();
        nodeX = new javax.swing.JCheckBox();
        nodeY = new javax.swing.JCheckBox();
        nodeFixed = new javax.swing.JCheckBox();
        linkOrig = new javax.swing.JCheckBox();
        linkDest = new javax.swing.JCheckBox();
        linkWeight = new javax.swing.JCheckBox();
        linkLabel = new javax.swing.JCheckBox();
        linkWidth = new javax.swing.JCheckBox();
        linkColor = new javax.swing.JCheckBox();
        linkVar1 = new javax.swing.JCheckBox();
        linkVar2 = new javax.swing.JCheckBox();
        linkHide = new javax.swing.JCheckBox();
        attrToggle = new javax.swing.JButton();
        attrDump = new javax.swing.JButton();
        attrCancel = new javax.swing.JButton();

        attrInfo.setText("Select the attributes to dump to the database");
        nodeAttrLabel.setText("Node attributes");
        linkAttrLabel.setText("Link attributes");
        nodeId.setText("id");
        nodeId.setSelected(true);
        nodeId.setEnabled(false);
        nodeLabel.setText("label");
        nodeColor.setText("color");
        nodeBorderColor.setText("border color");
        nodeSize.setText("size");
        nodeShape.setText("shape");
        nodeWidth.setText("width");
        nodeHide.setText("hide");
        nodeVar1.setText("var1");
        nodeVar2.setText("var2");
        nodeX.setText("x");
        nodeY.setText("y");
        nodeFixed.setText("fixed");
        linkOrig.setText("id origiin");
        linkOrig.setSelected(true);
        linkOrig.setEnabled(false);
        linkDest.setText("id destination");
        linkDest.setSelected(true);
        linkDest.setEnabled(false);
        linkWeight.setText("weight");
        linkLabel.setText("label");
        linkWidth.setText("width");
        linkColor.setText("color");
        linkVar1.setText("var1");
        linkVar2.setText("var2");
        linkHide.setText("hide");

        attrDump.setText("Dump");
        attrDump.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	List<String> nodeAttrs = new LinkedList<String>();
            	List<String> linkAttrs = new LinkedList<String>();
            	if(nodeId.isSelected())
            		nodeAttrs.add("id");
            	if(nodeLabel.isSelected())
            		nodeAttrs.add("label");
            	if(nodeColor.isSelected())
            		nodeAttrs.add("color");
            	if(nodeBorderColor.isSelected())
            		nodeAttrs.add("borderColor");
            	if(nodeSize.isSelected())
            		nodeAttrs.add("size");
            	if(nodeShape.isSelected())
            		nodeAttrs.add("shape");
            	if(nodeWidth.isSelected())
            		nodeAttrs.add("width");
            	if(nodeHide.isSelected())
            		nodeAttrs.add("hide");
            	if(nodeVar1.isSelected())
            		nodeAttrs.add("var1");
            	if(nodeVar2.isSelected())
            		nodeAttrs.add("var2");
            	if(nodeX.isSelected())
            		nodeAttrs.add("x");
            	if(nodeY.isSelected())
            		nodeAttrs.add("y");
            	if(nodeFixed.isSelected())
            		nodeAttrs.add("fixed");
            	
            	if(linkOrig.isSelected())
            		linkAttrs.add("id_origin");
            	if(linkDest.isSelected())
            		linkAttrs.add("id_dest");
            	if(linkWeight.isSelected())
            		linkAttrs.add("weight");
            	if(linkLabel.isSelected())
            		linkAttrs.add("label");
            	if(linkWidth.isSelected())
            		linkAttrs.add("width");
            	if(linkColor.isSelected())
            		linkAttrs.add("color");
            	if(linkVar1.isSelected())
            		linkAttrs.add("var1");
            	if(linkVar2.isSelected())
            		linkAttrs.add("var2");
            	if(linkHide.isSelected())
            		linkAttrs.add("hide");            	
            
				(new DumpToDBWorker(networkPanel, (CxfNetwork)networkPanel.getNetwork(), networkPanel.getNetworkLayout(), nodeField.getText(), linksField.getText(), conn, nodeAttrs, linkAttrs, replaceTables.isSelected()) ).execute();							
            	selectAttrs.setVisible(false);
            }
        });

        attrCancel.setText("Cancel");
        attrCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	System.out.println(replaceTables.isSelected());
            	selectAttrs.setVisible(false);
            }
        });
        
        attrToggle.setText("Toggle All");
        attrToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	nodeLabel.setSelected(!nodeLabel.isSelected());
            	nodeColor.setSelected(!nodeColor.isSelected());
            	nodeBorderColor.setSelected(!nodeBorderColor.isSelected());
            	nodeSize.setSelected(!nodeSize.isSelected());
            	nodeShape.setSelected(!nodeShape.isSelected());
            	nodeWidth.setSelected(!nodeWidth.isSelected());
            	nodeHide.setSelected(!nodeHide.isSelected());
            	nodeVar1.setSelected(!nodeVar1.isSelected());
            	nodeVar2.setSelected(!nodeVar2.isSelected());
            	nodeX.setSelected(!nodeX.isSelected());
            	nodeY.setSelected(!nodeY.isSelected());
            	nodeFixed.setSelected(!nodeFixed.isSelected());

            	linkWeight.setSelected(!linkWeight.isSelected());
            	linkLabel.setSelected(!linkLabel.isSelected());
            	linkWidth.setSelected(!linkWidth.isSelected());
            	linkColor.setSelected(!linkColor.isSelected());
            	linkVar1.setSelected(!linkVar1.isSelected());
            	linkVar2.setSelected(!linkVar2.isSelected());
            	linkHide.setSelected(!linkHide.isSelected());
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(selectAttrs.getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(nodeFixed)
        				.addComponent(nodeY)
        				.addComponent(nodeX)
        				.addComponent(nodeVar2)
        				.addComponent(attrInfo)
        				.addGroup(layout.createSequentialGroup()
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addComponent(nodeAttrLabel)
        						.addComponent(nodeVar1)
        						.addComponent(nodeHide)
        						.addComponent(nodeWidth)
        						.addComponent(nodeShape)
        						.addComponent(nodeSize)
        						.addComponent(nodeBorderColor)
        						.addComponent(nodeColor)
        						.addComponent(nodeLabel)
        						.addComponent(nodeId)
        						.addGroup(layout.createSequentialGroup()
        							.addGap(44)
        							.addComponent(attrToggle)))
        					.addGap(18)
        					.addComponent(attrDump)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addComponent(linkDest)
        						.addComponent(attrCancel)
        						.addComponent(linkHide)
        						.addComponent(linkVar2)
        						.addComponent(linkVar1)
        						.addComponent(linkColor)
        						.addComponent(linkWidth)
        						.addComponent(linkLabel)
        						.addComponent(linkWeight)
        						.addComponent(linkOrig)
        						.addComponent(linkAttrLabel))))
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(attrInfo)
        			.addGap(18)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(nodeAttrLabel)
        				.addComponent(linkAttrLabel))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(nodeId)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeLabel)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeColor)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeBorderColor)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeSize)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeShape)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeWidth)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeHide)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeVar1)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeVar2)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeX)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeY)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(nodeFixed))
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(linkOrig)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(linkDest)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(linkWeight)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(linkLabel)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(linkWidth)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(linkColor)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(linkVar1)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(linkVar2)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(linkHide)))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(attrToggle)
        				.addComponent(attrDump)
        				.addComponent(attrCancel))
        			.addContainerGap())
        );
        selectAttrs.getContentPane().setLayout(layout);
    }
}
