/****************************************************************************
*
*                             dmental links
*    Copyright (C) 2005 AtKaaZ, AtKaaZ at users.sourceforge.net
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
* Description:
*
****************************************************************************/

#include "_gcdefs.h"
#include <allegro.h>
#include "pnotetrk.h"
#include "timedmouse.h"

#ifdef ENABLE_TIMED_INPUT
        #ifndef MOUSE_USES_THIS_TIMEVARIABLE
                #error "please set MOUSE_USES_THIS_TIMEVARIABLE to a volatile variable to be used as a timer source for .TimeDiff(and .Time) from MOUSE_TYPE structure."
        #endif
#endif



#ifdef ENABLE_TIMED_INPUT
volatile GLOBAL_TIMER_TYPE gLastMouseTime;
#endif

volatile MOUSE_TYPE gMouseBuf[MAX_MOUSE_EVENTS_BUFFERED];
int gMouseBufHead;//points to first element to be extracted (FIFO order); there exists one only if gMouseBufCount > 0
volatile int gMouseBufTail;//points to last element filled
volatile int gLostMouseEvents;
volatile int gMouseBufCount;//how many elements in buffer
volatile mutex_t gMouseLock;//first FIFO item from buf is under lock
enum {
        gMUnLock=0,
        gMLock=1
};


//functions:
/*****************************************************************************/
int
MOUSE_TYPE::operator==(const MOUSE_TYPE &rhs)//, const MOUSE_TYPE &rhs)
{
        if  (this!=&rhs) {
                //TimeDiff is ignored
                if (Flags!=rhs.Flags)
                        return 0;//not eq
                if (MouseZ!=rhs.MouseZ)
                        return 0;
                if (MickeyX!=rhs.MickeyX)
                        return 0;
                if (MickeyY!=rhs.MickeyY)
                        return 0;
        }//fi1
        return 1;//eq
}

/*****************************************************************************/
MOUSE_TYPE&
MOUSE_TYPE::operator=(const MOUSE_TYPE & source)
{
        if (&source==this)
                return *this;
#ifdef ENABLE_TIMED_INPUT
        TimeDiff=source.TimeDiff;
        Time=source.Time;
#endif
        Flags=source.Flags;
        MouseZ=source.MouseZ;
        MickeyX=source.MickeyX;
        MickeyY=source.MickeyY;
        return *this;
}

/*****************************************************************************/
MOUSE_TYPE&
MOUSE_TYPE::operator=(const volatile MOUSE_TYPE & source)
{
        if (&source==this)
                return *this;
#ifdef ENABLE_TIMED_INPUT
        TimeDiff=source.TimeDiff;
        Time=source.Time;
#endif
        Flags=source.Flags;
        MouseZ=source.MouseZ;
        MickeyX=source.MickeyX;
        MickeyY=source.MickeyY;
        return *this;
}
/*****************************************************************************/
#ifdef ENABLE_TIMED_INPUT
function
MMouseInputInterface::GetMeTime(void * const&from, GLOBAL_TIMER_TYPE *dest)
{
        __tIF(from==NULL);
        __tIF(dest==NULL);
        *dest=((MOUSE_TYPE *)from)->Time;
        _OK;
}
#endif
/*****************************************************************************/
EFunctionReturnTypes_t
MMouseInputInterface::MoveFirstFromBuffer(void *into)
{
        __tIF(HowManyInBuffer() < 0);

        if (HowManyInBuffer()<=0) {
                _F;
        } else {//aka non empty buffer
                __tIF(into==NULL);

                //lock it so if we get interrupted the int.
                //cannot modify Head of first item from buffer
                __tIF(0 != mutex_lock((mutex_t *)&gMouseLock));

                *(MOUSE_TYPE *)into=gMouseBuf[gMouseBufHead];

                SET_NEXT_ROTATION(gMouseBufHead, MAX_MOUSE_EVENTS_BUFFERED);//remove it

                gMouseBufCount--;

                __tIF(0 != mutex_unlock((mutex_t *)&gMouseLock));

                _OK;
        }
}

/*****************************************************************************/
void MouseIntHandler(int flags)
{
        int mz=0;
        signed int mikx,miky;

        if (flags & MOUSE_FLAG_MOVE_Z) {
                mz=mouse_z;
        }//fi
        if (flags & MOUSE_FLAG_MOVE) {
                get_mouse_mickeys(&mikx,&miky);
                //prevent dups
                if (mutex_trylock((mutex_t *)&gMouseLock) == 0) {//==0 => we just locked it!
                //if (gMouseLock == gMUnLock) {
                        //not locked yet, if locked gMouseBufCount may change since the condition gMouseBufCount > 0 and until lock check(when lock check was last in this series of ifs)
                        //gMouseLock=gMLock;//prevent MoveFirstFromBuffer()
                        if ((gMouseBufCount > 0/*at least last element is present*/)&&(mz==gMouseBuf[gMouseBufTail].MouseZ)&&(flags==gMouseBuf[gMouseBufTail].Flags)) {
                                //so we do need lock in case Head==Tail and we're about to both remove and increment this element from program and from this int.handler
                                //just compute the difference
                                gMouseBuf[gMouseBufTail].MickeyX+=mikx;
                                gMouseBuf[gMouseBufTail].MickeyY+=miky;
                                //gMouseLock=gMUnLock;
                                ERR_IF(0!=mutex_unlock((mutex_t *)&gMouseLock));
                                return;
                        }//fi
                 ERR_IF(0!=mutex_unlock((mutex_t *)&gMouseLock));
                }//filock
        }//fi move
        else {//no move
                mikx=miky=0;
        }//else move

        if (gMouseBufCount < MAX_MOUSE_EVENTS_BUFFERED) { ///buffer not full yet
                //consistency reasons for tmp_tail and not directly gMouseBufTail set; see timedkeys.cpp
                int tmp_tail=NEXT_ROTATION(gMouseBufTail, MAX_MOUSE_EVENTS_BUFFERED);
                //tail always points to the next empty space to be filled
                gMouseBuf[tmp_tail].MouseZ=mz;
                gMouseBuf[tmp_tail].MickeyX=mikx;
                gMouseBuf[tmp_tail].MickeyY=miky;
                gMouseBuf[tmp_tail].Flags=flags;

#ifdef ENABLE_TIMED_INPUT
                GLOBAL_TIMER_TYPE td;
                GLOBAL_TIMER_TYPE timenow=MOUSE_USES_THIS_TIMEVARIABLE;
                if (gLastMouseTime <= timenow) {
                        td=timenow-gLastMouseTime;
                } else {
                        td=GLOBALTIMER_WRAPSAROUND_AT-gLastMouseTime+1+timenow;
                }//else
                gMouseBuf[tmp_tail].TimeDiff=td;
                gMouseBuf[tmp_tail].Time=timenow;
                gLastMouseTime=timenow;
#endif

                gMouseBufTail=tmp_tail;
                gMouseBufCount++;//FIXME: remove tmp_tail eventually, also from timedkeys.cpp;
#ifdef USING_COMMON_INPUT_BUFFER
                ToCommonBuf(kMouseInputType);
#endif
                //you're right, we could avoid using tmp_tail because noone would MoveFirstFromBuffer unless this counter says it exists, so as u can see, we only say that after the element is fully and consistenly appended
        }//fi
        else gLostMouseEvents++;
} END_OF_FUNCTION(MouseIntHandler);

/*****************************************************************************/
/*****************************************************************************/

function
MMouseInputInterface::UnInstall()
{
        if (fMouseFlags & kRealMouse) {
                mouse_callback = NULL;
                remove_mouse();
        }
        //FIXME: forgetting to clear the buffer here, also those by ENABLE_TIMED_INPUT, and other flags

        _OK;
}
/*****************************************************************************/

function
MMouseInputInterface::Install()
{

        gLostMouseEvents=0;
        gMouseBufHead=0;//head is always one element past tail when buffer is empty but we don't look at this, we use gMouseBufCount to know that the buffer is empty
        gMouseBufTail=MAX_MOUSE_EVENTS_BUFFERED-1;//zero based index
        gMouseBufCount=0;
        __tIF(0!=mutex_init((mutex_t* )&gMouseLock, NULL));//always returns 0, they say.

if (fMouseFlags & kRealMouse) {
        LOCK_VARIABLE(gLostMouseEvents);
        LOCK_VARIABLE(gMouseBufTail);
        LOCK_VARIABLE(gMouseBufHead);

        LOCK_VARIABLE(gMouseBuf);

        LOCK_VARIABLE(gMouseLock);

        LOCK_VARIABLE(gMouseBufCount);



#ifdef ENABLE_TIMED_INPUT
        LOCK_VARIABLE(gLastMouseTime);
        gLastMouseTime=MOUSE_USES_THIS_TIMEVARIABLE;
#endif
        LOCK_FUNCTION(MouseIntHandler);
        LOCK_FUNCTION(mutex_lock);
        LOCK_FUNCTION(mutex_trylock);
        LOCK_FUNCTION(mutex_unlock);



                __tIF(install_mouse() == -1);//can't call this twice! so the question is, what if we want a real mouse and an emulated one in parallel?!
                mouse_callback = MouseIntHandler;
        }

        _OK;
}

int inline
MMouseInputInterface::HowManyInBuffer()
{
        /*if (gMouseBufTail >= gMouseBufHead)
                return gMouseBufTail-gMouseBufHead;
        else
                return (MAX_MOUSE_EVENTS_BUFFERED-gMouseBufHead)+gMouseBufTail;
        */
        __tIF(gMouseBufCount < 0);
        __tIF(gMouseBufCount > MAX_MOUSE_EVENTS_BUFFERED);
        return gMouseBufCount;
}

bool inline
MMouseInputInterface::IsBufferFull()
{
        return (HowManyInBuffer() == MAX_MOUSE_EVENTS_BUFFERED);
}

function
MMouseInputInterface::Alloc(void *&dest)//alloc mem and set dest ptr to it
{
        dest=NULL;
        __( dest=new MOUSE_TYPE );
        __tIF(dest==NULL);
        _OK;
}


function
MMouseInputInterface::CopyContents(const void *&src,void *&dest)
{
        __tIF(src==NULL);
        __tIF(dest==NULL);
        *(MOUSE_TYPE *)dest=*(MOUSE_TYPE *)src;

        _OK;
}


function
MMouseInputInterface::DeAlloc(void *&dest)//freemem
{
        __tIF(dest==NULL);
        delete (MOUSE_TYPE *)dest;
        dest=NULL;
        _OK;
}

function
MMouseInputInterface::Compare(void *what, void *withwhat, int &result)
{//'what' is a ptr to MOUSE_TYPE (structure)
        if ( ((MOUSE_TYPE *)what)->Flags == ((MOUSE_TYPE *)withwhat)->Flags){
                result=0;//equal
        }//fi
        else result=-1;//less than equal


        _OK;
}

