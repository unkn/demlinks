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

#include "timedinput.h"
#include "pnotetrk.h"

#ifdef USING_COMMON_INPUT_BUFFER
        #include "timedmouse.cpp"
        #include "timedkeys.cpp"
#endif

volatile INPUT_TYPE gInputBuf[MAX_INPUT_EVENTS_BUFFERED];
volatile int gInputBufTail;
volatile int gInputBufPrevTail;//last Tail
int gInputBufHead;
volatile int gLostInput[kMaxInputTypes];
volatile int gInputLock;//first FIFO item is under lock or not?
enum {
        gUnLock=0,
        gLock=1
};
/*****************************************************************************/
/*INPUT_TYPE &
INPUT_TYPE::operator=(const INPUT_TYPE & source)
{
        if (&source==this)
                return *this;
        type=source.type;
        how_many=source.how_many;
        return *this;
}*/
/*****************************************************************************/
INPUT_TYPE&
INPUT_TYPE::operator=(const volatile INPUT_TYPE & source)
{
        if (&source==this)
                return *this;
        type=source.type;
        how_many=source.how_many;
        return *this;
}
/*****************************************************************************/
INPUT_TYPE &
INPUT_TYPE::operator=(const INPUT_TYPE & source)
{
        if (&source==this)
                return *this;
        type=source.type;
        how_many=source.how_many;
        return *this;
}
/*****************************************************************************/
EFunctionReturnTypes_t
RemoveNextInputFromBuffer(INPUT_TYPE *into)
{
        if (gInputBufTail != gInputBufHead) {//aka non empty buffer
                gInputLock=gLock;
                *into=gInputBuf[gInputBufHead];
                /*into->type = gInputBuf[gInputBufHead].type;
                into->how_many = gInputBuf[gInputBufHead].how_many;*/

                //unnecessary clear

                //gInputBuf[gInputBufHead]=kClearInput;

                gInputBuf[gInputBufHead].type=kNoInputType;
                gInputBuf[gInputBufHead].how_many=0;
                //end u.c.
                gInputBufHead = (gInputBufHead+1) % MAX_INPUT_EVENTS_BUFFERED;
                //remove it
                gInputLock=gUnLock;
                return kFuncOK;
        }
        return kFuncFailed;
}
/*****************************************************************************/
int
HowManyInputsInBuffer()
{
        if (gInputBufTail >= gInputBufHead)
                return gInputBufTail-gInputBufHead;
        else
                return (MAX_INPUT_EVENTS_BUFFERED-gInputBufHead)+gInputBufTail;
}
/*****************************************************************************/
bool
IsInputBufferFull()
{
        return (HowManyInputsInBuffer()==MAX_INPUT_EVENTS_BUFFERED-gInputBufHead-1);
}

/*****************************************************************************/



EFunctionReturnTypes_t
InstallTimedInput(int a_Flags, int a_KeyboardTimerFreq, int a_MouseTimerFreq)
{
        LOCK_VARIABLE(gInputBuf);
        LOCK_VARIABLE(gInputBufTail);
        LOCK_VARIABLE(gInputBufHead);
        LOCK_VARIABLE(gLostInput);
        LOCK_VARIABLE(gInputBufPrevTail);
        LOCK_VARIABLE(gInputLock);
        LOCK_FUNCTION(ToCommonBuf);
        gInputLock=gUnLock;
        gInputBufTail=gInputBufHead=gInputBufPrevTail=0;
        for (int i=0;i<kMaxInputTypes;i++) {
                gLostInput[i]=0;
        }//for
        gInputBuf[gInputBufPrevTail].type=kNoInputType;

        ERR_IF(!InstallTimedKeyboard(a_Flags, a_KeyboardTimerFreq)
                ,return kFuncFailed;
                );
        ERR_IF(!InstallTimedMouse(a_Flags, a_MouseTimerFreq)
                ,return kFuncFailed;
                );

        return kFuncOK;
}

/*****************************************************************************/
void
UnInstallTimedInput()
{
        UnInstallTimedMouse();
        UnInstallTimedKeyboard();
}

/*****************************************************************************/
inline void
ToCommonBuf(int input_type)
{
        //if we have gInputLock==gLock we're in the process of removing one 
        //input and this input may be the last so we cannot increment as it 
        //could be lost
        //now add to common aka input buf:
              //if unlocked and same type at tail just increment
        if ((gInputLock==gUnLock)&&(gInputBufHead!=gInputBufTail)
                        &&(gInputBuf[gInputBufPrevTail].type == input_type)) {
                //no need to add a new item, just increment the one
                //already there since it's <mouse type>
                gInputBuf[gInputBufPrevTail].how_many++;
                //we just said there's one more mouse[!] input 2b read
        } else {
                //if locked even if have same type at tail just add another one
                //we'd like to add a new one if input buf not full:
                //for that we'll calc next pos for Tail
                int tmp_input_tail=
                        (gInputBufTail +1) % MAX_INPUT_EVENTS_BUFFERED;
                if (tmp_input_tail != gInputBufHead) {
                        //input buf not full yet
                        //so add one more
                        gInputBuf[gInputBufTail].type=input_type;
                        gInputBuf[gInputBufTail].how_many=1;
                        //remember that this is the last item added:
                        gInputBufPrevTail=gInputBufTail;
                        //get to the next position waiting for next one
                        gInputBufTail = tmp_input_tail;
                        //gInputBufTail is always ahead with one
                }//fi
                else gLostInput[input_type]++;
        }//fielse
} END_OF_FUNCTION(ToCommonBuf);
/*****************************************************************************/
//FIXME: if ever implementing a ClearBuf func remember to consider PrevTail var

