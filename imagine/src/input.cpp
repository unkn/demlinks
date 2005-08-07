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

#include <math.h>

//#include <stdio.h>
#include <allegro.h>

#include "_gcdefs.h"

/*****************************************************************************/

#include "consts.h"
#include "input.h"
#include "common.h"
#include "excamera.h"
#include "pnotetrk.h"
#include "timedinput.h"//both mouse and keyboard united
//#include "genericinput.h"
#include "combifile.h"
#include "activefunx.h"
#include "actionsreplay.h"

/*****************************************************************************/

const char *kCombiFileName="combinations.dat";

TAnyAction *AllActions[kAllocatedActions];//TODO:must be a double linked list

void (*activation_funcs[kAllocatedActions])(void);
void (*deactivation_funcs[kAllocatedActions])(void);

const char *ActionNames[kAllocatedActions]= {
        "kActQuit",
        "kToggleCurCam",
        "kChooseNextCam",
        "kHold1_Key",
        "kIncFOV",
        "kIncAspect",
        //"kLeftSlideView",
        //"kRightSlideView",
        //"kUpSlideView",
        //"kDownSlideView",
        "kLeftSlideCam",
        "kRightSlideCam",
        "kUpSlideCam",
        "kDownSlideCam",
        "kLeftTurnCam",
        "kRightTurnCam",
        "kUpPitchCam",
        "kDownPitchCam",
        "kLeftRollCam",
        "kRightRollCam",
        "kForwardSlideCam",
        "kBackwardSlideCam",
        //"kEnlargeDown_View",
        //"kShrinkUp_View",
        //"kEnlargeRight_View",
        //"kShrinkLeft_View"

};
const char *TriggerNames[kMaxTriggers]= {
        "kTrigger_ActivateAction",
        "kTrigger_DeActivateAction"
};

bool Hold1_Key=false;


TActionsReplayBuffer ActionsBuffer;



//functions:
/*****************************************************************************/

/*****************************************************************************/

EFunctionReturnTypes_t
InitActions()
{

        //init all function pointers
        ERR_IF(kFuncOK !=
                        InitFunx(),
                        return kFuncFailed;
                );
//open file
        //FIXME:load all actions from a predefined file ***work in progress now
        TFacileFile zfile;
        ERR_IF(kFuncOK != zfile.AllInOne(kCombiFileName,
                                kAllocatedActions,kMaxTriggers),
                        return kFuncFailed);




//begin CUT here
/*
        for (int i=0;i<kAllocatedActions;i++) {
                AllActions[i]=new TAnyAction;
                AllActions[i]->SetFunx(activation_funcs[i],deactivation_funcs[i]);
        }
        KEY_TYPE esc;
        esc.ScanCode=RELEASE(KEY_ESC);

        ERR_IF(kFuncOK != AllActions[kActQuit]->AddKey(kTrigger_ActivateAction,&esc),
                        return kFuncFailed;);

        esc.ScanCode=PRESS(KEY_Q);
        ERR_IF(kFuncOK != AllActions[kActQuit]->AddKey(kTrigger_ActivateAction,&esc),
                        return kFuncFailed;);

        esc.ScanCode=RELEASE(KEY_Q);
        ERR_IF(kFuncOK != AllActions[kActQuit]->AddKey(kTrigger_DeActivateAction,&esc),
                        return kFuncFailed;);
//end CUT here
*/

        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
InitInput()
{
        ERR_IF( kFuncOK !=
                InstallTimedInput(kRealKeyboard | kRealKeyboardTimer |
                        kRealMouse | kRealMouseTimer,
                        1000,200),
                return kFuncFailed;
               );
        ERR_IF(kFuncOK != InitActions(),
                        return kFuncFailed;);
        return kFuncOK;
}



/*****************************************************************************/
EFunctionReturnTypes_t
AllActions_HandleInput(const int a_Type,
                        const void *from)
{
        LAME_PROGRAMMER_IF(kAllocatedActions==0,
                        return kFuncFailed);
        for (int i=0;i<kAllocatedActions;i++) {
                //pass the same input to each action, only enabled ones MAY
                //trigger
                //bool was_active=AllActions[i]->IsActive();
                //double(or more) DEactivation is ignored;
                //only double(or more) activation is put into buffer
                for (int trigger=0;trigger<kMaxTriggers;trigger++) {
                        bool result;
                        ERR_IF(kFuncOK !=
                        AllActions[i]->HandleInput(a_Type,from,trigger,&result),
                                return kFuncFailed;);
                        //if result=true, then it recognized the combination of
                        //current trigger, thus we add it to buffer since it
                        //means change considering prev state, also can occur:
                        //on-off-on and we'd not miss it
                        if (result==true) {
                                ERR_IF(kFuncOK !=
                                       ActionsBuffer.ToActionsBuffer(i,
                                               trigger==0),
                                        return kFuncFailed);
                        }//fi
                }//for

        }//for
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
RAR_ConsumeMouse(const int a_HowMany)
{
        MOUSE_TYPE mou;
        PARANOID_IF(a_HowMany > MAX_MOUSE_EVENTS_BUFFERED,
                        return kFuncFailed;);
        int i=0;
        while (i<a_HowMany) {
                PARANOID_IF(kFuncOK !=
                                RemoveNextMouseEventFromBuffer(&mou),
                        return kFuncFailed;);
                //save from replay buffer
                //ReplayInputs.PutInput(kMouseInputType,&mou);
                //see which actions trigger
                ERR_IF(kFuncOK !=
                                AllActions_HandleInput(kMouseInputType,
                                        (const void *)&mou),
                        return kFuncFailed);
                i++;
        }//while
        return kFuncOK;
}

EFunctionReturnTypes_t
RAR_ConsumeKey(const int a_HowMany)
{
        KEY_TYPE tmpk;
        PARANOID_IF(a_HowMany > MAX_KEYS_BUFFERED,
                        return kFuncFailed;);
        int i=0;
        while (i<a_HowMany) {
                PARANOID_IF(kFuncOK !=
                    RemoveNextKeyFromBuffer(&tmpk),
                    return kFuncFailed;);
          //save from replay buffer; replay should be for actions
          //ReplayInputs.PutInput(kKeyboardInputType,&tmpk);
          //see which actions trigger
          ERR_IF(kFuncOK !=
               AllActions_HandleInput(kKeyboardInputType,
                        (const void *)&tmpk),
               return kFuncFailed);
          i++;
        }//while
        return kFuncOK;
}

//issue this if any input is available
EFunctionReturnTypes_t
RefreshActionsAndReplay_ConsumingInput(const INPUT_TYPE *from)
{
        switch (from->type) {
                case kMouseInputType: {
                        ERR_IF(kFuncOK !=
                                        RAR_ConsumeMouse(from->how_many),
                                        return kFuncFailed);
                        break;
                }
                case kKeyboardInputType: {
                        ERR_IF(kFuncOK !=
                                        RAR_ConsumeKey(from->how_many),
                                        return kFuncFailed);
                        break;
                }
                default: {
                        ERR(lame or undefined input);
                        return kFuncFailed;
                }
        }//swi

        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
ExecuteAllActions()
{
//executes the actions from the buffer of actions, emptying it, buff may have
//more than one action if we're lagging (ie. slow PC)
        while (ActionsBuffer.HasActions()) {
                int i;
                bool active;
                ERR_IF(kFuncOK!= ActionsBuffer.GetLastActionFromBuf(&i,&active),
                                return kFuncFailed);
                PARANOID_IF((i>=kAllocatedActions) || (i<0),
                        return kFuncFailed);
                if (active) {//activated
                        ERR_IF(kFuncOK != AllActions[i]->PerformActive(),
                                return kFuncFailed);
                } else {//deactivated
                        ERR_IF(kFuncOK != AllActions[i]->PerformNotActive(),
                                return kFuncFailed);
                }//else
        }//while
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
Executant()
//transform INPUTs to ACTIONs
{
        for (int i=0;i<kAllocatedActions;i++) {
                //clear state of all cams, so we know which didn't change
                AllActions[i]->ClearState();
        }//for
//see if any new actions go active:
        if (HowManyInputsInBuffer()) {
                //so if we got input we transform it into actions, putting
                //actions as we encounter them into the actionsbuffer
                INPUT_TYPE into;
                //one input group at a time; as in all key or all mouse
                while (kFuncOK==RemoveNextInputFromBuffer(&into)){
                        ERR_IF(kFuncOK !=
                                  RefreshActionsAndReplay_ConsumingInput(&into),
                                return kFuncFailed);
                }//while
        }//fi

//add into buffer all the actions that previously stayed active
        for (int i=0;i<kAllocatedActions;i++) {
                if ((false==AllActions[i]->HasStateChanged())&&
                        (AllActions[i]->IsActive())){
                        //ignoring deactivated actions, when not just detected
                        //we're here because user is for ie. keeping a key
                        //pressed which in far past activated at least one
                        //action, and now we must keep executing that action
                        //even tho no inputs happened since.
                        //but what we actually do here, is put it into the buff
                //putting it into buffer in active state
                        ERR_IF(kFuncOK !=
                              ActionsBuffer.ToActionsBuffer(i,true/*isactive*/),
                                        return kFuncFailed);
                }//fi
        }//for

        //empties the actionsbuffer by executing them in order as they appear
        //in that buffer, gee
        ERR_IF(kFuncOK != ExecuteAllActions(),
              return kFuncFailed);//if and only those that are active

        return kFuncOK;
}



/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
