import org.demlinks.node.AllTestsNode;
import org.demlinks.references.AllTestsReferences;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith( Suite.class )
@Suite.SuiteClasses( value = {
		AllTestsNode.class, AllTestsReferences.class,
} )
public class AllTests {
}
