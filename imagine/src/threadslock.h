/****************************************************************************
*
*                             dmental links
*    Copyright (C) 2006 AtKaaZ, AtKaaZ at users.sourceforge.net
*
*  ========================================================================
*
*    This program is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*  ========================================================================
*
* Description: mutex lock and threads create
*
****************************************************************************/

#ifndef THREADSLOCK_H
#define THREADSLOCK_H

//the following excerpt from Berkeley db example file TxnGuide.cpp
#include <pthread.h>
#include <unistd.h>

typedef pthread_t thread_t;
#define thread_create(thrp, attr, func, arg)                               \
    pthread_create((thrp), (attr), (func), (arg))
#define thread_join(thr, statusp) pthread_join((thr), (statusp))

typedef pthread_mutex_t mutex_t;
#define mutex_init(m, attr)     pthread_mutex_init((m), (attr)) /*"If |mutex‐attr| is !NULL!, default attributes are used instead."(man 3 pthread_mutex_lock) (default=fast mutex).*/
#define mutex_lock(m)           pthread_mutex_lock(m)
#define mutex_trylock(m)           pthread_mutex_trylock(m)
#define mutex_unlock(m)         pthread_mutex_unlock(m)
/*
 *!pthread_mutex_destroy!  destroys a mutex object, freeing the resources
       it might hold. The mutex must be unlocked on entrance.  In  the  Linux‐
       Threads implementation, no resources are associated with mutex objects,
       thus !pthread_mutex_destroy! actually does nothing except checking that
       the mutex is unlocked. */
/*HOWTO:
mutex_t mutexvar;

 // Initialize a mutex.
        (void)mutex_init(&mutexvar, NULL);
  // lock mutex
    (void)mutex_(try)lock(&mutexvar);

        //do code here; the code will be serialized

  //unlock mutex
    (void)mutex_unlock(&mutexvar);


 */

#endif
