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
	
	private final static RAMTwoWayHashMapOfNonNullUniques<NodeGenericExtensions, NodeGenericImpl>	extensionInstances;
	static {
		// just don't want this assignment(=) to shift the end part too far to the right due to lame'o'indentation
		// that's why it's in a static block
		extensionInstances = new RAMTwoWayHashMapOfNonNullUniques<NodeGenericExtensions, NodeGenericImpl>();
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
	}
	
	
	public final StorageGeneric getStorage() {
		return storage;
	}
	
	
	private NodeGenericExtensions getExtensionInstance( final ExtensionTypes type, final NodeGeneric node,
														final Object... extras ) {
		final Class<? extends NodeGenericExtensions> cls = getClassForType( type );
		Constructor<? extends NodeGenericExtensions> constructor = null;
		try {
			System.out.println( "constructors for `" + cls + "` == " + cls.getConstructors().length );
			// org.dml.storage.Level2.Extension_Pointer_ToChild
			constructor = cls.getConstructor( StorageGeneric.class, NodeGeneric.class );
			assert Q.nn( constructor );
			final NodeGenericExtensions inst = constructor.newInstance( getStorage(), node, extras );
			assert Q.nn( inst );
			return inst;
		} catch ( NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e ) {
			throw Q.rethrow( e );
		}
	}
	
	
	public synchronized NodeGenericExtensions
			createNewExtensionInstance( final ExtensionTypes type, final NodeGeneric selfNode ) {
		assert Q.nn( selfNode );
		final NodeGenericImpl impl = selfNode.getSelfImpl();
		final NodeGenericExtensions existingInstance = getExtensionInstanceForNodeImpl( impl );
		if ( null != existingInstance ) {
			Q.badCall( "already existed, cannot exclusively create!" );
		}
		final NodeGenericExtensions newInstance = getExtensionInstance( type, selfNode );
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
			assert Z.isSameOrDescendantOfClass_throwIfNull( existingInstance, expectedExtensionClass ) : "this node `"
				+ selfNode
				+ "` "
				+ "was used for a different Extension namely for `"
				+ existingInstance
				+ "`\n"
				+ "thus you cannot use this same node for a different extension type - because this would be a bad usage: a node "
				+ "is supposed to represent only one of these extensions ie. can't be a pointer and a set at the same time because"
				+ "treating it as a set would most likely violate pointer constrains ie. by adding more than 1 children";
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
			return L0Set_OfChildren.class;
		}
		throw Q.bug( "not reached due to eclipse compiler letting us know that we missed a case;"
			+ "or you forgot to add a return above" );
	}
	
	
	public synchronized NodeGenericExtensions getOrCreateExtensionInstance( final ExtensionTypes type,
																			final NodeGeneric selfNode ) {
		assert Q.nn( selfNode );
		NodeGenericExtensions existingInstance = internal_get_Extension( type, selfNode );
		if ( null == existingInstance ) {
			existingInstance = createNewExtensionInstance( type, selfNode );
			assert Q.nn( existingInstance );
		}
		return existingInstance;
	}
}
