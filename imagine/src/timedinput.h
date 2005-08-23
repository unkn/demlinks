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

#ifndef TIMEDINPUT_H
#define TIMEDINPUT_H
//this unit is necessary to make the diff between which type of input came
//first AKA the order of inputs ie. A, mouse_move_up, B; w/o this u'd get A,B
//of keyboard unit, then mouse_move_up on mouse unit, not knowing which input
//came first not to mention that between A and B there was the mouse motion.

#include "pnotetrk.h"
#ifdef ENABLE_TIMED_INPUT
        #include "globaltimer.h"
#endif

#ifdef ENABLE_TIMED_INPUT
#define KEY_USES_THIS_TIMEVARIABLE gTimer //gActualKeyboardTime

#define MOUSE_USES_THIS_TIMEVARIABLE gTimer //gActualMouseTime
#endif

//FIXME(in progress): there are some hacks in timed*.* files battling to keep generalization but obviously failing

enum {
        kKeyboardInputType
        ,kMouseInputType
//last:
        ,kMaxInputTypes
};

struct Passed_st {//a ptr to this struct is passed when calling Install(..)
        int fKeyFlags;
        int fMouseFlags;
};


class TBaseInputInterface {
public:
        TBaseInputInterface(){};
        virtual ~TBaseInputInterface(){};

        //removes it from buffer
        virtual EFunctionReturnTypes_t
        MoveFirstFromBuffer(void *into)=0;

        virtual int
        HowManyInBuffer()=0;

        virtual bool
        IsBufferFull()=0;

        virtual EFunctionReturnTypes_t
        UnInstall()=0;

        virtual EFunctionReturnTypes_t
        Alloc(void *&dest)=0;//alloc mem and set dest ptr to it

        virtual EFunctionReturnTypes_t
        DeAlloc(void *&dest)=0;//freemem

        virtual EFunctionReturnTypes_t
        CopyContents(const void *&src,void *&dest)=0;

        virtual EFunctionReturnTypes_t
        Install(const Passed_st *a_Params)=0;

        virtual EFunctionReturnTypes_t
        Compare(void *what, void *withwhat, int &result)=0;
        
#ifdef ENABLE_TIMED_INPUT
        virtual EFunctionReturnTypes_t
        GetMeTime(void * const &from, GLOBAL_TIMER_TYPE *dest)=0;
#endif

};//class

extern TBaseInputInterface *AllLowLevelInputs[kMaxInputTypes];//array of pointers

#define USING_COMMON_INPUT_BUFFER //anywhere since timedinput.h is included in:
#include "timedmouse.h" //for constants only
#include "timedkeys.h"


#define MAX_INPUT_EVENTS_BUFFERED (MAX_MOUSE_EVENTS_BUFFERED+MAX_KEYS_BUFFERED)
#define INPUT_TYPE InputWithTimer_st

struct INPUT_TYPE {
        int type;
        int how_many;
        INPUT_TYPE& operator=(const INPUT_TYPE & source);
        INPUT_TYPE& operator=(const volatile INPUT_TYPE & source);
};



#ifdef DISCREETE_CLEARENCE
extern volatile INPUT_TYPE gInputBuf[MAX_INPUT_EVENTS_BUFFERED];
extern volatile int gInputBufTail;
extern volatile int gInputBufPrevTail;//last Tail, used to cumulate input
extern int gInputBufHead;
#endif
extern volatile int gLostInput[kMaxInputTypes];


EFunctionReturnTypes_t
MoveFirstGroupFromBuffer(INPUT_TYPE *into);

int
HowManyDifferentInputsInBuffer();

bool
IsBufferFull();//of many different inputs? (consecutively different)

EFunctionReturnTypes_t
UnInstallAllInputs();

EFunctionReturnTypes_t
InstallAllInputs(const Passed_st *a_Params);



void
ToCommonBuf(int input_type);

#endif
