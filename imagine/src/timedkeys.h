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

#ifndef TIMEDKEYS_H
#define TIMEDKEYS_H

#include "allegro.h"
#include "pnotetrk.h"

enum {
        kSimulatedKeyboard=0,
        kSimulatedKeyboardTimer=0,//just informative
        kRealKeyboard=1,
        kRealKeyboardTimer=2,
};

#define MAX_KEYS_BUFFERED (72) //this many keys will be held in a buffer

#define TIMER_TYPE int

#define SCANCODE_TYPE unsigned char
#define KEY_TYPE KeyWithTimer_st
struct KEY_TYPE {
        TIMER_TYPE Time;
        SCANCODE_TYPE ScanCode;//scancode of the key( with state)
        KEY_TYPE& operator=(const KEY_TYPE & source);
        KEY_TYPE& operator=(const volatile KEY_TYPE & source);
};

extern volatile bool gKeys[KEY_MAX];//keeps only presses

//lost how many different gKeys (presses and/or releases)
extern volatile int gLostKeysReleased;
extern volatile int gLostKeysPressed;
extern int gLostKeysDueToClearBuf;

#ifdef DISCREETE_CLEARENCE
extern int gKeyBufHead;
extern volatile int gKeyBufTail;
extern volatile KEY_TYPE gKeyBuf[MAX_KEYS_BUFFERED];
#endif

extern volatile TIMER_TYPE gActualKeyboardTime;


//macros:
#define RELEASE(_a_) ((_a_ | 0x80)) //set the high bit aka bit 7 the MSB one
#define PRESS(_a_) ((_a_ & 0x7f)) //clear the high bit aka bit 7 the MSB one
#define ISPRESSED(_a_) (!(_a_ & 0x80))//returns false if it's a released key
#define GETPLAINKEY(_a_) (PRESS(_a_))

//functions
/*SCANCODE_TYPE
GetPlainKey(const KEY_TYPE *what);
*/

//bool
//IsPressed(const KEY_TYPE *kb);
//true if key is down that's bit 7(last aka MSB) is NOT set.

EFunctionReturnTypes_t
RemoveNextKeyFromBuffer(KEY_TYPE *into);

int
HowManyKeysInBuffer();

void
ClearKeyBuffer();

bool
IsKeyBufferFull();

bool
IsAnyKeyHeld();

EFunctionReturnTypes_t
InstallTimedKeyboard(int a_Flags);

void
UnInstallTimedKeyboard();

const char *
GetKeyName(const KEY_TYPE *kb);


#endif
