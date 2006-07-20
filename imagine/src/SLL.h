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
* Description: Generic Single-Linked list prototype
*
****************************************************************************/

#ifndef SLL_H
#define SLL_H

template <class T>
struct GenericSingleLinkedList_st {
        GenericSingleLinkedList_st<T> *Next;
        T *Data;//anything;; alloc-ing and dealloc-ing this is not up to us!

        GenericSingleLinkedList_st(const T *a_Data){
                //a copy of the contents is NOT made!
                Next=NULL;
                Data=(T *)a_Data;
        };
        GenericSingleLinkedList_st(const T *a_Data,
                        const GenericSingleLinkedList_st<T> *a_Next){
                //a copy of the contents is NOT made!
                Next=(GenericSingleLinkedList_st<T> *)a_Next;
                Data=(T *)a_Data;
        };
        ~GenericSingleLinkedList_st(){//deletes this parent and all children
                //interesting avalanching here:

                //the WARN line is not working since at_exit goes first and.
                //deinits the notif. system WARN_IF(Data!=NULL,);
                if (Next != NULL)
                        delete Next;
                //Next=NULL;
        };//destructor
        EFunctionReturnTypes_t New(const T*a_Data){
                //a copy of the contents is NOT made!
                //refusing to add a new one if there's already a Next
                ERR_IF(Next!=NULL,
                                return kFuncFailed);
                Insert(a_Data);
                return kFuncOK;
        };


        EFunctionReturnTypes_t Insert(const T*a_Data){
                //a copy of the contents is NOT made!
                //refusing to add a new one if there's already a Next
                GenericSingleLinkedList_st<T> *tmp=NULL;
                tmp=new GenericSingleLinkedList_st<T>(a_Data,Next);
                Next=tmp;//safer
                ERR_IF(Next==NULL,
                                return kFuncFailed);
                return kFuncOK;
        };
};

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/


#endif

