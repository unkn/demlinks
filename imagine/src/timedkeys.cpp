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


int gKeyBufHead;//not volatile because not changed within an interrupt handler; only read
volatile int gKeyBufTail;//points to last filled!
volatile int gKeyBufCount;//how many in buffer (ge 0)

volatile KEY_TYPE gKeyBuf[MAX_KEYS_BUFFERED];//storing scancode full byte

/*****************************************************************************/
//cannot unify these 2 operator= into one because of the ifdef of ENABLE_TIMED_INPUT
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
/*
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
*/
/*****************************************************************************/
function
MKeyboardInputInterface :: MoveFirstFromBuffer(void *into)
{

        int howMany;
        __tIFnok(Query4HowManyInBuffer(howMany));

        if (howMany <= 0) {
                _F;
        } else {//aka non empty buffer
                __tIF(NULL == into);

                __( *(KEY_TYPE *)into=gKeyBuf[gKeyBufHead] );//copy contents

                SET_NEXT_ROTATION(gKeyBufHead,MAX_KEYS_BUFFERED);//remove it
                gKeyBufCount--;

                _OK;
        }
}
/*****************************************************************************/

function
MKeyboardInputInterface::Query4HowManyInBuffer(int &m_HowMany)
{
        __tIF(gKeyBufCount<0);
        __tIF(gKeyBufCount>MAX_KEYS_BUFFERED);
        __( m_HowMany = gKeyBufCount);
        _OK;
}

/*****************************************************************************/

function
MKeyboardInputInterface::Query4BufferFull(bool &m_Bool)
{
        int howMany;
        __tIFnok( Query4HowManyInBuffer(howMany) );
        __tIF(howMany > MAX_KEYS_BUFFERED);
        m_Bool = (howMany == MAX_KEYS_BUFFERED);
        _OK;
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

                        if (gKeyBufCount < MAX_KEYS_BUFFERED) {
                                //calc next pos in buf
                                //we want this in temporary tail for some reasons pertaining to locking and modification like tail is already set to next but this next isn't yet filled when we're MoveFirstFromBuffer() err that is consistency ;)
                                int tmp_tail=NEXT_ROTATION(gKeyBufTail, MAX_KEYS_BUFFERED);//(gKeyBufTail+1) % MAX_KEYS_BUFFERED;
                        //then buffer wasn't full before this
                                gKeyBuf[tmp_tail].ScanCode=a_ScanCode;
#ifdef ENABLE_TIMED_INPUT
                                //compute time diff, depending on last timer and current timer
                                GLOBAL_TIMER_TYPE td;
                                GLOBAL_TIMER_TYPE timenow=KEY_USES_THIS_TIMEVARIABLE;
                                if (gLastKeyboardTime <= timenow) {
                                        td=timenow-gLastKeyboardTime;
                                } else {//bigger? means timer did wraparound
                                        td=GLOBALTIMER_WRAPSAROUND_AT-gLastKeyboardTime+1+timenow;
                                }//else
                                gKeyBuf[tmp_tail].TimeDiff=td;
                                gKeyBuf[tmp_tail].Time=timenow;
                                gLastKeyboardTime=timenow;
#endif

                                gKeyBufTail = tmp_tail;
                                gKeyBufCount++;//you're right, we could avoid using tmp_tail because noone would MoveFirstFromBuffer unless this counter says it exists, so as u can see, we only say that after the element is fully and consistenly appended
#ifdef USING_COMMON_INPUT_BUFFER
                                ToCommonBuf(kKeyboardInputType);
#endif

                        }//fi
                        else gLostKeysPressed++;
                }//fi
        }
        else {//key was just released
                if (gKeys[which_key]) {//if was pressed and kept down already
                        gKeys[which_key]=false;//is up now

                        if (gKeyBufCount!=MAX_KEYS_BUFFERED) {
                                int tmp_tail=NEXT_ROTATION(gKeyBufTail, MAX_KEYS_BUFFERED);//(gKeyBufTail+1) % MAX_KEYS_BUFFERED;
                                gKeyBuf[tmp_tail].ScanCode=a_ScanCode;
#ifdef ENABLE_TIMED_INPUT
                                //compute time diff, depending on last timer and current timer
                                GLOBAL_TIMER_TYPE td;
                                GLOBAL_TIMER_TYPE timenow=KEY_USES_THIS_TIMEVARIABLE;
                                if (gLastKeyboardTime <= timenow) {
                                        td=timenow-gLastKeyboardTime;
                                } else {//bigger? means timer did wraparound
                                        td=GLOBALTIMER_WRAPSAROUND_AT-gLastKeyboardTime+1+timenow;
                                }//else
                                gKeyBuf[tmp_tail].TimeDiff=td;
                                gKeyBuf[tmp_tail].Time=timenow;
                                gLastKeyboardTime=timenow;
#endif

                                gKeyBufTail=tmp_tail;
                                gKeyBufCount++;
#ifdef USING_COMMON_INPUT_BUFFER
                                ToCommonBuf(kKeyboardInputType);
#endif
                        }
                        else gLostKeysReleased++;
                }//fi
        }//else
} END_OF_FUNCTION(LowLevelKeyboardInterruptHandler);


/*****************************************************************************/
function
MKeyboardInputInterface::Alloc(void *&dest)
{
        dest=NULL;//being too safe here
        __( dest=new KEY_TYPE );
        __tIF(dest==NULL);
        _OK;
}//alloc mem and set dest ptr to it

#ifdef ENABLE_TIMED_INPUT
function
MKeyboardInputInterface::GetMeTime(void * const&from, GLOBAL_TIMER_TYPE *dest)
{
        __tIF(from==NULL);
        __tIF(dest==NULL);
        *dest=((KEY_TYPE *)from)->Time;
        _OK;
}
#endif



function
MKeyboardInputInterface::DeAlloc(void *&dest)//freemem
{
        __tIF(dest==NULL);
        delete (KEY_TYPE *)dest;
        dest=NULL;
        _OK;
}

/*****************************************************************************/
function
MKeyboardInputInterface::UnInstall()
{

        if (fKeyFlags & kRealKeyboard) {
                keyboard_lowlevel_callback=NULL;
                remove_keyboard();
        }
        //FIXME: forgetting to clear the buffer here, also those by ENABLE_TIMED_INPUT
        //clearing all flags
        for (int i=0; i<KEY_MAX; i++) {
                gKeys[i]=false;//no key pressed;
        }//for


        _OK;
}

/*
void
ClearKeyBuffer()
{
        //FIXME: we might be inside int handler before gKeyBufCount=0 but after gKeyBufHead=gKeyBufTail; so it messes things up!
        gLostKeysDueToClearBuf+=AllLowLevelInputs[kKeyboardInputType]->HowManyInBuffer();
        gKeyBufHead=NEXT_ROTATION(gKeyBufTail,MAX_KEYS_BUFFERED);//head after tail => empty buffer
        gKeyBufCount=0;
}
*/

/*****************************************************************************/
function
MKeyboardInputInterface::Install()
{
//FIXME: still cannot run this twice
        gLostKeysReleased=gLostKeysPressed=0;
        gKeyBufHead=0;
        gKeyBufTail=MAX_KEYS_BUFFERED-1;//zero based index
        gKeyBufCount=0;
        gLostKeysDueToClearBuf=0;//used with ClearKeyBuffer()
        for (int i=0;i<KEY_MAX;i++) {
                //first time init
                gKeys[i]=false;
        }//for

if (fKeyFlags & kRealKeyboard) {
        LOCK_VARIABLE(gLostKeysPressed);
        LOCK_VARIABLE(gLostKeysReleased);

        LOCK_VARIABLE(gKeyBufHead);
        LOCK_VARIABLE(gKeyBufTail);

        LOCK_VARIABLE(gKeyBufCount);



#ifdef ENABLE_TIMED_INPUT
        //initial time
        gLastKeyboardTime=KEY_USES_THIS_TIMEVARIABLE;
        LOCK_VARIABLE(gLastKeyboardTime);
#endif

        LOCK_VARIABLE(gKeys);

        LOCK_VARIABLE(gKeyBuf);

        LOCK_FUNCTION(LowLevelKeyboardInterruptHandler);


                __tIF(install_keyboard() != 0);//cannot call this twice!
                keyboard_lowlevel_callback = LowLevelKeyboardInterruptHandler;
        }

        key_led_flag=0;//don't update keyboard LEDs, FIXME: apparently doesn't work in X (graphical, not console mode)

        _OK;
}

/*****************************************************************************/
const char *
GetKeyName(const KEY_TYPE *kb)
{
        return scancode_to_name(GETPLAINKEY(kb->ScanCode));
}


/*****************************************************************************/
function
MKeyboardInputInterface::Compare(void *what, void *withwhat, int &result)
{//'what' is a pointer to KEY_TYPE
        //FIXME:
        __tIF(NULL==what);
        __tIF(NULL==withwhat);
        if ( ((KEY_TYPE *)what)->ScanCode == ((KEY_TYPE *)withwhat)->ScanCode){
                result=0;//equal
        }//fi
        else result=-1;//less than equal

        _OK;
}

/*****************************************************************************/
/*****************************************************************************/
