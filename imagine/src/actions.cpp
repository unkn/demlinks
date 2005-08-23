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

#include "actions.h"
#include "activefunx.h"
#include "DLL.h"

/*****************************************************************************/
ACTIONS_TYPE (*Actions)/*[kMaxAIs]*/=NULL;//a pointer to an array of ACTIONS_TYPE
EFunctionReturnTypes_t (*Functions[kMaxAIs])(void);//an array of pointers_to_functions

#define QUEUE_TYPE GenericDoubleLinkedList_st<EnumAllAI_t>
QUEUE_TYPE gQueuedActionsForExecution_DLL;


/*****************************************************************************/
EFunctionReturnTypes_t
ActionsClass::SetFunc(EnumAllAI_t a_WhichFunc)
{
        ERR_IF(!IsFuncWithinLimits(a_WhichFunc),
                        return kFuncFailed);
        fWhichFunc=a_WhichFunc;

        return kFuncOK;
}
/*****************************************************************************/
ActionsClass::ActionsClass(EnumAllAI_t a_WhichFunc)//constructor
        ://fEnabled(false),
        fType(fContinousActionType),
        fRemove(kAI_Undefined)
        //:fWhichFunc(a_WhichFunc)
{
        WARN_IF(kFuncOK!=SetFunc(a_WhichFunc),);//cannot return error here
}
/*****************************************************************************/
/*****************************************************************************/
ActionsClass::~ActionsClass()//destructor
{
}
/*****************************************************************************/
bool
ActionsClass::IsFuncWithinLimits(EnumAllAI_t thisone)
{
        if ((thisone < kMaxAIs)&&(thisone >= kAI_Undefined)) {
                if (NULL != Functions[thisone])
                        return true;
        }
        return false;
}
/*****************************************************************************/
EFunctionReturnTypes_t
ActionsClass::Execute()
{
        //FIXME:
        //each executed action must be thrown into a replay buffer
        //OR make the ActionsBuffer such that when an item gets removed is
        //saved into a replay buffer of some sort(or a file, eventually)
        //but first...
        ERR_IF(!IsFuncWithinLimits(fWhichFunc),
                        return kFuncFailed);
        ERR_IF(kFuncOK!=Functions[fWhichFunc](),
                        return kFuncFailed);
/*        if (fType==fToggleActionType) {
                ERR_IF(kFuncOK!= SetDisabled(),
                                return kFuncFailed);
        }//fi*/
        return kFuncOK;
}
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
EFunctionReturnTypes_t
ExecuteAllQueuedActions()
{
        if ( ( !gQueuedActionsForExecution_DLL.IsEmpty()))
        {//not empty?
                //got actions to be executed, good:
                //so parse all from first to last:
                GenericDoubleLinkedElement_st<EnumAllAI_t> *parser=
                        gQueuedActionsForExecution_DLL.Head;
                while (parser!=NULL) {
                        PARANOID_IF(parser->Data==NULL,
                                        return kFuncFailed);
                        //*parser->Data==0..kMaxAIs-1 aka which action is there
                        const EnumAllAI_t which=*parser->Data;
                        PARANOID_IF((which < 0) || (which >= kMaxAIs),
                                        return kFuncFailed);

                        //execute that action
                        ERR_IF(kFuncOK!=Actions[which].Execute(),
                                        return kFuncFailed);

                        //prepare ourselves prior parser's eventual death
                        GenericDoubleLinkedElement_st<EnumAllAI_t> *whosnext=parser->Next;

                        if (Actions[which].IsToggleType(/*&which*/)) {
                                //we must remove fRemove action ONLY if previously started, not if queued for start ie. after us , as a next queue element
                                ERR_IF(kFuncOK!= gQueuedActionsForExecution_DLL.RemoveByContents_StartForwardFrom_Until(
                                                        &Actions[which].fRemove,
                                                        gQueuedActionsForExecution_DLL.Head,
                                                        parser),
                                                return kFuncFailed);
                                if (which != Actions[which].fRemove) {
                                        //if the previous one (fRemove) didn't
                                        //specify us then also remove us
                                        ERR_IF(kFuncOK!=gQueuedActionsForExecution_DLL.RemoveByContents_StartForwardFrom_Until(
                                                                &which,
                                                                gQueuedActionsForExecution_DLL.Head,
                                                                parser),
                                                        return kFuncFailed);
                                }//fi
                                //either way since we're a Toggle type we 
                                //mustn't exist for the next time
                                PARANOID_IF(parser!=NULL,
                                                return kFuncFailed);
                        }//fi

                        //go next from queue
                        parser=whosnext;
                }//while
        }//if

        return kFuncOK;
}
EFunctionReturnTypes_t
QueueAction(const ACTIONSINPUT_TYPE *which)
{
        ERR_IF(which==NULL,
                        return kFuncFailed);

        //add this action into a buffer that holds actions to be later executed
        //the toggle(one-time) actions get removed from that buffer, continous ones don't; those actions that disable the continous ones get executed and removed and remove those which they disable
        //append:
        EnumAllAI_t *newone=new EnumAllAI_t;
        *newone=which->Significant;
        ERR_IF(kFuncOK!=
                gQueuedActionsForExecution_DLL.Append(newone),
                        return kFuncFailed);
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
InitActionTypes()
{
        //FIXME:Feed from file
#undef STT
#define STT(_a) \
        Actions[_a##_stop].SetToggleActionType(_a);

#undef STS
#define STS(_a) \
        Actions[_a].SetToggleActionType(_a);

        STS(kAI_NextSetOfValues);
        STS(kAI_QuitProgram);
        STS(kAI_Hold1Key);
        STS(kAI_Hold1Key_stop);
        STS(kAI_CamRollRight_byMouse);



        STT(kAI_CamSlideBackward);
        STT(kAI_CamSlideForward);
        STT(kAI_CamSlideDown);
        STT(kAI_CamSlideUp);
        STT(kAI_CamSlideRight);
        STT(kAI_CamSlideLeft);
        STT(kAI_CamRollRight);
        STT(kAI_CamRollLeft);
        STT(kAI_CamPitchDown);
        STT(kAI_CamPitchUp);
        STT(kAI_CamTurnRight);
        STT(kAI_CamTurnLeft);
        STT(kAI_Aspect);
        STT(kAI_FOV);

#undef STT
#undef STS
        return kFuncOK;
}
/*****************************************************************************/

EFunctionReturnTypes_t
InitActions()
{
        ERR_IF(kFuncOK!=InitFunctions(),
                        return kFuncFailed);
        //typedef ACTIONS_TYPE some_odd_shit[kMaxAIs];//one_of nasty workaround
        Actions=new ACTIONS_TYPE[kMaxAIs];//some_odd_shit[kMaxAIs];//alloc the entire array
        ERR_IF(NULL==Actions,
                        return kFuncFailed);
        for (int i=1;i<kMaxAIs;i++) {
                ERR_IF(kFuncOK!=Actions[i].SetFunc(EnumAllAI_t(i)),
                                return kFuncFailed);
        }//for
        ERR_IF(kFuncOK!=InitActionTypes(),
                        return kFuncFailed);
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
DoneActions()
{
        //FIXME:
        delete [] Actions;
        Actions=NULL;
        return kFuncOK;
}


/*****************************************************************************/

