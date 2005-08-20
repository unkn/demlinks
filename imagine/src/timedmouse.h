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

#define MOUSE_TYPE MouseWithTimer_st
//#define MOUSE_TIMER_TYPE int defined in timedinput.h

struct MOUSE_TYPE {
        MOUSE_TIMER_TYPE TimeDiff;
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
#endif

extern volatile int gLostMouseEvents;

extern volatile MOUSE_TIMER_TYPE gActualMouseTime;

enum {
        kSimulatedMouse=0,
        kSimulatedMouseTimer=0,
        kNoMouseTimer=0,

        kRealMouse=64,//for timedinput.h don't use same k values like t..keys.h
        kRealMouseTimer=128
};

class MMouseInputInterface:public TBaseInputInterface {
public:
        MMouseInputInterface(){};
        virtual ~MMouseInputInterface(){};

        virtual EFunctionReturnTypes_t
        MoveFirstFromBuffer(void *into);

        virtual int
        HowManyInBuffer();

        virtual EFunctionReturnTypes_t
        Alloc(void *&dest);//alloc mem and set dest ptr to it

        virtual EFunctionReturnTypes_t
        DeAlloc(void *&dest);//freemem

        virtual EFunctionReturnTypes_t
        CopyContents(const void *&src,void *&dest);


        virtual bool
        IsBufferFull();

        virtual EFunctionReturnTypes_t
        UnInstall();

        virtual EFunctionReturnTypes_t
        Install(const Passed_st *a_Params);

        virtual EFunctionReturnTypes_t
        Compare(void *what, void *withwhat, int &result);

};//class


#endif

