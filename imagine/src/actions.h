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


/*********************/
//each action is actually a void function
extern EFunctionReturnTypes_t (*Functions[kMaxAIs])(void);//lot of pointers to functions
//one does Functions[x]=real_funcname;
/*********************/
class ActionsClass {
private:
        EnumAllAI_t fWhichFunc;//0..kMaxAIs-1
public:
        ActionsClass(EnumAllAI_t a_WhichFunc=kAI_Undefined);
        ~ActionsClass();

        EFunctionReturnTypes_t
        SetFunc(EnumAllAI_t a_WhichFunc);

        bool
        IsFuncWithinLimits(EnumAllAI_t thisone);

        EFunctionReturnTypes_t
        Execute();
};//end of class
/*********************/
#define ACTIONS_TYPE ActionsClass
extern ACTIONS_TYPE (*Actions);//[kMaxAIs];
/*********************/

EFunctionReturnTypes_t
ExecuteAction(const ACTIONSINPUT_TYPE *which);

/*********************/
EFunctionReturnTypes_t
InitActions();

/*********************/
EFunctionReturnTypes_t
DoneActions();

/*********************/
#endif
