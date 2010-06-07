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



package org.dml.level050;



import org.dml.level010.JavaID;
import org.dml.level010.Symbol;
import org.dml.level025.SetOfTerminalSymbols;
import org.dml.level040.Level040_DMLEnvironment;



/**
 * 
 *
 */
public class Level050_DMLEnvironment extends Level040_DMLEnvironment {
	
	private static final JavaID	allNodes_JavaID			= JavaID.ensureJavaIDFor( "AllNodes" );
	public SetOfTerminalSymbols	allNodes_Set			= null;
	
	private static final JavaID	allNodeParents_JavaID	= JavaID.ensureJavaIDFor( "AllNodeParents" );
	public SetOfTerminalSymbols	allNodeParents_Set		= null;
	
	private static final JavaID	allNodeChildren_JavaID	= JavaID.ensureJavaIDFor( "AllNodeChildren" );
	public SetOfTerminalSymbols	allNodeChildren_Set		= null;
	
	@Override
	protected void initGeneralSymbols() {

		super.initGeneralSymbols();
		allNodes_Set = this.getAsSet( this.ensureSymbol( allNodes_JavaID ) );
		allNodeParents_Set = this.getAsSet( this.ensureSymbol( allNodeParents_JavaID ) );
		allNodeChildren_Set = this.getAsSet( this.ensureSymbol( allNodeChildren_JavaID ) );
	}
	
	
	/**
	 * @param existingSelfSymbol
	 * @return
	 */
	public Node getExistingNode( Symbol existingSelfSymbol ) {

		return Node.getExistingNode( this, existingSelfSymbol );
	}
	
	public Node getExistingNode( Symbol existingSelfSymbol, boolean expectedAllowChildrenDUPsValue ) {

		return Node.getExistingNode( this, existingSelfSymbol, expectedAllowChildrenDUPsValue );
	}
	
	public Node getNewNode( Symbol existingSelfSymbol, boolean allowChildrenDUPs ) {

		return Node.getNewNode( this, existingSelfSymbol, allowChildrenDUPs );
	}
	
	public Node ensureNode( Symbol existingSelfSymbol, boolean allowChildrenDUPs ) {

		return Node.ensureNode( this, existingSelfSymbol, allowChildrenDUPs );
	}
	
}
