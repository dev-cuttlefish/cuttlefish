/*
    
    Copyright (C) 2008  Markus Michael Geipel

    This program is free software: you can redistribute it and/or modify
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

package ch.ethz.sg.cuttlefish.gui.widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.misc.XMLUtil;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
//import ch.ethz.sg.cuttlefish.networks.IDatabaseSource;

public class DataSourcePanel extends BrowserWidget {

	private static final long serialVersionUID = 1L;
	private JButton jButton = null;
	private JComboBox jComboBox = null;
	private NodeList sources;  //  @jve:decl-index=0:
	private Document sourcesDocument;

	/**
	 * This is the default constructor
	 */
	public DataSourcePanel() {
		super();
		initialize();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints1.gridx = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 2;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getJButton(), gridBagConstraints);
		this.add(getJComboBox(), gridBagConstraints1);
	}

	@Override
	public void onActiveChanged() {
	}



	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("go");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					Node source = sources.item(jComboBox.getSelectedIndex());
					boolean isDatabaseSource = true;
					
					String className = source.getAttributes().getNamedItem("class").getNodeValue();
					
					String database = null;
					String host = null;
					String login = null;
					String password = null;
					
					try {
						database = source.getAttributes().getNamedItem("database").getNodeValue();
						host = source.getAttributes().getNamedItem("host").getNodeValue();
						login = source.getAttributes().getNamedItem("login").getNodeValue();
						password = source.getAttributes().getNamedItem("password").getNodeValue();
						
					} catch (Exception e) {
						isDatabaseSource = false;
					}

					Class<?> clazz;
					
					try {
						clazz = Class.forName(className);
						BrowsableNetwork network = (BrowsableNetwork) clazz.newInstance();
					/*	if(isDatabaseSource){
							((IDatabaseSource)network).loadFromDB(host, login, password, database);
						}*/
						
						network.setArguments(XMLUtil.getArguments(source));
						//network.init();
						getBrowser().setNetwork(network);
						
					} catch (Exception e1) {

						e1.printStackTrace();
						getBrowser().setNetwork(new BrowsableNetwork());
						return;
					}
					

					
				}

				
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
		}
		return jComboBox;
	}








	@Override		
	public void init() {
		try {  
			DocumentBuilderFactory factory =
	        DocumentBuilderFactory.newInstance();
	        factory.setValidating(false);  
	        factory.setNamespaceAware(true);
	       File schemaFile = new File(this.getClass().getResource("/ch/ethz/sg/cuttlefish/resources/datasources.xsd").getFile());
	        Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaFile);
	       factory.setSchema(schema);
	        
	        
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        
	        System.out.println("loading sources from " + getArgument("sources"));
	        File sourcesFile = new File(getArgument("sources"));
	        
	        sourcesDocument = builder.parse(sourcesFile);
	          
			jComboBox.removeAllItems();
			
			this.sources = sourcesDocument.getElementsByTagName("Source");
			for(int i = 0; i < sources.getLength(); i++){
				Node source = sources.item(i);
				jComboBox.addItem(source.getAttributes().getNamedItem("name").getNodeValue());
			}
           
	          
	        } catch (Exception e) {
	           e.printStackTrace();
	         
	        }
		
	}
}
