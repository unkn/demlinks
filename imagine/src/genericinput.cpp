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

//#include <stdio.h>

#include "_gcdefs.h"
/*****************************************************************************/

#include "pnotetrk.h"
#include "genericinput.h"
#include "buffer.h"
#include "macros.h"

/*****************************************************************************/
//here the order of recomposing the combination of inputs matters
GI_SLLTransducersArray_st GI_StrictOrderSLL[kMaxInputTypes];//head, may be NULL

//here it doesn't:
GI_SLLTransducersArray_st GI_RelaxedOrderSLL[kMaxInputTypes];//head -//-

TBuffer<GENERICINPUT_TYPE> GenericInputBuffer(10);
#ifdef ENABLE_TIMED_INPUT
GLOBAL_TIMER_TYPE gLastGenericInputTime;
#endif

/*****************************************************************************/
/*****************************************************************************/
EFunctionReturnTypes_t
ResetAllStartedConsecutives(GenericSingleLinkedList_st<OneGenericInputTransducer_st> *start_from,
                const int howmany)
{

        __tIF(howmany<=0);
        __tIF(start_from==NULL);

        //casting from 'const'
        GenericSingleLinkedList_st<OneGenericInputTransducer_st>
                *parser=start_from;
                /*(GenericSingleLinkedList_st<OneGenericInputTransducer_st> *)
                start_from;*/

        for (int k=0;k<howmany;k++) {
                __tIF(parser==NULL);
                //if started then, it failed since the input we just got
                //is of another type
                __tIFnok(parser->Data->RestartIfStarted());

                parser=parser->Next;
        }//for2
        //unsynced HowMany ? :
        __tIF(parser!=NULL);

        _OK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
ConsumeIntoGenericInput(
                const UnifiedInput_st & from,
                GenericSingleLinkedList_st<OneGenericInputTransducer_st> *start_from,
                const int howmany,
                bool reset_when_failed)
{

//this would compare "from" with INputs from all 'howmany' Transducers starting
//from "*start_from", each transducer has a list of combinations that when
//fullfilled would push that generic input into the geninputbuffer
        __tIF(start_from==NULL);
        __tIF(howmany<=0);

       GenericSingleLinkedList_st<OneGenericInputTransducer_st> *ptr=start_from;
       for (int i=0;i<howmany;i++) {//parse all genericinputs of this type
               //aka all transducers
               __tIF(ptr==NULL);//unsync-ed howmany

                OneGenericInputTransducer_st *data=ptr->Data;

                __tIF(NULL==data);

                //see if tmp->WhosNext == *from
                __fIFnok(data->EatThis(from,reset_when_failed));

                //parse next item from the list of transducers
                ptr=ptr->Next;
        }//for
       _OK;
}

/*****************************************************************************/
function
GenericInputHandler(
                const UnifiedInput_st & from)
{//only one input at a time
//consumes the input(one key OR one mouse...) passed as param
        //see if there are any consecutives that are of other type than 'from'
        //if so, cancel them (aka reset them to zero, they failed)
        for (int i=0; i < kMaxInputTypes; i++) {
                if ((i != from.type)) {//parse all other types
                        //start from the beginning and parse all items of this
                        //type, see if each is started, if so reset it
                        //
                        //* so other types(non from->type) if any begun a
                        //combination, we must reset them all to 0, since what
                        //we just got (from->type) violates the idea of what
                        //they get must be consecutive...

                        int howmany=GI_StrictOrderSLL[i].HowManySoFar;
                        if (howmany>0) {
                                __tIFnok(
                                ResetAllStartedConsecutives(
                                                GI_StrictOrderSLL[i].Head,
                                                howmany)
                                );
                        }//fi howmany
                } else {//our type, let's check it now
                                //parse both noncons and cons and give them
                                //our input (from->*)
                        int howmany=GI_StrictOrderSLL[i].HowManySoFar;
                                if (GI_StrictOrderSLL[i].Head!=NULL)
                                __fIFnok(
                                        ConsumeIntoGenericInput(from,
                                                GI_StrictOrderSLL[i].Head,
                                                howmany,
                                                true//reset combi if this is
                                                //not what was expected
                                                )
                                        );
                        howmany=GI_RelaxedOrderSLL[i].HowManySoFar;
                                if (GI_RelaxedOrderSLL[i].Head!=NULL)
                                __tIFnok(
                                        ConsumeIntoGenericInput(from,
                                               GI_RelaxedOrderSLL[i].Head,
                                                howmany,
                                                false// no reset if not the
                                                        //expected one
                                                        )
                                        );
                }//else was ourtype
        }//for1

        _OK;
}

/*****************************************************************************/
function
InitGenericInput()
{//this sets all the combinations upon which the conversion from multiple input
//to generic input happens.


//temp
        KEY_TYPE *newkey=NULL;
        MOUSE_TYPE *newmouse=NULL;
        OneGenericInputTransducer_st *newtrans=NULL;
        for (int i=0; i<kMaxInputTypes; i++) {
                __tIF(AllLowLevelInputs[i]==NULL);
        }
        void *damnbugs=NULL;

#define NEWTRANSITION(_typ_,_stuff_,_wha_,...)                             \
        newtrans=NULL;                                          \
        newtrans=new OneGenericInputTransducer_st;              \
        __tIF(newtrans==NULL);                                  \
        __VA_ARGS__;                                            \
                {EnumAllGI_t tmpi=_wha_;                        \
                        newtrans->Result=tmpi;      \
                }                                               \
        __tIFnok(                                        \
                GI_##_stuff_##OrderSLL[k##_typ_##InputType].Append(newtrans));

#define NEWKT(_stuff_,_wha_,...)                             \
        NEWTRANSITION(Keyboard,_stuff_,_wha_,__VA_ARGS__);

#define NEWK(_a_)                                             \
        damnbugs=NULL;                                          \
        __tIFnok(                                        \
                AllLowLevelInputs[kKeyboardInputType]->Alloc(damnbugs));\
        newkey=(KEY_TYPE *)damnbugs;                                \
        __tIF(newkey==NULL);                                    \
                newkey->ScanCode=_a_;                           \
                __tIFnok( newtrans->Append(newkey) );

#define NEWMT(_stuff_,_wha_,...)                             \
        NEWTRANSITION(Mouse,_stuff_,_wha_,__VA_ARGS__);

#define NEWMF(_a_)                                             \
        damnbugs=NULL;                                          \
        __tIFnok(                                        \
                AllLowLevelInputs[kMouseInputType]->Alloc(damnbugs));\
        newmouse=(MOUSE_TYPE *)damnbugs;                                \
        __tIF(newmouse==NULL);                                    \
        newmouse->Flags=_a_;                           \
        __tIFnok( newtrans->Append(newmouse) );

#define NEWME(_easier_,_aflag_) \
        NEWMT(Strict, _easier_, NEWMF(_aflag_));

#define NTE(_easier_,_akey_) \
        NEWKT(Strict, _easier_, NEWK(PRESS(_akey_)));

#define NTB(_easier_,_akey_) \
        NEWKT(Strict, _easier_, NEWK(PRESS(_akey_)));   \
        NEWKT(Strict, _easier_##_stop, NEWK(RELEASE(_akey_)));


        NEWKT(Relaxed,
                        kGI_NextSetOfValues,
                        NEWK(PRESS(KEY_TILDE));
                        NEWK(RELEASE(KEY_TILDE)));
        NEWKT(Strict,
                        kGI_Quit,
                        NEWK(PRESS(KEY_ESC)));

        NTB(kGI_CamSlideBackward,KEY_S);
        NTB(kGI_CamSlideForward,KEY_W);
        NTB(kGI_CamSlideDown,KEY_CAPSLOCK);
        NTB(kGI_CamSlideUp,KEY_TAB);
        NTB(kGI_CamSlideRight,KEY_D);
        NTB(kGI_CamSlideLeft,KEY_A);
        NTB(kGI_CamRollRight,KEY_E);
        NTB(kGI_CamRollLeft,KEY_Q);
        NTB(kGI_CamPitchDown,KEY_DOWN);
        NTB(kGI_CamPitchUp,KEY_UP);
        NTB(kGI_CamTurnRight,KEY_RIGHT);
        NTB(kGI_CamTurnLeft,KEY_LEFT);
        NTB(kGI_Aspect,KEY_G);
        NTB(kGI_FOV,KEY_F);

        NTB(kGI_Hold1Key,KEY_LSHIFT);
        NTB(kGI_Hold1Key,KEY_RSHIFT);


        NEWME(kGI_CamRollRight_byMouse,MOUSE_FLAG_MOVE);

//last
#undef NTE
#undef NTE
#undef NEWTRANSITION
#undef NEWMT
#undef NEWMF
#undef NEWME
#undef NEWKT
#undef NEWK
//done
        __tIF(GI_StrictOrderSLL[kKeyboardInputType].HowManySoFar<=0);
        __tIF(GI_StrictOrderSLL[kKeyboardInputType].Head->Data->HowManySoFar<=0);
        __tIF(GI_RelaxedOrderSLL[kKeyboardInputType].HowManySoFar<=0);
        __tIF(GI_RelaxedOrderSLL[kKeyboardInputType].Head->Data->HowManySoFar<=0);


        //FIXME:feed from file;NO, will feed from the demlinks environment(kept inside a berkeley database)
        _OK;
}

/*****************************************************************************/
function
DisposeGISLLArray(GI_SLLTransducersArray_st *which, const int input_type)
{
        //we need to dispose all Data elements, manually
        __tIF(NULL==which);

        GenericSingleLinkedList_st<OneGenericInputTransducer_st> *parser=
                which->Head;//head transducer from list of transducers
        for (int j=0;j<which->HowManySoFar;j++){
                //parsing transducers with 'j'
                __tIF(parser==NULL);

                OneGenericInputTransducer_st *transd=parser->Data;
                __tIF(transd==NULL);

                GenericSingleLinkedList_st<TRANSDUCER_S__TYPE> *trElem=transd->Head;

                for (int a=0;a<transd->HowManySoFar;a++) {
                        __tIF(trElem == NULL);
                        if (trElem->Data != NULL) {
                                __tIF(AllLowLevelInputs[input_type]==NULL);
                                __tIFnok( AllLowLevelInputs[input_type]->DeAlloc(trElem->Data) );
                        }//fi
                        trElem=trElem->Next;
                }//for3

                //kills all within 'transd'(incl. Head..Tail)
                //SAFE_delete(transd);//unsafe
                parser=parser->Next;
        }//for2
        _OK;
}
/*****************************************************************************/

function
DoneGenericInput()
{
        for (int i=0;i<kMaxInputTypes;i++) {
                __tIFnok(DisposeGISLLArray(&GI_StrictOrderSLL[i],i));
                __tIFnok(DisposeGISLLArray(&GI_RelaxedOrderSLL[i],i));
        }//for
        _OK;
}

/*****************************************************************************/
/*****************************************************************************/
OneGenericInputTransducer_st::OneGenericInputTransducer_st()
{//constructor
//        Head=NULL;
                Head=Tail=NULL;
                WhosNext=Head;
                HowManySoFar=0;
                LostInputsBecauseTheyDidntMatch=0;
                Result=kGI_Undefined;

  //      MakeSureChildsAreGone();
/*        Tail=NULL;
        WhosNext=Head;
        HowManySoFar=0;
        Result=kGI_Undefined;//aka must be set later
        LostInputsBecauseTheyDidntMatch=0;*/
}
/*****************************************************************************/
OneGenericInputTransducer_st::~OneGenericInputTransducer_st()
{
        if (Head!=NULL) {
                delete Head;/*Tail is done automagically*/
                //Head=NULL; this is the destructor you know ;)
        }
}
/*****************************************************************************/
EFunctionReturnTypes_t
OneGenericInputTransducer_st::Append(const TRANSDUCER_S__TYPE * a_Dat)
{
        //a COPY OF the CONTENTS of passed param IS NOT MADE
        __tIF(NULL==a_Dat);

        if (Head==NULL) {
                //first time
                Head=new GenericSingleLinkedList_st<TRANSDUCER_S__TYPE>(a_Dat);
                Tail=Head;
                WhosNext=Head;
                __tIF(Head==NULL);
        } else {
                //already has at least one stuff allocated
                //thus we append
                __tIFnok(Tail->New(a_Dat));
                Tail=Tail->Next;
                __tIF(Tail==NULL);
        }//else
        HowManySoFar++;
        _OK;
}
/*****************************************************************************/
bool
OneGenericInputTransducer_st :: HasStarted()
{
        return ((WhosNext != NULL)&&(WhosNext != Head));
}
/*****************************************************************************/
function
OneGenericInputTransducer_st :: RestartIfStarted()
{
        __if (HasStarted()) {
                __tIFnok( Reset() );
        }__fi

        _OK;
}
/*****************************************************************************/
function
OneGenericInputTransducer_st :: Reset()
{
        WhosNext=Head;//reset
        _OK;
}
/*****************************************************************************/
function
OneGenericInputTransducer_st :: EatThis(
                const UnifiedInput_st & what,
                const bool reset_when_failed)
{
        //here we compare *what with WhosNext, if they somehow match
        //then we move along, if that was Tail then we Reset after
        //pushing this GenericInput to the buffer
        int result;
        __tIF( (what.type >= kMaxInputTypes) || (what.type < 0) );
        __tIF(WhosNext == NULL);//FIXME: can't get to the 'if' section below, thus this func throws when what==Tail (last element of buffer)
        __tIF(WhosNext->Data == NULL);
        __tIFnok(AllLowLevelInputs[what.type]->Compare(
                                what.data,
                                WhosNext->Data,
                                result)
                );
        //FIXME:perhaps provide a * or ? like A_*A~ any num of keys
        //between A pressed and A released; with a certain limit tho
        if (result==0) {//equal
                __tIF((WhosNext==Tail)&&(WhosNext->Next!=NULL));
                WhosNext=WhosNext->Next;
                if (WhosNext==NULL) {//das wars Tail
                        //then this is one generic input completed
#ifdef ENABLE_TIMED_INPUT
                        GLOBAL_TIMER_TYPE td;
                        GLOBAL_TIMER_TYPE timenow;
                        __tIFnok(AllLowLevelInputs[what.type]->GetMeTime(what.data,&timenow) );
                        if (gLastGenericInputTime<=timenow) {
                                td=timenow -gLastGenericInputTime;
                        } else {
                                td=GLOBALTIMER_WRAPSAROUND_AT - gLastGenericInputTime+1+timenow;
                        }//else
                        Result.Time=timenow;
                        Result.TimeDiff=td;
                        gLastGenericInputTime=timenow;
#endif

                        __fIFnok( PushToBuffer() );
                        __tIFnok( Reset() );
                }//fi
        }//fi
        else {//not equal
                //reset only if we're StrictOrder
                if (reset_when_failed) {
                        __tIFnok( Reset() );
                }
                LostInputsBecauseTheyDidntMatch++;
        }//else
        _OK;
}
/*****************************************************************************/
function
OneGenericInputTransducer_st :: PushToBuffer()
{
        __fIFnok(GenericInputBuffer.CopyIntoBuffer(&Result));
        _OK;
}
/*****************************************************************************/
GI_SLLTransducersArray_st::GI_SLLTransducersArray_st()
{//constructor
        Head=NULL;
        Tail=NULL;
        HowManySoFar=0;
}
/*****************************************************************************/
GI_SLLTransducersArray_st::~GI_SLLTransducersArray_st()
{//destructor me
        if (Head!=NULL) {
                delete Head;/*Tail is done automagically*/
        }
}
/*****************************************************************************/
function
GI_SLLTransducersArray_st::Append(
                const OneGenericInputTransducer_st * const a_Dat)
{
        //a COPY OF the CONTENTS of passed param IS NOT MADE, so a_Dat must
        //be preallocated before calling this func, and must not be disposed
        //outside this func

        __tIF(NULL==a_Dat);
        if (Head==NULL) {
                //first time
                Head=new GenericSingleLinkedList_st<OneGenericInputTransducer_st>(a_Dat);
                __tIF(NULL == Head);
                Tail=Head;
        } else {
                //already has at least one stuff allocated
                //thus we append
                __tIFnok(Tail->New(a_Dat));
                __tIF(Tail==NULL);
                Tail=Tail->Next;
                __tIF(Tail==NULL);
        }//else
        HowManySoFar++;
        _OK;
}
/*****************************************************************************/
/*OneGenericInputTransducer_st&
OneGenericInputTransducer_st::operator=(
        const OneGenericInputTransducer_st & source)
{
        if (&source==this)
                return *this;
        MakeSureChildsAreGone();//Head==Tail==NULL
//FIXME: we'd rather need a copy constructor here
        return *this;
}*/
/*****************************************************************************/
/*void
OneGenericInputTransducer_st::MakeSureChildsAreGone()
{
        if (Head!=NULL) {
                delete Head;//Tail is done automagically
                Head=Tail=NULL;
                WhosNext=Head;
                HowManySoFar=0;
                Result=kGI_Undefined;//aka must be set later
                LostInputsBecauseTheyDidntMatch=0;
        }//fi
}*/
/*****************************************************************************/
/*OneGenericInputTransducer_st::OneGenericInputTransducer_st(
                const OneGenericInputTransducer_st &rhs)
{
        //FIXME:
}*/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
