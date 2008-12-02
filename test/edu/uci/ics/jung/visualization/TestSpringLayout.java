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
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.SpringLayout;

/**
 * @author danyelf
 */
public class TestSpringLayout extends LayoutTester {

	/* (non-Javadoc)
	 * @see test.edu.uci.ics.jung.visualization.LayoutTester#getIters()
	 */
	protected int getIters() {
		return 7;
	}
	public static Test suite() {
		TestSuite suite = new TestSuite( TestSpringLayout.class.getName());
		suite.addTestSuite(TestSpringLayout.class);
		return suite;
	}

	public void testFilter() {
		super.testFilter();
	}

	public void testLayoutIterationBigSpace() {
		super.testLayoutIterationBigSpace();
	}

	public void testLayoutIterationSmallSpace() {
		super.testLayoutIterationSmallSpace();
	}

	public void testLayoutCreation() {
		super.testLayoutCreation();
	}

	public void testResize() {
		super.testResize();
	}

	public void testRestart() {
		super.testRestart();
	}
	protected Layout getLayout(Graph g) {
		return new SpringLayout(g);
	}

}
