/*
 * Created on Aug 11, 2004
 *
 */
package test.edu.uci.ics.jung.algorithms.importance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import junit.framework.TestCase;
import edu.uci.ics.jung.algorithms.importance.VoltageRanker;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.NumberVertexValue;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.TypedVertexGenerator;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.utils.VertexGenerator;

/**
 * 
 *  
 * @author Joshua O'Madadhain
 */
public class VoltageRankerTest extends TestCase
{
    protected Graph g;
    protected Vertex[] v;
    
    public void setUp()
    {
        g = new SparseGraph();
        VertexGenerator vg = new TypedVertexGenerator(g);
        v = new Vertex[7];
        for (int i = 0; i < v.length; i++)
            v[i] = g.addVertex(vg.create());
        
        g.addEdge(new UndirectedSparseEdge(v[0], v[1]));
        g.addEdge(new UndirectedSparseEdge(v[0], v[2]));
        g.addEdge(new UndirectedSparseEdge(v[1], v[3]));
        g.addEdge(new UndirectedSparseEdge(v[2], v[3]));
        g.addEdge(new UndirectedSparseEdge(v[3], v[4]));
        g.addEdge(new UndirectedSparseEdge(v[3], v[5]));
        g.addEdge(new UndirectedSparseEdge(v[4], v[6]));
        g.addEdge(new UndirectedSparseEdge(v[5], v[6]));
    }
    
    public final void testCalculateVoltagesSourceTarget()
    {
        VertexVoltages vv = new VertexVoltages();
        VoltageRanker vr = new VoltageRanker(vv, 100, 0.001);
        double[] voltages = {1.0, 0.75, 0.75, 0.5, 0.25, 0.25, 0};
        
        vr.calculateVoltages(v[0], v[6]);
        for (int i = 0; i < v.length; i++)
            assertEquals(vv.getNumber(v[i]).doubleValue(), voltages[i], 0.01);
    }
    
    public final void testCalculateVoltagesSourcesTargets()
    {
        VertexVoltages vv = new VertexVoltages();
        VoltageRanker vr = new VoltageRanker(vv, 100, 0.001);
        double[] voltages = {1.0, 0.5, 0.66, 0.33, 0.16, 0, 0};
        
        Map sources = new HashMap();
        sources.put(v[0], new Double(1.0));
        sources.put(v[1], new Double(0.5));
        Set sinks = new HashSet();
        sinks.add(v[6]);
        sinks.add(v[5]);
        
        vr.calculateVoltages(g, sources, sinks);
        for (int i = 0; i < v.length; i++)
            assertEquals(vv.getNumber(v[i]).doubleValue(), voltages[i], 0.01);
    }
    
    protected static class VertexVoltages implements NumberVertexValue
    {
        protected final static String VOLTAGE_KEY = "VoltageRankerTest.KEY";

        public Number getNumber(ArchetypeVertex v)
        {
            return (Number)v.getUserDatum(VOLTAGE_KEY);
        }

        public void setNumber(ArchetypeVertex v, Number n)
        {
            v.setUserDatum(VOLTAGE_KEY, n, UserData.REMOVE);
        }
    }
}
