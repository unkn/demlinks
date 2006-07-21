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

#include "_gcdefs.h"
#include "timedinput.h"
#include "pnotetrk.h"

volatile INPUT_TYPE gInputBuf[MAX_INPUT_EVENTS_BUFFERED];
volatile int gInputBufTail;
int gInputBufHead;
volatile int gLostInput[kMaxInputTypes];
volatile mutex_t gInputLock;//first FIFO item is under lock or not?
volatile int gInputBufCount;//how many items in buffer
enum {
        gUnLock=0,
        gLock=1
};
TBaseInputInterface *AllLowLevelInputs[kMaxInputTypes];//array of pointers

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
/*****************************************************************************/
function
MoveFirstGroupFromBuffer(INPUT_TYPE *into)
{
        __tIF(HowManyDifferentInputsInBuffer() <0);

        if (HowManyDifferentInputsInBuffer() <=0) {//aka empty buffer
                _F;
        } else {//aka non empty buffer
                __tIF(into==NULL);

                //wait for lock!
                __tIF(0 != mutex_lock((mutex_t *)&gInputLock));

                *into=gInputBuf[gInputBufHead];

                SET_NEXT_ROTATION(gInputBufHead, MAX_INPUT_EVENTS_BUFFERED);
                //remove it
                gInputBufCount--;

                __tIF(0 != mutex_unlock((mutex_t *)&gInputLock));

                _OK;
        }
}
/*****************************************************************************/
int
HowManyDifferentInputsInBuffer()
{
        __tIF(gInputBufCount < 0);
        __tIF(gInputBufCount > MAX_INPUT_EVENTS_BUFFERED);
        return gInputBufCount;
}
bool
IsBufferFull()
{
        return (HowManyDifferentInputsInBuffer()==MAX_INPUT_EVENTS_BUFFERED);
}

/*****************************************************************************/

/*****************************************************************************/



function
InstallAllInputs()
{

        LOCK_VARIABLE(gInputBuf);
        LOCK_VARIABLE(gInputBufTail);
        LOCK_VARIABLE(gInputBufHead);
        LOCK_VARIABLE(gLostInput);
        LOCK_VARIABLE(gInputBufCount);//new
        LOCK_VARIABLE(gInputLock);
        LOCK_FUNCTION(ToCommonBuf);

        LOCK_FUNCTION(mutex_lock);
        LOCK_FUNCTION(mutex_trylock);
        LOCK_FUNCTION(mutex_unlock);


        __tIF(0!=mutex_init((mutex_t* )&gInputLock, NULL));//always returns 0, they say.
                
        gInputBufHead=0;
        gInputBufTail=MAX_INPUT_EVENTS_BUFFERED - 1;//zero-based index
        gInputBufCount=0;//new

        /*these (two lines) need to be changed/added by programmer if any new 
         * input interfaces are created/deleted */
        AllLowLevelInputs[kKeyboardInputType]=new MKeyboardInputInterface(kRealKeyboard);
        AllLowLevelInputs[kMouseInputType]=new MMouseInputInterface(kRealMouse);

        for (int i=0;i<kMaxInputTypes;i++) {
                gLostInput[i]=0;//because it must be locked we don't make it part of the class
                __tIF(AllLowLevelInputs[i]==NULL);
                __tIFnok(AllLowLevelInputs[i]->Install());
        }//for

        _OK;
}

/*****************************************************************************/
function
UnInstallAllInputs()
{
        for (int i=0;i<kMaxInputTypes;i++) {
                __tIFnok(AllLowLevelInputs[i]->UnInstall());
                __tIF(NULL==AllLowLevelInputs[i]);
                delete AllLowLevelInputs[i];
                AllLowLevelInputs[i]=NULL;
        }//for
        _OK;
}

/*****************************************************************************/
//used inside an int handler!
void
ToCommonBuf(int input_type)
{
        //if we have gInputLock==gLock we're in the process of removing one 
        //input and this input may be the last so we cannot increment as it 
        //could be lost
        //now add to common aka input buf:
              //if unlocked and same type at tail just increment

        if (mutex_trylock((mutex_t *)&gInputLock) == 0) {//==0 => we just locked it!
                if (gInputBufCount > 0){ //at least last elem.present
                //maybe Head=Tail when only one element present, then we cannot remove it using MoveFirstFromBuffer()!
                        if (gInputBuf[gInputBufTail].type == input_type){
                        //no need to add a new item, just increment the one
                        //already there since it's ie. <mouse type>
                                gInputBuf[gInputBufTail].how_many++;
                                ERR_IF(0!=mutex_unlock((mutex_t *)&gInputLock));
                                return;
                        //we just said there's one more mouse[!] input 2b read
                        }//fi3
                }//fi2
                ERR_IF(0!=mutex_unlock((mutex_t *)&gInputLock));
        }//fi1
                //if locked even if have same type at tail just add another one
                //we'd like to add a new one if input buf not full:
                //for that we'll calc next pos for Tail
                if (gInputBufCount < MAX_INPUT_EVENTS_BUFFERED) {//not full
                        int tmp_input_tail = NEXT_ROTATION(gInputBufTail, MAX_INPUT_EVENTS_BUFFERED);
                        //input buf not full yet
                        //so add one more
                        gInputBuf[tmp_input_tail].type=input_type;
                        gInputBuf[tmp_input_tail].how_many=1;
                        //remember that this is the last item added:
                        gInputBufTail = tmp_input_tail;//always points to last inserted!
                        gInputBufCount++;
                }//fi
                else gLostInput[input_type]++;//loosing in the wild
} END_OF_FUNCTION(ToCommonBuf);
/*****************************************************************************/

