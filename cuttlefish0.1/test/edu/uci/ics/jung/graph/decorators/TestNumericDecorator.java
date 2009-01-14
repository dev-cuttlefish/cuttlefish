/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package test.edu.uci.ics.jung.graph.decorators;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.NumericDecorator;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.utils.MutableDouble;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author Scott White
 */
public class TestNumericDecorator extends TestCase {
	public static Test suite() {
		return new TestSuite(TestNumericDecorator.class);
	}

	protected void setUp() {
	}

    public void test() {
        Vertex v = new SparseVertex();
        NumericDecorator decorator = new NumericDecorator("test",UserData.REMOVE);
        decorator.setValue(new MutableDouble(0.5),v);
        Assert.assertEquals(0.5,decorator.getValue(v).doubleValue(),.01);

        decorator.removeValue(v);
        Assert.assertNull(decorator.getValue(v));

    }

}
