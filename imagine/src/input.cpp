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
#include "camera.h"
#include "excamera.h"
#include "pnotetrk.h"
#include "timedinput.h"//both mouse and keyboard united
//#include "genericinput.h"
#include "combifile.h"
#include "activefunx.h"
//#include "actionsreplay.h"
#include "actionsinput.h"
#include "actions.h"

/*****************************************************************************/

const char *kCombiFileName="combinations.dat";


/*****************************************************************************/



//functions:
/*****************************************************************************/

/*****************************************************************************/

/*****************************************************************************/
EFunctionReturnTypes_t
InitInput()
{
        Passed_st temp;
        temp.fKeyFlags=kRealKeyboard;
        temp.fMouseFlags=kRealMouse;
        ERR_IF( kFuncOK !=
                InstallAllInputs(&temp),
                return kFuncFailed;
               );

        ERR_IF(kFuncOK != InitActionsInput(),
                        return kFuncFailed;);
        ERR_IF(kFuncOK != InitGenericInput(),
                        return kFuncFailed;);

        ERR_IF(kFuncOK != InitActions(),
                        return kFuncFailed;);
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
DeInitInput()
{
        ERR_IF(kFuncOK!=DoneActions(),
                        return kFuncFailed);
        ERR_IF(kFuncOK!=DoneGenericInput(),
                        return kFuncFailed);
        ERR_IF(kFuncOK!=DoneActionsInput(),
                        return kFuncFailed);
        //last because AllLowLevelInpus must still be available, not freed
        ERR_IF(kFuncOK!=UnInstallAllInputs(),
                        return kFuncFailed);
        return kFuncOK;
}



/*****************************************************************************/
/*****************************************************************************/
//issue this if any input is available
EFunctionReturnTypes_t
TransformToGenericInputs(const INPUT_TYPE *from)
{//get all inputs of group from buffer, and pass them to <genericinput> handler
        PARANOID_IF(from==NULL,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF((from->type<0) || (from->type>=kMaxInputTypes),
                        return kFuncFailed);
        //FIXME: shame we've to alloc and free a ptr on each entrace in this
        //function
        void *anydatastruc=NULL;
        ERR_IF(kFuncOK!=
                AllLowLevelInputs[from->type]->Alloc(anydatastruc),
                        return kFuncFailed);
        PARANOID_IF(anydatastruc==NULL,
                        return kFuncFailed);

        int i=0;
        while (i<from->how_many) {
                //get one input
                ERR_IF(kFuncOK!=
                        AllLowLevelInputs[from->type]->MoveFirstFromBuffer(anydatastruc),
                                return kFuncFailed);

                //now we've to transform `anydatastruc` into generic input
                //and also push it into generic input's buffer(if!)
                UnifiedInput_st passed;
                passed.type=from->type;
                passed.data=anydatastruc;
                //handle the passed input(key,mouse,serial) such as it might
                //be a part of or complete generic input
                ERR_IF(kFuncOK !=
                       GenericInputHandler(&passed),
                       //free before returning error
                        ERR_IF(kFuncOK!=
                                AllLowLevelInputs[from->type]->DeAlloc(anydatastruc),
                               return kFuncFailed);
                        return kFuncFailed);
                i++;
        }//while
        //freemem
        ERR_IF(kFuncOK!=
                        AllLowLevelInputs[from->type]->DeAlloc(anydatastruc),
                return kFuncFailed);

        return kFuncOK;
}

/*****************************************************************************/
/*static GLOBAL_TIMER_TYPE gTarget_ExecuteActionTimer_Time;
//depends on game timer, not interrupt driven timer
GLOBAL_TIMER_TYPE gCurrent_ExecuteActionTimer_Time=0;
*/

EFunctionReturnTypes_t
QueueAllActions()
{
        while (!ActionsInputBuffer.IsEmpty()) {//not empty!
                ACTIONSINPUT_TYPE got;

                //remove actioninput from buffer
                ERR_IF(kFuncOK!=ActionsInputBuffer.MoveLastFromBuffer(&got),
                                return kFuncFailed);
                //queue it, don't execute it now, later.
                ERR_IF(kFuncOK!=QueueAction(&got),
                                return kFuncFailed);

        }//while

        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
MakeSureWeHaveActions()
{//transform generic inputs into actions
        //pops genericinputs from their buffer and transforming them eventually puts actionsinputs in actionsinput buffer
        while (!GenericInputBuffer.IsEmpty()) {
                GENERICINPUT_TYPE got;
                ERR_IF(kFuncOK!=GenericInputBuffer.MoveLastFromBuffer(&got),
                                return kFuncFailed);
                //this will perhaps generate items in ActionBuffer
                ERR_IF(kFuncOK!=TransformToActions(&got),
                                return kFuncFailed);
        }//fi
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
MakeSureWeHaveGenericInput()
{//transform multiple input types into generic inputs
        if (HowManyDifferentInputsInBuffer()) {
                //FIXME:temporary, remove it:
                //cams[0].SetNeedRefresh();

                INPUT_TYPE into;
                //one input group at a time; as in all key or all mouse
                while (kFuncOK==MoveFirstGroupFromBuffer(&into)){
                        ERR_IF(kFuncOK !=
                                  TransformToGenericInputs(&into),
                                return kFuncFailed);
                }//while
        }//fi

        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
MangleInputs()
//transform INPUTs to ACTIONs but doesn't execute those actions
{
        ERR_IF(kFuncOK!= MakeSureWeHaveGenericInput(),
                        return kFuncFailed);
        ERR_IF(kFuncOK!= MakeSureWeHaveActions(),
                        return kFuncFailed);

        ERR_IF(kFuncOK != QueueAllActions(),
                return kFuncFailed);
        return kFuncOK;
}


/*****************************************************************************/
EFunctionReturnTypes_t
Executant()
{

        //now here, we keep executing all enabled actions, some might get disabled from within(those that are one-time actions) other will execute their function one more time on each call to this function
        ERR_IF(kFuncOK!= ExecuteAllQueuedActions(),
                        return kFuncFailed);

        return kFuncOK;
}
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
