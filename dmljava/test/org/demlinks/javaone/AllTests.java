package org.demlinks.javaone;

import org.junit.runner.*;
import org.junit.runners.*;


@RunWith(Suite.class)
@Suite.SuiteClasses(value={
		EnvironmentTest.class,
		NodeTest.class
		})
		
public class AllTests {

}
