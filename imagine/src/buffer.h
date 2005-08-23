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

#ifndef BUFFER_H
#define BUFFER_H

#include "_gcdefs.h"
#include "pnotetrk.h"

#define DEFAULT_MAX_IN_BUF 200

template <class _data_>
class TBuffer {//FIFO predefined array buffer
private:
        int itsSize;
public:
        int fHead;
        int fTail;
        _data_ *Buffer;//ptr to an array of _data_ Buffer[i]==_data_

        int
        GetSize()const{ return itsSize;};

        TBuffer(int a_Size=DEFAULT_MAX_IN_BUF);
        ~TBuffer();

        bool
        IsEmpty();//buffer

        EFunctionReturnTypes_t
        ThrowLastFromBuffer();

        EFunctionReturnTypes_t
        MoveLastFromBuffer(_data_ *dest);

        EFunctionReturnTypes_t
        PeekAtLastFromBuffer(_data_ *into);

        EFunctionReturnTypes_t
        CopyIntoBuffer(_data_ *from);

}; //END of class

/*****************************************************************************/
template <class _data_>
TBuffer<_data_>::TBuffer(int a_Size)//constructor
{
        fHead=fTail=0;
        itsSize=a_Size;
        Buffer=new _data_[itsSize];
}

/*****************************************************************************/
template <class _data_>
TBuffer<_data_>::~TBuffer()//destructor
{
        delete [] Buffer;
}
/*****************************************************************************/
template <class _data_>
bool
TBuffer<_data_>::IsEmpty()
{
        return (fHead==fTail);
}

/*****************************************************************************/
template <class _data_>
EFunctionReturnTypes_t
TBuffer<_data_>::ThrowLastFromBuffer()
{
        if (fTail != fHead) {
                fHead=(fHead+1) % itsSize;
                return kFuncOK;
        }//fi
        return kFuncFailed;
}
/*****************************************************************************/
template <class _data_>
EFunctionReturnTypes_t
TBuffer<_data_>::MoveLastFromBuffer(_data_ *dest)
{

        LAME_PROGRAMMER_IF(dest==NULL,
                        return kFuncFailed);
        if (fTail != fHead) {
                //aka non empty buffer
                *dest=Buffer[fHead];
                fHead=(fHead+1) % itsSize;
                return kFuncOK;
        }//fi
        return kFuncFailed;
}
/*****************************************************************************/
template <class _data_>
EFunctionReturnTypes_t
TBuffer<_data_>::PeekAtLastFromBuffer(_data_ *into)
{
        LAME_PROGRAMMER_IF(into==NULL,
                        return kFuncFailed);
        if (fTail != fHead) {
                //aka non empty buffer
                *into=Buffer[fHead];
                return kFuncOK;
        }//fi
        return kFuncFailed;
}
/*****************************************************************************/
template <class _data_>
EFunctionReturnTypes_t
TBuffer<_data_>::CopyIntoBuffer(_data_ *from)
{
//a COPY OF CONTENTS of passed parameter is MADE
        LAME_PROGRAMMER_IF(from==NULL,
                        return kFuncFailed);
        int new_tail=(fTail+1) % itsSize;
        if (new_tail != fHead) {//buffer wasn't full before this, 
                //so add one more
                Buffer[fTail]=*from;
                fTail=new_tail;
        } else {
                ERR(buffer was already full so cannot add anymore and losing it
                                is unacceptable so we FAILed);
                return kFuncFailed;
        }//else
        return kFuncOK;
}
/*****************************************************************************/


#endif
