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

#ifndef COMBIFILE_H
#define COMBIFILE_H

#include "pnotetrk.h"

//FIXME: more error handling from programmer side
class TFacileFile {
        int fFileHandle;

        EFunctionReturnTypes_t
        adv_save(const void *dest,int a_howmany);

        EFunctionReturnTypes_t
        adv_save_int(const int *dest);

        EFunctionReturnTypes_t
        adv_read(void *dest,int a_howmany);

        EFunctionReturnTypes_t
        adv_read_int(int *dest);

        EFunctionReturnTypes_t
        Open(const char *whatfile, int flags);

        EFunctionReturnTypes_t
        Close();

        EFunctionReturnTypes_t
        SaveAll(int a_AllocatedActions, int a_MaxTriggers);

        EFunctionReturnTypes_t
        LoadAll(int a_AllocatedActions, int a_MaxTriggers);


public:
        TFacileFile():fFileHandle(-1){};
        ~TFacileFile(){};

        EFunctionReturnTypes_t
        SaveAllInOne(
                const char *fromfilename,
                int a_AllocatedActions, int a_MaxTriggers);
        EFunctionReturnTypes_t
        AllInOne(
                const char *fromfilename,
                int a_AllocatedActions, int a_MaxTriggers);
};

#endif

