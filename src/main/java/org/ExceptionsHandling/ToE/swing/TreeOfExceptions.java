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

package org.ExceptionsHandling.ToE.swing;



import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.ExceptionsHandling.ToE.*;
import org.JUnitCommons.*;
import org.q.*;
import org.references2.*;
import org.tools.swing.*;
import org.toolza.*;



/**
 * the swing tree which includes the data tree<br>
 * exceptions are shows in the order of occurrence where each parent is the cause for it's child(ren)<br>
 * when unrelated exceptions occur they are added-last to root depending on which cause they cite, if no cause then
 * added to root else added to the parent that is that `cause`
 * XXX: rule: no Swing calls outside of EDT aka Event Dispatch Thread including whatever low level data EDT uses ie. treeSource
 * for model
 * 
 * TODO: obviously need to find a way to report all exceptions inside queueFIFO if size>0, maybe a shutdown hook?
 */
public final class TreeOfExceptions
{
	
	// if the queu is full for this amount of time, it's assumed it deadlocked and will `throw`
	private static final int										QUEUEFULL_TIMEOUT_MILLIS		= 3000;
	
	// this value should be max expected concurrent threads * 2 for it's assumed worst case scenario that they all want to add
	// an exception and they're waiting on the lock, so if at least half of the queue is empty then they are all allowed to do
	// so before returning back to QP which will continue to process the queue
	private static final int										queueCapacity					= 150;
	private final LinkedBlockingQueue<QueuedItem>					queueFIFO						=
																										new LinkedBlockingQueue<QueuedItem>(
																											queueCapacity );
	// private static volatile boolean willConstruct =
	// false;
	// Tree Visibility Change ie. tree is visible or tree is not
	private final static ReentrantLock								lockTVC							= new ReentrantLock();
	private volatile boolean										isQPScheduledToRun				= false;
	private volatile boolean										isQPRunning						= false;
	private final static ReentrantLock								lockQP							= new ReentrantLock();
	private final Condition											condQueueNotFull				= lockQP.newCondition();
	private final ReentrantLock										synchronizedReplacerLock		= new ReentrantLock();
	// private final static ReentrantLock lockFirstTimeInit =
	// new ReentrantLock();
	
	// private final static ReentrantLock lockSecondBlock =
	// new ReentrantLock();
	// private static Condition condQueueNotFull =
	// lockQP.newCondition();
	private static Condition										condIsVisible					= lockTVC.newCondition();
	
	// private static Condition condStartedInit =
	// lockFirstTimeInit.newCondition();
	private static Condition										condIsShutDown					= lockTVC.newCondition();
	// private static boolean initStarted =
	// false;
	// the only lock synchronizing all operations ie. add and get from two diff threads like main and the jframe AWTQueue one
	// static Object toeLock =
	// new Object();
	
	private static volatile TreeOfExceptions						singleton						= null;
	// private static volatile TreeOfExceptions singletonFirstTimeInit =
	// null;
	private static boolean											toeGUIVisible					= false;
	public static volatile boolean									wasShutdownOnce					= false;
	
	// private static volatile Thread queueProcessingThread =
	// null;
	private final SwingTreeModelForTreeOfExceptions					model;
	
	// this will be our main tree, always updated
	private volatile TreeOfNonNullUniques<NodeForTreeOfExceptions>	treeSource;
	// this filter tree will hold only certain items from main tree and it's kinda temporary
	private TreeOfNonNullUniques<NodeForTreeOfExceptions>			filterTreeSource				= null;
	private boolean													filterTreeActive				= false;
	// XXX: the following 2 checkboxes do not require mirroring bools since they are only querried inside EDT
	private JCheckBox												filterOutJUnitFromExceptions	= null;
	
	// change // this // only // from // EDT:
	private boolean													showExAgain						= false;
	
	// private JCheckBox filterOutAspectFromExceptions = null;
	// checkboxes only accessible inside EDT
	private JCheckBox												excludeInfo						= null;
	private JCheckBox												excludeWarn						= null;
	private JCheckBox												excludeHandled					= null;
	// defaults for these checkboxes, these are the mirroring bools for the checkboxes, so they can be queried outside EDT:
	private boolean													boolExcludeInfo					= true;
	private boolean													boolExcludeWarn					= false;
	private boolean													boolExcludeHandled				= true;
	
	
	private JTree													jtree							= null;
	private long													lastModif						= 0;
	// in milliseconds
	private final long												allowCloseAfter					= 2000;
	
	// private final Runnable runnableRepaint =
	// null;
	private final NodeForTreeOfExceptions							rootNode1;
	
	
	// final JPanel panel;
	
	public// don't need synchronized
			NodeForTreeOfExceptions getRoot() {
		final Boolean prev =
		// TODO: see what other places need to use alternate reporting method esp. inside ie. WindowClosing
			ThrowWrapper.useAlternateExceptionReportMethod.get();
		assert !prev.booleanValue();
		try {
			ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_TRUE );
			
			// S.entry();
			// lockSerializedAccess.lock();
			// try
			// {
			// NodeForTreeOfExceptions t =
			// treeSource.getRootUserObject();can't access this outside EDT! well, shouldn't!
			assert Q.nn( rootNode1 );
			return rootNode1;
			// }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// S.exit();
			// }
		} finally {
			ThrowWrapper.useAlternateExceptionReportMethod.set( prev );
		}
	}
	
	
	// public static// no need for synchronized
	// Object
	// toeLock
	// {
	// return toeLock;
	// }
	
	
	private boolean alreadyExists( final Throwable exception ) {
		L.tryLock( synchronizedReplacerLock );
		try {
			// ====================
			
			
			// lockSerializedAccess.lock();
			// try
			// {
			assert E.inEDTNow();
			// (
			// SwingUtilities.isEventDispatchThread() );
			assert !( R.isRecursionDetectedForCurrentThread() );
			
			assert Q.nn( exception );
			final NodeForTreeOfExceptions existingNode =
				new NodeForTreeOfExceptions( exception, StateOfAnException.INVALID_STATE );
			// if ( null == existingNode )
			// {
			// return false;
			// }
			// else
			// {
			// existingNode=model.getUserObject(existingNode);
			return treeSource.hasUserObject( existingNode );
			// {
			// Q.bug( "inconsistency between what the tree has and what the Node statics have. Maybe I removed a node "
			// + "from tree and forgot to also remove it from Node statics, but the latter isn't necessary" );
			// // investigate maybe need to remove this check OR need to return the value for hasUserObject when here
			// // so far with our TreeOfExceptions this shouldn't ever happen because we don't remove exceptions from
			// // this tree
			// }
			// return true;
			// }
			// }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// }
			
			// ====================
		} finally {
			synchronizedReplacerLock.unlock();
		}
	}
	
	
	
	private final Runnable	maRun	= null;
	
	
	/**
	 * this will change the state of an exception<br>
	 * no add/remove will happen<br>
	 * 
	 * @param existingException
	 *            must already exist in tree
	 * @param state
	 */
	public// synchronized// never synchronized, it will deadlock!
			void markAs( final Throwable existingException, final StateOfAnException state ) {
		final Boolean prev = ThrowWrapper.useAlternateExceptionReportMethod.get();
		try {
			ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_TRUE );
			
			assert Q.nn( existingException );
			assert Q.nn( state );
			if ( prev.booleanValue() )// ThrowWrapper.useAlternateExceptionReportMethod.get().booleanValue() )
			{
				// cannot mark this exception because it was never added to the tree, it was reported on console using alternate
				// reporting method, therefore to avoid QP complaining that exception doesn't exist in tree, we ignore this
				// markAs
				Q.consolifyMark( existingException, state );
				return;
			}
			assert !prev.booleanValue();
			// boolean mustNotify =
			// false;
			// S.entry();
			// lockSerializedAccess.lock();
			// try
			// {
			// assert E.inEDTNow();can be either
			assert !( R.isRecursionDetectedForCurrentThread() );// aka current method called
																// within
			// itself =
			// false
			addThis_and_makeSureQueueProcessorWillRun( new QueuedItem(
				EnumQueueProcessorTypes.MARK_EXCEPTION,
				existingException,
				state ) );// don't block
			// }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// // S.exit();
			// }
		} finally {
			ThrowWrapper.useAlternateExceptionReportMethod.set( prev );
		}
	}
	
	
	private void repaint() {
		L.tryLock( synchronizedReplacerLock );
		try {
			// ====================
			
			// S.entry();
			// // lockSerializedAccess.lock();
			// try
			// {
			assert !( R.isRecursionDetectedForCurrentThread() );// aka current method called within
			// itself = false
			assert E.inEDTNow();
			assert ( isVisible() );
			assert Q.nn( jtree );
			// we're doing only a repaint because we know tree struct didn't change as in add/remove
			// but the state of some nodes did change ie. diff color
			// if ( null == runnableRepaint )
			// {
			// runnableRepaint =
			// new Runnable()
			// {
			//
			// @SuppressWarnings( "synthetic-access" )
			// @Override
			// public
			// void
			// run()
			// {
			// // lockSerializedAccess.lock();
			// // try
			// // {
			// ( SwingUtilities.isEventDispatchThread() );
			jtree.repaint();
			// }
			// finally
			// {
			// lockSerializedAccess.unlock();
			// }
			// }
			//
			// };
			// E.addToQueue( runnableRepaint );
			// }
			// }
			// catch ( InvocationTargetException e )
			// {
			// Q.rethrow( e );
			// }
			// catch ( InterruptedException e )
			// {
			// Q.rethrow( e );
			// }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// S.exit();
			// }
			
			// ====================
		} finally {
			synchronizedReplacerLock.unlock();
		}
	}
	
	
	public static// do not synchronized! IT WILL DEADLOCK
			boolean isVisible() {
		// synchronized ( toeLock )
		// {
		// S.entry();
		lockTVC.lock();// must!
		try {
			return toeGUIVisible;
		} finally {
			lockTVC.unlock();
			// S.exit();
		}
		// }
	}
	
	
	
	// private static volatile boolean isEDTHere =
	// false;
	private static volatile boolean	mustInitSecondBlock		= false;
	private static volatile boolean	queuedSecondBlock		= false;
	private static volatile boolean	removeSecBlkFromQueue	= false;
	
	
	/**
	 * there can be only one
	 * two or more threads can enter this at the same time, EDT can be one of them, OR only one thread can enter this and it can
	 * be EDT, and while one thread is here another may enter this at any time(which is the same as the first statement)<br>
	 * 
	 * @return the tree
	 */
	public static// synchronized// do not! synchronized// on .class so no two threads can get this at the same time, but instead
			// serialized
			TreeOfExceptions getTreeOfExceptions()// this can happen in any thread including EDT
	{
		final Boolean prev =
		// just in case this is called outside of aspectj ie. in ExHandler
			ThrowWrapper.useAlternateExceptionReportMethod.get();
		// assert !prev.booleanValue();
		try {
			ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_TRUE );
			if ( null == singleton ) {
				// if ( E.inEDTNow() )
				// {
				// isEDTHere =
				// true;
				// }
				// System.out.println( "before firstblock"
				// + " inEDTnow:"
				// + E.inEDTNow()
				// + " thread:"
				// + Thread.currentThread().getName() );
				// need to prevent multiple gets to init it at the same time
				lockTVC.lock();// multiple concurrent threads will hold on this
				try {// any thread can exec this block but only one thread at a time!
						// System.out.println( "in firstblock before"
						// + " inEDTnow:"
						// + E.inEDTNow()
						// + " thread:"
						// + Thread.currentThread().getName() );
						//
					if ( null == singleton ) {// only one thread will reach this and proceed with init
												// System.out.println( "in firstblock"
												// + " inEDTnow:"
												// + E.inEDTNow()
												// + " thread:"
												// + Thread.currentThread().getName() );
						
						assert !( R.isRecursionDetectedForCurrentThread() );// aka current method
						// called
						// within
						// itself = false
						assert ( ThrowWrapper.useAlternateExceptionReportMethod.get().booleanValue() );
						assert ( null == singleton );
						// (null == singletonFirstTimeInit );
						mustInitSecondBlock = true;// set this first
						singleton = new TreeOfExceptions();// set this second
						// if throw happens inside this, singleton will remain null
						assert Q.nn( singleton );
					}
				}// try
				finally {
					// try
					// {
					// System.out.println( "in firstblock after lock"
					// + " inEDTnow:"
					// + E.inEDTNow()
					// + " thread:"
					// + Thread.currentThread().getName() );
					// }
					// finally
					// {
					lockTVC.unlock();
					// }
				}
			}
			assert Q.nn( singleton );
			// System.out.println( "done firstblock before secondblock"
			// + " inEDTnow:"
			// + E.inEDTNow()
			// + " thread:"
			// + Thread.currentThread().getName() );
			if ( mustInitSecondBlock ) {
				// assert !( R.isRecursionDetectedForCurrentThread() );
				assert ( ThrowWrapper.useAlternateExceptionReportMethod.get().booleanValue() );
				// if ( !isEDTHere )
				// {
				// if ( E.inEDTNow() )
				// {
				// isEDTHere =
				// true;
				// }
				// }
				// System.out.println( "in secondblock"
				// + " inEDTnow:"
				// + E.inEDTNow()
				// + " thread:"
				// + Thread.currentThread().getName() );
				
				// if ( !isVisible() )
				// {
				lockTVC.lock();
				try {// allowed only 1 thread at a time, unless stuck on .await()
						// System.out.println( "in secondblock after locked"
						// + " inEDTnow:"
						// + E.inEDTNow()
						// + " thread:"
						// + Thread.currentThread().getName() );
					if ( !isVisible() )// absolutely needed!
					{
						if ( E.inEDTNow() ) {
							// System.out.println( "in secondblock executing EDTinit"
							// + " inEDTnow:"
							// + E.inEDTNow()
							// + " thread:"
							// + Thread.currentThread().getName() );
							assert E.inEDTNow();
							try {
								assert !( removeSecBlkFromQueue );
								singleton.rSecondBlock.run();
								// }
								// finally
								// {
								// // even if it threw, we shouldn't enter SecondBlock init again, the below isVisible
								// // should
								// // throw
								// // instead
								// mustInitSecondBlock =
								// false;
								// }
							} finally {
								if ( queuedSecondBlock ) {// if it was queued, then when the queued one is executing quickly
															// exit from it
									removeSecBlkFromQueue = true;// the queued one will be ignored, only one, shouldn't be
																	// queued more than once
								}
							}
						} else {
							if ( !queuedSecondBlock ) {// only one thread will queue this
								assert !( queuedSecondBlock );
								queuedSecondBlock = true;
								E.addToQueue( singleton.rSecondBlock );
							}
							// not in EDT, but EDT is concurrently running here
							// so EDT is here therefore I let EDT do secondBlock by waiting&lock is released while in wait
							// try
							// {
							// any number of threads will reach this and await()
							while ( !isVisible() ) {
								try {
									// System.out.println( "in secondblock non-EDT waiting"
									// + " inEDTnow:"
									// + E.inEDTNow()
									// + " thread:"
									// + Thread.currentThread().getName() );
									condIsVisible.await();// will release lockConstr while in wait
								} catch ( final InterruptedException e ) {
									Q.rethrow( e );
								}
								// System.out.println( "in secondblock non-EDT done waiting"
								// + " inEDTnow:"
								// + E.inEDTNow()
								// + " thread:"
								// + Thread.currentThread().getName() );
							}
							// }
							// finally
							// {
							// // even if it threw, we shouldn't enter SecondBlock init again, the below isVisible should
							// // throw
							// // instead
							// mustInitSecondBlock =
							// false;
							// }
						}
						assert !( mustInitSecondBlock );
					}
					// so by the time we are here, isVisible() is true!
					assert ( isVisible() );
					// though a third thread can can isVisible directly and yield false while inside above waits for ToE to
					// change visibility but right between the release of lockTVC and EDT getting to the part of queue about to
					// execute making ToE visible
					assert !( mustInitSecondBlock );
				} finally {
					// try
					// {
					// // System.out.println( "in secondblock lockdone"
					// // + " inEDTnow:"
					// // + E.inEDTNow()
					// // + " thread:"
					// // + Thread.currentThread().getName() );
					// }
					// finally
					// {
					lockTVC.unlock();
					// }
				}
				// }// if
				
				// if ( null == singleton )
				// {
				// if ( !isVisible() )
				// {
				// lockTVC.lock();
				// try
				// {
				// if ( !isVisible() )
				// {
				// assert Q.nn( singleton );
				// assert Q.nn( singleton.rConstruct );
				//
				// if ( !willConstruct )
				// {
				// lockConstr.lock();
				// try
				// {
				// if ( !willConstruct )
				// {
				// willConstruct =
				// true;
				// if ( E.inEDTNow() )
				// {
				// singleton.rConstruct.run();
				// }
				// else
				// {
				// E.addToQueue( singleton.rConstruct );
				// while ( !isVisible() )
				// {
				// try
				// {
				// condIsVisible.await();// will release lockConstr while in wait
				// }
				// catch ( InterruptedException e )
				// {
				// Q.rethrow( e );
				// }
				// }
				// }
				// ( isVisible() );
				// }
				// }
				// finally
				// {
				// lockConstr.unlock();
				// }
				// }
				assert ( isVisible() );
				assert !( mustInitSecondBlock );
				// mustInitSecondBlock =
				// false;
				// // }
				// // finally
				// // {
				// // ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_TRUE );
				// // }
				//
				// // }
				// // finally
				// // {
				// // S.exit();
				// // // lockSerializedAccess.unlock();
				// // }
				// // }
				// // finally
				// // {
				// // S.exit();
				// // }
				// }
				// }// try
				// finally
				// {
				// lockTVC.unlock();
				// }
				// }// if
				// }// if
				
			}// if
			
			
			// System.out.println( "afterAll"
			// + " inEDTnow:"
			// + E.inEDTNow()
			// + " thread:"
			// + Thread.currentThread().getName() );
			assert !( mustInitSecondBlock );
			assert Q.nn( singleton );
			assert ( wasShutdownOnce ? true : ( isVisible() ) );
			// if ( !wasShutdownOnce ) {
			// }
			return singleton;
			
		} finally {
			ThrowWrapper.useAlternateExceptionReportMethod.set( prev );
		}
	}
	
	// public synchronized// never synchronized, it will deadlock!
	// void
	// addException(
	// Throwable unwrappedException )
	// {
	// S.entry();
	// assumedIsNotShutdown();// done: if tree is not active or shutdown, show exceptions on console or something
	// try
	// {
	// assert Q.nn( unwrappedException );
	// assert !( Q.isAspectWrappedException( unwrappedException ) );
	// try
	// {
	// if ( !queue.offer(
	// unwrappedException,
	// 3,
	// TimeUnit.SECONDS ) )
	// {
	// Q.bug( "failed to add exception in queue" );
	// }
	// }
	// catch ( InterruptedException e )
	// {
	// Q.rethrow( e );
	// }
	// }
	// finally
	// {
	// S.exit();
	// }
	// }
	
	
	private final Runnable	qpRun						= new Runnable()
														{
															
															@SuppressWarnings( "synthetic-access" )
															@Override
															public void run()// this is QP aka QueueProcessor
															{
																// System.out.println( "this is inside queue processor" );
																final Boolean prevVal =
																	ThrowWrapper.useAlternateExceptionReportMethod.get();
																// .booleanValue();
																assert !prevVal.booleanValue();
																ThrowWrapper.useAlternateExceptionReportMethod
																	.set( A.BOOL_TRUE );
																try {
																	// Q.thro( new RuntimeException(
																	// "for fun" ) );
																	// System.err.println( "in QP: prev="
																	// + prevVal );
																	assert !( prevVal.booleanValue() );
																	
																	assert E.inEDTNow();
																	assert !( R.isRecursionDetectedForCurrentThread() );
																	lockQP.lock();
																	try {
																		assert !( isQPRunning );
																		isQPRunning = true;
																		assert ( isQPScheduledToRun );
																		isQPScheduledToRun = false;
																		
																		
																		// lockQueue.lock();
																		// try
																		// {
																		// System.out.println( "I'm INNNNNNNNNNNNNNNNNN" );
																		// Q.info( "for kicks" );
																		// Q.thro( new RuntimeException(
																		// "wtw" ) );
																		do {
																			// System.out.println( "size:"
																			// + queueFIFO.size() );
																			// assert !( queue.size() > 1 );
																			if ( queueFIFO.size() <= 0 ) {
																				Q
																					.bug( "this should not have been queued in EDT since previous queued one drained all queue items" );
																			}
																			final QueuedItem qi = queueFIFO.peek();// do not
																													// remove
																													// yet, only
																													// after
																													// successfully
																													// added/shown
																													// below
																			assert Q.nn( qi );
																			// System.out.println( "Processing: "
																			// + qi );
																			final Throwable uwEx = qi.getEx();
																			assert Q.nn( uwEx );// cannot be null
																			// really
																			// assert !(
																			// Q.isAspectWrappedException(
																			// uwEx ) );// but
																			// it
																			// can
																			// be
																			// Q.thro
																			// exception
																			StateOfAnException state;
																			
																			switch ( qi.getType() ) {
																			case ADD_EXCEPTION:
																				// uwEx.printStackTrace();
																				if ( alreadyExists( uwEx ) ) {
																					Q
																						.bug( "the exception we caught wasn't new, shouldn't happen; shouldn't already exist in tree. Must investigate! ex:"
																							+ uwEx );
																				}
																				// this will put the causes first in the
																				// list, so the
																				// exception
																				// is
																				// last
																				// TODO: make this threadlocal static
																				final ListOfUniqueNonNullObjects<Throwable> reverseOrdered =
																					new ListOfUniqueNonNullObjects<Throwable>();
																				Throwable parser = uwEx;
																				while ( null != parser ) {
																					if ( reverseOrdered.addObjectAtPosition(
																						Position.FIRST,
																						parser ) ) {
																						Q.bug( "couldn't have already existed" );
																					}
																					parser = parser.getCause();
																				}
																				parser =
																					reverseOrdered.getObjectAt( Position.FIRST );
																				Throwable prevParser = null;// TreeOfExceptions.getRootException();
																				assert Q.nn( parser );
																				Throwable next;
																				
																				while ( null != parser ) {
																					next =
																						reverseOrdered.getObjectAt(
																							Position.AFTER,
																							parser );
																					final boolean existsAlready =
																						alreadyExists( parser );
																					if ( !existsAlready ) {
																						// doesn't exist
																						// make anew
																						if ( null != next )// hasNext?
																											// yes:
																						{
																							// wasn't last aka wasn't
																							// the new exception
																							state =
																								StateOfAnException.CAUSE_WHICH_WAS_UNCAUGHT_BY_ASPECTJ;
																						} else {
																							// was last so it's the new
																							// exception
																							state = StateOfAnException.DEFAULT;
																						}
																						// NodeForTreeOfExceptions
																						// addedOne =
																						addNewExceptionUnderParent( parser,// new
																							state,// its
																									// state
																							prevParser// existing
																										// parent
																										// or
																										// null
																										// for
																										// no
																										// parent
																										// (aka
																										// root
																										// then)
																						);
																						// XXX:TreeOfCalls.getTreeOfCalls().addException(
																						// addedOne );
																						// System.out.println( addedOne
																						// );
																					}
																					// get the parent `parser` for this
																					// cause `prevParser`
																					prevParser = parser;
																					assert ( reverseOrdered
																						.containsObject( parser ) );
																					parser = next;
																				}
																				// XXX:TreeOfCalls.getTreeOfCalls().notifyThatTreeStructureChanged();
																				notifyThatTreeStructureChangedToUpdateGUI();
																				if ( !isVisible() ) {
																					System.err
																						.println( "~~~~~~~~~~~~~~~~~~~~~~~~~ToE not visible, showing on console~~~~~~~~~~~~~~~~~~~~~~~~~begin" );
																					uwEx.printStackTrace();
																					System.err.println( "~~~~~~~~~~~~~~~~end" );
																					// if (
																					// SwingUtilities.isEventDispatchThread()
																					// )
																					// {
																					// // then it will quite possibly
																					// not be seen in the
																					// ToE,
																					// due to
																					// ToE
																					// being
																					// broken at
																					// this
																					// point
																					// // so we show the exception on
																					// console:
																					// // Q.rethrow( unwrappedException
																					// );
																					// Q.thro( new RuntimeException(
																					// "ToE cannot display the passed exception(because it's not visible and exception happened inside EDT)"
																					// +
																					// ", so we allow the underlaying layer to "
																					// +
																					// "show this one and the passed one on console instead",
																					// unwrappedException ) );
																					// }
																					// else
																					// {
																					// // it's either not yet visible,
																					// or it was closed aka
																					// permanently
																					// closed
																					// and by it
																					// I
																					// mean
																					// ToE
																					// Q.thro( new RuntimeException(
																					// "not visible ToE, showing to console",
																					// unwrappedException ) );
																					// }
																				}// not visible
																				break;
																			case MARK_EXCEPTION:
																				// ThrowWrapper.useAlternateExceptionReportMethod.set(
																				// A.BOOL_FALSE );
																				// try
																				// {
																				// Q.info( "bam!" );
																				// TODO: maybe mark all parents as
																				// handled also? not yet
																				// sure
																				// since a parent can have more than
																				// 1 children
																				// because they are cited as cause for
																				// more than 1
																				// exception, so
																				// if one of those is marked as
																				// handled maybe
																				// the
																				// parents shouldn't be if they have
																				// more than 1 child,
																				// but
																				// all
																				// parent for the marked exception
																				// could maybe
																				// be
																				// marked until a parent with more than
																				// 1 child is
																				// encountered,
																				// uhm and even then maybe if later
																				// adding an
																				// exception to that it will have to not
																				// have its
																				// parents
																				// marked
																				// as handled; this talk was
																				// strictly only
																				// for:
																				// the handled state, not any other
																				state = qi.getStateForMarkAs();
																				assert Q.nn( state );
																				NodeForTreeOfExceptions existingNode =
																					new NodeForTreeOfExceptions(
																						uwEx,
																						StateOfAnException.INVALID_STATE );
																				assert Q.nn( existingNode );
																				existingNode =
																					treeSource.getUserObject( existingNode );
																				assert null != existingNode :
																				// if ( null == existingNode ) {
																				Q
																					.badCall( "the exception should've already existed in tree by the time this call is made" );
																				// return;// XXX: ignored if didn't exist to
																				// // mark it
																				// }
																				// if ( null != existingNode ) {
																				existingNode.setState( state );
																				assert ( existingNode.getState() == state );
																				boolean strucChanged = false;
																				if ( filterTreeActive ) {
																					if ( !isValidForFilterTree( existingNode ) ) {
																						// we need to remove it and
																						// all it's
																						// children
																						// from
																						// filterTree
																						// filterTreeSource.removeSubTree(
																						// existingNode
																						// );
																						filterTreeSource.clearAllExceptRoot();
																						rebuildFilterTree();
																						// mustNotify =
																						// true;
																						notifyThatTreeStructureChangedToUpdateGUI();
																						strucChanged = true;
																					}
																				}
																				
																				if ( !strucChanged ) {
																					if ( isVisible() ) {
																						assert ( E.inEDTNow() );
																						repaint();
																					}
																					
																				}// else struct changed thus repaint
																					// is being done
																					// by
																					// that
																				
																				// XXX:TreeOfCalls.getTreeOfCalls().repaint();
																				// if ( filterTreeActive )
																				// {
																				// filterTreeSource.clearAllExceptRoot();
																				// rebuildFilterTree();
																				// }
																				// }
																				// finally
																				// {
																				// ThrowWrapper.useAlternateExceptionReportMethod.set(
																				// A.BOOL_TRUE );
																				// }
																				// }// else didn't exist but we ignore that
																				break;
																			
																			default:
																				Q
																					.bug( "you forgot to add the case for the newly added type!" );
																				break;
																			}
																			
																			
																			// if all went well THEN and only THEN remove
																			// that from queue, if
																			// threw
																			// then
																			// this won't be reached
																			final QueuedItem ret = queueFIFO.poll();
																			// System.out.println( "removed from queue:" + ret
																			// );
																			assert Q.nn( ret );// clearly
																								// not
																								// null
																			condQueueNotFull.signal();
																			if ( queueFIFO.remainingCapacity() >= ( queueCapacity / 2 ) ) {// if
																																			// at
																																			// least
																																			// half
																																			// empty
																																			// then
																																			// allow
																																			// pending
																																			// threads
																																			// waiting
																																			// on
																																			// the
																																			// lock,
																																			// to
																																			// add,
																																			// basically
																																			// if
																																			// there
																																			// are
																																			// queueCapacity/2
																																			// active
																																			// threads
																																			// this
																																			// should
																																			// not
																																			// fill
																																			// up
																																			// the
																																			// queue,
																																			// assuming
																																			// all
																																			// locked
																																			// on
																																			// the
																																			// lock
																																			// waiting
																																			// for
																																			// it(and
																																			// thus
																																			// all
																																			// wanting
																																			// to
																																			// add
																																			// one)
																				try {
																					lockQP.unlock();
																					// temporarily allow pending morons to
																					// fill up the queue
																				} finally {
																					lockQP.lock();
																				}
																			}
																		} while ( queueFIFO.peek() != null );
																		// isScheduledToRunOrIsRunning_QP =
																		// A.BOOL_FALSE;
																		// System.out.println( "I'm OUUUUUUUUUUUUUUT" );
																	} finally {
																		try {
																			isQPRunning = false;
																		} finally {
																			lockQP.unlock();
																		}
																	}
																} finally {
																	ThrowWrapper.useAlternateExceptionReportMethod
																		.set( prevVal );// A.BOOL_FALSE );//
																	// System.out.println( "queue processor finished" );
																}
																// }
																// finally
																// {
																// try
																// {
																// condNotProcessing.signalAll();
																// }
																// finally
																// {
																// lockQueue.unlock();
																// }
																// }
															}
															
														};
	
	private boolean			alreadyAddedShutdownHook	= false;
	
	
	/**
	 * don't block/wait
	 */
	private// synchronized
			void addThis_and_makeSureQueueProcessorWillRun( final QueuedItem thisQI ) {
		assert Q.nn( thisQI );
		
		// if ( queueFIFO.remainingCapacity() <= 0 ) {
		// try {
		// // FIXME: use lock Condition
		// System.out.println( "sleeping before add so it won't fail too soon, inEDT=" + E.inEDTNow() );
		// Thread.sleep( 1000 );// XXX: waiting for queue processor but not wait too much, it may be stuck forever
		// } catch ( final InterruptedException e ) {
		// throw Q.rethrow( e );
		// }
		// }
		lockQP.lock();
		try {
			if ( !alreadyAddedShutdownHook ) {// TODO: maybe move this somewhere such that it's exec-d only once w/o IF-ing
				alreadyAddedShutdownHook = true;
				Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
				{
					
					@Override
					public void run() {
						popQueue();
					}
				}, "popQueueThrd" ) );
			}
			// System.out.println( queueFIFO.size()
			// + "/"
			// + queueFIFO.remainingCapacity()
			// + " / "
			// + thisQI.getEx() );
			// boolean threwInsideQP =
			// // actually this could be only due to a Q.info() so it doesn't break QP
			// ( isQPRunning.booleanValue() )
			// && ( E.inEDTNow() );
			// try
			// {
			// if ( !E.inEDTNow() )
			// {
			// // not in EDT then we can limit other threads from filling up the queue by allowing them to fill it only
			// // half, if queue is already half full they will have to wait for queue to become less filled
			// // but they only wait if QP is already running OR scheduled to run, 'cause otherwise the wait will never be
			// // signaled
			// while ( queueFIFO.remainingCapacity() <= queueCapacity / 2 )
			// {
			// // isQP running && in EDT then we threw inside QP, we must never wait
			// // isQP running but not inside EDT? then another thread (not EDT) threw and attempts to add/mark
			// // exception
			// // so it's ok to wait
			// // if isQP is not running but it is scheduled to run soon, this means we can wait for it will soon run
			// // and
			// // signal us
			// // however if is not running and is not scheduled, then we should bug fail
			// if ( ( !isQPRunning.booleanValue() )
			// && ( !isQPScheduledToRun.booleanValue() ) )
			// {
			// Q.bug( "should not be able to reach this state, where capacity left is half or under half and "
			// + "there is no scheduled or running QP" );
			// }
			// // if ( isQPRunning.booleanValue() )
			// // {
			// // // due to the lock, this means a throw happened inside QP
			// // assert E.inEDTNow();
			// // Q.bug(
			// // "queue was full and we threw inside QueueProcessor, so we cannot add/handle the current exception"
			// // + " which needed to be added to queue" );
			// // }
			// // if queue is full then that part which is doing the signaling and emptying must be queued to soon run
			// // or
			// // already running, actually if it's already running we cannot enter this lockQP block, so we are here
			// // only
			// // when QP is scheduled to run but did not yet begin running, so we can wait
			// // ( isQPScheduledToRun.booleanValue() );
			// // || isQPRunning.booleanValue() );
			// // false if the waiting time detectably elapsed before return from the method, else true
			// assert !( E.inEDTNow() );// we can't wait while in EDT, it will block forever (that is 5 secs =) )
			// ( condQueueNotFull.await(
			// // this will release lockQP while waiting
			// 5,// 5 sec timeout
			// TimeUnit.SECONDS ) );
			// // basically the QP should remove at least 1 exception per 5 seconds, else this will throw
			// }// while
			// }// if
			// else
			// {// we're inside EDT and queue is full? then throw, else move on and add
			// if ( queueFIFO.remainingCapacity() <= 0 )
			// {
			// Q.bug( "this should not happen, unless EDT added so many exceptions that were thrown inside it "
			// + "that it filled the queue, considering that other threads were waiting if the queue was "
			// + "less than half empty, this means EDT added at least half a queue of exceptions and filled it" );
			// }// else it's ok queue is not full yey
			// }
			
			
			while ( queueFIFO.remainingCapacity() <= 0 ) {// while full
				boolean ret = false;// time elapsed
				try {
					ret = condQueueNotFull.await( QUEUEFULL_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS );
				} catch ( final InterruptedException e ) {
					// ignore
					ret = true;// got interrupted, we pretend that queue isn't full
				}
				if ( !ret ) {
					Q.bug( "time wasn't supposed to elapse waiting for queueFIFO to get not-full" );
				}
			}
			
			if ( queueFIFO.remainingCapacity() <= 0 ) {
				try {
					if ( thisQI.getType().equals( EnumQueueProcessorTypes.ADD_EXCEPTION ) ) {
						Q.rethrow( "the exception that was meant to be in tree (is as cause):", thisQI.getEx() );
					}
				} finally {
					Q.bug( "queueFIFO is at full capacity! this should not happen, unless(which is usually what is:)"
						+ " EDT added so many exceptions that were thrown inside it "
						+ "that it filled the queue, considering that other threads were waiting if the queue was "
						+ "less than half empty, this means EDT added at least half a queue of exceptions and filled it\n"
						+ "queueSize now=" + queueFIFO.size() );
				}
			}// else it's ok queue is not full yey
			
			// true if this collection changed as a result of the call
			final boolean ret = queueFIFO.add( thisQI );
			assert ret;
			// System.out.println( "added to queue: " + thisQI );
			// ,
			// 5,// 5 sec timeout
			// TimeUnit.SECONDS ) );
			// }
			// catch ( InterruptedException e )
			// {
			// Q.rethrow( e );
			// }
			
			
			if ( ( ( !isQPScheduledToRun ) && ( !isQPRunning ) ) )// || ( threwInsideQP ) )
			{
				assert Q.nn( qpRun );
				E.addToQueue( qpRun );// can't wait, it will deadlock on lockSA in two diff threads: EDT and ExHandler...
				isQPScheduledToRun = true;
				// System.out.println( "scheduled queue processor to run" );
			}
		} finally {
			try {
				lastModif = System.currentTimeMillis();
			} finally {
				lockQP.unlock();
			}
		}
		
		
	}
	
	
	/**
	 * @param unwrappedException
	 *            should be already unwrapped
	 */
	public// synchronized// never synchronized, it will deadlock!
			void addException( final Throwable unwrappedException ) {
		final Boolean prev = ThrowWrapper.useAlternateExceptionReportMethod.get();
		try {
			if ( prev.booleanValue() ) {
				Q.insideToE_Exception_reportOnConsole( unwrappedException );
				return;
			}
			assert !prev.booleanValue();
			ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_TRUE );
			// S.entry();
			// // System.out.println( ( (Object)unwrappedException ).toString() );
			// // unwrappedException.printStackTrace();
			// // lockSerializedAccess.lock();
			// try
			// {
			// Q.info( "addEx" );
			assert !( R.isRecursionDetectedForCurrentThread() ); // this still allows two diff
			// threads to
			// enter this
			// ( isVisible() );
			// lockQueue.lock();
			// try
			// {
			// boolean timeDidNotElapse =
			// true;
			// while ( ( queue.remainingCapacity() == 0 )
			// && ( !timeDidNotElapse ) )
			// {
			// timeDidNotElapse =
			// condNotProcessing.await(
			// 10,
			// TimeUnit.SECONDS );
			// }
			// if ( !timeDidNotElapse )
			// {
			// Q.bug( "possibly deadlock detected" );
			// }
			addThis_and_makeSureQueueProcessorWillRun( new QueuedItem(
				EnumQueueProcessorTypes.ADD_EXCEPTION,
				unwrappedException,
				StateOfAnException.INVALID_STATE ) );// don't block
			// ( queue.offer(
			// unwrappedException,
			// 10,
			// TimeUnit.SECONDS ) );
			// }
			// finally
			// {
			// lockQueue.unlock();
			// }
			// add( unwrappedException ) );// true means collection changed
			// System.out.println( queue.size() );
			// makeSureQueueProcessorWillRun();// do not block
			// }
			// // catch ( InterruptedException e )
			// // {
			// // Q.rethrow( e );
			// // }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// S.exit();
			// }
			// outside of sync block!
		} finally {
			ThrowWrapper.useAlternateExceptionReportMethod.set( prev );
		}
	}
	
	
	//
	// private synchronized
	// void
	// add2Queue(
	// QueuedItem qi )
	// {
	// assert Q.nn( qi );
	// try
	// {
	// ( queueFIFO.offer(
	// qi,
	// 10,
	// TimeUnit.SECONDS ) );
	// }
	// catch ( InterruptedException e )
	// {
	// Q.rethrow( e );
	// }
	// }
	
	
	/**
	 * @param newChildException
	 * @param itsState
	 * @param existingParent
	 *            if null then root is assumed
	 */
	private NodeForTreeOfExceptions addNewExceptionUnderParent( final Throwable newChildException,
																final StateOfAnException itsState,
																final Throwable existingParent ) {
		L.tryLock( synchronizedReplacerLock );
		try {
			// ====================
			
			
			// lockSerializedAccess.lock();
			assert E.inEDTNow();
			assert !( R.isRecursionDetectedForCurrentThread() );
			assert Q.nn( newChildException );
			assert Q.nn( itsState );
			assert !( alreadyExists( newChildException ) );
			
			NodeForTreeOfExceptions existingParentNode;
			if ( null == existingParent ) {
				existingParentNode = null;// implies == root
			} else {
				existingParentNode = new NodeForTreeOfExceptions( existingParent,
				// the state is ignored when comparing two
				// NodeForTreeOfExceptions
					StateOfAnException.INVALID_STATE );
				assert ( alreadyExists( existingParentNode.getException() ) );// not needed redundant check
			}
			
			final NodeForTreeOfExceptions newChildNode = new NodeForTreeOfExceptions( newChildException, itsState );
			assert Q.nn( newChildNode );
			
			final MethodReturnsForTree ret = treeSource.addChildInParentAtPos( newChildNode, existingParentNode,// can be null
																												// to
				// signify
				// root
				// aka no parent
				Position.LAST );
			if ( filterTreeActive ) {
				// if filterTree is active also update it
				if ( ( null == existingParentNode )// meaning root
					|| ( filterTreeSource.hasUserObject( existingParentNode ) ) ) {
					
					// }
					// NodeForTreeOfExceptions realParent =
					// null == existingParentNode
					// ? null
					// : filterTreeSource.getUserObject( existingParentNode );
					// if ( null != realParent )
					// {
					// we have this parent in our filterTree then we can add this child
					addToFilterTree(
					// filterTreeSource.addChildInParentAtPos(
						newChildNode,
						existingParentNode,
						Position.LAST );
				}
			}
			
			assert Q.nn( ret );
			assert ( MethodReturnsForTree.SUCCESS_AND_DIDNT_ALREADY_EXIST == ret ) : Q.bug( "unexpected return: `" + ret + "`" );
			// }
			return newChildNode;
			
			// ====================
		} finally {
			synchronizedReplacerLock.unlock();
		}
	}
	
	
	// public// never synchronized, it will deadlock!
	// void
	// notifyThatStuffWasAddedToTreeToUpdateGUI()
	// {
	// // synchronized ( toeLock )
	// // {
	// S.entry();
	// lockSerializedAccess.lock();
	// try
	// {
	// // System.out.println( "in notify" );
	// assert Q.nn( model );
	// // jtree.clearSelection();
	// model.notifyThatTreeStructureChanged();
	// // System.out.println( "out of notify" );
	// }
	// finally
	// {
	// lockSerializedAccess.unlock();
	// S.exit();
	// }
	// // }
	// }
	
	
	
	// private final Runnable nttsctuguiRun =
	// null;
	
	
	/**
	 * 
	 */
	public// never synchronized, it will deadlock!
			void notifyThatTreeStructureChangedToUpdateGUI() {
		L.tryLock( synchronizedReplacerLock );
		try {
			// ====================
			
			
			// S.entry();
			// // lockSerializedAccess.lock();
			// try
			// {
			assert E.inEDTNow();
			assert !( R.isRecursionDetectedForCurrentThread() );// aka current method called within
			// itself = false
			// if ( null == nttsctuguiRun )
			// {
			// nttsctuguiRun =
			// new Runnable()
			// {
			//
			// @SuppressWarnings( "synthetic-access" )
			// @Override
			// public
			// void
			// run()
			// {
			// assert E.inEDTNow();
			// ( SwingUtilities.isEventDispatchThread() );
			assert Q.nn( model );
			
			
			// jtree.clearSelection();
			// if ( treeChanged )
			// {
			//
			// // jtree.is
			// // jtree.clearSelection();
			//
			// }
			assert Q.nn( jtree );
			final TreePath current = jtree.getSelectionPath();
			// System.out.println( "Current selection: "
			// + current );
			model.notifyThatTreeStructureChanged();// this will clear the selection for sure
			if ( null != current ) {
				// System.out.println( jtree.isCollapsed( current )
				// || jtree.isExpanded( current ) );
				jtree.setSelectionPath( current );// restore whatever was selected
				// jtree.isP
			}
			// if ( treeChanged )
			// {
			// assert Q.nn( current );
			// assert Q.nn( jtree );
			// TreePath current =
			// jtree.getSelectionPath();
			// if ( null != current )
			// {
			// // System.out.println( jtree.isCollapsed( current )
			// // || jtree.isExpanded( current ) );
			// jtree.setSelectionPath( current );
			// // jtree.isP
			// }
			// }
			// }
			// };
			// }
			// assert Q.nn( nttsctuguiRun );
			// EDT.addToQueue( nttsctuguiRun );
			// SwingUtilities.invokeLater( nttsctuguiRun );
			// }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// S.exit();
			// }
			// }
			
			
			
			// ====================
		} finally {
			synchronizedReplacerLock.unlock();
		}
	}
	
	
	
	private final Runnable	rSecondBlock;
	
	
	/**
	 * constructor
	 */
	private TreeOfExceptions() {
		assert ( ThrowWrapper.useAlternateExceptionReportMethod.get().booleanValue() );
		// S.entry();
		// lockSerializedAccess.lock();// redundant , but otherwise would be assuming it's always called from the static
		// singleton
		// getter
		// try
		// {
		// assert !( SwingUtilities.isEventDispatchThread() );it can be in EDT but mostly it isn't
		assert !( R.isRecursionDetectedForCurrentThread() );
		assert ( null == singleton ) : Q.badCall( "must not init this more than once!" );
		// if ( null != singleton )
		// // || ( null != singletonFirstTimeInit ) )
		// {
		
		// } else {
		assert ( this.getClass() == TreeOfExceptions.class ) : Q.badCall( "must not subclass this" );
		// if
		
		// {
		
		// }
		// }
		
		rootNode1 = new NodeForTreeOfExceptions( new ToERoot(), StateOfAnException.ROOT );
		treeSource = new TreeOfNonNullUniques<NodeForTreeOfExceptions>( rootNode1 );
		model = new SwingTreeModelForTreeOfExceptions( treeSource );
		
		// boolExcludeHandled =
		// true;
		// boolExcludeInfo =
		// true;
		// boolExcludeWarn =
		// false;
		
		// must not wait for this!:
		
		// (null == rSecondBlock );
		rSecondBlock = new Runnable()
		{
			
			@SuppressWarnings( "synthetic-access" )
			@Override
			public void run() {
				// ( ThrowWrapper.useAlternateExceptionReportMethod.get().booleanValue() );
				final Boolean prev = ThrowWrapper.useAlternateExceptionReportMethod.get();
				assert !prev.booleanValue();
				try {
					ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_TRUE );
					if ( removeSecBlkFromQueue ) {
						removeSecBlkFromQueue = false;
						System.err.println( "ignored rSecondBlock that was queued" );
						return;
						// ( queuedSecondBlock );
					}
					// initStarted =
					// true;
					lockTVC.lock();
					try {
						// ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_TRUE );
						// Q.info( "thrown while inside constructor (sorta) of ToE, remove me" );
						// S.entry();
						// // lockSerializedAccess.lock();// a must
						// try
						// {
						assert E.inEDTNow();
						assert !( R.isRecursionDetectedForCurrentThread() );
						// ( SwingUtilities.isEventDispatchThread() );
						jtree = new JTree();
						
						
						assert Q.nn( model );
						jtree.setModel( model );
						jtree.setCellRenderer( new CellRendererForTreeOfExceptions() );
						jtree.setShowsRootHandles( true );
						jtree.setEditable( false );
						jtree.setRootVisible( true );
						jtree.putClientProperty( "JTree.lineStyle", "Angled" );
						jtree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
						final JFrame frame = DebugGUI.getDebugJFrame();
						jtree.addTreeSelectionListener( new TreeSelectionListener()
						{
							
							private NodeForTreeOfExceptions	lastProcessedOne	= null;
							
							
							@Override
							public void valueChanged( final TreeSelectionEvent e ) {
								// FIXME: see why is this triggered also after adding elements to tree, until then using
								// workaround via lastProcessedOne
								
								// System.out.println( "valueChanged=" + e );
								// S.entry();
								// // lockSerializedAccess.lock();
								// try
								// {
								assert E.inEDTNow();
								assert !( R.isRecursionDetectedForCurrentThread() );
								// ( SwingUtilities.isEventDispatchThread() );
								
								final NodeForTreeOfExceptions node =
									( (NodeForTreeOfExceptions)jtree.getLastSelectedPathComponent() );
								if ( null != node ) {// yes it can be null when ie. parent's child is selected and you
														// collapse
														// parent
									assert Q.nn( node );
									if ( filterTreeActive ) {
										if ( !filterTreeSource.hasUserObject( node ) )// doesn't exist
										{
											return;
										}
									}
									if ( lastProcessedOne != null ) {
										if ( ( !showExAgain )
											&& ( Z.equalsWithExactSameClassTypes_enforceNotNull( lastProcessedOne, node ) ) ) {
											return;// already processed/shown on console
										}
									}
									lastProcessedOne = node;
									if ( showExAgain ) {
										showExAgain = false;
									}
									final Throwable t = node.getException();
									assert Q.nn( t );
									System.err.println( "=x===============================================" );
									System.err.println( t.getClass().getName() );
									System.err.println( t.getMessage() );
									// if ( null != t )
									// {
									final StackTraceElement[] stea = t.getStackTrace();
									StackTraceElement ste;
									boolean gotAtLeastANormalOne = false;
									boolean gotAtLeastAJUnitOne = false;
									for ( final StackTraceElement element : stea ) {
										// TODO: correct the line numbers that get messed up due to
										// using
										// aspectj hook
										// on
										// calls, yeah they still do
										ste = element;
										// if ( ( filterOutAspectFromExceptions.isSelected() )
										// && ( AspectJ.isAspectInnerMethod( ste.getMethodName() ) ) ) {
										// continue;
										// }
										if ( ( filterOutJUnitFromExceptions.isSelected() )
											&& ( ste.toString().startsWith( "org.junit." ) ) ) {
											if ( !gotAtLeastAJUnitOne ) {
												gotAtLeastAJUnitOne = true;
											}
											if ( gotAtLeastANormalOne ) {
												break;
											} else {
												continue;
											}
										}
										if ( ste.getLineNumber() <= 1 )// $AjcClosure##.run(file.java:1)
										{
											continue;
										}
										System.err.println( ste );
										if ( !gotAtLeastANormalOne ) {
											gotAtLeastANormalOne = true;
										}
									}
									// }
									System.err.println( "===============================================x" );
								}
								// }
								// finally
								// {
								// // lockSerializedAccess.unlock();
								// S.exit();
								// }
							}
						} );
						
						final JScrollPane treePane = new JScrollPane( jtree );
						// pane.setViewportView( tree );
						treePane.setMinimumSize( new Dimension( 300, 250 ) );
						final JSplitPane splitPane = DebugGUI.getDebugSplitPane();
						splitPane.add( treePane );
						
						frame.add( splitPane, BorderLayout.CENTER );
						frame.addWindowListener( new WindowAdapter()
						{
							
							/*
							 * (non-Javadoc)
							 * 
							 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
							 */
							@Override
							public synchronized void windowClosing( final WindowEvent e ) {
								// check if this conflicts with the getToE method & inits-apparently it doesn't
								// dome: add a timer and prevent&cancel/delay closing if last added
								// exception
								// was lt 3 secs ago
								// S.entry();
								lockTVC.lock();
								try {
									assert E.inEDTNow();// must be!
									assert !( R.isRecursionDetectedForCurrentThread() );
									// ( SwingUtilities.isEventDispatchThread() );// must be!
									if ( wasShutdownOnce ) {// XXX: we can click close button twice and get here after close
										System.err
											.println( "Ignored windowClosing EDT event since ToE was already shutdown..." );
										return;
									}
									if ( ( System.currentTimeMillis() - lastModif ) <= allowCloseAfter ) {
										System.err
											.println( "Cannot allow close yet, tree was modified recently "
												+ "enough for you to possibly miss a new addition, check tree and retry a bit later" );
										return;
									}
									assert isVisible() :
									// if ( !isVisible() ) {
									Q.bug( "should be visible otherwise how did this close event get here?!" );
									// }
									
									assert ( isVisible() );
									assert !( wasShutdownOnce );
									toeGUIVisible = false;
									wasShutdownOnce = true;
									assert !( isVisible() );
									assert ( wasShutdownOnce );
									// ThrowWrapper
									// ( SwingUtilities.isEventDispatchThread() );
									assert E.inEDTNow();
									// System.out.println(
									// "attempting dispose() from within Event Dispatch Thread so main and other threads "
									// + "shouldn't be affected if still running" );
									// System.exit( 0 );// prevent any immature system exit while program
									// isn't
									// done
									// DebugGUI.getDebugJFrame().setVisible(
									// false );
									DebugGUI.getDebugJFrame().dispose();// we dispose even if in junit,
																		// though
																		// junit does kill
																		// it anyway
																		// System.out.println(
																		// "disposed ToE, isInsideJUnit="
									// + JUnitHooker.isInsideJUnit() );
									
									if ( JUnitHooker.isInsideJUnit() ) {// we're inside a JUnit run
																		// however the waiter may or may not be already waiting,
																		// if it's
																		// not, it
																		// won't be
																		// reached until the current lock is released and then
																		// it will not
																		// wait
																		// because
																		// isVisible() returns false
																		// but this is for the case it was already waiting, it
																		// will stop
																		// waiting
																		// which will make
																		// junit continue and auto system exit or something
																		// System.err.println(
																		// "we're in a JUnit run, notifying the waiter which is in main thread waiting, "
																		// + "to not wait anymore" );
										condIsShutDown.signalAll();
										// as I said, if it's not already waiting, after we release the
										// lockSerializedAccess
										// from here, it will check isVisible() before waiting so it won't
										// wait
									}
								} finally {
									lockTVC.unlock();
								}
							}
						} );
						
						// frame.setLayout( new BoxLayout(
						// frame.getContentPane(),
						// BoxLayout.Y_AXIS ) );
						final JToolBar toolBar = new JToolBar( "drag meeeh" );
						// toolBar.setMinimumSize( new Dimension(
						// 300,
						// 20 ) );
						// toolBar.setMaximumSize( new Dimension(
						// 300,
						// 50 ) );
						
						
						
						// filterOutAspectFromExceptions = new JCheckBox( "filterOutAspectFromExceptions" );
						filterOutJUnitFromExceptions = new JCheckBox( "filterOutJUnitFromExceptions" );
						// toolBar.add( filterOutAspectFromExceptions );
						toolBar.add( filterOutJUnitFromExceptions );
						
						excludeInfo = new JCheckBox( "excludeInfo" );
						excludeWarn = new JCheckBox( "excludeWarn" );
						excludeHandled = new JCheckBox( "excludeHandled" );
						toolBar.add( excludeInfo );
						toolBar.add( excludeWarn );
						toolBar.add( excludeHandled );
						
						// filterOutAspectFromExceptions.setSelected( false );// default for this checkbox
						if ( JUnitHooker.isInsideJUnit() ) {
							filterOutJUnitFromExceptions.setSelected( true );// default for this checkbox
						}
						
						final ActionListener showExceptionAgain = new ActionListener()
						{
							
							@Override
							public void actionPerformed( final ActionEvent actionEvent ) {
								// S.entry();
								// // lockSerializedAccess.lock();
								// try
								// {
								// (
								// SwingUtilities.isEventDispatchThread()
								// );
								assert E.inEDTNow();
								assert !( R.isRecursionDetectedForCurrentThread() );
								showExAgain = true;
								final TreePath current = jtree.getSelectionPath();
								jtree.clearSelection();
								jtree.setSelectionPath( current );
								// }
								// finally
								// {
								// // lockSerializedAccess.unlock();
								// S.exit();
								// }
								// S.exit();
							}
						};
						// filterOutAspectFromExceptions.addActionListener( showExceptionAgain );
						filterOutJUnitFromExceptions.addActionListener( showExceptionAgain );
						final ActionListener commonExcludeAction = new ActionListener()
						{
							
							@Override
							public void actionPerformed( final ActionEvent actionEvent ) {
								// S.entry();
								// System.out.println(
								// "commonExcludeAction" );
								// TreeOfExceptions.lockSerializedAccess.lock();
								// try
								// {
								assert E.inEDTNow();
								// (
								// SwingUtilities.isEventDispatchThread()
								// );
								assert !( R.isRecursionDetectedForCurrentThread() );
								boolExcludeHandled = excludeHandled.isSelected();
								boolExcludeInfo = excludeInfo.isSelected();
								boolExcludeWarn = excludeWarn.isSelected();
								final boolean shouldBeActiveFilterTree =
									boolExcludeHandled || boolExcludeInfo || boolExcludeWarn;
								// ||
								// getTreeOfExceptions().excludeInfo.isSelected()
								// ||
								// getTreeOfExceptions().excludeWarn.isSelected();
								if ( shouldBeActiveFilterTree != filterTreeActive ) {// state change
									assert Q.nn( model );
									// need to enable or
									// disable
									// the filter
									// tree
									if ( !filterTreeActive ) {
										// it's not active
										// yet,
										// we must
										// activate it
										if ( null == filterTreeSource ) {
											// one time init
											assert Q.nn( treeSource );
											filterTreeSource =
												new TreeOfNonNullUniques<NodeForTreeOfExceptions>(
													treeSource.getRootUserObject() );
										}
										// now must copy all
										// from treeSource
										// into filterTree
										// and also apply
										// that
										// filtering
										assert Q.nn( filterTreeSource );
										rebuildFilterTree();
										model.changeTreeSourceWithoutNotify( filterTreeSource );
									} else {
										// it was active,
										// must
										// deactivate it
										// by switching to
										// our primary tree
										assert Q.nn( treeSource );
										model.changeTreeSourceWithoutNotify( treeSource );
										filterTreeSource.clearAllExceptRoot();
									}
									filterTreeActive = shouldBeActiveFilterTree;
								} else {// just one of the flags
										// changed, but w/o
										// changing
										// filter state to
										// deactivate so we
										// rebuild
									if ( filterTreeActive ) {
										filterTreeSource.clearAllExceptRoot();
										rebuildFilterTree();
									}
								}
								// if we're in this method
								// then
								// GUI needs to
								// update because
								// tree changed contents
								// anyway
								notifyThatTreeStructureChangedToUpdateGUI();
								// }
								// finally
								// {
								// //
								// TreeOfExceptions.lockSerializedAccess.unlock();
								// S.exit();
								// }
								
							}
						};
						// FilterTreeActionListener.getFilterTreeActionListener(
						// model,
						// getTreeOfExceptions() );
						excludeInfo.addActionListener( commonExcludeAction );
						excludeWarn.addActionListener( commonExcludeAction );
						excludeHandled.addActionListener( commonExcludeAction );
						excludeHandled.setSelected( boolExcludeHandled );
						excludeInfo.setSelected( boolExcludeInfo );
						excludeWarn.setSelected( boolExcludeWarn );
						
						frame.add( toolBar, BorderLayout.PAGE_END );
						frame.setAutoRequestFocus( true );
						assert E.inEDTNow();
						// ( SwingUtilities.isEventDispatchThread() );// else the following
						// would
						// maybe deadlock
						
						
						lockTVC.lock();
						try {
							frame.setVisible( true );// apparently this or the following, will cause a wait
														// to
							// happen
							// System.out.println( "doing click" );
							// excludeHandled.doClick( 4000 ); // OR:
							// System.out.println( "done click" );
							commonExcludeAction.actionPerformed( null );
							
							// ( SwingUtilities.isEventDispatchThread() );
							assert E.inEDTNow();
							assert !( isVisible() );
							if ( JUnitHooker.isInsideJUnit() ) {
								// this hook automatically missed JUnitStarts and possibly some
								// JUnitTestClassStarts and
								// JUnitTestClassEnds
								// but it won't miss the JUnitEnds which is what we're after
								// even though the following will be run in a different thread than
								// current(=EDT) ie. in main,
								// it
								// will not run
								// before we exit this current method due to the held lock that both methods
								// are
								// using
								JUnitHooker.addJUnitListener( new JUnitEndsAdapter()
								{
									
									@Override
									public void JUnitEnds() {
										// S.entry();
										lockTVC.lock();
										try {
											assert !( E.inEDTNow() );
											assert !( R.isRecursionDetectedForCurrentThread() );
											// assert !(
											// SwingUtilities.isEventDispatchThread() );
											// System.out.println(
											// "JUnit: going to wait for ToE to get active now..."
											// );
											// do
											// {
											// try
											// {
											// condIsActivated.await();
											// }
											// catch ( InterruptedException e )
											// {
											// Q.rethrow( e );
											// }
											// }
											// while ( !TreeOfExceptions.isActivated() );
											
											// : the ToE can be not visible here if we close
											// it
											// before JUnit ends ie. long time
											// junit execution
											// ( TreeOfExceptions.isVisible()
											// );//
											// not possible due to the lock
											// is not visible if already closed before
											// reaching
											// end of junit test runs, so we
											// don't
											// wait, but if it is visible we wait, since we
											// hold
											// the lock, nothing else can set
											// it
											// not visible until after we call the .await()
											// which then can be signalling it and
											// we
											// get out of it perfectly
											// System.out.println( "JUnit: reports all tests completed..." );
											while ( isVisible() ) {
												// System.out.println(
												// "JUnit: going to wait for ToE to close now..." );
												try {
													condIsShutDown.await();
												} catch ( final InterruptedException e ) {
													// System.err.println(
													// "JUnit: done waiting, for ToE to close! got interrupted"
													// );
													Q.rethrow( e );
												}
												// System.out.println( "JUnit: done waiting, ToE closed!" );
											}
											assert ( wasShutdownOnce );
											assert !( isVisible() );
											// if (!wasShutdownOnce) {
											System.err.println( "JUnit: ends" );
										} finally {
											lockTVC.unlock();
											// S.exit();
										}
									}// JUnitEnds
								} );// listener
								
							}// only when was a junit run
							
							// lockSecondBlock.lock();
							// try
							// {
							assert !( wasShutdownOnce );
							assert !( isVisible() );
							toeGUIVisible = true;// first
							assert ( isVisible() );
							assert !( wasShutdownOnce );
							mustInitSecondBlock = false;// second 'cause must be isVisible first
							condIsVisible.signalAll();
							// }
							// finally
							// {
							// lockSecondBlock.unlock();
							// }
							// System.out.println( "ToE active..." );
						} finally {
							lockTVC.unlock();// TODO: check if lockTVC should replace lockQP in QP
						}
						// }
						// finally
						// {
						// // lockSerializedAccess.unlock();
						// S.exit();
						// }
					} finally {
						// try
						// {
						// ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_FALSE );
						// }
						// finally
						// {
						lockTVC.unlock();
						// }
					}// try/lock
				} finally {
					ThrowWrapper.useAlternateExceptionReportMethod.set( prev );
				}
			}// run
		};// new Runnable
			// }
		// finally
		// {
		// // lockSerializedAccess.unlock();
		// S.exit();
		// }
	}
	
	
	// private Runnable rbFTrun =
	// null;
	
	
	private void rebuildFilterTree() {
		L.tryLock( synchronizedReplacerLock );
		try {
			// ====================
			
			
			// S.entry();
			// // lockSerializedAccess.lock();
			// try
			// {
			assert !( R.isRecursionDetectedForCurrentThread() );// aka current method called within
			// itself = false
			// if ( null == rbFTrun )
			// {
			// rbFTrun =
			// new Runnable()
			// {
			//
			// @SuppressWarnings( "synthetic-access" )
			// @Override
			// public
			// void
			// run()
			// {
			assert E.inEDTNow();
			// ( SwingUtilities.isEventDispatchThread() );
			// filterTreeSource.clear();
			assert ( filterTreeSource.size() == 0 );// root remained though!
			internal_DeepClone( null );// aka root initially
			// }
			// };
			//
			// }
			// assert Q.nn( rbFTrun );
			// SwingUtilities.invokeLater( rbFTrun );
			// }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// S.exit();
			// }
			
			
			// ====================
		} finally {
			synchronizedReplacerLock.unlock();
		}
	}
	
	
	/**
	 * XXX:if a parent is excluded then all it's children are also excluded ie. they don't appear, even if they don't
	 * satisfy the filter conditions, this shouldn't be fixed though<br>
	 * 
	 * @param parentObject
	 */
	private void internal_DeepClone( final NodeForTreeOfExceptions parentObject ) {
		L.tryLock( synchronizedReplacerLock );
		try {
			// ====================
			
			// S.entry();
			// // lockSerializedAccess.lock();
			// try
			// {
			// System.err.println( "deepclone begins" );
			assert E.inEDTNow();
			final int childCount = treeSource.getChildCount0( parentObject );
			int count = 0;
			while ( count < childCount ) {
				final NodeForTreeOfExceptions child = treeSource.getChildAtIndex( parentObject, count );
				count++;
				if ( addToFilterTree( child, parentObject, Position.LAST ) ) {
					internal_DeepClone( child );
				}
			}
			// System.err.println( "deepclone ends" );
			// }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// S.exit();
			// }
			
			// ====================
		} finally {
			synchronizedReplacerLock.unlock();
		}
	}
	
	
	/**
	 * @param child
	 * @param parent
	 * @param pos
	 * @return true if added; false if not
	 */
	private boolean addToFilterTree( final NodeForTreeOfExceptions child, final NodeForTreeOfExceptions parent,
										final Position pos ) {
		L.tryLock( synchronizedReplacerLock );
		try {
			// ====================
			
			
			// S.entry();
			// // lockSerializedAccess.lock();
			// try
			// {
			assert E.inEDTNow();
			assert !( R.isRecursionDetectedForCurrentThread() );
			
			assert Q.nn( child );
			if ( isValidForFilterTree( child ) ) {
				// System.err.println( "Adding child:"
				// + child
				// + " in parent:"
				// + parent );
				// so we add it:
				final MethodReturnsForTree tempRet = filterTreeSource.addChildInParentAtPos( child, parent, pos );
				assert ( MethodReturnsForTree.SUCCESS_AND_DIDNT_ALREADY_EXIST == tempRet );
				return true;
			}
			return false;
			// }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// S.exit();
			// }
			
			
			
			// ====================
		} finally {
			synchronizedReplacerLock.unlock();
		}
	}
	
	
	private boolean isValidForFilterTree( final NodeForTreeOfExceptions item ) {
		L.tryLock( synchronizedReplacerLock );
		try {
			// ====================
			
			
			// S.entry();
			// // lockSerializedAccess.lock();
			// try
			// {
			assert !( R.isRecursionDetectedForCurrentThread() );
			assert E.inEDTNow();
			// ( SwingUtilities.isEventDispatchThread() );can be in EDT or not, both can be
			// XXX: try to not access these from anywhere other than EDT ie. mirror those in boolean vars instead! - done
			assert Q.nn( item );
			final StateOfAnException itemState = item.getState();
			assert ( itemState != StateOfAnException.INVALID_STATE );
			if ( boolExcludeHandled && ( itemState == StateOfAnException.HANDLED ) ) {
				return false;
			}
			if ( boolExcludeInfo && ( itemState == StateOfAnException.INFO ) ) {
				return false;
			}
			if ( boolExcludeWarn && ( itemState == StateOfAnException.WARNING ) ) {
				return false;
			}
			return true;
			// }
			// finally
			// {
			// // lockSerializedAccess.unlock();
			// S.exit();
			// }
			
			
			
			// ====================
		} finally {
			synchronizedReplacerLock.unlock();
		}
	}
	
	
	/**
	 * XXX:this is assumed to be called only in shutdown hook
	 */
	public void popQueue() {
		System.err.println( "popQueue: started ; queue size=" + queueFIFO.size() );
		// Boolean prev=ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_FALSE);
		assert !( ThrowWrapper.useAlternateExceptionReportMethod.get().booleanValue() );
		ThrowWrapper.useAlternateExceptionReportMethod.set( A.BOOL_TRUE );
		// ( wasShutdownOnce );actually it could be it was never inited
		// assert !( isVisible() );also it could fail to properly close it and the flag is still on visible
		// done: make sure it uses alternate reporting method
		lockQP.lock();
		try {
			final int size = queueFIFO.size();
			if ( size > 0 ) {
				System.err.println( "popQueue: detected " + size
					+ " unreported exceptions(including markAs ones) in queueFIFO, " + "as follows(not showing markAs):" );
				while ( queueFIFO.peek() != null ) {
					final QueuedItem qi = queueFIFO.poll();
					assert Q.nn( qi );
					if ( qi.getType().equals( EnumQueueProcessorTypes.ADD_EXCEPTION ) ) {
						// QueuedItem findMark =
						// new QueuedItem(
						// EnumQueueProcessorTypes.MARK_EXCEPTION,
						// qi.getEx(),
						// StateOfAnException.INVALID_STATE );
						// if ( !queueFIFO.contains( findMark ) )
						// {// we just don't know what kind of mark it is...
						System.err.println( "popQueue: /////////////////////\\\\\\\\\\\\\\\\\\\\ begin" );
						qi.getEx().printStackTrace();
						System.err.println( "popQueue: /////////\\\\\\\\ end" );
						// }
					}
				}
			}
			// else
			// {
			// System.err.println( "popQueue: was already empty" );
			// }
		} finally {
			lockQP.unlock();
			// System.err.println( "popQueue: finished" );
		}
	}
	
	// /**
	// * @return
	// */
	// public static
	// boolean
	// isShutDown()
	// {
	// lockSA.lock();
	// try
	// {
	// return wasShutdownOnce;
	// }
	// finally
	// {
	// lockSA.unlock();
	// }
	// }
	
	
	// /**
	// * signals that the GUI part is activated
	// */
	// private static// do not synchronized
	// void
	// setIsVisible()
	// {
	// lockSerializedAccess.lock();
	// try
	// {
	// ( SwingUtilities.isEventDispatchThread() );
	//
	// assert !( isVisible() );
	//
	//
	// if ( JUnitHooker.isInsideJUnit() )
	// {
	// // this hook automatically missed JUnitStarts and possibly some JUnitTestClassStarts and JUnitTestClassEnds
	// // but it won't miss the JUnitEnds which is what we're after
	// // even though the following will be run in a different thread than current(=EDT) ie. in main, it will not run
	// // before we exit this current method due to the held lock that both methods are using
	// JUnitHooker.addJUnitListener( new JUnitAdapter()
	// {
	//
	// @SuppressWarnings( "synthetic-access" )
	// @Override
	// public
	// void
	// JUnitEnds()
	// {
	// S.entry();
	// lockSerializedAccess.lock();
	// try
	// {
	// assert !( SwingUtilities.isEventDispatchThread() );
	// // System.out.println( "JUnit: going to wait for ToE to get active now..." );
	// // do
	// // {
	// // try
	// // {
	// // condIsActivated.await();
	// // }
	// // catch ( InterruptedException e )
	// // {
	// // Q.rethrow( e );
	// // }
	// // }
	// // while ( !TreeOfExceptions.isActivated() );
	//
	// ( TreeOfExceptions.isVisible() );// not possible due to the lock
	//
	// while ( isVisible() )
	// {
	// System.out.println( "JUnit: going to wait for ToE to close now..." );
	// try
	// {
	// condIsDeActivated.await();
	// }
	// catch ( InterruptedException e )
	// {
	// System.err.println( "JUnit: done waiting, for ToE to close! got interrupted" );
	// Q.rethrow( e );
	// }
	// System.out.println( "JUnit: done waiting, ToE closed!" );
	// }
	// }
	// finally
	// {
	// lockSerializedAccess.unlock();
	// S.exit();
	// }
	// }// JUnitEnds
	// } );// listener
	//
	// }// only when was a junit run
	//
	// assert !( isVisible() );
	// toeGUIVisible =
	// true;
	// ( isVisible() );
	// condIsActivated.signalAll();
	// }
	// finally
	// {
	// lockSerializedAccess.unlock();
	// }
	// }
	
	
	// /**
	// *
	// */
	// private static// no synchronized!
	// void
	// setNotVisibleAnymore()
	// {
	// S.entry();
	// lockSerializedAccess.lock();
	// try
	// {
	// ( SwingUtilities.isEventDispatchThread() );
	//
	// ( isVisible() );
	// toeGUIVisible =
	// false;
	// // if (TreeOfCalls.isActivated()) {
	// // TreeOfCalls.
	// // }
	// // JUnitHooker.removeJUnitListener( JUnitHackForTreeOfExceptions.getSingleton() );
	// assert !( isVisible() );
	//
	// // : see what happens when junit is still processing and we close the window!
	// if ( JUnitHooker.isInsideJUnit() )
	// {// we're inside JUnit
	// System.err.println( "we're in a JUnit run, notifying the waiter which is in main thread waiting, "
	// + "to not wait anymore" );
	// // : what if it's not already waiting, ie. you try closing tree before JUnit reached end(and so stuck on
	// // waiting)
	// condIsDeActivated.signalAll();
	// }
	// else
	// {
	// ( SwingUtilities.isEventDispatchThread() );
	// System.err.println( "attempting dispose() from within Event Dispatch Thread so main and other threads "
	// + "shouldn't be affected if still running" );
	// // System.exit( 0 );// prevent any immature system exit while program isn't done
	// // DebugGUI.getDebugJFrame().setVisible(
	// // false );
	// DebugGUI.getDebugJFrame().dispose();
	// }
	// }
	// finally
	// {
	// lockSerializedAccess.unlock();
	// S.exit();
	// }
	// }
	
	
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.simple.way.BasicShutdowner#destroy()
	// */
	// @Override
	// protected
	// void
	// destroy()
	// {
	// S.entry();
	// lockSerializedAccess.lock();
	// try
	// {
	// ( SwingUtilities.isEventDispatchThread() );
	//
	// ( TreeOfExceptions.isActivated() );
	// TreeOfExceptions.setDeactivated();
	// assert !( TreeOfExceptions.isActivated() );
	//
	// // : see what happens when junit is still processing and we close the window!
	// if ( JUnitHooker.isInsideJUnit() )
	// {// we're inside JUnit
	// System.err.println( "we're in a JUnit run, notifying the waiter which is in main thread waiting, "
	// + "to not wait anymore" );
	// // : what if it's not already waiting, ie. you try closing tree before JUnit reached end(and so stuck on
	// // waiting)
	// condIsDeActivated.signalAll();
	// }
	// else
	// {
	// ( SwingUtilities.isEventDispatchThread() );
	// System.err.println( "attempting dispose() from within Event Dispatch Thread so main and other threads "
	// + "shouldn't be affected if still running" );
	// // System.exit( 0 );// prevent any immature system exit while program isn't done
	// // DebugGUI.getDebugJFrame().setVisible(
	// // false );
	// DebugGUI.getDebugJFrame().dispose();
	// }
	// }
	// finally
	// {
	// lockSerializedAccess.unlock();
	// S.exit();
	// }
	// }
}
