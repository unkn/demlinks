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

#define USING_COMMON_INPUT_BUFFER //before the two includes
#include "timedmouse.h"
#include "timedkeys.h"
#include "pnotetrk.h"


#define MAX_INPUT_EVENTS_BUFFERED (MAX_MOUSE_EVENTS_BUFFERED+MAX_KEYS_BUFFERED)
#define INPUT_TYPE InputWithTimer_st
enum {
        kNoInputType=0
        ,kMouseInputType
        ,kKeyboardInputType
//last:
        ,kMaxInputTypes
};
struct INPUT_TYPE {
        int type;
        int how_many;
//public:
        //INPUT_TYPE & operator=(const INPUT_TYPE & source);
        INPUT_TYPE& operator=(const INPUT_TYPE & source);
        INPUT_TYPE& operator=(const volatile INPUT_TYPE & source);

        //volatile INPUT_TYPE & operator=(const INPUT_TYPE & source);
};



#ifdef DISCREETE_CLEARENCE
extern volatile INPUT_TYPE gInputBuf[MAX_INPUT_EVENTS_BUFFERED];
extern volatile int gInputBufTail;
extern volatile int gInputBufPrevTail;//last Tail
extern int gInputBufHead;
#endif
extern volatile int gLostInput[kMaxInputTypes];


EFunctionReturnTypes_t
RemoveNextInputFromBuffer(INPUT_TYPE *into);

int
HowManyInputsInBuffer();

bool
IsInputBufferFull();

void
UnInstallTimedInput();

EFunctionReturnTypes_t
InstallTimedInput(int a_Flags, int a_KeyboardTimerFreq, int a_MouseTimerFreq);

inline void
ToCommonBuf(int input_type);

#endif
