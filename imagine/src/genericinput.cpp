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

#include <stdio.h>

#include "_gcdefs.h"
/*****************************************************************************/

#include "pnotetrk.h"
#include "genericinput.h"
//#include "timedinput.h"
/*****************************************************************************/
/*
EFunctionReturnTypes_t
TAnyAction::SetCombiKeyBuf(
                int a_Input2ActAction,KEY_TYPE from[],int howmany)
{
        for (int i=0;i<howmany;i++)
                Act[a_Input2ActAction].CombiKeyBuf[i]=from[i];
        return kFuncOK;
}

EFunctionReturnTypes_t
TAnyAction::SetCombiMouseBuf(
                int a_Input2ActAction,MOUSE_TYPE from[],int howmany)
{
        for (int i=0;i<howmany;i++)
                Act[a_Input2ActAction].CombiMouseBuf[i]=from[i];
        return kFuncOK;
}

EFunctionReturnTypes_t
TAnyAction::SetCombiInputBuf(
                int a_Input2ActAction,INPUT_TYPE from[],int howmany)
{
        for (int i=0;i<howmany;i++)
                Act[a_Input2ActAction].CombiInputBuf[i]=from[i];
        return kFuncOK;
}

*****************************************************************************/
EFunctionReturnTypes_t
TAnyAction::_AddInput(int which_trigger,int type)
{
        LAME_PROGRAMMER_IF(Act[which_trigger].CombiVars[kHowManyType][kInputInt]>=MAX_INPUT_EVENTS_BUFFERED,
                        return kFuncFailed;);
        PARANOID_IF(Act[which_trigger].CombiVars[kHowManyType][kInputInt]<0,
                        return kFuncFailed;);
        if ((Act[which_trigger].CombiVars[kHowManyType][kInputInt]!=0)&&
                        (Act[which_trigger].CombiInputBuf[Act[which_trigger].CombiVars[kHowManyType][kInputInt]-1].type==type)) {
                Act[which_trigger].CombiInputBuf[Act[which_trigger].CombiVars[kHowManyType][kInputInt]-1].how_many++;
PARANOID_IF(
((
  Act[which_trigger].CombiInputBuf[Act[which_trigger].CombiVars[kHowManyType][kInputInt]-1].type
        ==kKeyboardInputType)
 &&(
  Act[which_trigger].CombiInputBuf[Act[which_trigger].CombiVars[kHowManyType][kInputInt]-1].how_many 
        >MAX_KEYS_BUFFERED
))
||
((
  Act[which_trigger].CombiInputBuf[Act[which_trigger].CombiVars[kHowManyType][kInputInt]-1].type
        ==kMouseInputType)
 &&(
  Act[which_trigger].CombiInputBuf[Act[which_trigger].CombiVars[kHowManyType][kInputInt]-1].how_many 
        >MAX_MOUSE_EVENTS_BUFFERED
)), return kFuncFailed;);
        }
        else {
                Act[which_trigger].CombiInputBuf[Act[which_trigger].CombiVars[kHowManyType][kInputInt]].type=type;
                Act[which_trigger].CombiInputBuf[Act[which_trigger].CombiVars[kHowManyType][kInputInt]].how_many=1;
                Act[which_trigger].CombiVars[kHowManyType][kInputInt]++;//next ofs
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
TAnyAction::AddKey(int which_trigger, const KEY_TYPE *from)
{
        LAME_PROGRAMMER_IF(from ==NULL,
                        return kFuncFailed;);
        LAME_PROGRAMMER_IF((which_trigger>=kMaxTriggers) ||(which_trigger<0),
                        return kFuncFailed;);
        ERR_IF( kFuncOK != _AddInput(which_trigger,kKeyboardInputType),
                        return kFuncFailed;);
        PARANOID_IF(Act[which_trigger].CombiVars[kHowManyType][kInputInt]<=0,
                return kFuncFailed);
        PARANOID_IF((Act[which_trigger].CombiVars[kHowManyType][kKeyInt] < 0),
                return kFuncFailed;);
        LAME_PROGRAMMER_IF(Act[which_trigger].CombiVars[kHowManyType][kKeyInt] == MAX_KEYS_BUFFERED,
                return kFuncFailed;);

        Act[which_trigger].CombiKeyBuf[Act[which_trigger].CombiVars[kHowManyType][kKeyInt]]=*from;
        Act[which_trigger].CombiVars[kHowManyType][kKeyInt]++;
        PARANOID_IF(Act[which_trigger].CombiVars[kHowManyType][kKeyInt] > MAX_KEYS_BUFFERED,
                        return kFuncFailed;);
/*                printf("!%d ptr(%p)!\n",
                                Act[which_trigger].CombiVars[kHowManyType][kInputInt],
                                Act
                                );*/
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
TAnyAction::AddMouse(int which_trigger, const MOUSE_TYPE *from)
{
        LAME_PROGRAMMER_IF(from ==NULL,
                        return kFuncFailed;);
        LAME_PROGRAMMER_IF((which_trigger>=kMaxTriggers) ||(which_trigger<0),
                        return kFuncFailed;);
        ERR_IF( kFuncOK != _AddInput(which_trigger,kMouseInputType),
                        return kFuncFailed;);
        PARANOID_IF((Act[which_trigger].CombiVars[kHowManyType][kMouseInt] < 0),
                return kFuncFailed;);
        LAME_PROGRAMMER_IF(Act[which_trigger].CombiVars[kHowManyType][kMouseInt] == MAX_MOUSE_EVENTS_BUFFERED,
                return kFuncFailed;);

        Act[which_trigger].CombiMouseBuf[Act[which_trigger].CombiVars[kHowManyType][kMouseInt]]=*from;
        Act[which_trigger].CombiVars[kHowManyType][kMouseInt]++;
        PARANOID_IF(Act[which_trigger].CombiVars[kHowManyType][kMouseInt] > MAX_MOUSE_EVENTS_BUFFERED,
                        return kFuncFailed;);
        return kFuncOK;
}

/*****************************************************************************/
bool
TAnyAction::IsActive()
{
        return fIsActive;
}
void
TAnyAction::SetActive()
{
        WARN_IF(fIsActive,);
        fIsActive=true;
}
void
TAnyAction::SetNotActive()
{
        WARN_IF((fConstructed && !fIsActive),
                        return);//already not active
        fIsActive=false;
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
TAnyAction::~TAnyAction()
{
}

/*****************************************************************************/
void
TAnyAction::ZeroOffsets(Act_st *act)
{
        for (int i=0;i<kMax_Int;i++) {
                act->CombiVars[kOfsType][i]=0;
        }//for
        /*act->CombiVars[kOfsType][kInputInt]=0;
        act->CombiVars[kOfsType][kKeyInt]=0;
        act->CombiVars[kOfsType][kMouseInt]=0;*/
}

/*****************************************************************************/
TAnyAction::TAnyAction():
        RunAsActive(NULL),
        RunAsNotActive(NULL),
        fConstructed(false)
        //,fIsActive(false)
{
        SetNotActive();
        ClearState();
//        for (int i=0;i<kMaxTriggers;i++)
  //              PARANOID_IF(kFuncOK != EnableTrigger(i),);

/*        for (int j=0;j<kMaxTriggers;j++) {
                Act[j].CombiVars[kHowManyType][kInputInt]=0;
                Act[j].CombiVars[kHowManyType][kKeyInt]=0;
                Act[j].CombiVars[kHowManyType][kMouseInt]=0;
                for(int i=0;i<MAX_INPUT_EVENTS_BUFFERED;i++) {
                        Act[j].CombiInputBuf[i].type=kNoInputType;
                        Act[j].CombiInputBuf[i].how_many=0;
                }//for2
        }//for1 */

//last
        fConstructed=true;
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
EFunctionReturnTypes_t
TAnyAction::HandleMouse(const MOUSE_TYPE *from,Act_st *which_act,bool * result)
{
        //FIXME::
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
TAnyAction::CompareKeys(
                const KEY_TYPE *one,
                const KEY_TYPE *two,
                int *result)
{
        LAME_PROGRAMMER_IF(one==NULL,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(two==NULL,
                        return kFuncFailed);

        if (one->ScanCode==two->ScanCode)
                *result=0;//they're equal
        else
                *result=-1;//key one is less than key two (dummy)
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
TAnyAction::HandleKey(const KEY_TYPE *from,const KEY_TYPE *existing,bool * result)
{//handles only one key at a time (the on given as param)
//returns result==true if the combi was complete; false otherwise

        int arekeysequal;
        ERR_IF(kFuncOK !=
                        CompareKeys(from,
                                existing,
                                &arekeysequal),
                        return kFuncFailed);

        *result=(arekeysequal==0);//0 as in YES
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
TAnyAction::HandleInput(const int a_Type,
                        const void *from,
                        const int which_trigger,
                        bool *result)
{
//returns result==true if the combi was complete; false otherwise

        LAME_PROGRAMMER_IF(from == NULL,
                        return kFuncFailed);

        //cannot handle more than the 2 triggers since it involves bool and
        //we must change very much of the source for that
        LAME_PROGRAMMER_IF(kMaxTriggers!=2,
                        return kFuncFailed);

        LAME_PROGRAMMER_IF(which_trigger >= kMaxTriggers,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(which_trigger < 0,
                        return kFuncFailed);

        *result=false;
        Act_st *act=&Act[which_trigger];

        //activation must have a combi; deactivation is optional tho
        LAME_PROGRAMMER_IF((which_trigger==0)&&(act->CombiVars[kHowManyType][kInputInt] == 0),
                        return kFuncFailed;);
        bool input_was_recognized=false;
        int what_int;
        switch (a_Type) {
                case kKeyboardInputType: {
                        ERR_IF(kFuncOK != HandleKey((const KEY_TYPE *)from,
                                                &act->CombiKeyBuf[act->CombiVars[kOfsType][kKeyInt]],
                                                &input_was_recognized),
                                        return kFuncFailed);
                        what_int=kKeyInt;
                        break;
                }//case
                case kMouseInputType: {
                        ERR_IF(kFuncOK !=
                                        HandleMouse((const MOUSE_TYPE*)from,
                                                act,
                                                &input_was_recognized),
                                        return kFuncFailed);
                        what_int=kMouseInt;
                        break;
                }
                default:
                        LAME_PROGRAMMER(invalid input type);
                        return kFuncFailed;
        }//swi

if (input_was_recognized) {
        PARANOID_IF(kFuncOK != act->GoNext(what_int),
                        return kFuncFailed);

        //was the prev-one the last one from this part of combination?
        if (act->CombiIsThisIntDone(what_int)) {
                PARANOID_IF(kFuncOK != act->GoNextInputType(),
                        return kFuncFailed;);

                //if the entire combination complete?
                if (act->CombiCompleted()) {
                        //allrite combination is complete
                        *result=true;
                        ZeroOffsets(act);//also resets all curoffsets
                        SetStateChanged();
                        if ((RunAsNotActive!=NULL))
                        { //combi complete so, we must set active or nonactiv
                                if (which_trigger==kTrigger_ActivateAction) {
                                        SetActive();//only set active if there's means to 
                                //deactivate it
                                }
                                else {//assuming kTrigger_DeActivateAction
                                        if (IsActive()) {
                                                //we're setting it active
                                                SetNotActive();
                                        }//if
                                }//else
                        }//fi
                }//fi
                //CombiVars[kOfsType][kKeyInt]=0;NO!
        }//fi
}//fi
else {
        //since Consecutive failed we'll take it over
        //again, from start => combination failed
        *result=false;
        if (act->CombiVars[kHowManyType][what_int] > 0) {
                //we're here because we had an input which is in combination
                //as a type (ie. key or mouse) but wasn't recognized thus since
                //we're so far enforcing consecutiveness we'll reset what we
                //recognized so far so we can begin detecting a future possible
                //correct combination
                //so to give a particular example here suppose we have combi:
                //A_A~  (which means 'A pressed, A released' if we do A_ then
                //move mouse then A~ the combi will succeed since with this 'if'
                //from above we ignore the mouse movements because they don't 
                //exist in our predefined combination (A_A~).
                ZeroOffsets(act);
        }//fi
}//combi failed


        return kFuncOK;
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*TReplayBuffer::TReplayBuffer():
        Ihowmany(0),
        Khowmany(0),
        Mhowmany(0)
{
} 
TReplayBuffer::~TReplayBuffer()
{
}
EFunctionReturnTypes_t
TReplayBuffer::Add2Input(const int a_Type)
{
        //FIXME:
        return kFuncOK;
}
EFunctionReturnTypes_t
TReplayBuffer::Add2Key(const KEY_TYPE *from)
{
        //FIXME:
        return kFuncOK;
}
EFunctionReturnTypes_t
TReplayBuffer::Add2Mouse(const MOUSE_TYPE *from)
{
        //FIXME:
        return kFuncOK;
}
EFunctionReturnTypes_t
TReplayBuffer::PutInput(const int a_Type,
                        const void *from)
{
        LAME_PROGRAMMER_IF(NULL==from,
                        return kFuncFailed;);
        ERR_IF(kFuncOK != Add2Input(a_Type),
                        return kFuncFailed);

        switch (a_Type) {
                case kKeyboardInputType:
                        ERR_IF(kFuncOK !=
                                        Add2Key((KEY_TYPE *)from),
                                        return kFuncFailed);
                        break;
                case kMouseInputType:
                        ERR_IF(kFuncOK !=
                                        Add2Mouse((MOUSE_TYPE *)from),
                                        return kFuncFailed);
                        break;
                default:
                        LAME_PROGRAMMER(no damn valid input type);
        }//swi
        return kFuncOK;
}
EFunctionReturnTypes_t
SaveBuffer()
{
        //FIXME:
        return kFuncOK;
}
EFunctionReturnTypes_t
LoadBuffer()
{
        //FIXME:
        return kFuncOK;
} */
/*****************************************************************************/
/*****************************************************************************/
