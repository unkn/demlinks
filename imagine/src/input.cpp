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
function
TransformToGenericInputs(const INPUT_TYPE &from)
{//get all inputs of group from buffer, and pass them to <genericinput> handler

        __tIF((from.type < 0) || (from.type >= kMaxInputTypes));
        //FIXME: shame we've to alloc and free a ptr on each entrace in this
        //function
        void *anydatastruc=NULL;//either key or mouse types
        __tIFnok( AllLowLevelInputs[from.type]->Alloc(anydatastruc) );
        __tIF(NULL == anydatastruc);
        //we allocate it here, so we won't have to alloc/dealloc after each input got, inside the 'while'
#define THROW_HOOK \
        __tIFnok(AllLowLevelInputs[from.type]->DeAlloc(anydatastruc));

        int i=1;
        while (i <= from.how_many) {
                //get one input

                _htIFnok(AllLowLevelInputs[from.type]->MoveFirstFromBuffer(anydatastruc));

                //now we've to transform `anydatastruc` into generic input
                //and also push it into generic input's buffer(if!)
                UnifiedInput_st passed;
                passed.type=from.type;//key or mouse
                passed.data=anydatastruc;//pointer
                //handle the passed input(key,mouse,serial) such as it might
                //be a part of or complete generic input

//free before returning error
                _hfIFnok(GenericInputHandler(passed));
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

function
QueueAllActions()
{
        bool once=false;
        while (true) {
                bool empty;
                __tIFnok( ActionsInputBuffer.Query4Empty(empty));
                if (empty) {
                        if (once) {
                                _OK;
                        } else {
                                _F;
                        }
                }

                if (!once) {
                        once=true;
                }

                ACTIONSINPUT_TYPE got;
                //remove actioninput from buffer
                __tIFnok(ActionsInputBuffer.MoveLastFromBuffer(got));
                //queue it, don't execute it now, later.
                __tIFnok(QueueAction(got));

        }//while

        _FA(cannot reach this);
}

/*****************************************************************************/
function
MakeSureWeHaveActions()
{//transform generic inputs into actions
        //pops genericinputs from their buffer and transforming them eventually puts actionsinputs in actionsinput buffer
        bool once=false;
        while (true) {
                bool empty;
                __tIFnok( GenericInputBuffer.Query4Empty(empty) );
                if (empty) {
                        if (once) {
                                _OK;
                        } else {
                                _F;
                        }
                }
                if (!once) {
                        once=true;
                }
                GENERICINPUT_TYPE got;
                __tIFnok(GenericInputBuffer.MoveLastFromBuffer(got));
                //this will perhaps generate items in ActionBuffer
                __tIFnok(TransformToActions(&got));
        }//fi

        _FA(cannot reach this);
}
/*****************************************************************************/
function
MakeSureWeHaveGenericInput()
{//transform multiple input types into generic inputs
        int howMany;
        bool once=false;
        while (true) {
                __tIFnok( Query4HowManyDifferentInputsInBuffer(howMany) );
                if (howMany <= 0) {
                        if (once) {
                                _OK;
                        } else {
                                _F;
                        }
                }

                if (!once) {
                        once=true;
                }

                INPUT_TYPE into;
                //one input group at a time; ie. all key OR all mouse
                __doIFok (MoveFirstGroupFromBuffer(into)) {
                        __fIFnok(TransformToGenericInputs(into));
                }__kofiod
        }//while

        _FA(cannot reach this);
}
/*****************************************************************************/
function
MangleInputs()
//transform INPUTs to ACTIONs but doesn't execute those actions
{
        function err;
        __( err=MakeSureWeHaveGenericInput() );
        if (kFuncOK == err) {//if we had any inputs this means we prolly put them into the genericinput buffer thus :
                __( err=MakeSureWeHaveActions());//genericinput TO actionsinput buffer
                if (kFuncOK == err) {
                        __(err=QueueAllActions());
                        if (kFuncOK==err) {
                                _OK;
                        } else {
                                _fret kFuncNoActions;
                        }
                } else {
                        _fret kFuncNoGenericInputs;
                }
        } else {
                _fret kFuncNoLowLevelInputs;
        }

        _FA(cannot reach this);
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
