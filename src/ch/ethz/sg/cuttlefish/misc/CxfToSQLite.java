package ch.ethz.sg.cuttlefish.misc;

import java.io.File;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class CxfToSQLite {
	
	public static boolean cxfToSQLiteDump(String cxfFile, String sqliteFile) {
		File dbFile	 = new File(sqliteFile);
		dbFile.delete();
		
		try {
			SqlJetDb db = SqlJetDb.open(dbFile, true);
			db.createTable("CREATE TABLE test(id INT(10) primary key)");			
			db.close();
		} catch (SqlJetException e) {
			e.printStackTrace();
		} 
		return true;
	}
	
	public static void main(String[] args) {
		cxfToSQLiteDump("", "/home/ptsankov/db.dat");
	}
}
