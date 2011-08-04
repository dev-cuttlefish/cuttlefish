package ch.ethz.sg.cuttlefish.misc;

import java.io.File;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class CxfToSQLite {
	
	public static boolean cxfToSQLiteDump(String cxfFile, String sqliteFile) {
		File dbFile	 = new File(sqliteFile);
		dbFile.delete();
		
		SqlJetDb db = new SqlJetDb(dbFile, true);
		try {
			db.getOptions().setAutovacuum(true);
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			db.commit();
			db.close();
		} catch (SqlJetException e) {
			e.printStackTrace();
		} 
		return true;
	}
}
