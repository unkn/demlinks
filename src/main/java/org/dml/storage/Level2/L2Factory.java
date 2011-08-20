/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
 * File created on Aug 19, 2011 5:53:43 PM
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'DemLinks' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dml.storage.Level2;

import java.lang.reflect.*;

import org.dml.storage.commons.*;
import org.q.*;
import org.references.*;
import org.toolza.*;



/**
 *
 */
public class L2Factory
{
	
	private final StorageGeneric																	storage;
	private final static StorageHookImplementation													storageHook;
	private final static RAMTwoWayHashMapOfNonNullUniques<NodeGenericExtensions, NodeGenericImpl>	extensionInstances;
	static {
		// just don't want this assignment(=) to shift the end part too far to the right due to lame'o'indentation
		// that's why it's in a static block
		extensionInstances = new RAMTwoWayHashMapOfNonNullUniques<NodeGenericExtensions, NodeGenericImpl>();
		
		storageHook = new StorageHookImplementationAdapter()
		{
			
			@SuppressWarnings( "synthetic-access" )
			@Override
			public void onBeforeShutdown( final boolean inShutdownHook ) {
				if ( !inShutdownHook ) {
					// : remove all nodes or extensions that are using this storage (getStorage() one)
					// Q.ni();
					extensionInstances.removeAll();
				}
			}
		};
	}
	
	
	protected synchronized static NodeGenericExtensions getExtensionInstanceForNodeImpl( final NodeGenericImpl nodeImpl ) {
		assert Q.nn( nodeImpl );
		NodeGenericExtensions existingInstance = extensionInstances.getKey( nodeImpl );
		if ( null != existingInstance ) {
			// returning a clone just to make sure we catch any == bugs or similars
			existingInstance = existingInstance.clone();
		}
		return existingInstance;
	}
	
	
	protected synchronized static void putExtensionInstanceForNodeImpl( final NodeGenericExtensions newInstance,
																		final NodeGenericImpl impl ) {
		assert Q.nn( newInstance );
		assert Q.nn( impl );
		assert null == getExtensionInstanceForNodeImpl( impl );
		final boolean existed = extensionInstances.ensureExists( newInstance, impl );
		assert !existed;
		assert Z.equals_enforceExactSameClassTypesAndNotNull( getExtensionInstanceForNodeImpl( impl ), newInstance );
	}
	
	
	/**
	 * constructor
	 * 
	 * @param stor
	 */
	public L2Factory( final StorageGeneric stor ) {
		storage = stor;
		assert Q.nn( getStorage() );
		// getStorage().assertIsStillValid();
		getStorage().addHook( storageHook );// before creating new instance
	}
	
	
	public final StorageGeneric getStorage() {
		storage.assertIsStillValid();// TODO: must call this wherever we use storage params :/
		return storage;
	}
	
	
	/**
	 * @param type
	 * @param node
	 * @param extras
	 *            ie. domain node or other extra params for the constructors;<br>
	 *            or null if none, or rather unspecified<br>
	 * @return
	 */
	private NodeGenericExtensions getExtensionInstance( final ExtensionTypes type, final NodeGeneric node,
														final Object... extras ) {
		assert Q.nn( node );
		final Class<? extends NodeGenericExtensions> cls = getClassForType( type );
		// Constructor<? extends NodeGenericExtensions> constructor = null;
		try {
			// System.out.println( "constructors for `" + cls + "` == " + cls.getDeclaredConstructors().length );
			final Constructor<?>[] ctors = cls.getDeclaredConstructors();
			assert 1 == ctors.length : "more than 1 declared constructors for class `" + cls + "`;" + "this is unexpected";
			// org.dml.storage.Level2.Extension_Pointer_ToChild
			// constructor = cls.getDeclaredConstructor( StorageGeneric.class, NodeGeneric.class );
			final Constructor<?> constructor = ctors[0];
			assert Q.nn( constructor );
			int howMany = 2;
			if ( null != extras ) {
				howMany += extras.length;
			}
			final Object[] params = new Object[howMany];
			// XXX1309: constructor param order should be same; see other XXX1309
			params[0] = getStorage();
			params[1] = node;
			if ( null != extras ) {
				for ( int i = 2; i < params.length; i++ ) {
					params[i] = extras[i - 2];
				}
			}
			
			assert Z.equalsSimple_enforceNotNull( node.getStorage(), params[0] ) : Q
				.badCall( "the node was from a different storage, bad call" );
			
			// problem here is that in multiple threads, one thread can shutdown the storage while I'm about to new instance
			// here and thus concurrency will break something
			final NodeGenericExtensions inst = (NodeGenericExtensions)constructor.newInstance( params );
			assert Q.nn( inst );
			return inst;
		} catch ( SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e ) {
			throw Q.rethrow( e );
		}
	}
	
	
	public synchronized NodeGenericExtensions createNewExtensionInstance( final ExtensionTypes type,
																			final NodeGeneric selfNode, final Object... extras ) {
		assert Q.nn( selfNode );
		final NodeGenericImpl impl = selfNode.getSelfImpl();
		final NodeGenericExtensions existingInstance = getExtensionInstanceForNodeImpl( impl );
		if ( null != existingInstance ) {
			Q.badCall( "already existed, cannot exclusively create!" );
		}
		final NodeGenericExtensions newInstance = getExtensionInstance( type, selfNode, extras );
		putExtensionInstanceForNodeImpl( newInstance, impl );
		assert Z.equals_enforceExactSameClassTypesAndNotNull( getExtensionInstanceForNodeImpl( impl ), newInstance );
		return newInstance;
	}
	
	
	public synchronized NodeGenericExtensions getExistingExtensionInstance( final ExtensionTypes type,
																			final NodeGeneric selfNode ) {
		assert Q.nn( selfNode );
		final NodeGenericExtensions existingInstance = internal_get_Extension( type, selfNode );
		if ( null == existingInstance ) {
			throw Q.badCall( "cannot exclusively get, it didn't already exist!" );
		} else {
			return existingInstance;
		}
	}
	
	
	protected synchronized NodeGenericExtensions internal_get_Extension( final ExtensionTypes type, final NodeGeneric selfNode
	// ,final Class<T> expectedExtensionClass
			) {
		assert Q.nn( selfNode );
		final Class<? extends NodeGenericExtensions> expectedExtensionClass = getClassForType( type );
		final NodeGenericImpl impl = selfNode.getSelfImpl();
		final NodeGenericExtensions existingInstance = getExtensionInstanceForNodeImpl( impl );
		if ( null != existingInstance ) {
			assert Z.isSameOrDescendantOfClass_throwIfNull( existingInstance, expectedExtensionClass ) : Q
				.badCall( "this node `"
					+ selfNode
					+ "` "
					+ "was used for a different Extension namely for `"
					+ existingInstance
					+ "`\n"
					+ "thus you cannot use this same node for a different extension type - because this would be a bad usage: a node "
					+ "is supposed to represent only one of these extensions ie. can't be a pointer and a set at the same time because"
					+ "treating it as a set would most likely violate pointer constrains ie. by adding more than 1 children" );
			return existingInstance;
		} else {
			return null;
		}
	}
	
	
	/**
	 * @param type
	 * @return
	 */
	private static Class<? extends NodeGenericExtensions> getClassForType( final ExtensionTypes type ) {
		switch ( type ) {
		case DomainPointer:
			return L0DomainPointer_ToChild.class;
		case DomainSet:
			return L0DomainSet_OfChildren.class;
			// case HashMap:
			// return L0HashMap_OfNodes.class;
		case Pointer:
			return Extension_Pointer_ToChild.class;
		case Set:
			return Extension_Set_OfChildren.class;
		}
		throw Q.bug( "not reached due to eclipse compiler letting us know that we missed a case;"
			+ "or you forgot to add a return above" );
	}
	
	
	public synchronized NodeGenericExtensions getOrCreateExtensionInstance( final ExtensionTypes type,
																			final NodeGeneric selfNode, final Object... extras ) {
		assert Q.nn( selfNode );
		NodeGenericExtensions existingInstance = internal_get_Extension( type, selfNode );
		if ( null == existingInstance ) {
			existingInstance = createNewExtensionInstance( type, selfNode, extras );
			assert Q.nn( existingInstance );
		} else {
			// TODO: check if extras are as expected: the same
			// or not, seems to adhoc to implement
			System.err.println( "been here..........................." );
			final Class<? extends NodeGenericExtensions> cls = existingInstance.getClass();
			// if ( existingInstance instanceof IExtension_AnyDomain ) {
			// System.out.println( "`````````````````````````````````````````````lkhkl``````````````````````" );
			// }
			// if ( cls == IExtension_AnyDomain.class ) {
			// System.err.println( "`````````````````````````````````````````````lkhkl``````````````````````" );
			// }
			// if ( cls.isAssignableFrom( IExtension_AnyDomain.class ) ) {
			if ( IExtension_AnyDomain.class.isAssignableFrom( cls ) ) {
				System.err.println( "`````````````````````````````````````````````lkhkl``````````````````````" + cls );
				// } else {
				// System.err.println( cls );
				// }
				// if ( ( cls == L0DomainPointer_ToChild.class ) || ( cls == L0DomainSet_OfChildren.class ) ) {
				final IExtension_AnyDomain domType = (IExtension_AnyDomain)existingInstance;
				assert Q.nn( extras );
				assert extras.length == 1;// it's the node representing the domain
				assert Z.isSameOrDescendantOfClass_throwIfNull( extras[0], NodeGeneric.class );
				assert Z.equals_enforceExactSameClassTypesAndNotNull( domType.getDomain(), extras[0] );
			}
		}
		return existingInstance;
	}
}
