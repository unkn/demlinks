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
* Description: fixed size FIFO queue list
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

        function
        Query4Empty(bool &m_Bool);

        function
        ThrowLastFromBuffer();

        function
        MoveLastFromBuffer(_data_ &m_Dest);

        function
        PeekAtLastFromBuffer(_data_ *into);

        function
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
        __( delete [] Buffer );
}
/*****************************************************************************/
template <class _data_>
function
TBuffer<_data_>::Query4Empty(bool &m_Bool)
{
        m_Bool=(fHead==fTail);
        _OK;
}

/*****************************************************************************/
/*****************************************************************************/
template <class _data_>
function
TBuffer<_data_>::ThrowLastFromBuffer()
{
        if (fTail != fHead) {
                fHead=(fHead+1) % itsSize;
                _OK;
        }//fi
        _F;
}
/*****************************************************************************/
template <class _data_>
function
TBuffer<_data_>::MoveLastFromBuffer(_data_ &m_Dest)
{
        if (fTail != fHead) {
                //aka non empty buffer
                m_Dest=Buffer[fHead];
                fHead=(fHead+1) % itsSize;
                _OK;
        }//fi
        _F;
}
/*****************************************************************************/
template <class _data_>
function
TBuffer<_data_>::PeekAtLastFromBuffer(_data_ *into)
{
        __tIF(into==NULL);
        if (fTail != fHead) {
                //aka non empty buffer
                *into=Buffer[fHead];
                _OK;
        }//fi
        _F;
}
/*****************************************************************************/
template <class _data_>
function
TBuffer<_data_>::CopyIntoBuffer(_data_ *from)
{
//a COPY OF CONTENTS of passed parameter is MADE
        __tIF(from==NULL);
        int new_tail=(fTail+1) % itsSize;
        if (new_tail != fHead) {//buffer wasn't full before this, 
                //so add one more
                Buffer[fTail]=*from;
                fTail=new_tail;
        } else {
                _FA(buffer was already full so cannot add anymore and losing it
                                is unacceptable so we FAILed);
        }//else
        _OK;
}
/*****************************************************************************/


#endif
