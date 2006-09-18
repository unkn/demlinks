/****************************************************************************
*
*                             dmental links
*    Copyright (C) June 2006 AtKaaZ, AtKaaZ at users.sourceforge.net
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
* Description: dmlenv level 1
*
****************************************************************************/

#include "dmlenvl1.h"
#include "dmlenv.hpp"

/*******************************/
MDMLFIFOBuffer :: MDMLFIFOBuffer(TLink *m_WorkingOnThisTLink):
                TDMLCursor(m_WorkingOnThisTLink),
                MDMLDomainPointer(m_WorkingOnThisTLink)
{//constructor
}
/*******************************/
MDMLFIFOBuffer :: ~MDMLFIFOBuffer()
{ //destructor
}
/*******************************/
/*******************************/
/*******************************/
MDMLDomainPointer :: MDMLDomainPointer (TLink *m_WorkingOnThisTLink):
        TDMLPointer(m_WorkingOnThisTLink)
{ //constructor
}
/*******************************/
MDMLDomainPointer :: ~MDMLDomainPointer()
{ //destructor
}
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
/*******************************/
