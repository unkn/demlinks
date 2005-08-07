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

#include "actionsreplay.h"
#include "pnotetrk.h"

/*****************************************************************************/
//constructor
TActionsReplayBuffer::TActionsReplayBuffer():
        fHead(0),
        fTail(0)
{
}
/*****************************************************************************/
//destructor
TActionsReplayBuffer::~TActionsReplayBuffer()
{
}
/*****************************************************************************/
bool
TActionsReplayBuffer::HasActions()
{
        return (fHead != fTail);
}

/*****************************************************************************/
EFunctionReturnTypes_t
TActionsReplayBuffer::GetLastActionFromBuf(int *actnum, bool *isactive)
{
        LAME_PROGRAMMER_IF((actnum==NULL)||(isactive==NULL),
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(*actnum < 0,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(*actnum >= kAllocatedActions,
                        return kFuncFailed);

        LAME_PROGRAMMER_IF(!HasActions(),
                        return kFuncFailed);
        *actnum=Buffer[fHead].index;
        *isactive=Buffer[fHead].isactive;
        fHead=(fHead+1) % MAX_ACTIONS_IN_REPLAY_BUF;
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
TActionsReplayBuffer::ToActionsBuffer(int src,bool isactive)
{
        LAME_PROGRAMMER_IF(trigger >= kMaxTriggers,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(which_trigger < 0,
                        return kFuncFailed);

        int new_tail=(fTail+1) % MAX_ACTIONS_IN_REPLAY_BUF;
        if (new_tail == fHead) {//buffer is full
                ERR_IF(kFuncOK != SaveBuffer(),
                                return kFuncFailed);
                fHead=fTail;
        }//fi
        //by here we should have place to put stuff into buffer
        Buffer[fTail].index=src;
        Buffer[fTail].isactive=isactive;
        fTail=new_tail;

        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
TActionsReplayBuffer::SaveBuffer()
{
//FIXME:
        return kFuncOK;
}
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
