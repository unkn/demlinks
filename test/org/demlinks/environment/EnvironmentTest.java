
package org.demlinks.environment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;



public class EnvironmentTest {
	
	Environ	env;
	
	@Before
	public void init() {
		this.env = new Environ();
	}
	
	@Test
	public void testInit() {
		assertTrue( this.env.addPhrase( "something for nothing." ) );
		assertTrue( this.env.addWord( "test" ) );
		
		// false=already exists
		assertFalse( this.env.addPhrase( "something for nothing." ) );
	}
}
