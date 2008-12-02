/*
 * Copyright (c) 2004, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Aug 10, 2004
 */
package test.edu.uci.ics.jung.algorithms.cluster;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.uci.ics.jung.algorithms.cluster.KMeansClusterer;
import edu.uci.ics.jung.algorithms.cluster.KMeansClusterer.NotEnoughClustersException;
import edu.uci.ics.jung.statistics.DiscreteDistribution;

import junit.framework.TestCase;

/**
 * 
 *  
 * @author Joshua O'Madadhain
 */
public class KMeansTest extends TestCase
{
    protected KMeansClusterer kmc;
    protected Map object_locations;
    public void setUp()
    {
        Object[] objects = new Object[5];
        for (int i = 0; i < objects.length; i++)
            objects[i] = new Object();
        double[][] locations = {{0}, {1}, {2}, {9}, {10}};
        object_locations = new HashMap();
        for (int i = 0; i < objects.length; i++)
            object_locations.put(objects[i], locations[i]);
        kmc = new KMeansClusterer(100, .01);
    }
    
    public void testCluster() 
    {
        Collection clusters = null;
        try
        {
            clusters = kmc.cluster(object_locations, 2);
        }
        catch (NotEnoughClustersException nece) 
        { 
            fail("unexpected failure: NotEnoughClustersException thrown");
        }
        
        try
        {
            clusters = kmc.cluster(object_locations, object_locations.size() + 1);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException iae) { }
        catch (NotEnoughClustersException nece) 
        {
            fail("unexpected failure: NotEnoughClustersException thrown");
        }

        // try to make 3 clusters out of data that doesn't have enough unique points
        try
        {
            Object[] objects = new Object[5];
            for (int i = 0; i < objects.length; i++)
                objects[i] = new Object();
            double[][] locations = {{0}, {0}, {0}, {0}, {10}};
            Map bad_locations = new HashMap();
            for (int i = 0; i < objects.length; i++)
                bad_locations.put(objects[i], locations[i]);
            kmc.cluster(bad_locations, 3);
            fail("should have thrown IllegalArgumentException");
        }
        catch (NotEnoughClustersException nece) {}

        
        assertEquals(clusters.size(), 2);
        
        Iterator iter = clusters.iterator();
        Map cluster1 = (Map)iter.next();
        Map cluster2 = (Map)iter.next();
        double[] centroid1 = DiscreteDistribution.mean(cluster1.values());
        double[] centroid2 = DiscreteDistribution.mean(cluster2.values());
        
        if (((cluster1.size() == 2) && (cluster2.size() == 3)))
        {
            assertEquals(centroid1[0], 9.5, 0.01);
            assertEquals(centroid2[0], 1.0, 0.01);
        }
        else if (((cluster1.size() == 3) && (cluster2.size() == 2)))
        {
            assertEquals(centroid1[0], 1.0, 0.01);
            assertEquals(centroid2[0], 9.5, 0.01);
        }
        else
            fail("unexpected cluster sizes");
    }
}
