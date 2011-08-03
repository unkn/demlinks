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


import org.demlinks.node.AllTestsNode;
import org.demlinks.nodemaps.AllTestsNodeMaps;
import org.dml.database.AllTestsDatabase;
import org.dml.level010.AllTestsL010;
import org.dml.level020.AllTestsL020;
import org.dml.level025.AllTestsL025;
import org.dml.level030.AllTestsL030;
import org.dml.level040.AllTestsL040;
import org.dml.level050.AllTestsL050;
import org.dml.tools.AllTestsTools;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.references.AllTestsReferences;
import org.references.method.AllTestsReferencesMethod;



@RunWith( Suite.class )
@Suite.SuiteClasses(
		value =
		{
				AllTestsReferences.class,
				AllTestsNode.class,
				AllTestsNodeMaps.class,
				
				AllTestsReferences.class,
				AllTestsReferencesMethod.class,
				AllTestsTools.class,
				AllTestsDatabase.class,
				AllTestsL010.class,
				AllTestsL020.class,
				AllTestsL025.class,
				AllTestsL030.class,
				AllTestsL040.class,
				AllTestsL050.class
		} )
public class AllTests2
{
}
