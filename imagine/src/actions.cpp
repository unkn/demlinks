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
/*****************************************************************************/
ACTIONS_TYPE (*Actions)/*[kMaxAIs]*/=NULL;//a pointer to an array of ACTIONS_TYPE
EFunctionReturnTypes_t (*Functions[kMaxAIs])(void);//an array of pointers_to_functions

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
        ERR_IF(!IsFuncWithinLimits(fWhichFunc),
                        return kFuncFailed);
        ERR_IF(kFuncOK!=Functions[fWhichFunc](),
                        return kFuncFailed);
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
ExecuteAction(const ACTIONSINPUT_TYPE *which)
{
        //FIXME:
        //each executed action must be thrown into a replay buffer
        //OR make the ActionsBuffer such that when an item gets removed is
        //saved into a replay buffer of some sort(or a file, eventually)
        //but first...
        ERR_IF(which==NULL,
                        return kFuncFailed);
        ERR_IF(!Actions[kAI_Undefined].IsFuncWithinLimits(which->Significant),
                        return kFuncFailed);
        ERR_IF(kFuncOK!=Actions[which->Significant].Execute(),
                        return kFuncFailed);
        return kFuncOK;
}

/*****************************************************************************/
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

