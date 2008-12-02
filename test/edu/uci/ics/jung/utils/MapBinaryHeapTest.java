/*
 * 
 * Created on Oct 29, 2003
 */
package test.edu.uci.ics.jung.utils;

import junit.framework.TestCase;
import edu.uci.ics.jung.utils.MapBinaryHeap;

/**
 * 
 * @author Joshua O'Madadhain
 */
public class MapBinaryHeapTest extends TestCase
{
    private final static int NUM_OBJECTS = 10;
    private static MapBinaryHeap mbh;

    private final static int DECREASE = 0;
    private final static int INCREASE = 1;
    private final static int RANDOM = 2;
    
    protected void setUp()
    {
        mbh = new MapBinaryHeap();
    }
    
    protected void tearDown()
    {
    	mbh.clear();
    }
    
    public void testInsertAndRemove()
    {
        // insert in order
//        System.out.println("in order");
		for (int i = 0; i < NUM_OBJECTS; i++)
            mbh.add(new Integer(i));        
        assertEquals(mbh.size(), NUM_OBJECTS);
        checkOrder();
        
        // insert in reverse order
//        System.out.println("reverse order");
        for (int i = 0; i < NUM_OBJECTS; i++)
            mbh.add(new Integer(NUM_OBJECTS - i));        
        assertEquals(mbh.size(), NUM_OBJECTS);
        checkOrder();
        
        // insert in random order (probably duplicates)
//        System.out.println("random order");
        for (int i = 0; i < NUM_OBJECTS; i++)
            mbh.add(new Integer((int)(Math.random() * NUM_OBJECTS)));        
        assertEquals(mbh.size(), NUM_OBJECTS);
        checkOrder();
    }

    public void testUpdate()
    {
        updateTestHelper(DECREASE);
        updateTestHelper(INCREASE);
        updateTestHelper(RANDOM);
    }
    
    private void updateTestHelper(int mode)
    {
        Int[] mi;
        String mode_str;
        
        switch(mode)
        {
        	case DECREASE:
                mode_str = "Decrease: "; break;
            case INCREASE:
                mode_str = "Increase: "; break;
            case RANDOM: 
                mode_str = "Random: "; break;
            default:
                throw new IllegalArgumentException("bad mode");
        }
//        System.out.println(mode_str);
        
//        System.out.println("reverse");
        mi = new Int[NUM_OBJECTS];
        for (int i = 0; i < mi.length; i++)
        {
            mi[i] = new Int(i+NUM_OBJECTS);
            mbh.add(mi[i]);
        }
        for (int i = mi.length-1; i >= 0; i--)
        {
//            System.out.println("\noriginal value: " + mi[i]);
            mi[i].intval += getValueShift(mode);
//            System.out.println("new value: " + mi[i]);
            mbh.update(mi[i]);
            
        }
        assertEquals(mbh.size(), NUM_OBJECTS);
        checkOrder();

//        System.out.println("\n\nforward");
        mi = new Int[NUM_OBJECTS];
        for (int i = 0; i < mi.length; i++)
        {
            mi[i] = new Int(i+NUM_OBJECTS);
            mbh.add(mi[i]);
        }
        for (int i = 0; i < mi.length; i++)
        {
//            System.out.println("\noriginal value: " + mi[i]);
            mi[i].intval += getValueShift(mode);
//            System.out.println("new value: " + mi[i]);
            mbh.update(mi[i]);
            
        }
        assertEquals(mbh.size(), NUM_OBJECTS);
        checkOrder();
    }

    private int getValueShift(int mode)
    {
    	switch (mode)
        {
    		case DECREASE:
                return -NUM_OBJECTS;
            case INCREASE:
                return NUM_OBJECTS;
            case RANDOM:
                return (int)(Math.random() * 2 * NUM_OBJECTS) - NUM_OBJECTS;
            default:
                throw new IllegalArgumentException("invalid mode");
        }
    }
    
    
    private void checkOrder()
    {
        // elements should be removed in nondecreasing order
        int prev = ((Number)mbh.pop()).intValue();
        while (!mbh.isEmpty())
        {
            Number n_top = (Number)mbh.peek();
            Number n_cur = (Number)mbh.pop();
            int top = n_top.intValue();
            int cur = n_cur.intValue();
//            System.out.println("prev: " + prev + ", cur: " + cur);
            assertEquals(top, cur);
            assertSame(n_top, n_cur);
            assertTrue(prev <= cur);
            prev = cur;
        }
    }
    
    private class Int extends Number implements Comparable
    {
        int intval;

        Int(int i)
        {
        	intval = i;
        }
        
        public int intValue()
        {
        	return intval;
        }
        
        public long longValue()
        {
        	return intval;
        }

        public double doubleValue()
        {
            return intval;
        }
        
        public float floatValue()
        {
        	return intval;
        }

        public int compareTo(Object o)
        {
            
            Int n = (Int)o;
            return intval - n.intval;
        }
        
        public String toString()
        {
        	return Integer.toString(intval);
        }
    }
    
}
