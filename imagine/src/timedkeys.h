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
#include "timedinput.h"

enum {
        kSimulatedKeyboard=0,//don't install interrupt function
        kRealKeyboard=1,
};

#define MAX_KEYS_BUFFERED (10) //this many keys will be held in a buffer


#define SCANCODE_TYPE unsigned char

#ifdef ENABLE_TIMED_INPUT
        #define KEY_TYPE KeyWithTimer_st
#else
        #define KEY_TYPE KeyWithoutTimer_st
#endif
struct KEY_TYPE{
#ifdef ENABLE_TIMED_INPUT
        GLOBAL_TIMER_TYPE TimeDiff;
        GLOBAL_TIMER_TYPE Time;
#endif
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



//macros:
#define RELEASE(_a_) ((_a_ | 0x80)) //set the high bit aka bit 7 the MSB one
#define PRESS(_a_) ((_a_ & 0x7f)) //clear the high bit aka bit 7 the MSB one
#define ISPRESSED(_a_) (!(_a_ & 0x80))//returns false if it's a released key
#define GETPLAINKEY(_a_) (PRESS(_a_))

class MKeyboardInputInterface:public TBaseInputInterface {
public:
        MKeyboardInputInterface(){};
        virtual ~MKeyboardInputInterface(){};

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

#ifdef ENABLE_TIMED_INPUT
        virtual EFunctionReturnTypes_t
        GetMeTime(void * const &from, GLOBAL_TIMER_TYPE *dest);
#endif

};//class

void
ClearKeyBuffer();

bool
IsAnyKeyHeld();

const char *
GetKeyName(const KEY_TYPE *kb);




#endif
