

package org.demlinks.environment;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith( Suite.class )
@Suite.SuiteClasses( value = {
		CharMappingTest.class, WordMappingTest.class,
} )
public class AllTestsEnvironment {
}
