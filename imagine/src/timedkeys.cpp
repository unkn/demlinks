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
#include "_gcdefs.h"
#include "pnotetrk.h"
#include "timedkeys.h"

#ifdef ENABLE_TIMED_INPUT
        #ifndef KEY_USES_THIS_TIMEVARIABLE
                #error "please set KEY_USES_THIS_TIMEVARIABLE to a volatile variable to be used as a timer source for .TimeDiff(and .Time) from KEY_TYPE structure."
        #endif
#endif



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
#ifdef ENABLE_TIMED_INPUT
        TimeDiff=source.TimeDiff;
        Time=source.Time;
#endif
        ScanCode=source.ScanCode;
        return *this;
}

KEY_TYPE&
KEY_TYPE::operator=(const volatile KEY_TYPE & source)
{
        if (&source==this)
                return *this;
#ifdef ENABLE_TIMED_INPUT
        TimeDiff=source.TimeDiff;
        Time=source.Time;
#endif
        ScanCode=source.ScanCode;
        return *this;
}


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
MKeyboardInputInterface::MoveFirstFromBuffer(void *into)
{
        if (gKeyBufTail != gKeyBufHead) {//aka non empty buffer
                LAME_PROGRAMMER_IF(into==NULL,
                                return kFuncFailed);
                *(KEY_TYPE *)into=gKeyBuf[gKeyBufHead];

                gKeyBufHead = (gKeyBufHead+1) % MAX_KEYS_BUFFERED;//remove it
                return kFuncOK;
        }
        return kFuncFailed;
}
/*****************************************************************************/

int
MKeyboardInputInterface::HowManyInBuffer()
{
        if (gKeyBufTail >= gKeyBufHead)
                return gKeyBufTail-gKeyBufHead;
        else//  '0 1 2 3t 4 5h 6 7 8'   B_M=9
                return (MAX_KEYS_BUFFERED-gKeyBufHead)+gKeyBufTail;
}

/*****************************************************************************/

bool
MKeyboardInputInterface::IsBufferFull()
{
        return (HowManyInBuffer()==MAX_KEYS_BUFFERED-1);
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
#ifdef ENABLE_TIMED_INPUT
volatile GLOBAL_TIMER_TYPE gLastKeyboardTime;
#endif

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
                        gKeys[which_key]=true;//set as pressed (flag)

                        //calc next pos in buf
                        int tmp_tail=(gKeyBufTail+1) % MAX_KEYS_BUFFERED;
                        if (tmp_tail != gKeyBufHead) {
                        //then buffer wasn't full before this
                        //add one more, on gKeyBufTail pos not tmp_tail pos
                                gKeyBuf[gKeyBufTail].ScanCode=a_ScanCode;
#ifdef ENABLE_TIMED_INPUT
                                //compute time diff, depending on last timer and current timer
                                GLOBAL_TIMER_TYPE td;
                                GLOBAL_TIMER_TYPE timenow=KEY_USES_THIS_TIMEVARIABLE;
                                if (gLastKeyboardTime <= timenow) {
                                        td=timenow-gLastKeyboardTime;
                                } else {//bigger? means timer did wraparound
                                        td=GLOBALTIMER_WRAPSAROUND_AT-gLastKeyboardTime+1+timenow;
                                }//else
                                gKeyBuf[gKeyBufTail].TimeDiff=td;
                                gKeyBuf[gKeyBufTail].Time=timenow;
                                gLastKeyboardTime=timenow;
#endif

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
                        gKeys[which_key]=false;//is up now

                        int tmp_tail=(gKeyBufTail+1) % MAX_KEYS_BUFFERED;
                        if (tmp_tail != gKeyBufHead) //buffer not yet full
                        {
                                gKeyBuf[gKeyBufTail].ScanCode=a_ScanCode;
#ifdef ENABLE_TIMED_INPUT
                                //compute time diff, depending on last timer and current timer
                                GLOBAL_TIMER_TYPE td;
                                GLOBAL_TIMER_TYPE timenow=KEY_USES_THIS_TIMEVARIABLE;
                                if (gLastKeyboardTime <= timenow) {
                                        td=timenow-gLastKeyboardTime;
                                } else {//bigger? means timer did wraparound
                                        td=GLOBALTIMER_WRAPSAROUND_AT-gLastKeyboardTime+1+timenow;
                                }//else
                                gKeyBuf[gKeyBufTail].TimeDiff=td;
                                gKeyBuf[gKeyBufTail].Time=timenow;
                                gLastKeyboardTime=timenow;
#endif
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
EFunctionReturnTypes_t
MKeyboardInputInterface::Alloc(void *&dest)
{
        dest=NULL;
        dest=new KEY_TYPE;
        ERR_IF(dest==NULL,
                        return kFuncFailed);
        return kFuncOK;
}//alloc mem and set dest ptr to it

#ifdef ENABLE_TIMED_INPUT
EFunctionReturnTypes_t
MKeyboardInputInterface::GetMeTime(void * const&from, GLOBAL_TIMER_TYPE *dest)
{
        PARANOID_IF(from==NULL,
                        return kFuncFailed);
        PARANOID_IF(dest==NULL,
                        return kFuncFailed);
        *dest=((KEY_TYPE *)from)->Time;
        return kFuncOK;
}
#endif

EFunctionReturnTypes_t
MKeyboardInputInterface::CopyContents(const void *&src,void *&dest)
{
        ERR_IF(src==NULL,
                        return kFuncFailed);
        ERR_IF(dest==NULL,
                        return kFuncFailed);
        *(KEY_TYPE *)dest=*(KEY_TYPE *)src;
#ifdef ENABLE_TIMED_INPUT
        PARANOID_IF(((KEY_TYPE *)src)->TimeDiff != ((KEY_TYPE *)dest)->TimeDiff,
                        return kFuncFailed);
        PARANOID_IF(((KEY_TYPE *)src)->Time!= ((KEY_TYPE *)dest)->Time,
                        return kFuncFailed);
#endif
        PARANOID_IF(((KEY_TYPE *)src)->ScanCode != ((KEY_TYPE *)dest)->ScanCode,
                        return kFuncFailed);
        return kFuncOK;
}


EFunctionReturnTypes_t
MKeyboardInputInterface::DeAlloc(void *&dest)//freemem
{
        ERR_IF(dest==NULL,
                        return kFuncFailed);
        delete (KEY_TYPE *)dest;
        dest=NULL;
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
MKeyboardInputInterface::UnInstall()
{
        keyboard_lowlevel_callback=NULL;
        //clearing all flags
        for (int i=0; i<KEY_MAX; i++) {
                gKeys[i]=false;//no key pressed;
        }//for
        remove_keyboard();
        return kFuncOK;
}

void
ClearKeyBuffer()
{
        gLostKeysDueToClearBuf+=AllLowLevelInputs[kKeyboardInputType]->HowManyInBuffer();
        gKeyBufHead=gKeyBufTail;
}

/*****************************************************************************/
EFunctionReturnTypes_t
MKeyboardInputInterface::Install(const Passed_st *a_Params)
{
        gLostKeysReleased=gLostKeysPressed=0;
        LOCK_VARIABLE(gLostKeysPressed);
        LOCK_VARIABLE(gLostKeysReleased);

        gKeyBufHead=gKeyBufTail=0;
        LOCK_VARIABLE(gKeyBufHead);
        LOCK_VARIABLE(gKeyBufTail);

        gLostKeysDueToClearBuf=0;//used with ClearKeyBuffer()


#ifdef ENABLE_TIMED_INPUT
        //initial time
        gLastKeyboardTime=KEY_USES_THIS_TIMEVARIABLE;
        LOCK_VARIABLE(gLastKeyboardTime);
#endif

        for (int i=0;i<KEY_MAX;i++) {
                //first time init
                gKeys[i]=false;
        }//for
        LOCK_VARIABLE(gKeys);

        LOCK_VARIABLE(gKeyBuf);

        LOCK_FUNCTION(LowLevelKeyboardInterruptHandler);

        ERR_IF(install_keyboard(),
                return kFuncFailed;
              );

        if (a_Params->fKeyFlags & kRealKeyboard)
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
EFunctionReturnTypes_t
MKeyboardInputInterface::Compare(void *what, void *withwhat, int &result)
{//'what' is a pointer to KEY_TYPE
        //FIXME:
        if ( ((KEY_TYPE *)what)->ScanCode == ((KEY_TYPE *)withwhat)->ScanCode){
                result=0;//equal
        }//fi
        else result=-1;//less than equal

        return kFuncOK;
}

/*****************************************************************************/
/*****************************************************************************/
