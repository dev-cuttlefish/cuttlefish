/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
/*
 * Created on Jul 26, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.visualization;

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.Layout;

/**
 * @author danyelf
 */
public class TestFRLayout extends LayoutTester {

	public static Test suite() {
		TestSuite suite = new TestSuite(TestFRLayout.class.getName());
		suite.addTestSuite( TestFRLayout.class );
		return suite;
	}

	protected int getIters() {
		return 30;
	}

	protected Layout getLayout(Graph g) {
		return new FRLayout( g );
	}


}
