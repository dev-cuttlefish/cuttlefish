/*
  
    Copyright (C) 2011  Markus Michael Geipel, David Garcia Becerra,
    Petar Tsankov

	This file is part of Cuttlefish.
	
 	Cuttlefish is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
*/

package ch.ethz.sg.cuttlefish.gui2.toolbars;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.networks.DBNetwork;

public class DBExploreNetwork extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Map<String, String> vertexFields;
	private static Map<String, String> edgeFields;
	private static String[] operators = {"=", "<", ">", "contains"};	
	private NetworkPanel networkPanel;
	private JFrame exploreNetwork;
	private String vertexSQLFilter = "";
	private String edgeSQLFilter = "";
	private Map<String, String> operatorSQLMap;
	private Map<String, String> operatorQuotes;
    private javax.swing.JButton addEdgeFilter;
    private javax.swing.JButton addVertexFilter;
    private javax.swing.JButton cancel;
    private javax.swing.JButton clearEdgeFilters;
    private javax.swing.JButton clearVertexFilters;
    private javax.swing.JTextField edgeFilter;
    private javax.swing.JScrollPane edgeFilters;
    private javax.swing.JLabel edgeLabel;
    private javax.swing.JComboBox edgeOperator;
    private javax.swing.JComboBox edgeProperties;
    private javax.swing.JLabel vertexInfo;
    private javax.swing.JLabel edgeInfo;
    private javax.swing.JLabel warningInfo;
    private javax.swing.JTextArea vertexFilterList;
    private javax.swing.JTextArea edgeFilterList;
    private javax.swing.JScrollPane nodeFilters;
    private javax.swing.JButton ok;
    private javax.swing.JTextField vertexFilter;
    private javax.swing.JLabel vertexLabel;
    private javax.swing.JLabel info;
    private javax.swing.JComboBox vertexOperator;
    private javax.swing.JComboBox vertexProperties;

	public DBExploreNetwork(NetworkPanel networkPanel) {
		this.setLocationRelativeTo(networkPanel);
		this.setSize(534,390);
		this.networkPanel = networkPanel;
		exploreNetwork = this;
		this.setTitle("Filter network");		
		initialize();
		countSelected();
	}
	
	private DBNetwork getDBNetwork() {
		return (DBNetwork)networkPanel.getNetwork();
	}

	private void countSelected() {
		getDBNetwork().setNodeFilter(vertexSQLFilter);
		getDBNetwork().setEdgeFilter(edgeSQLFilter);
		(new SelectedCounter(getDBNetwork(), vertexInfo, edgeInfo, warningInfo)).execute();
	}
	
	public void update() {
		countSelected();
	}
	
	private void initialize() {		
		vertexFields = new HashMap<String, String>();
		vertexFields.put("id", "id");
		vertexFields.put("label", "label");
		vertexFields.put("size", "size");
		vertexFields.put("var1", "var1");
		vertexFields.put("var2", "var2");
		edgeFields = new HashMap<String, String>();
		edgeFields.put("origin", "id_origin");
		edgeFields.put("destination", "id_dest");
		edgeFields.put("weight", "weight");
		edgeFields.put("label", "label");
		edgeFields.put("var1", "var1");
		edgeFields.put("var2", "var2");
		
		operatorSQLMap = new HashMap<String, String>();
		operatorSQLMap.put("=", "=");
		operatorSQLMap.put("<", "<");
		operatorSQLMap.put(">", ">");
		operatorSQLMap.put("contains", "like");
		operatorQuotes = new HashMap<String, String>();
		operatorQuotes.put("=", "\"");
		operatorQuotes.put("contains", "\"");
		operatorQuotes.put("<", "");
		operatorQuotes.put(">", "");
		
		initComponents();
		
		addVertexFilter.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String updatedList;
				if(vertexFilterList.getText().length() > 0)
					updatedList = vertexFilterList.getText() + ", ";
				else
					updatedList = "";
				vertexFilterList.setText( updatedList + vertexProperties.getSelectedItem() + ' ' + 
						vertexOperator.getSelectedItem() + ' ' + vertexFilter.getText() );
				String filter = vertexFilter.getText();
				if(vertexOperator.getSelectedItem().toString() == "contains")
					filter = '%' + filter + '%';
				String newFilter = vertexFields.get(vertexProperties.getSelectedItem().toString()).toString() + ' ' + 
				operatorSQLMap.get(vertexOperator.getSelectedItem().toString()) + ' ' + operatorQuotes.get(vertexOperator.getSelectedItem().toString()) + filter + operatorQuotes.get(vertexOperator.getSelectedItem().toString());
				if(vertexSQLFilter.length() > 0)
					vertexSQLFilter += " AND " + newFilter;
				else
					vertexSQLFilter += newFilter;
				getDBNetwork().setNodeFilter(vertexSQLFilter);
				countSelected();
			}
		});
		
		addEdgeFilter.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String updatedList;
				if(edgeFilterList.getText().length() > 0)
					updatedList = edgeFilterList.getText() + ", ";
				else
					updatedList = "";
				edgeFilterList.setText( updatedList + edgeProperties.getSelectedItem() + ' ' + 
						edgeOperator.getSelectedItem() + ' ' + edgeFilter.getText() );
				String filter = edgeFilter.getText();
				if(edgeOperator.getSelectedItem().toString() == "contains")
					filter = '%' + filter + '%';
				String newFilter = edgeFields.get(edgeProperties.getSelectedItem().toString()).toString() + ' ' + 
				operatorSQLMap.get(edgeOperator.getSelectedItem().toString()) + ' ' + operatorQuotes.get(edgeOperator.getSelectedItem().toString()) + filter + operatorQuotes.get(edgeOperator.getSelectedItem().toString());
				if(edgeSQLFilter.length() > 0)
					edgeSQLFilter += " AND " + newFilter;
				else
					edgeSQLFilter += newFilter;
				getDBNetwork().setEdgeFilter(edgeSQLFilter);
				countSelected();
			}
		});
		
		clearVertexFilters.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				vertexSQLFilter = "";
				vertexFilterList.setText("");
				getDBNetwork().setNodeFilter("");
				countSelected();
			}
		});
		
		clearEdgeFilters.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				edgeSQLFilter = "";
				edgeFilterList.setText("");
				getDBNetwork().setEdgeFilter("");
				countSelected();
			}
		});
		
		ok.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				getDBNetwork().emptyNetwork();
				getDBNetwork().setNodeFilter("");
				getDBNetwork().setEdgeFilter("");
				if(vertexSQLFilter.length() > 0)
					getDBNetwork().setNodeFilter(vertexSQLFilter);
				if(edgeSQLFilter.length() > 0)
					getDBNetwork().setEdgeFilter(edgeSQLFilter);
				String nodeSQLQuery = "SELECT * FROM " + getDBNetwork().getNodeTable();
				getDBNetwork().nodeQuery(nodeSQLQuery);
				networkPanel.onNetworkChange();
				networkPanel.getNetworkLayout().reset();
				networkPanel.repaintViewer();
				networkPanel.stopLayout();
				getDBNetwork().setNodeFilter("");
				getDBNetwork().setEdgeFilter("");
				exploreNetwork.setVisible(false);
			}
		});
		
		cancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				exploreNetwork.setVisible(false);
			}
		});
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}
	
	/*
	 * Netbeans generated code for laying out the components
	 */
    private void initComponents() {

        vertexProperties = new javax.swing.JComboBox();
        ok = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        vertexInfo = new javax.swing.JLabel();
        vertexOperator = new javax.swing.JComboBox();
        edgeOperator = new javax.swing.JComboBox();
        edgeProperties = new javax.swing.JComboBox();
        addVertexFilter = new javax.swing.JButton();
        addEdgeFilter = new javax.swing.JButton();
        nodeFilters = new javax.swing.JScrollPane();
        vertexFilterList = new javax.swing.JTextArea();
        edgeFilters = new javax.swing.JScrollPane();
        edgeFilterList = new javax.swing.JTextArea();
        vertexFilter = new javax.swing.JTextField();
        edgeFilter = new javax.swing.JTextField();
        vertexLabel = new javax.swing.JLabel();
        edgeLabel = new javax.swing.JLabel();
        edgeInfo = new javax.swing.JLabel();
        warningInfo = new javax.swing.JLabel();
        info = new javax.swing.JLabel();
        clearVertexFilters = new javax.swing.JButton();
        clearEdgeFilters = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form");

        vertexProperties.setModel(new javax.swing.DefaultComboBoxModel(vertexFields.keySet().toArray()));
        vertexProperties.setName("vertexProperties");

        ok.setText("OK");
        ok.setName("ok");

        cancel.setText("Cancel");
        cancel.setName("cancel");

        vertexInfo.setText("Selected nodes: ");
        vertexInfo.setName("info");

        vertexOperator.setModel(new javax.swing.DefaultComboBoxModel(operators));
        vertexOperator.setName("vertexOperator");

        edgeOperator.setModel(new javax.swing.DefaultComboBoxModel(operators));
        edgeOperator.setName("edgeOperator");

        edgeProperties.setModel(new javax.swing.DefaultComboBoxModel(edgeFields.keySet().toArray()));
        edgeProperties.setName("edgeProperties");

        addVertexFilter.setText("Add");
        addVertexFilter.setName("addVertexFilter");

        addEdgeFilter.setText("Add");
        addEdgeFilter.setName("addEdgeFilter");

        nodeFilters.setName("nodeFilters");
        vertexFilterList.setColumns(20);
        vertexFilterList.setEditable(false);
        vertexFilterList.setRows(2);
        vertexFilterList.setText("");
        vertexFilterList.setName("vertexFilterList");
        nodeFilters.setViewportView(vertexFilterList);

        edgeFilters.setName("edgeFilters");
        edgeFilterList.setColumns(20);
        edgeFilterList.setEditable(false);
        edgeFilterList.setRows(2);
        edgeFilterList.setText("");
        edgeFilterList.setName("edgeFilterList");
        edgeFilters.setViewportView(edgeFilterList);

        vertexFilter.setText("");
        vertexFilter.setName("vertexFilter");
        
        info.setText("The resulting network contains only nodes and links matching the filters below");

        edgeFilter.setText("");
        edgeFilter.setName("edgeFilter");

        vertexLabel.setText("Filter nodes");
        vertexLabel.setName("vertexLabel");

        edgeLabel.setText("Filter links");
        edgeLabel.setName("edgeLabel");

        edgeInfo.setText("Selected links: ");
        edgeInfo.setName("info1");

        warningInfo.setForeground(Color.RED);
        warningInfo.setText("");
        warningInfo.setName("info2");

        clearVertexFilters.setText("Clear");
        clearVertexFilters.setName("clearVertexFilters");

        clearEdgeFilters.setText("Clear");
        clearEdgeFilters.setName("clearEdgeFilters");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()                    	
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vertexInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(edgeInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(edgeLabel)
                            .addComponent(warningInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(edgeFilters, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(edgeProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(edgeOperator, 0, 79, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(edgeFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(addEdgeFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(clearEdgeFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(nodeFilters, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(vertexProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(vertexLabel)
                                    )
                                .addGap(18, 18, 18)
                                .addComponent(vertexOperator, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(vertexFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addVertexFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(clearVertexFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(29, 29, 29))
            .addGroup(layout.createSequentialGroup()
                .addGap(183, 183, 183)
                .addComponent(ok)
                .addGap(18, 18, 18)
                .addComponent(cancel)
                .addContainerGap(191, Short.MAX_VALUE))
             .addGroup(layout.createSequentialGroup()
              .addGap(18, 18, 18)
             .addComponent(info))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(info)
                .addGap(18, 18, 18)
                .addComponent(vertexLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addVertexFilter)
                    .addComponent(vertexFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vertexProperties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vertexOperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nodeFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(edgeLabel))
                    .addComponent(clearVertexFilters))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edgeProperties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addEdgeFilter)
                    .addComponent(edgeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edgeOperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(edgeFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearEdgeFilters))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vertexInfo)
                    .addComponent(edgeInfo))
                .addGap(18, 18, 18)
                .addComponent(warningInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ok)
                    .addComponent(cancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>
    
    class SelectedCounter extends SwingWorker<Object, Object>{
    	private JLabel nodesInfo;
    	private JLabel edgesInfo;
    	private DBNetwork dbNetwork;
    	private JLabel warningInfo;
    	SelectedCounter(DBNetwork dbNetwork, JLabel nodesInfo, JLabel edgesInfo, JLabel warningInfo) {
    		this.nodesInfo = nodesInfo;
    		this.edgesInfo = edgesInfo;
    		this.dbNetwork = dbNetwork;
    		this.warningInfo = warningInfo;
    	}
		@Override
		protected Object doInBackground() throws Exception {
			nodesInfo.setText("Selected nodes: updating");
			Set<Integer> selectedNodes = dbNetwork.getSelectedNodes();
			nodesInfo.setText("Selected nodes: " + selectedNodes.size());
			if(selectedNodes.size() > 500)
				warningInfo.setText("Warning: Selecting more than 500 nodes may take a long time to visualize");
			else
				warningInfo.setText("");
			edgesInfo.setText("Selected links: updating");
			int numEdges = dbNetwork.countEdges(selectedNodes, selectedNodes);
			edgesInfo.setText("Selected links: " + numEdges);
			return null;
		}
    }

}
