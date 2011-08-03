/*
 * Copyright (C) 2005-2008 AtKaaZ <atkaaz@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */


package org.demlinks.javathree;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;



public class EnvironmentTest
{
	
	Environment	env;
	Id			ida, idb;
	
	
	@Before
	public
			void
			init()
					throws Exception
	{
		env = new Environment();
		ida = new Id(
						"a" );
		idb = new Id(
						"b" );
		// link two new nodes "a"->"b"
		assertTrue( env.linkForward(
										ida,
										idb ) );
	}
	
	
	@Test
	public
			void
			testLinkTo()
	{
		
		assertTrue( env.isLinkForward(
										ida,
										idb ) );
		assertTrue( env.isLinkForward(
										new Id(
												"a" ),
										new Id(
												"b" ) ) );// working is
															// the king of
															// war
		assertTrue( ( new Id(
								"b" ) ).equals( new Id(
														"b" ) ) );
	}
	
	
	@Test
	public
			void
			testLink()
					throws Exception
	{
		
		Id idc = new Id(
							"c" );
		assertFalse( env.isNode( idc ) ); // inexistent Node
		
		// assertTrue( null == env.getID(new Node(env)));
		
		assertTrue( env.linkForward(
										ida,
										idc ) );
		assertTrue( env.isLinkForward(
										ida,
										idc ) );
		assertTrue( env.isLinkForward(
										ida,
										new Id(
												"c" ) ) );
		assertTrue( env.isLinkForward(
										new Id(
												"a" ),
										idc ) );
		assertTrue( env.isLinkForward(
										new Id(
												"a" ),
										new Id(
												"c" ) ) );
		
		assertTrue( env.linkForward(
										idb,
										idc ) ); // link two existing nodes
		assertTrue( env.isLinkForward(
										idb,
										idc ) );
		
		env.linkForward(
							new Id(
									"d" ),
							idc );// new node "d"
		assertTrue( env.isLinkForward(
										new Id(
												"d" ),
										idc ) );
		
		// -------------------------
		
		Id allChars = new Id(
								"AllChars" );
		Id __A = new Id(
							"A" );
		assertTrue( env.linkForward(
										allChars,
										__A ) );
		assertFalse( env.linkForward(
										allChars,
										__A ) );
		assertFalse( env.linkForward(
										allChars,
										new Id(
												"A" ) ) );
		assertFalse( env.linkForward(
										allChars,
										new Id(
												String.format(
																"%c",
																65 ) ) ) );
		assertTrue( env.getSize(
									allChars,
									List.FORWARD ) == 1 );
		this.addAllChars();
		
		this.parseTree(
						allChars,
						20,
						"" );
		this.parseTree(
						new Id(
								"a" ),
						20,
						"" );
		this.parseTree(
						new Id(
								"d" ),
						20,
						"" );
		this.parseTree(
						new Id(
								"c" ),
						20,
						"" );
		
	}
	
	
	public
			void
			addAllChars()
					throws Exception
	{
		Id _ac = new Id(
							"AllChars" );
		for ( int i = 65; i < 72; i++ )
		{
			env.linkForward(
								_ac,
								new Id(
										String.format(
														"%c",
														i ) ) );
			assertTrue( env.isLinkForward(
											_ac,
											new Id(
													String.format(
																	"%c",
																	i ) ) ) );
		}
	}
	
	
	public
			void
			parseTree(
						Id nodeID,
						int downToLevel,
						String whatWas )
					throws Exception
	{
		whatWas += nodeID;
		if ( downToLevel < 0 )
		{
			System.out.println( whatWas
								+ " {max level reached}" );
			return;
		}
		if ( !env.isNode( nodeID ) )
		{ // this will never happen (unless first call)
			throw new NoSuchElementException();
		}
		NodeParser parser = env.getParser(
											nodeID,
											List.FORWARD,
											Location.FIRST );
		// NodeParser parser = env.getParser(nodeID, List.FORWARD,
		// Location.AFTER, null);
		nodeID = parser.getCurrentID();
		if ( null == nodeID )
		{ // no more children
			System.out.println( whatWas );
			return;
		}
		else
		{
			do
			{
				this.parseTree(
								nodeID,
								downToLevel - 1,
								whatWas
										+ "->" );
				parser.go( Location.AFTER );// aka go next
				nodeID = parser.getCurrentID();
			}
			while ( null != nodeID );
		}
		
	}
	
	
	@Test
	public
			void
			testNullParameters()
					throws Exception
	{
		
		Id nullId = null;
		Id fullId = new Id(
							"something" );
		
		boolean excepted = false;
		try
		{
			env.linkForward(
								nullId,
								nullId );
		}
		catch ( NullPointerException e )
		{
			excepted = true;
		}
		assertTrue( excepted );
		
		excepted = false;
		try
		{
			env.linkForward(
								fullId,
								nullId );
		}
		catch ( NullPointerException e )
		{
			excepted = true;
		}
		assertTrue( excepted );
		
		excepted = false;
		try
		{
			env.linkForward(
								nullId,
								fullId );
		}
		catch ( NullPointerException e )
		{
			excepted = true;
		}
		assertTrue( excepted );
		
		excepted = false;
		try
		{
			env.linkForward(
								ida,
								nullId );
		}
		catch ( NullPointerException e )
		{
			excepted = true;
		}
		assertTrue( excepted );
		
		excepted = false;
		try
		{
			env.linkForward(
								nullId,
								ida );
		}
		catch ( NullPointerException e )
		{
			excepted = true;
		}
		assertTrue( excepted );
		
	}
	
	
	@Test
	public
			void
			testUnLink()
					throws Exception
	{
		Id idc = new Id(
							"c" );
		Id nulId = null;
		assertFalse( env.isLinkForward(
										ida,
										idc ) );
		assertFalse( env.unLinkForward(
										ida,
										idc ) );
		assertTrue( env.linkForward(
										ida,
										idc ) );
		assertTrue( env.unLinkForward(
										ida,
										idc ) );
		
		boolean excepted = false;
		try
		{
			assertFalse( env.unLinkForward(
											ida,
											nulId ) );
		}
		catch ( NullPointerException e )
		{
			excepted = true;
		}
		assertTrue( excepted );
		
		excepted = false;
		try
		{
			assertFalse( env.unLinkForward(
											nulId,
											ida ) );
		}
		catch ( NullPointerException e )
		{
			excepted = true;
		}
		assertTrue( excepted );
		
		excepted = false;
		try
		{
			assertFalse( env.unLinkForward(
											nulId,
											nulId ) );
		}
		catch ( NullPointerException e )
		{
			excepted = true;
		}
		assertTrue( excepted );
	}
}
