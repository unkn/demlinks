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

#include "uniqstr.h"

static char gUniqStr[_MAX_UNIQCHARS+1];//includes a \0
static bool gInitedUniqStr=false;

function
GetUniqueString(NodeId_t &m_Ret)//on each call a different string is returned
{
//------ begin
//getting current
        std::string tmp(gUniqStr);
//setting the next val for the future
        bool carry=false;
        for (int i=_MAX_UNIQCHARS-1;i>=0;i--) {
                if ((carry)||(i==_MAX_UNIQCHARS-1)){//first char? or carry ? then increment it
                        __if (gUniqStr[i] >= _UNIQENDING_CHAR) {
                                gUniqStr[i] = _UNIQLEADING_CHAR;
                                carry=true;
                                __tIF(i==0);//overflow
                        } __fielse {
                                (gUniqStr[i])++;
                                if (carry)
                                        carry=false;
                        }__fi
                }
        }//for
        while ((tmp.length()>1)&&(tmp.at(0)==_UNIQLEADING_CHAR)) {
                __( tmp.erase(0,1); );
        };
        m_Ret = tmp;

//------ done
        _OK;
}

function
UnconditionallyInitUniqueString()
{
        for (int i=0; i<_MAX_UNIQCHARS; i++)
                gUniqStr[i]=_UNIQLEADING_CHAR;
        gUniqStr[_MAX_UNIQCHARS]='\0';
//------ register as inited already
        if (!gInitedUniqStr) {
                gInitedUniqStr=true;
        } else {
                //was already inited;
                _fret(kFuncAlreadyExists);
        }
//----- done
        _OK;
}

function
MakeSureUniqueStringIsInited()
{
        if (! gInitedUniqStr) {
                __fIFnok( UnconditionallyInitUniqueString() );
        } else {
                //already inited
                _fret(kFuncAlreadyExists);
        }
//------- done
        _OK;
}



