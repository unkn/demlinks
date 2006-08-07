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

#ifndef ACTIONS_H
#define ACTIONS_H

#include "_gcdefs.h"
#include "pnotetrk.h"
#include "actionsinput.h"
#include "SLL.h"

/*********************/

/*********************/
enum EActionTypes_t {
        fToggleActionType=0,
        fContinousActionType,
//last:
        fMaxActionTypes
};
/*********************/
//each action is actually a void function
extern EFunctionReturnTypes_t (*Functions[kMaxAIs])(void);//lot of pointers to functions
//one does Functions[x]=real_funcname;
/*********************/
class ActionsClass {
private:
        ACTIONSINPUT_TYPE fWhichFunc;//0..kMaxAIs-1
       // bool fEnabled;
        EActionTypes_t fType;
public:
        ACTIONSINPUT_TYPE fRemove;//0..kMaxAIs-1

        bool
        IsToggleType() {//const ACTIONSINPUT_TYPE *who_are_we) {
                /*TRAP(LAME_PROGRAMMER_IF(who_are_we==NULL,));
                //a toggle-type means it gets itself executed only once and also kills fRemove if ever was activated as a continous action
                if (fRemove==kAI_Undefined) {//means we must remove ourselves
                        fRemove=*who_are_we;
                } this was temp*/
        return (fType==fToggleActionType)&&(fRemove>0)&&(fRemove<kMaxAIs);
        };
        
        ActionsClass(ACTIONSINPUT_TYPE a_WhichFunc=kAI_Undefined);
        ~ActionsClass();

        EFunctionReturnTypes_t
        SetToggleActionType(ACTIONSINPUT_TYPE whoami) {
                fType=fToggleActionType;
                fRemove=whoami;
                return kFuncOK;
        };
        EFunctionReturnTypes_t
        SetFunc(ACTIONSINPUT_TYPE a_WhichFunc);

        bool
        IsFuncWithinLimits(ACTIONSINPUT_TYPE thisone);

        /*bool
        IsEnabled() { return fEnabled;};

        EFunctionReturnTypes_t
        SetDisabled(){ fEnabled=false; return kFuncOK;};


        EFunctionReturnTypes_t
        SetEnabled(){ fEnabled=true; return kFuncOK;};
       */
        function
        Execute();
};//end of class
/*********************/
#define ACTIONS_TYPE ActionsClass
extern ACTIONS_TYPE (*Actions);//[kMaxAIs];

/*********************/
/*********************/

/*********************/
function
QueueAction(const ACTIONSINPUT_TYPE & a_Which);

/*********************/
function
ExecuteAllQueuedActions();

/*********************/
function
InitActions();

/*********************/
function
DoneActions();

/*********************/
#endif
