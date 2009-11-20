/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

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

package ch.ethz.sg.cuttlefish.gui.widgets;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.ethz.sg.cuttlefish.gui.BrowserWidget;
import ch.ethz.sg.cuttlefish.misc.Utils;
import ch.ethz.sg.cuttlefish.misc.XMLUtil;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;

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
					String className = source.getAttributes().getNamedItem("class").getNodeValue();

					Class<?> clazz;
					
						try {
							clazz = Class.forName(className);
							BrowsableNetwork network = (BrowsableNetwork) clazz.newInstance();
						  
							network.setArguments(XMLUtil.getArguments(source));
							getBrowser().setNetwork(network);
					
						} catch (ClassNotFoundException cnfEx) {
							JOptionPane.showMessageDialog(null,cnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
							System.err.println("Nonexisting class in data source");
							cnfEx.printStackTrace();
							getBrowser().setNetwork(new BrowsableNetwork());
						} catch (InstantiationException instEx) {
							JOptionPane.showMessageDialog(null,instEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
							System.err.println("Impossible to instantiante class in data source");
							instEx.printStackTrace();
							getBrowser().setNetwork(new BrowsableNetwork());
						} catch (IllegalAccessException iaEx) {
							JOptionPane.showMessageDialog(null,iaEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
							System.err.println("Ilegal access in data source");
							iaEx.printStackTrace();
							getBrowser().setNetwork(new BrowsableNetwork());
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
			Schema schema;
			DocumentBuilderFactory factory =
	        DocumentBuilderFactory.newInstance();
	        factory.setValidating(false);  
	        factory.setNamespaceAware(true);
	        
	    	InputStream schemaStream = this.getClass().getResourceAsStream("/ch/ethz/sg/cuttlefish/resources/datasources.xsd");
	   		File schemaFile = new File("sources_aux.xsd");
	   		OutputStream auxStream;
			auxStream = new FileOutputStream(schemaFile);
			byte buf[]=new byte[1024];
	        int len;
	        while((len=schemaStream.read(buf))>0)
	        	auxStream.write(buf,0,len);
	   		auxStream.close();
	   		schemaStream.close();
	   		schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaFile);
	   		factory.setSchema(schema);
	   		schemaFile.deleteOnExit();
	   		
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        
	        System.out.println("loading sources from " + getArgument("sources"));
	        File sourcesFile = new File(getArgument("sources"));
	        if (!sourcesFile.exists())
	        {
				 
	        	sourcesFile = Utils.createLocalFile("/ch/ethz/sg/cuttlefish/resources/default_datasources.xml", (Object) this);
	        	System.out.println("WARNING: datasources file not found - using default");
	        }
	        sourcesDocument = builder.parse(sourcesFile);
	          
			jComboBox.removeAllItems();
			
			this.sources = sourcesDocument.getElementsByTagName("Source");
			for(int i = 0; i < sources.getLength(); i++){
				Node source = sources.item(i);
				jComboBox.addItem(source.getAttributes().getNamedItem("name").getNodeValue());
			}
		} 
	/*	catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(null,fnfEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Schema file for data sources not found");
			fnfEx.printStackTrace();
		}*/ 
		catch (IOException ioEx) {
			JOptionPane.showMessageDialog(null,ioEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Input exception in data source schema file");
			ioEx.printStackTrace();
		} 
		catch (SAXException saxEx) {
			JOptionPane.showMessageDialog(null,saxEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Error validating data sources XML");
			saxEx.printStackTrace();
		} 
		catch (ParserConfigurationException pcEx) {
			JOptionPane.showMessageDialog(null,pcEx.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			System.err.println("Parse error in data sources XML");
			pcEx.printStackTrace();
		}  
	}
}
