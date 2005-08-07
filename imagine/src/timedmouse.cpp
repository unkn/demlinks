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

#include <allegro.h>
#include "timedmouse.h"
#include "pnotetrk.h"

#define DEFAULT_MOUSE_TIMER_FREQ_PER_SECOND (200)

volatile MOUSE_TIMER_TYPE gActualMouseTime;

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
                //Time is ignored
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
        Time=source.Time;
        Flags=source.Flags;
        MouseZ=source.MouseZ;
        MickeyX=source.MickeyX;
        MickeyY=source.MickeyY;
        return *this;
}

MOUSE_TYPE&
MOUSE_TYPE::operator=(const volatile MOUSE_TYPE & source)
{
        if (&source==this)
                return *this;
        Time=source.Time;
        Flags=source.Flags;
        MouseZ=source.MouseZ;
        MickeyX=source.MickeyX;
        MickeyY=source.MickeyY;
        return *this;
}

/*****************************************************************************/
EFunctionReturnTypes_t
RemoveNextMouseEventFromBuffer(MOUSE_TYPE *into)
{
        if (gMouseBufTail != gMouseBufHead) {//aka non empty buffer
                gMouseLock=gMLock;//lock it so if we get interrupted the int 
                //cannot modify Head of first item from buffer

                //volatile MOUSE_TYPE *t=&gMouseBuf[gMouseBufHead];
                *into=gMouseBuf[gMouseBufHead];
/*                into->Flags = t->Flags;
                into->MouseZ = t->MouseZ;
                into->MickeyX = t->MickeyX;
                into->MickeyY = t->MickeyY;
                into->Time = t->Time;*/
                //unnecessary clear
                gMouseBuf[gMouseBufHead].Flags=0;
                /*
                t->Flags=0;
                t->MouseZ=0;
                t->MickeyX=0;
                t->MickeyY=0;
                t->Time=0;*/
                //end u.c.

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
                //FIXME: if we want to cumulate movements we add mikx&miky
                //to the last entry
                if ( (gMouseBufHead!=gMouseBufTail) &&//necessary for PrevTail
                        (mz==gMouseBuf[gMouseBufPrevTail].MouseZ) &&
                        (flags==gMouseBuf[gMouseBufPrevTail].Flags) ) {
                        gMouseBuf[gMouseBufPrevTail].MickeyX+=mikx;
                        gMouseBuf[gMouseBufPrevTail].MickeyY+=miky;
                        return;
                }//fi
        }//fi
        int tmp_tail=(gMouseBufTail +1 ) % MAX_MOUSE_EVENTS_BUFFERED;
        if (tmp_tail != gMouseBufHead) { ///buffer not full yet
                //tail always points to the next empty space to be filled
                gMouseBuf[gMouseBufTail].MouseZ=mz;
                gMouseBuf[gMouseBufTail].MickeyX=mikx;
                gMouseBuf[gMouseBufTail].MickeyY=miky;
                gMouseBuf[gMouseBufTail].Flags=flags;
                gMouseBuf[gMouseBufTail].Time=gActualMouseTime;
#ifdef USING_COMMON_INPUT_BUFFER
                ToCommonBuf(kMouseInputType);
#endif
                gMouseBufPrevTail=gMouseBufTail;
                gMouseBufTail=tmp_tail;
        }//fi
        else gLostMouseEvents++;
} END_OF_FUNCTION(MouseIntHandler);

/*****************************************************************************/
void MouseTimerHandler()
{
        gActualMouseTime++;
        //this implies a certain wrap-around at perhaps 2GB+1=-2GB
        //but it doesn't quite matter!
} END_OF_FUNCTION(MouseTimerHandler);
/*****************************************************************************/

void
UnInstallTimedMouse()
{
        remove_int(MouseTimerHandler);
        remove_mouse();
}
/*****************************************************************************/

bool
InstallTimedMouse(int a_Flags, int a_MouseTimerFreq)
{
        gLostMouseEvents=0;
        LOCK_VARIABLE(gLostMouseEvents);

        gMouseBufTail=gMouseBufHead=gMouseBufPrevTail=0;
        LOCK_VARIABLE(gMouseBufTail);
        LOCK_VARIABLE(gMouseBufPrevTail);
        LOCK_VARIABLE(gMouseBufHead);

        LOCK_VARIABLE(gMouseBuf);
        LOCK_VARIABLE(gMouseLock);


        gActualMouseTime=0;
        LOCK_VARIABLE(gActualMouseTime);
        LOCK_FUNCTION(MouseTimerHandler);
        LOCK_FUNCTION(MouseIntHandler);

        ERR_IF(install_timer()
                ,return false;
              );
        if (a_Flags & kRealMouseTimer) {
                if (a_MouseTimerFreq <= 0)
                        a_MouseTimerFreq = DEFAULT_MOUSE_TIMER_FREQ_PER_SECOND;
                ERR_IF(install_int_ex(MouseTimerHandler,
                                BPS_TO_TIMER(a_MouseTimerFreq))
                        ,return false;
                      );
        }//fi

        ERR_IF(install_mouse() == -1
                ,return false;
                );
        if (a_Flags & kRealMouse)
                mouse_callback = MouseIntHandler;

        return true;
}

