/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
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

import org.dml.database.AllTestsDatabase;
import org.dml.level1.AllTestsL1;
import org.dml.level2.AllTestsL2;
import org.dml.level3.AllTestsL3;
import org.dml.level4.AllTestsLevel4;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.references.AllTestsReferences;



@RunWith( Suite.class )
@Suite.SuiteClasses( value = {
		AllTestsReferences.class, AllTestsDatabase.class,
		AllTestsLevel4.class, AllTestsL1.class, AllTestsL2.class,
		AllTestsL3.class
} )
public class AllTests {
}
