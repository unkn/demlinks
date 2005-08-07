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
#include "pnotetrk.h"
#include "timedkeys.h"


#define DEFAULT_KEYBOARD_TIMER_FREQ_PER_SECOND 1000 //this is precision; direct proportional


volatile bool gKeys[KEY_MAX];//127 (0..126); true if keyheld; false if not held

//lost how many different gKeys (presses and/or releases)
volatile int gLostKeysReleased;
volatile int gLostKeysPressed;
int gLostKeysDueToClearBuf;


//if head==tail => buf empty; thus tail=head-1 => buf full; also memleak 1 elem
int gKeyBufHead;
volatile int gKeyBufTail;

volatile KEY_TYPE gKeyBuf[MAX_KEYS_BUFFERED];//storing scancode full byte

/*****************************************************************************/
KEY_TYPE&
KEY_TYPE::operator=(const KEY_TYPE & source)
{
        if (&source==this)
                return *this;
        Time=source.Time;
        ScanCode=source.ScanCode;
        return *this;
}

KEY_TYPE&
KEY_TYPE::operator=(const volatile KEY_TYPE & source)
{
        if (&source==this)
                return *this;
        Time=source.Time;
        ScanCode=source.ScanCode;
        return *this;
}


/*SCANCODE_TYPE
GetPlainKey(const KEY_TYPE *what)
{//returns scancode without state flag
        return (what->ScanCode & 0x7F);
        //remove keydown or keyup FLAG (aka state)
}
*/
/*****************************************************************************/
/*bool
IsPressed(const KEY_TYPE *kb)//returns false if it's a released key
{//true if key is down that's bit 7(last aka MSB) is NOT set.
        return (!(kb->ScanCode & 0x80));
}
*/
/*****************************************************************************/

int
WrapAround_Aware_Counter(int a_Head, int a_Tail, int a_WrapsAroundAt_MinusOne)
{//a_rapsAroundAt_MinusOne is the highest last allowable value
//dontFIXME:lowest limit is 0, highest limit it a_WrapsAroundAt_MinusOne; all inclusive;  maybe we could raise the lower limit, but so far is unnecessary!!
        //counts the number of elements between gKeyBufHead and gKeyBufTail, even if
        //gKeyBufHead > gKeyBufTail or gKeyBufTail > gKeyBufHead;  gKeyBufHead=gKeyBufTail has no elements
        //gKeyBufHead=h gKeyBufTail=t wrap=w result=r
        //ie. h1 t2 w=? => r1 
        //      h1 t1 w=? => r0
        //      h20 t19 w=29 => ie if h or t gets 30 they actually get 0
        //              29-20
        //      h21 t19 w=21 => r19
        //      0 1 2 3 h4 5 6 7 8 t9 10 11 w12 13  => r5  (13 doesn't exist)
        //      0 1 2 t3 4 5 h6 7 8 9 10 11 w12 13  => r10  (when 13 it gets 0)
        //      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 t9 h0 1 2 3 4 5 6 7 8 w9
        //      => r29
        //max counter is when t=h-1   then r=w and has one element leaked since
        //      gKeyBufHead=gKeyBufTail is considered to be empty buffer ;)
        if (a_Head <= a_Tail)
                return +(a_Tail-a_Head);
        else
                return +(1+(a_WrapsAroundAt_MinusOne-(a_Head-a_Tail)));
        //NOT anymore!the sign: if negative then tail is before head
}

/*****************************************************************************/
EFunctionReturnTypes_t
RemoveNextKeyFromBuffer(KEY_TYPE *into)
{
        if (gKeyBufTail != gKeyBufHead) {//aka non empty buffer
                *into=gKeyBuf[gKeyBufHead];
                /*into->ScanCode = gKeyBuf[gKeyBufHead].ScanCode;
                into->Time = gKeyBuf[gKeyBufHead].Time;*/
                //unnecessary clear
/*                gKeyBuf[gKeyBufHead].ScanCode=0;
                gKeyBuf[gKeyBufHead].Time=0;*/
                //end u.c.
                gKeyBufHead = (gKeyBufHead+1) % MAX_KEYS_BUFFERED;//remove it
                return kFuncOK;
        }
        return kFuncFailed;
}
/*****************************************************************************/

int
HowManyKeysInBuffer()
{
        if (gKeyBufTail >= gKeyBufHead)
                return gKeyBufTail-gKeyBufHead;
        else//  '0 1 2 3t 4 5h 6 7 8'   B_M=9
                return (MAX_KEYS_BUFFERED-gKeyBufHead)+gKeyBufTail;
}

/*****************************************************************************/

bool
IsKeyBufferFull()
{
        return (HowManyKeysInBuffer()==MAX_KEYS_BUFFERED-1);
}

/*****************************************************************************/

bool
IsAnyKeyHeld()
{
        for (int i=0;i<KEY_MAX;i++)
                if (gKeys[i])
                        return true;
        return false;
}
/*****************************************************************************/
volatile TIMER_TYPE gActualKeyboardTime;
void TimerHandler()
{
        gActualKeyboardTime++;
        //this implies a certain wrap-around at perhaps 2GB+1=-2GB
        //but it doesn't quite matter!
} END_OF_FUNCTION(TimerHandler);
/*****************************************************************************/

void LowLevelKeyboardInterruptHandler(int a_ScanCode)
{
//note key[KEY_*] doesn't work inside this so we must use ours 'gKeys[]'
        int which_key=a_ScanCode & 0x7f;
        int released=a_ScanCode & 0x80;
//notice the lack of variables here in the more global scope, we don't need 
//to bother the stack N times per second just for keeping down the same key

        if (!released) {//aka pressed
                if (!gKeys[which_key]) { //if not kept down already
                        TIMER_TYPE time_now=gActualKeyboardTime;
                        gKeys[which_key]=true;//set as pressed (flag)

                        //calc next pos in buf
                        int tmp_tail=(gKeyBufTail+1) % MAX_KEYS_BUFFERED;
                        if (tmp_tail != gKeyBufHead) {
                        //then buffer wasn't full before this
                        //add one more, on gKeyBufTail pos not tmp_tail pos
                                gKeyBuf[gKeyBufTail].ScanCode=a_ScanCode;
                                gKeyBuf[gKeyBufTail].Time=time_now;
#ifdef USING_COMMON_INPUT_BUFFER
                                ToCommonBuf(kKeyboardInputType);
#endif
                                gKeyBufTail = tmp_tail;
                        }//fi
                        else gLostKeysPressed++;
                }//fi
        }
        else {//key was just released
                if (gKeys[which_key]) {//if was pressed and kept down already
                        TIMER_TYPE time_now=gActualKeyboardTime;
                        gKeys[which_key]=false;//is up now

                        int tmp_tail=(gKeyBufTail+1) % MAX_KEYS_BUFFERED;
                        if (tmp_tail != gKeyBufHead) //buffer not yet full
                        {
                                gKeyBuf[gKeyBufTail].ScanCode=a_ScanCode;
                                gKeyBuf[gKeyBufTail].Time=time_now;
#ifdef USING_COMMON_INPUT_BUFFER
                                ToCommonBuf(kKeyboardInputType);
#endif
                                gKeyBufTail=tmp_tail;
                        }
                        else gLostKeysReleased++;
                }//fi
        }//else
} END_OF_FUNCTION(LowLevelKeyboardInterruptHandler);


/*****************************************************************************/
void
UnInstallTimedKeyboard()
{
        keyboard_lowlevel_callback=NULL;
        remove_int(TimerHandler);
        //clearing all flags
        for (int i=0; i<KEY_MAX; i++) {
                gKeys[i]=false;//no key pressed;
        }//for
        remove_keyboard();
}

void
ClearKeyBuffer()
{
        gLostKeysDueToClearBuf+=HowManyKeysInBuffer();
        gKeyBufHead=gKeyBufTail;
}

/*****************************************************************************/
EFunctionReturnTypes_t
InstallTimedKeyboard(int a_Flags, int a_KeyboardTimerFreq)
{
        gLostKeysReleased=gLostKeysPressed=0;
        LOCK_VARIABLE(gLostKeysPressed);
        LOCK_VARIABLE(gLostKeysReleased);

        gKeyBufHead=gKeyBufTail=0;
        LOCK_VARIABLE(gKeyBufHead);
        LOCK_VARIABLE(gKeyBufTail);

        gLostKeysDueToClearBuf=0;//used with ClearKeyBuffer()

        gActualKeyboardTime=0;
        LOCK_VARIABLE(gActualKeyboardTime);

        for (int i=0;i<KEY_MAX;i++) {
                //first time init
                gKeys[i]=false;
        }//for
        LOCK_VARIABLE(gKeys);

        LOCK_VARIABLE(gKeyBuf);

        LOCK_FUNCTION(LowLevelKeyboardInterruptHandler);
        LOCK_FUNCTION(TimerHandler);

        ERR_IF(install_timer(),
                return kFuncFailed;
              );

        if (a_Flags & kRealKeyboardTimer) {
                if (a_KeyboardTimerFreq <= 0)
                        a_KeyboardTimerFreq = DEFAULT_KEYBOARD_TIMER_FREQ_PER_SECOND;
                ERR_IF(install_int_ex(TimerHandler,
                                        BPS_TO_TIMER(a_KeyboardTimerFreq)),
                        return kFuncFailed;
                      );
        }//fi

        ERR_IF(install_keyboard(),
                return kFuncFailed;
              );

        if (a_Flags & kRealKeyboard)
                keyboard_lowlevel_callback = LowLevelKeyboardInterruptHandler;

        key_led_flag=0;//don't update keyboard LEDs
        return kFuncOK;
}

/*****************************************************************************/
const char *
GetKeyName(const KEY_TYPE *kb)
{
        return scancode_to_name(GETPLAINKEY(kb->ScanCode));
}


/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
