/*
 * Copyright (c) 2004, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Jul 21, 2004
 */
package test.edu.uci.ics.jung.utils;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

import junit.framework.TestCase;
import samples.preview_new_graphdraw.CoordinateUtil;
import samples.preview_new_graphdraw.Coordinates;

/**
 * 
 *  
 * @author Joshua O'Madadhain
 */
public class CoordinateUtilsTest extends TestCase
{
    protected Coordinates s1;
    protected Coordinates s2;
    protected Coordinates d1;
    protected Coordinates d2;
    protected Rectangle2D bounds;
    
    protected void setUp() throws Exception
    {
        s1 = new Coordinates(0,0);
        s2 = new Coordinates(10,20);
        d1 = new Coordinates(10,10);
        d2 = new Coordinates(20,30);
        bounds = new Rectangle2D.Double(15,25,10,10);
    }

    public void testGetIntersections()
    {
        Collection intersections;
        
        Coordinates i1_1 = new Coordinates(25, 25); // the intersection wrt s1,d1
        intersections = CoordinateUtil.getIntersections(s1,d1,bounds);
        assertEquals(intersections.size(), 1);
        assertEquals(intersections.iterator().next(), i1_1);
        
        Coordinates i1_2a = new Coordinates(50.0/3, 25); // an intersection wrt s1,d2
        Coordinates i1_2b = new Coordinates(70.0/3, 35); // an intersection wrt s1,d2
        intersections = CoordinateUtil.getIntersections(s1,d2,bounds);
        assertEquals(intersections.size(), 2);
        assertTrue(intersections.contains(i1_2a));
        assertTrue(intersections.contains(i1_2b));
        
        intersections = CoordinateUtil.getIntersections(s2,d1,bounds);
        assertEquals(intersections.size(), 0);
        
        Coordinates i2_2a = new Coordinates(15,25);
        Coordinates i2_2b = new Coordinates(15,25);
        intersections = CoordinateUtil.getIntersections(s2,d2,bounds);
        assertEquals(intersections.size(), 2);
        assertTrue(intersections.contains(i2_2a));
        assertTrue(intersections.contains(i2_2b));
    }
    
}
