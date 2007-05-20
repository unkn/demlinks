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

#ifndef TIMEDMOUSE_H
#define TIMEDMOUSE_H

#include "pnotetrk.h"
#include "timedinput.h"


#define MAX_MOUSE_EVENTS_BUFFERED (10)

#ifdef ENABLE_TIMED_INPUT
        #define MOUSE_TYPE MouseWithTimer_st
#else
        #define MOUSE_TYPE MouseWithoutTimer_st
#endif

struct MOUSE_TYPE {
#ifdef ENABLE_TIMED_INPUT
        GLOBAL_TIMER_TYPE TimeDiff;
        GLOBAL_TIMER_TYPE Time;
#endif
        int Flags;
        signed int MouseZ;
        signed int MickeyX;
        signed int MickeyY;
        MOUSE_TYPE& operator=(const MOUSE_TYPE & source);
        MOUSE_TYPE& operator=(const volatile MOUSE_TYPE & source);
        //MOUSE_TYPE& operator=(volatile MOUSE_TYPE & source);
        int operator==(const MOUSE_TYPE &rhs);//, const MOUSE_TYPE &rhs);
};//struct


#ifdef DISCREETE_CLEARENCE
extern int gMouseBufHead;
extern volatile int gMouseBufTail;
extern volatile MOUSE_TYPE gMouseBuf[MAX_MOUSE_EVENTS_BUFFERED];
extern volatile int gMouseBufCount;//how many elements in buffer
#endif

extern volatile int gLostMouseEvents;


enum {
        kSimulatedMouse=0,

        kRealMouse=64,//for timedinput.h don't use same k values like t..keys.h
};

class MMouseInputInterface:public TBaseInputInterface {
public:
        //constructor
        MMouseInputInterface(const int a_MouseFlags)
        {//:fMouseFlags(a_MouseFlags){}; what, C bug again ?
                fMouseFlags=a_MouseFlags;
        };
        //constructor
        MMouseInputInterface(){
                fMouseFlags=kRealMouse;
        };
        //destructor
        virtual ~MMouseInputInterface(){};

        virtual EFunctionReturnTypes_t
        MoveFirstFromBuffer(void *into);

        virtual function
        Query4HowManyInBuffer(int &m_HowMany);

        virtual function
        Alloc(void *&dest);//alloc mem and set dest ptr to it

        virtual function
        DeAlloc(void *&dest);//freemem

        virtual function
        Query4BufferFull(bool &m_Bool);

        virtual function
        UnInstall();

        virtual function
        Install();

        virtual function
        Compare(void *what, void *withwhat, int &result);

#ifdef ENABLE_TIMED_INPUT
        virtual function
        GetMeTime(void * const&from, GLOBAL_TIMER_TYPE *dest);
#endif


};//class


#endif

