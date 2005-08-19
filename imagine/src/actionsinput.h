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
#include "generichelper.h"
#include "genericinput.h"
#include "SLL.h"
#include "buffer.h"

/*****************************************************************************/
enum EnumAllAI_t{//action indexes
        kAI_Undefined=0,//index in an array so must be starting from 0
        kAI_NextSetOfValues,//temp
        kAI_QuitProgram,
        kAI_CamSlideBackward,
        kAI_CamSlideForward,
        kAI_CamSlideDown,
        kAI_CamSlideUp,
        kAI_CamSlideRight,
        kAI_CamSlideLeft,
        kAI_CamRollRight,
        kAI_CamRollLeft,
        kAI_CamPitchDown,
        kAI_CamPitchUp,
        kAI_CamTurnRight,
        kAI_CamTurnLeft,
        kAI_Aspect,
        kAI_FOV,
        kAI_Hold1KeyPress,
        kAI_Hold1KeyRelease,
        //last:
        kMaxAIs
};

#define ACTIONSINPUT_TYPE GenericHelper_st<EnumAllAI_t>
//this buffer holds the queue of requests_of_actions to be executed
extern TBuffer<ACTIONSINPUT_TYPE> ActionsInputBuffer;
/*****************************************************************************/


struct OneActionsInputTransducer_st {
        GenericSingleLinkedList_st<GENERICINPUT_TYPE> *Head;//in a list of same type inputs
        GenericSingleLinkedList_st<GENERICINPUT_TYPE> *Tail;
        int HowManySoFar;//counter, prolly just informative
        GenericSingleLinkedList_st<GENERICINPUT_TYPE> *WhosNext;//to be compared with input

        //this is set once, to be the output generinc input when combination
        //is fulfilled
        ACTIONSINPUT_TYPE Result;//trasforming SerialInputs into one
        //GenericInput which is this 'Result'
        int LostInputsBecauseTheyDidntMatch;
/******************/
        OneActionsInputTransducer_st();
/******************/
        ~OneActionsInputTransducer_st();
/******************/
        EFunctionReturnTypes_t
        Append(const GENERICINPUT_TYPE * a_Dat);
/******************/
        bool HasStarted();
/******************/
        EFunctionReturnTypes_t
        RestartIfStarted();
/******************/
        void
        Reset();
/******************/
        EFunctionReturnTypes_t
        EatThis(const GENERICINPUT_TYPE *whichgi,
                        bool reset_when_failed);
/******************/
        EFunctionReturnTypes_t
        PushToBuffer();
/******************/
        /*EFunctionReturnTypes_t
        Compare(const GENERICINPUT_TYPE *one,
                        const GENERICINPUT_TYPE *two,
                        int *result);*/
/******************/
};//struc


/*****************************************************************************/
struct AI_SLLTransducersArray_st{
        GenericSingleLinkedList_st<OneActionsInputTransducer_st> *Head;
        GenericSingleLinkedList_st<OneActionsInputTransducer_st> *Tail;
        int HowManySoFar;//counter, prolly just informative

/******************/
        AI_SLLTransducersArray_st();
/******************/
        ~AI_SLLTransducersArray_st();
/******************/
        EFunctionReturnTypes_t Append(
                        const OneActionsInputTransducer_st * a_Dat);
/******************/
};//END struct

/*****************************************************************************/
extern AI_SLLTransducersArray_st AI_StrictOrderSLL;//head, may be NULL
extern AI_SLLTransducersArray_st AI_RelaxedOrderSLL;//head -//-

/*****************************************************************************/


/*****************************************************************************/

EFunctionReturnTypes_t
TransformToActions(const GENERICINPUT_TYPE *from);
/*****************************************************************************/
EFunctionReturnTypes_t
InitActionsInput();
/*****************************************************************************/
EFunctionReturnTypes_t
DoneActionsInput();
/*****************************************************************************/



#endif

