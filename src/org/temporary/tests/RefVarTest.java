/**
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
 * 
 * 
 * File creation: Aug 19, 2010 2:52:46 PM
 */


package org.temporary.tests;



import java.util.HashMap;

import org.dml.tools.RunTime;
import org.references.Ref2Boolean;
import org.references.Reference;
import org.references.method.MethodParams;
import org.references.method.ParamID;



/**
 * 
 *
 */
@Deprecated
public class RefVarTest
{
	
	Reference<String>		rStr;
	public static ParamID	normalExit	= ParamID.getNew( "normalExit" );
	
	
	public static
			void
			main(
					String[] args )
	{
		RefVarTest rv = new RefVarTest();
		MethodParams inputs = MethodParams.getNew();
		MethodParams outputs = MethodParams.getNew();
		
		Ref2Boolean rBool = new Ref2Boolean();
		// rBool.setObject( null );
		// rBool.setObject( Reference.unset );
		RunTime.assumedTrue( rBool.isUnSet() );
		// RunTime.assumedNull( rBool.getObject() );
		
		// outputs.set(
		// successStatus,
		// rBool );
		outputs.associate(
							normalExit,
							rBool );
		// HashMap<ParamID, Reference<Object>> m = new HashMap<ParamID, Reference<Object>>();
		// m.put( key, value )
		// Object[][] names =
		// {
		// {
		// normalExit,
		// rBool
		// },
		// {
		// normalExit,
		// rBool
		// }
		// };
		// rv.m( new Object[][]
		// {
		// {
		// normalExit,
		// rBool
		// },
		// {
		// normalExit,
		// rBool
		// }
		// } );
		// RunTime.assumedNotNull( outputs.get(
		// normalExit ).getObject() );
		// RunTime.assumedNull( ( (Reference<Object>)outputs.getEx( normalExit ) ).getObject() );
		Reference<Object> rNul = new Reference<Object>();
		rNul.setObject( null );
		// outputs.associate(
		// normalExit,
		// rNul );
		rv.method(
					inputs,
					outputs );
		
		// RunTime.assumedTrue( (Boolean)( ( (Reference<Object>)outputs.getEx( successStatus ) ).getObject() ) );
		// RunTime.assumedTrue( (Boolean)( outputs.getRVar( normalExit ).getObject() ) );
		// RunTime.assumedNotNull( rBool );
		// RunTime.assumedNotNull( rBool.getObject() );
		RunTime.assumedTrue( rBool.getObject() );
		// RunTime.assumedTrueRVar( rBool );
	}
	

	// /**
	// * @param objects
	// */
	// private
	// void
	// m(
	// Object[][] objects )
	// {
	// // TODO Auto-generated method stub
	//
	// }
	

	public RefVarTest()
	{
		rStr = new Reference<String>();
		
	}
	

	/**
	 * @param inputs
	 * @param outputs
	 */
	public
			void
			method(
					MethodParams inputs,
					MethodParams outputs )
	{
		// MethodParams.expectedInputs(inputs,);
		MethodParams.expectedOutputs(
										outputs,
										normalExit );
		outputs.setRVarValue(
								normalExit,
								false );
		// RunTime.assumedNotNull(
		// inputs,
		// outputs );
		// // RunTime.assumedTrue( inputs.size() > 0 );
		// RunTime.assumedTrue( outputs.size() > 0 );
		// Reference<Object> ref2refSS = outputs.get( successStatus );
		// RunTime.assumedNotNull( ref2refSS );// so must have this return param
		// Reference<Object> refSS = (Reference<Object>)ref2refSS.getObject();
		// RunTime.assumedNotNull( refSS );
		// RunTime.assumedNull( refSS.getObject() );// optional, used only in our case
		// refSS.setObject( true );
		outputs.setRVarValue(
								normalExit,
								true );
	}
}
