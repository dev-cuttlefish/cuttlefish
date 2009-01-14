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
 * Created on Jun 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.edu.uci.ics.jung.utils;

import junit.framework.TestCase;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.utils.DefaultUserData;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.utils.UserDataContainer;

/**
 * @author danyelf
 *
 */
public class UserDataTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
        // FIXME: this should be fixed to test other types of user data as well;
        // see Danyel's vertex testing code
		udc = new DefaultUserData();
	}

	protected void tearDown() throws Exception {
		udc = null;
	}

	UserDataContainer udc;
	String keyRemove = "Key1";
	String keyReplicate = "Key2";
	String keyShared = "Key3";
	String datumRemove = "Datum1";
	String datumReplicate = "Datum2";
	String datumShared = "Datum3";
	
	String keyCloneRemove = "Key4";
	String keyCloneReplicate = "Key5";
	String keyCloneShared = "Key6";
	TestDataHolder clonedRemove = new TestDataHolder( 10 );
	TestDataHolder clonedReplicate = new TestDataHolder( 20 );
	TestDataHolder clonedShared = new TestDataHolder( 30 );

	public static class TestDataHolder implements Cloneable {
		public TestDataHolder(int arg) {
			this.i = arg;
			this.increment = incrementor++;
		}

		static int incrementor = 0;
		int increment;
		int i;

		public boolean equals(Object obj) {
			return this.i == ((TestDataHolder) obj).i;
		}

		public Object clone() throws CloneNotSupportedException {
			TestDataHolder tdh = (TestDataHolder) super.clone();
			tdh.i = this.i;
			return tdh;
		}
	}

	public void testPutSomethingIn() {
		udc.addUserDatum(keyRemove, datumRemove, UserData.REMOVE);
		udc.addUserDatum(keyReplicate, datumReplicate, UserData.CLONE);
		udc.addUserDatum(keyShared, datumShared, UserData.SHARED);

		assertSame(datumRemove, udc.getUserDatum(keyRemove));
		assertSame(UserData.REMOVE, udc.getUserDatumCopyAction(keyRemove));
	}

    public void testAddFail() 
    {
        udc.addUserDatum(keyRemove, datumRemove, UserData.REMOVE);
        try
        {
            udc.addUserDatum(keyRemove, null, UserData.REMOVE);
            fail("should not allow adding with same key twice");
        }
        catch (IllegalArgumentException iae) {}
        
        try
		{
        	udc.addUserDatum(null, datumRemove, UserData.REMOVE);
        	fail("should not allow null key");
		}
        catch (IllegalArgumentException iae) {}
        
    }
    
    public void testImportFail()
    {
        udc.addUserDatum(keyShared, datumShared, UserData.SHARED);
        UserDataContainer udc2 = new DefaultUserData();
        udc2.addUserDatum(keyShared, datumShared, UserData.SHARED);
        try
        {
            udc2.importUserData(udc);
            fail("should not allow importing data into udc that has duplicate key");
        }
        catch (FatalException fe) {}
    }
    
    public void testContainsKey()
    {
        udc.addUserDatum(keyShared, datumShared, UserData.SHARED);
        assertTrue(udc.containsUserDatumKey(keyShared));
        assertFalse(udc.containsUserDatumKey(keyReplicate));
        udc.addUserDatum(keyReplicate, "whee!", UserData.CLONE);
        assertTrue(udc.containsUserDatumKey(keyReplicate));
    }
    
	public void testImportNonClone() {
		udc.addUserDatum(keyRemove, datumRemove, UserData.REMOVE);
//		udc.addUserDatum(keyReplicate, datumReplicate, UserData.REPLICATE);
		udc.addUserDatum(keyShared, datumShared, UserData.SHARED);

		UserDataContainer udc2 = new DefaultUserData();
		udc2.importUserData(udc);
		assertNull(udc2.getUserDatum(keyRemove));
//		assertEquals(datumReplicate, udc2.getUserDatum(keyReplicate));
//		assertSame( datumReplicate, udc.getUserDatum( keyReplicate )) ;
		assertSame(datumShared, udc2.getUserDatum(keyShared));
	}

	public void testCloneStandard() throws CloneNotSupportedException  {
		TestDataHolder tdh1 = new TestDataHolder( 5 );
		TestDataHolder tdh2 = new TestDataHolder( 6 );
		TestDataHolder tdh3 = new TestDataHolder( 6 );
		assertNotSame( tdh1, tdh2 );
		assertNotSame( tdh2, tdh3 );
		assertFalse( tdh1.equals( tdh2 ) );
		assertEquals( tdh2, tdh3 );
		TestDataHolder tdhClone = (TestDataHolder) tdh2.clone();
		assertEquals( tdhClone, tdh2 );
		assertEquals( tdhClone, tdh3 );
		assertNotSame( tdhClone, tdh2 );
		assertNotSame( tdhClone, tdh3 );
	}

	public void testImportClone() {
		// add cloneable data
		udc.addUserDatum(keyCloneRemove, clonedRemove, UserData.REMOVE);
		udc.addUserDatum(keyCloneReplicate, clonedReplicate, UserData.CLONE);
		udc.addUserDatum(keyCloneShared, clonedShared, UserData.SHARED);
						
		UserDataContainer udc2 = new DefaultUserData();
		udc2.importUserData(udc);
		assertNull(udc2.getUserDatum(keyCloneRemove));
		assertEquals( clonedReplicate, udc2.getUserDatum(keyCloneReplicate));
		assertNotSame(clonedReplicate, udc2.getUserDatum(keyCloneReplicate));
		assertSame(clonedShared, udc2.getUserDatum(keyCloneShared));
	}
	
//	public void testDemonstrateSharedConceptualScrewedUppedness() {
//		udc.addUserDatum(keyShared, datumShared, UserData.SHARED);
//
//		UserDataContainer udc2 = new UserData();
//		udc2.importUserData(udc);
//
//		assertSame( udc.getUserDatum( keyShared ), udc2.getUserDatum( keyShared ));
//	
//	}

}
