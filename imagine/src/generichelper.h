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

#ifndef GENERICHELPER_H
#define GENERICHELPER_H
/*****************************************************************************/
#include "_gcdefs.h"
#include "pnotetrk.h"


/*****************************************************************************/

template <class T>
struct GenericHelper_st {
        T Significant;

        EFunctionReturnTypes_t
        Compare(const GenericHelper_st*withwhat,
                        int *result);

        EFunctionReturnTypes_t
        Assign(const T *value);//contents of value are COPied, not shared
};

/*****************************************************************************/
template <class T>
EFunctionReturnTypes_t
GenericHelper_st<T>::Assign(const T *value)
{//a COPY of *value is made !!
        LAME_PROGRAMMER_IF(NULL==value,
                        return kFuncFailed);
        Significant=*value;
        return kFuncOK;
}
/*****************************************************************************/
template <class T>
EFunctionReturnTypes_t
GenericHelper_st<T>::Compare(const GenericHelper_st*withwhat,
                int *result)
{
        PARANOID_IF(NULL==withwhat,
                        return kFuncFailed);
        if (Significant==withwhat->Significant)
                *result=0;//equal
        else
                *result=-1;//less than equal //FIXME: prolly when needed

        return kFuncOK;
}

/*****************************************************************************/

#endif
