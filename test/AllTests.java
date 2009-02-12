import org.demlinks.node.AllTestsCrap;
import org.demlinks.references.AllTestsReferences;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith( Suite.class )
@Suite.SuiteClasses( value = {
		AllTestsCrap.class, AllTestsReferences.class,
} )
public class AllTests {
}
