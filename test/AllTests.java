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
