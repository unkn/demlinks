/**
 * 
 * Copyright (C) 2005-2010 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2010 UnKn <unkn@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */



package org.dml.level010;



import java.io.File;

import org.dml.database.bdb.level1.Level1_Storage_BerkeleyDB;
import org.dml.tools.MainLevel0;
import org.dml.tools.RunTime;
import org.dml.tools.VarLevel;
import org.references.method.MethodParams;
import org.references.method.PossibleParams;

import com.sleepycat.je.DatabaseException;



/**
 * should throw only StorageException.<br>
 * this is done mostly for wrapping Exceptions under StorageException<br>
 */
public class Level010_DMLStorage_BerkeleyDB
		extends
		MainLevel0
		implements
		Level010_DMLStorageWrapper
{
	
	@VarLevel
	private final Level1_Storage_BerkeleyDB	bdb						= null;
	
	private final static String				DEFAULT_BDB_ENV_PATH	= "." + File.separator + "bin" + File.separator
																			+ "mainEnv" + File.separator;
	
	
	/**
	 * constructor, don't forget to call init(...)
	 */
	public Level010_DMLStorage_BerkeleyDB()
	{
		
		super();
	}
	

	@Override
	protected
			MethodParams
			getDefaults()
	{
		
		MethodParams def = super.getDefaults();
		
		def.set(
					PossibleParams.homeDir,
					DEFAULT_BDB_ENV_PATH );
		def.set(
					PossibleParams.jUnit_wipeDB,
					false );
		return def;
	}
	

	// =============================================
	@Override
	public final
			JavaID
			getJavaID(
						Symbol identifiedByThisSymbol )
	{
		
		RunTime.assumedNotNull( identifiedByThisSymbol );
		return bdb.getDBMap_JavaIDs_To_Symbols().getJavaID(
															identifiedByThisSymbol );
	}
	

	@Override
	public final
			Symbol
			getSymbol(
						JavaID identifiedByThisJavaID )
	{
		
		RunTime.assumedNotNull( identifiedByThisJavaID );
		return bdb.getDBMap_JavaIDs_To_Symbols().getSymbol(
															identifiedByThisJavaID );
	}
	

	@Override
	public final
			Symbol
			createSymbol(
							JavaID fromJavaID )
	{
		
		RunTime.assumedNotNull( fromJavaID );
		return bdb.getDBMap_JavaIDs_To_Symbols().createSymbol(
																fromJavaID );
	}
	

	@Override
	public final
			Symbol
			ensureSymbol(
							JavaID theJavaID )
	{
		
		RunTime.assumedNotNull( theJavaID );
		return bdb.getDBMap_JavaIDs_To_Symbols().ensureSymbol(
																theJavaID );
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level1.Level1_DMLStorageWrapper#newUniqueSymbol()
	 */
	@Override
	public
			Symbol
			newUniqueSymbol()
	{
		
		return bdb.getUniqueSymbolsGenerator().getNewUniqueSymbol();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dml.level1.Level1_DMLStorageWrapper#link(org.dml.level1.Symbol,
	 * org.dml.level1.JavaID)
	 */
	@Override
	public
			boolean
			ensureLink(
						Symbol symbol,
						JavaID javaID )
	{
		
		RunTime.assumedNotNull(
								symbol,
								javaID );
		JavaID oldJid = this.getJavaID( symbol );
		Symbol oldSym = this.getSymbol( javaID );
		// true if already associated
		boolean existsJID = ( null != oldJid );
		boolean existsSym = ( null != oldSym );
		boolean sameJID = false;
		boolean sameSym = false;
		
		if ( existsJID )
		{
			// a jid is already associated with the symbol
			// is it javaID though? or a diff one
			if ( oldJid != javaID )
			{
				// a diff one
				RunTime.badCall( "another JavaID was already associated with the passed Symbol." );
			}
			else
			{
				sameJID = true;
			}
		}
		
		if ( existsSym )
		{
			if ( oldSym != symbol )
			{
				RunTime.badCall( "a different Symbol was already associated with the passed JavaID." );
			}
			else
			{
				sameSym = true;
			}
		}
		
		if ( sameSym && sameJID )
		{
			return true;// already exists
		}
		else
		{
			if ( ( sameSym ^ sameJID ) )
			{
				RunTime.bug();
			}
		}
		// both links are either both false or both true, never one true and
		// one false
		if ( ( existsJID ^ existsSym ) )
		{// xor 0^0=0; 1^1=0; 0^1=1
			// true means fail
			RunTime
					.badCall( "the above two calls failed. Both should be same. This means that the JID or the Symbol was already associated with another JID/Symbol" );
		}
		
		if ( bdb.getDBMap_JavaIDs_To_Symbols().link(
														javaID,
														symbol ) )
		{
			// existed already, impossible to reach this
			RunTime.bug( "huge discrepancy between getJavaID, getSymbol and .link here" );
		}
		return false;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dml.level1.Level1_DMLStorageWrapper#newLink(org.dml.level1.Symbol,
	 * org.dml.level1.JavaID)
	 */
	@Override
	public
			void
			newLink(
						Symbol symbol,
						JavaID javaID )
	{
		
		RunTime.assumedNotNull(
								symbol,
								javaID );
		RunTime.assumedFalse( this.ensureLink(
												symbol,
												javaID ) );
	}
	

}// end of class
