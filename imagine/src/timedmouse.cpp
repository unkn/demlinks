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
int gMouseBufHead;
volatile int gMouseBufTail;
volatile int gMouseBufPrevTail;
volatile int gLostMouseEvents;
volatile int gMouseLock;//first FIFO item from buf is under lock
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
EFunctionReturnTypes_t
MMouseInputInterface::GetMeTime(void * const&from, GLOBAL_TIMER_TYPE *dest)
{
        PARANOID_IF(from==NULL,
                        return kFuncFailed);
        PARANOID_IF(dest==NULL,
                        return kFuncFailed);
        *dest=((MOUSE_TYPE *)from)->Time;
        return kFuncOK;
}
#endif
/*****************************************************************************/
EFunctionReturnTypes_t
MMouseInputInterface::MoveFirstFromBuffer(void *into)
{
        if (gMouseBufTail != gMouseBufHead) {//aka non empty buffer
                LAME_PROGRAMMER_IF(into==NULL,
                                return kFuncFailed);
                gMouseLock=gMLock;//lock it so if we get interrupted the int 
                //cannot modify Head of first item from buffer


                *(MOUSE_TYPE *)into=gMouseBuf[gMouseBufHead];

                gMouseBufHead = (gMouseBufHead+1) % MAX_MOUSE_EVENTS_BUFFERED;
                //remove it
                gMouseLock=gMUnLock;
                return kFuncOK;
        }
        return kFuncFailed;
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
                if ( (gMouseBufHead!=gMouseBufTail) &&//necessary for PrevTail
                        (mz==gMouseBuf[gMouseBufPrevTail].MouseZ) &&
                        (flags==gMouseBuf[gMouseBufPrevTail].Flags)
                        //&&(flags & MOUSE_FLAG_MOVE) 
                        ) {
                        gMouseBuf[gMouseBufPrevTail].MickeyX+=mikx;
                        gMouseBuf[gMouseBufPrevTail].MickeyY+=miky;
                        return;
                }//fi
        }//fi
        else {
                mikx=miky=0;
        }//else move

        int tmp_tail=(gMouseBufTail +1 ) % MAX_MOUSE_EVENTS_BUFFERED;
        if (tmp_tail != gMouseBufHead) { ///buffer not full yet
                //tail always points to the next empty space to be filled
                gMouseBuf[gMouseBufTail].MouseZ=mz;
                gMouseBuf[gMouseBufTail].MickeyX=mikx;
                gMouseBuf[gMouseBufTail].MickeyY=miky;
                gMouseBuf[gMouseBufTail].Flags=flags;
#ifdef ENABLE_TIMED_INPUT
                GLOBAL_TIMER_TYPE td;
                GLOBAL_TIMER_TYPE timenow=MOUSE_USES_THIS_TIMEVARIABLE;
                if (gLastMouseTime <= timenow) {
                        td=timenow-gLastMouseTime;
                } else {
                        td=GLOBALTIMER_WRAPSAROUND_AT-gLastMouseTime+1+timenow;
                }//else
                gMouseBuf[gMouseBufTail].TimeDiff=td;
                gMouseBuf[gMouseBufTail].Time=timenow;
                gLastMouseTime=timenow;
#endif
#ifdef USING_COMMON_INPUT_BUFFER
                ToCommonBuf(kMouseInputType);
#endif
                gMouseBufPrevTail=gMouseBufTail;
                gMouseBufTail=tmp_tail;
        }//fi
        else gLostMouseEvents++;
} END_OF_FUNCTION(MouseIntHandler);

/*****************************************************************************/
/*****************************************************************************/

EFunctionReturnTypes_t
MMouseInputInterface::UnInstall()
{
        remove_mouse();
        return kFuncOK;
}
/*****************************************************************************/

EFunctionReturnTypes_t
MMouseInputInterface::Install(const Passed_st *a_Params)
{
        gLostMouseEvents=0;
        LOCK_VARIABLE(gLostMouseEvents);

        gMouseBufTail=gMouseBufHead=gMouseBufPrevTail=0;
        LOCK_VARIABLE(gMouseBufTail);
        LOCK_VARIABLE(gMouseBufPrevTail);
        LOCK_VARIABLE(gMouseBufHead);

        LOCK_VARIABLE(gMouseBuf);
        LOCK_VARIABLE(gMouseLock);



#ifdef ENABLE_TIMED_INPUT
        LOCK_VARIABLE(gLastMouseTime);
        gLastMouseTime=MOUSE_USES_THIS_TIMEVARIABLE;
#endif
        LOCK_FUNCTION(MouseIntHandler);


        ERR_IF(install_mouse() == -1
                ,return kFuncFailed;
                );

        if (a_Params->fMouseFlags & kRealMouse)
                mouse_callback = MouseIntHandler;

        return kFuncOK;
}

int
MMouseInputInterface::HowManyInBuffer()
{
        if (gMouseBufTail >= gMouseBufHead)
                return gMouseBufTail-gMouseBufHead;
        else
                return (MAX_MOUSE_EVENTS_BUFFERED-gMouseBufHead)+gMouseBufTail;
}

bool
MMouseInputInterface::IsBufferFull()
{
        return (HowManyInBuffer()==MAX_MOUSE_EVENTS_BUFFERED-1);
}

EFunctionReturnTypes_t
MMouseInputInterface::Alloc(void *&dest)//alloc mem and set dest ptr to it
{
        dest=NULL;
        dest=new MOUSE_TYPE;
        ERR_IF(dest==NULL,
                        return kFuncFailed);
        return kFuncOK;
}


EFunctionReturnTypes_t
MMouseInputInterface::CopyContents(const void *&src,void *&dest)
{
        ERR_IF(src==NULL,
                        return kFuncFailed);
        ERR_IF(dest==NULL,
                        return kFuncFailed);
        *(MOUSE_TYPE *)dest=*(MOUSE_TYPE *)src;
        return kFuncOK;
}


EFunctionReturnTypes_t
MMouseInputInterface::DeAlloc(void *&dest)//freemem
{
        ERR_IF(dest==NULL,
                        return kFuncFailed);
        delete (MOUSE_TYPE *)dest;
        dest=NULL;
        return kFuncOK;
}

EFunctionReturnTypes_t
MMouseInputInterface::Compare(void *what, void *withwhat, int &result)
{//'what' is a ptr to MOUSE_TYPE (structure)
        //FIXME:
        if ( ((MOUSE_TYPE *)what)->Flags == ((MOUSE_TYPE *)withwhat)->Flags){
                result=0;//equal
        }//fi
        else result=-1;//less than equal


        return kFuncOK;
}

