/**
 * 
 * Copyright (c) 2005-2011, AtKaaZ
 * All rights reserved.
 * this file is part of DemLinks
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

package org.aspectj.ExceptionsHandling.ToE.swing;



import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.aspectj.ExceptionsHandling.ToE.*;
import org.q.*;
import org.references2.*;
import org.tools.swing.*;



/**
 * XXX:don't change any `synchronized` states by adding/removing 'synchronized' keyword or any other locks
 * this is only supposed to be called from within EDT, else will fail!
 */
public class SwingTreeModelForTreeOfExceptions
		// extends
		// TreeOfNonNullUniques<NodeForTreeOfExceptions>
		implements TreeModel
{
	
	
	private volatile TreeOfNonNullUniques<NodeForTreeOfExceptions>	treeSrc			= null;
	
	private TreeModelEvent											event			= null;
	// those that are listening on this class to see if we're reporting changes in the tree holding the data
	protected EventListenerList										listenerList	= null;
	
	
	// private Runnable runnableStructChangedNotifier =
	// null;
	
	
	/**
	 * constructor
	 */
	public SwingTreeModelForTreeOfExceptions( final TreeOfNonNullUniques<NodeForTreeOfExceptions> treeToUseForSource ) {
		// S.entry();
		// try
		// {
		// Q.assumedFalse( SwingUtilities.isEventDispatchThread() );can be true OR false here
		treeSrc = treeToUseForSource;
		assert Q.nn( treeSrc );
		// }
		// finally
		// {
		// S.exit();
		// }
	}
	
	
	public synchronized void
			changeTreeSourceWithoutNotify( final TreeOfNonNullUniques<NodeForTreeOfExceptions> toThisNewTreeSource ) {
		// S.entry();
		// TreeOfExceptions.lockSerializedAccess.lock();
		// try
		// {
		assert ( SwingUtilities.isEventDispatchThread() );
		assert Q.nn( toThisNewTreeSource );
		treeSrc = toThisNewTreeSource;
		// notifyThatTreeStructureChanged();// so the gui will update its contents
		// }
		// finally
		// {
		// // TreeOfExceptions.lockSerializedAccess.unlock();
		// S.exit();
		// }
	}
	
	
	private synchronized// yeah sync
			EventListenerList getListenersList() {
		// S.entry();
		// try
		// {
		assert ( SwingUtilities.isEventDispatchThread() );
		// synchronized ( TreeOfExceptions.toeLock )
		// {
		if ( null == listenerList ) {
			listenerList = new EventListenerList();
		}
		assert Q.nn( listenerList );
		return listenerList;
		// }
		// finally
		// {
		// S.exit();
		// }
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public synchronized Object getRoot() {
		// S.entry();
		// // TreeOfExceptions.lockSerializedAccess.lock();
		// try
		// {
		assert ( SwingUtilities.isEventDispatchThread() );
		final Object ret = treeSrc.getRootUserObject();
		assert Q.nn( ret );
		// ret = "<span style=\"color: 'green'\">"
		// + ret
		// + "</span>";
		return ret;
		// }
		// finally
		// {
		// // TreeOfExceptions.lockSerializedAccess.unlock();
		// S.exit();
		// }
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
	public synchronized Object getChild( final Object parent, final int index ) {
		// S.entry();
		// TreeOfExceptions.lockSerializedAccess.lock();
		// try
		// {
		assert ( SwingUtilities.isEventDispatchThread() );
		
		assert Q.nn( parent );
		assert assumedRightInstance( parent );
		final Object ret = treeSrc.getChildAtIndex( (NodeForTreeOfExceptions)parent, index );
		assert Q.nn( ret );
		return ret;
		// }
		// finally
		// {
		// // TreeOfExceptions.lockSerializedAccess.unlock();
		// S.exit();
		// }
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public synchronized int getChildCount( final Object parent ) {
		// S.entry();
		// // TreeOfExceptions.lockSerializedAccess.lock();
		// try
		// {
		assert ( SwingUtilities.isEventDispatchThread() );
		assert Q.nn( parent );
		assert assumedRightInstance( parent );
		assert Q.nn( treeSrc );
		return treeSrc.getChildCount0( (NodeForTreeOfExceptions)parent );
		// }
		// finally
		// {
		// // TreeOfExceptions.lockSerializedAccess.unlock();
		// S.exit();
		// }
	}
	
	
	private static// not synchronized! unneeded
			boolean assumedRightInstance( final Object o ) {
		if ( !( o instanceof NodeForTreeOfExceptions ) )// not (NodeForTreeOfExceptions OR subclasses)
		{
			Q.badCall( "parameter `" + o + "` is not a NodeForTreeOfExceptions or one of its subclasses" );
		}
		return true;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public synchronized boolean isLeaf( final Object node ) {
		// S.entry();
		// // TreeOfExceptions.lockSerializedAccess.lock();
		// try
		// {
		assert ( SwingUtilities.isEventDispatchThread() );
		assert Q.nn( node );
		assert assumedRightInstance( node );
		assert Q.nn( treeSrc );
		if ( treeSrc.hasUserObject( (NodeForTreeOfExceptions)node ) ) {
			final int children = getChildCount( node );
			assert ( children >= 0 );
			return 0 == children;
		} else {
			return false;
		}
		// }
		// finally
		// {
		// // TreeOfExceptions.lockSerializedAccess.unlock();
		// S.exit();
		// }
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	@Override
	public synchronized void valueForPathChanged( final TreePath path, final Object newValue ) {
		assert ( SwingUtilities.isEventDispatchThread() );
		Q.badCall( "should never be called because the tree is not user editable ie. via GUI" );
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	@Override
	public synchronized int getIndexOfChild( final Object parent, final Object child ) {
		// S.entry();
		// // TreeOfExceptions.lockSerializedAccess.lock();
		// try
		// {
		assert ( SwingUtilities.isEventDispatchThread() );
		
		// XXX:make NodeForTreeOfExceptions type more general with regards to this class ie. maybe generics? - can't
		assert Q.nn( parent );// yeah I know I should return -1 but I'd rather catch bugs
		assert Q.nn( child );
		assert assumedRightInstance( parent );
		assert assumedRightInstance( child );
		assert Q.nn( treeSrc );
		if ( ( treeSrc.hasUserObject( (NodeForTreeOfExceptions)parent ) )
			&& ( treeSrc.hasUserObject( (NodeForTreeOfExceptions)child ) ) ) {
			return treeSrc.getIndexOfChild0( (NodeForTreeOfExceptions)parent, (NodeForTreeOfExceptions)child );
		} else {
			return -1;
		}
		// }
		// finally
		// {
		// // TreeOfExceptions.lockSerializedAccess.unlock();
		// S.exit();
		// }
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public synchronized// ye sync this
			void addTreeModelListener( final TreeModelListener l ) {
		assert ( SwingUtilities.isEventDispatchThread() );
		// synchronized ( TreeOfExceptions.toeLock )
		// {
		// Q.badCall( "should never be called" );
		// apparently this is called on JTree.setModel()
		// treeNodesInserted
		assert Q.nn( l );
		getListenersList().add( TreeModelListener.class, l );
		// }
	}
	
	
	/**
	 * @return the event that will say the subtree starting from root has changed structure
	 */
	private synchronized TreeModelEvent getEvent() {
		// S.entry();
		// // TreeOfExceptions.lockSerializedAccess.lock();
		// try
		// {
		assert ( SwingUtilities.isEventDispatchThread() );
		if ( null == event ) {
			event = new TreeModelEvent( this, new Object[] {
				getRoot()
			} );
		}
		return event;
		// }/*
		// finally
		// {
		// // TreeOfExceptions.lockSerializedAccess.unlock();
		// S.exit();
		// }*/
	}
	
	
	/**
	 * AKA update GUI to reflect changes in the underlying tree structure<br>
	 * ie. notify the GUI that the tree holding the data had structure changes, so the GUI will have to update<br>
	 * apparently if I make this synchronized it will sometimes cause deadlock because the AWT Event Queue thread will call
	 * isLeaf() which is also synchronized when treeStructureChanged() below is called and this main thread will wait for that
	 * thread to finish, so deadlock<br>
	 */
	protected synchronized// neverrrrrrr synchronized! it will deadlock for sure! with isLeaf() that is in two diff threads
			void notifyThatTreeStructureChanged() {
		// S.entry();
		// // TreeOfExceptions.lockSerializedAccess.lock();
		// try
		// {
		// ( SwingUtilities.isEventDispatchThread() );
		assert E.inEDTNow();
		// assert Q.nn( jtree );
		// TreePath current =
		// treeSrc.getSelectionPath();
		// if ( null != current )
		// {
		// // System.out.println( jtree.isCollapsed( current )
		// // || jtree.isExpanded( current ) );
		// jtree.setSelectionPath( current );
		// // jtree.isP
		// }
		// ( SwingUtilities.isEventDispatchThread() );can be true or false
		// if ( null == runnableStructChangedNotifier )
		// {
		// runnableStructChangedNotifier =
		// new Runnable()
		// {
		//
		// @SuppressWarnings( "synthetic-access" )
		// @Override
		// public
		// void
		// run()// this is called in AWT Event Queue thread
		// {
		// S.entry();
		// TreeOfExceptions.lockSerializedAccess.lock();
		// try
		// {
		// ( SwingUtilities.isEventDispatchThread() );
		final Object[] all = getListenersList().getListenerList();
		
		for ( int i = all.length - 2; i >= 0; i -= 2 ) {
			// even // // ones // // are // // that // // class
			assert ( all[i] == TreeModelListener.class );
			// and // odd // ones // are // the // instance // of // that //
			// class // XXX: // at // this // point // another // thread //
			// will
			// // call // isLeaf() // so // it'd // better // not //
			// deadlock //
			// for // example // by // both // isLeaf // and // this //
			// method
			// // we're // in // having // the // keyword // `synchronized`
			// //
			// in // their // def
			( (TreeModelListener)all[i + 1] ).treeStructureChanged( getEvent() );
			// }
		}
		// }
		// finally
		// {
		// TreeOfExceptions.lockSerializedAccess.unlock();
		// S.exit();
		// }
		// }
		// };
		// }
		// SwingUtilities.invokeLater( runnableStructChangedNotifier );
		// }
		// catch ( Throwable t )
		// {
		// Q.rethrow( t );
		// }
		// finally
		// {
		// // TreeOfExceptions.lockSerializedAccess.unlock();
		// S.exit();
		// }
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public synchronized void removeTreeModelListener( final TreeModelListener l ) {
		assert ( SwingUtilities.isEventDispatchThread() );
		Q.badCall( "it's not yet called so if it ever will be we will investigate" );
		// so we might need to remove this line later
	}
	
}
