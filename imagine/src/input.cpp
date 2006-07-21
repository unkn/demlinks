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
        __tIFnok(InstallAllInputs());

        //stop doing what u can, start doing what u want! 01:41, 17july2006
        __tIFnok(InitActionsInput());

        __tIFnok(InitGenericInput());

        __tIFnok(InitActions());

        _OK;
}

/*****************************************************************************/
function
DeInitInput()
{
        __tIFnok(DoneActions());
        __tIFnok(DoneGenericInput());
        __tIFnok(DoneActionsInput());
        //last because AllLowLevelInpus must still be available, not freed
        __tIFnok(UnInstallAllInputs());
        
        _OK;
}



/*****************************************************************************/
/*****************************************************************************/
//issue this if any input is available
EFunctionReturnTypes_t
TransformToGenericInputs(const INPUT_TYPE *from)
{//get all inputs of group from buffer, and pass them to <genericinput> handler

        __tIF(from==NULL);
        __tIF((from->type < 0) || (from->type >= kMaxInputTypes));
        //FIXME: shame we've to alloc and free a ptr on each entrace in this
        //function
        void *anydatastruc=NULL;
        __tIFnok(
                AllLowLevelInputs[from->type]->Alloc(anydatastruc));
        __tIF(anydatastruc==NULL);
#define THROW_HOOK \
        __tIFnok(AllLowLevelInputs[from->type]->DeAlloc(anydatastruc));

        int i=1;
        while (i <= from->how_many) {
                //get one input

                __tIFnok(AllLowLevelInputs[from->type]->MoveFirstFromBuffer(anydatastruc));
                /*
                 * error trapping:
                EFunctionReturnTypes_t err;
                int hm=from->how_many;
                int bufbefore=AllLowLevelInputs[from->type]->HowManyInBuffer();

                _(err=AllLowLevelInputs[from->type]->MoveFirstFromBuffer(anydatastruc));
                if (kFuncOK != err) {
                        allegro_message("i:%d hm:%d from->how_many:%d, bufbefore:%d bufafter:%d",i, hm, from->how_many, bufbefore, AllLowLevelInputs[from->type]->HowManyInBuffer());
                        _t(unhandled);
                }
                */


                //now we've to transform `anydatastruc` into generic input
                //and also push it into generic input's buffer(if!)
                UnifiedInput_st passed;
                passed.type=from->type;
                passed.data=anydatastruc;
                //handle the passed input(key,mouse,serial) such as it might
                //be a part of or complete generic input

//free before returning error
                _tIFnok(GenericInputHandler(&passed));
                i++;
        }//while
        //freemem
        THROW_HOOK;//dealloc!

        _OK;
#undef THROW_HOOK
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
function
MakeSureWeHaveGenericInput()
{//transform multiple input types into generic inputs
        while (HowManyDifferentInputsInBuffer() > 0) {
                //FIXME:temporary, remove it:
                //cams[0].SetNeedRefresh();

                INPUT_TYPE into;
                //one input group at a time; ie. all key OR all mouse
                        EFunctionReturnTypes_t err;
                        __(err=MoveFirstGroupFromBuffer(&into));
                        if (kFuncOK == err) {
                                __tIFnok(TransformToGenericInputs(&into));
                        } else {//until buffer is empty, or some error ie. gLock (this may cause some delay tho)
                                __t(unhandled);
                               /* if (err == kFuncLocked) {
                                        break;//while
                                } else {//assuming some fatal or unhandled error
                                        allegro_message("%d",err);
                                        __t(unhandled);
                                }*/
                        }
        }//while

        _OK;
}
/*****************************************************************************/
function
MangleInputs()
//transform INPUTs to ACTIONs but doesn't execute those actions
{
        __tIF(kFuncOK!= MakeSureWeHaveGenericInput());
        __tIF(kFuncOK!= MakeSureWeHaveActions());

        __tIF(kFuncOK != QueueAllActions());

        _OK;
}


/*****************************************************************************/
function
Executant()
{

        //now here, we keep executing all enabled actions, some might get disabled from within(those that are one-time actions) other will execute their function one more time on each call to this function
        __tIFnok(ExecuteAllQueuedActions());
        _OK;
}
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
