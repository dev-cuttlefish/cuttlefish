/*
 * Created on May 11, 2004
 */
package test.edu.uci.ics.jung.graph.impl;

import java.util.LinkedList;



/**
 * Dummy class just provides methods that force, force I say garbage
 * collection. Which is tough with java.
 * from http://www.javaworld.com/javatips/jw-javatip130_p.html
 * @author danyelf
 */
public class ForceGC //extends TestCase {
{

    /**
     *  
     */
    public static void gc() throws Exception {
        // It helps to call Runtime.gc()
        // using several method calls:
        for (int r = 0; r < 4; ++r)
            _runGC();
    }

    private static void _runGC() throws Exception {
        long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
        for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
            s_runtime.runFinalization();
            s_runtime.gc();
            Thread.yield();

            usedMem2 = usedMem1;
            usedMem1 = usedMemory();
        }
    }

    private static long usedMemory() {
        return s_runtime.totalMemory() - s_runtime.freeMemory();
    }

    private static final Runtime s_runtime = Runtime.getRuntime(); // TODO

    public static void main (String [] args) throws Exception
    {
        // Warm up all classes/methods we will use
        gc();
        usedMemory ();

        // Array to keep strong references to allocated objects
        final int count = 100000;
        Object [] objects = new Object [count];
        
        long heap1 = 0;

        // Allocate count+1 objects, discard the first one
        for (int i = -1; i < count; ++ i)
        {
            Object object = null;
            
            // Instantiate your data here and assign it to object
            
//            object = new Object ();
            //object = new Integer (i);
            //object = new Long (i);
            //object = new String ();
            //object = new byte [128][1]
            object = new LinkedList();
            
            
            for (int j = 0; j < 10; j++)
            {
                ((LinkedList)object).add(new Object());
            }
            
            if (i >= 0)
                objects [i] = object;
            else
            {
                object = null; // Discard the warm up object
                gc();
                heap1 = usedMemory (); // Take a before heap snapshot
            }
        }

        gc();
        long heap2 = usedMemory (); // Take an after heap snapshot:
        
        final int size = Math.round (((float)(heap2 - heap1))/count);
        System.out.println ("'before' heap: " + heap1 +
                            ", 'after' heap: " + heap2);
        System.out.println ("heap delta: " + (heap2 - heap1) +
            ", {" + objects [0].getClass () + "} size = " + size + " bytes");

        for (int i = 0; i < count; ++ i) objects [i] = null;
        objects = null;
    }

}
