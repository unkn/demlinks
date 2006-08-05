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
* Description:.
*
****************************************************************************/

#ifndef ACTIONSINPUT_H
#define ACTIONSINPUT_H

#include "_gcdefs.h"
#include "pnotetrk.h"
#include "genericinput.h"
#include "SLL.h"
#include "buffer.h"

/*****************************************************************************/
enum EnumAllAI_t{//action indexes
        //this must be the first one and ==0
        kAI_Undefined=0,//index in an array so must be starting from 0
//all other follow:
        kAI_NextSetOfValues,//temp
        kAI_QuitProgram,
        kAI_CamRollRight_byMouse,

        kAI_CamSlideBackward,
        kAI_CamSlideBackward_stop,
        kAI_CamSlideForward,
        kAI_CamSlideForward_stop,
        kAI_CamSlideDown,
        kAI_CamSlideDown_stop,
        kAI_CamSlideUp,
        kAI_CamSlideUp_stop,
        kAI_CamSlideRight,
        kAI_CamSlideRight_stop,
        kAI_CamSlideLeft,
        kAI_CamSlideLeft_stop,
        kAI_CamRollRight,
        kAI_CamRollRight_stop,
        kAI_CamRollLeft,
        kAI_CamRollLeft_stop,
        kAI_CamPitchDown,
        kAI_CamPitchDown_stop,
        kAI_CamPitchUp,
        kAI_CamPitchUp_stop,
        kAI_CamTurnRight,
        kAI_CamTurnRight_stop,
        kAI_CamTurnLeft,
        kAI_CamTurnLeft_stop,
        kAI_Aspect,
        kAI_Aspect_stop,
        kAI_FOV,
        kAI_FOV_stop,
        kAI_Hold1Key,
        kAI_Hold1Key_stop,
        //last:
        kMaxAIs
};

#define ACTIONSINPUT_TYPE EnumAllAI_t//GenericHelper_st<EnumAllAI_t>
//this buffer holds the queue of requests_of_actions to be executed
extern TBuffer<ACTIONSINPUT_TYPE> ActionsInputBuffer;
#ifdef ENABLE_TIMED_INPUT
extern GLOBAL_TIMER_TYPE gLastActionsInputTime;
#endif
/*****************************************************************************/


//this holds a list of genericinputs that's suppose to act as a combination of inputs that when complete(WhosNext points after Tail) it would generate an actioninput Result; ie. a combination of inputs that generates one single action
//ie. transform from a group of generic inputs(GIs) into one action input(AI)
struct OneActionsInputTransducer_st {
        GenericSingleLinkedList_st<GENERICINPUT_TYPE> *Head;//in a list of same type inputs
        GenericSingleLinkedList_st<GENERICINPUT_TYPE> *Tail;
        int HowManySoFar;//counter, prolly just informative
        GenericSingleLinkedList_st<GENERICINPUT_TYPE> *WhosNext;//to be compared with input; a ptr into the list marked with Head and Tail;

        //this is set once, to be the output generic input when combination
        //is fulfilled
        ACTIONSINPUT_TYPE Result;//transforming SerialInputs into one
                                //...GenericInput which is this 'Result'
        int LostInputsBecauseTheyDidntMatch;
/******************/
        OneActionsInputTransducer_st();
/******************/
        ~OneActionsInputTransducer_st();
/******************/
        function
        Append(const GENERICINPUT_TYPE * a_Dat);
/******************/
        bool HasStarted();
/******************/
        EFunctionReturnTypes_t
        RestartIfStarted();
/******************/
        function
        Reset();
/******************/
        EFunctionReturnTypes_t
        EatThis(const GENERICINPUT_TYPE *whichgi,
                        bool reset_when_failed);
/******************/
        EFunctionReturnTypes_t
        PushToBuffer();
/******************/
/******************/
};//struc


/*****************************************************************************/
//supposed to hold a list of (generic input)combinations(that each yields an action)
struct AI_SLLTransducersArray_st{
        GenericSingleLinkedList_st<OneActionsInputTransducer_st> *Head;
        GenericSingleLinkedList_st<OneActionsInputTransducer_st> *Tail;
        int HowManySoFar;//counter, howmany combinations

/******************/
        AI_SLLTransducersArray_st();
/******************/
        ~AI_SLLTransducersArray_st();
/******************/
        function
        Append(
                        const OneActionsInputTransducer_st * a_Dat);
/******************/
};//END struct

/*****************************************************************************/
//strict order means, the combinations contained in this list, each will enable(the action) only if they are fulfilled in the order of listed generic inputs
//that is A,B,E will trigger only if A,B,E pressed in this order and not A,E,B in this order
extern AI_SLLTransducersArray_st AI_StrictOrderSLL;//head, may be NULL
//here doesn't matter the order, A,E,B will still trigger a combination listed as A,B,E
extern AI_SLLTransducersArray_st AI_RelaxedOrderSLL;//head -//-

/*****************************************************************************/


/*****************************************************************************/

function
TransformToActions(const GENERICINPUT_TYPE *from);
/*****************************************************************************/
function
InitActionsInput();
/*****************************************************************************/
function
DoneActionsInput();
/*****************************************************************************/



#endif

