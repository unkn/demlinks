/*  Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 	
 	This file and its contents are part of DeMLinks.

    DeMLinks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DeMLinks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DeMLinks.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.demlinks.environment.AllTestsEnvironment;
import org.demlinks.node.AllTestsNode;
import org.demlinks.nodemaps.AllTestsNodeMaps;
import org.demlinks.references.AllTestsReferences;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith( Suite.class )
@Suite.SuiteClasses( value = {
		AllTestsReferences.class, AllTestsNode.class, AllTestsNodeMaps.class,
		AllTestsEnvironment.class,
} )
public class AllTests {
}
