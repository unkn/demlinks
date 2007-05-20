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
* Description: Generic Double-Linked list prototype
*
****************************************************************************/

#ifndef DLL_H
#define DLL_H

#include "_gcdefs.h"
#include "pnotetrk.h"

//don't forget this only links to the data, does NOT copy it!
template <class T>
struct GenericDoubleLinkedElement_st {
        GenericDoubleLinkedElement_st<T> *Prev;
        GenericDoubleLinkedElement_st<T> *Next;
        T *Data;//anything;; alloc-ing and dealloc-ing this is not up to us!
        GenericDoubleLinkedElement_st(const T *a_Data) {
                //pointer exchange, contents ARE NOT COPIED!
                //Data must be allocated already, by an outside force
                Data=(T *)a_Data;
                Prev=Next=NULL;
        };
        ~GenericDoubleLinkedElement_st() {
                //however, WE DO DEALLOC Data
                delete(Data);//no need for Data=NULL because this is the destructor
        }
};

template <class T>
struct GenericDoubleLinkedList_st {
        GenericDoubleLinkedElement_st<T> *Head;
        GenericDoubleLinkedElement_st<T> *Tail;

        bool
        IsEmpty(){
                return ((Head==NULL)||(Tail==NULL));
        };

        GenericDoubleLinkedList_st() {
                Head=Tail=NULL;
        };
        GenericDoubleLinkedList_st(const T *a_Data){
                //a copy of the contents is NOT made!

                //initializing the list with one element
                Head=new GenericDoubleLinkedElement_st<T>(a_Data);
                __tIF(NULL==Head);
                Tail=Head;
        };
        ~GenericDoubleLinkedList_st(){
                //dealloc entire list starting from Head
                GenericDoubleLinkedElement_st<T> *tmp=Head;
                while (tmp!=NULL) {
                        GenericDoubleLinkedElement_st<T> *next;
                        __( next=tmp->Next; );
                        delete(tmp);
                        tmp=next;
                }
        };//destructor

        EFunctionReturnTypes_t
        Prepend(const T*a_Data){//before Head
                //a copy of the contents is NOT made!
                if (Head==NULL) {
                        __tIF(Tail != NULL);
                        //make one element list
                        Head=new GenericDoubleLinkedElement_st<T>(a_Data);
                        __tIF(NULL==Head);
                        Tail=Head;
                } else {
                        //multi element list, add one more to Head
                        Head->Prev=new GenericDoubleLinkedElement_st<T>(a_Data);
                        __tIF(NULL == Head->Prev);
                        Head->Prev->Next=Head;
                        Head=Head->Prev;
                }//else
                _OK;
        };

        EFunctionReturnTypes_t
        Append(const T*a_Data){//after Tail
                //a copy of the contents is NOT made!
                if (Tail==NULL) {
                        __tIF(Head!=NULL);//head must also be NULL
                        //make one element list
                        Tail=new GenericDoubleLinkedElement_st<T>(a_Data);
                        __tIF(NULL == Tail);
                        Head=Tail;
                } else {
                        //multi element list, add one more to Tail
                        Tail->Next=new GenericDoubleLinkedElement_st<T>(a_Data);
                        __tIF(NULL == Tail->Next);
                        Tail->Next->Prev=Tail;
                        Tail=Tail->Next;
                }//else
                _OK;
        };


        EFunctionReturnTypes_t
        RemoveFromAnyPlace(GenericDoubleLinkedElement_st<T> *&elem) {

                __tIF(elem==NULL);
                //keep chain connections extracting us from it
                if (elem->Next!=NULL) {
                        elem->Next->Prev=elem->Prev;
                } else {//we might be Tail, since there's no Next
                        if (Tail==elem) {
                                Tail=elem->Prev;//which could be NULL but that' ok
                        }
                }//else

                if (elem->Prev!=NULL) {
                        elem->Prev->Next=elem->Next;
                } else {//we could be Head since there's no Prev
                        if (Head==elem) {//we ARE Head
                                Head=elem->Next;
                        }
                }//else

                delete(elem);
                elem=NULL;
                _OK;
        };

        EFunctionReturnTypes_t
        RemoveByContents_StartForwardFrom_Until(const T * a_Data,
                        GenericDoubleLinkedElement_st<T> *&from,
                        GenericDoubleLinkedElement_st<T> *&until
                        ) {
                //kill all elements containing a_Data, starting the parse from
                //'from' until 'until', inclusively
                //'from' and 'until' must be from a linked list
                __tIF(until==NULL);
                __tIF(from==NULL);
                __tIF(a_Data==NULL);
                //parse from 'from' until 'until', including.
                GenericDoubleLinkedElement_st<T> *parser=from;
                while (parser!=until->Next) {
                        __tIF(parser==NULL);
                        GenericDoubleLinkedElement_st<T> *whosnext=parser->Next;
                        if (*parser->Data==*a_Data) {
                                GenericDoubleLinkedElement_st<T> *former=parser;
                                //found our element, let's remove it
                                __tIFnok(RemoveFromAnyPlace(parser));
                                //but we're still searching for more elements
                                __tIF(parser!=NULL);
                                if (former==from)
                                        from=NULL;
                                if (former==until) {
                                        until=NULL;
                                        break;
                                }//fi
                        }//fi
                        parser=whosnext;
                        __tIF(until==NULL);
                }//while
                _OK;
        };
};

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/


#endif

