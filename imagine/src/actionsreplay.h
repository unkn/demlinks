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

#ifndef ACTIONSREPLAY_H
#define ACTIONSREPLAY_H

#include "pnotetrk.h"

struct ActionID_st {
        int index;
        bool isactive;
};

#define MAX_ACTIONS_IN_REPLAY_BUF 200

class TActionsReplayBuffer {
private:
        ActionID_st Buffer[MAX_ACTIONS_IN_REPLAY_BUF];
        int fHead;
        int fTail;
public:
        TActionsReplayBuffer();
        ~TActionsReplayBuffer();

        bool
        HasActions();

        EFunctionReturnTypes_t
        GetLastActionFromBuf(int *actnum, bool *isactive);

        EFunctionReturnTypes_t
        ToActionsBuffer(int src,bool isactive);


        EFunctionReturnTypes_t
        SaveBuffer();
};//class

#endif
