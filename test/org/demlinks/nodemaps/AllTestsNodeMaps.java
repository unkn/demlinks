

package org.demlinks.nodemaps;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith( Suite.class )
@Suite.SuiteClasses( value = {
		EnvironmentTest.class, PointerNodeTest.class,
		NodeWithDupChildrenTest.class, ChildlessNodeTest.class,
		CharNodeTest.class
} )
public class AllTestsNodeMaps {
}
