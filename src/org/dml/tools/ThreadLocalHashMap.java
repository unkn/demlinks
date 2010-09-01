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
 * File creation: Aug 26, 2010 4:42:20 AM
 */


package org.dml.tools;



import java.util.HashMap;



/**
 * @param <K>
 *            key
 * @param <V>
 *            value
 * 
 *            NOTE: do not rename classes starting with ThreadLocal* because they are set in RecursionDetector.aj aspect
 *            as excluded from call tracing and stuff - else they infinite loop
 */
public class ThreadLocalHashMap<K, V>
		extends
		ThreadLocal<HashMap<K, V>>
{
	
	public ThreadLocalHashMap()
	{
		super();
	}
	

	@Override
	protected synchronized
			HashMap<K, V>
			initialValue()
	{
		return new HashMap<K, V>();
	}
	
}
