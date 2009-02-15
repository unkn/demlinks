
package org.demlinks.node;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith( Suite.class )
@Suite.SuiteClasses( value = {
		NodeListTest.class, NodeTest.class, GlobalNodesTest.class,
		PointerNodeTest.class, NodeWithDupChildrenTest.class,
} )
public class AllTestsNode {
}
