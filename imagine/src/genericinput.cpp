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

/*****************************************************************************/
/*****************************************************************************/
EFunctionReturnTypes_t
ResetAllStartedConsecutives(GenericSingleLinkedList_st<OneGenericInputTransducer_st> *start_from,
                const int howmany)
{
        LAME_PROGRAMMER_IF(howmany<=0,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(start_from==NULL,
                        return kFuncFailed);

        //casting from 'const'
        GenericSingleLinkedList_st<OneGenericInputTransducer_st>
                *parser=start_from;
                /*(GenericSingleLinkedList_st<OneGenericInputTransducer_st> *)
                start_from;*/

        for (int k=0;k<howmany;k++) {
                PARANOID_IF(parser==NULL,
                                return kFuncFailed);
                //if started then, it failed since the input we just got
                //is of another type
                ERR_IF(kFuncOK!=
                        parser->Data->RestartIfStarted(),
                        return kFuncFailed);

                parser=parser->Next;
        }//for2
        //unsynced HowMany ? :
        PARANOID_IF(parser!=NULL,
                        return kFuncFailed);

        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
ConsumeIntoGenericInput(
                const UnifiedInput_st *from,
                GenericSingleLinkedList_st<OneGenericInputTransducer_st> *start_from,
                const int howmany,
                bool reset_when_failed)
{
//this would compare "from" with INputs from all 'howmany' Transducers starting
//from "*start_from", each transducer has a list of combinations that when
//fullfilled would push that generic input into the geninputbuffer
        LAME_PROGRAMMER_IF(from==NULL,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(start_from==NULL,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(howmany<=0,
                        return kFuncFailed);

       GenericSingleLinkedList_st<OneGenericInputTransducer_st> *ptr=start_from;
       for (int i=0;i<howmany;i++) {//parse all genericinputs of this type
               //aka all transducers
               PARANOID_IF(ptr==NULL,//unsync-ed howmany
                               return kFuncFailed);

                OneGenericInputTransducer_st *data=ptr->Data;

                PARANOID_IF(NULL==data,
                                return kFuncFailed);

                //see if tmp->WhosNext == *from
                ERR_IF(kFuncOK!=data->EatThis(from,reset_when_failed),
                                return kFuncFailed);

                //parse next item from the list of transducers
                ptr=ptr->Next;
        }//for
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
GenericInputHandler(
                const UnifiedInput_st *from)
{//only one input at a time
//consumes the input(one key OR one mouse...) passed as param
        LAME_PROGRAMMER_IF(from==NULL,
                        return kFuncFailed);
        //see if there are any consecutives that are of other type than 'from'
        //if so, cancel them (aka reset them to zero, they failed)
        for (int i=0;i<kMaxInputTypes;i++) {
                if ((i != from->type)) {//parse all other types
                        //start from the beginning and parse all items of this
                        //type, see if each is started, if so reset it
                        //
                        //* so other types(non from->type) if any begun a
                        //combination, we must reset them all to 0, since what
                        //we just got (from->type) violates the idea of what
                        //they get must be consecutive...

                        int howmany=GI_StrictOrderSLL[i].HowManySoFar;
                        if (howmany>0) {
                                ERR_IF(kFuncOK!=
                                ResetAllStartedConsecutives(
                                                GI_StrictOrderSLL[i].Head,
                                                howmany),
                                        return kFuncFailed);
                        }//fi howmany
                } else {//our type, let's check it now
                                //parse both noncons and cons and give them
                                //our input (from->*)
                        int howmany=GI_StrictOrderSLL[i].HowManySoFar;
                                if (GI_StrictOrderSLL[i].Head!=NULL)
                                ERR_IF(kFuncOK!=
                                        ConsumeIntoGenericInput(from,
                                                GI_StrictOrderSLL[i].Head,
                                                howmany,
                                                true//reset combi if this is
                                                //not what was expected
                                                ),
                                        return kFuncFailed);
                        howmany=GI_RelaxedOrderSLL[i].HowManySoFar;
                                if (GI_RelaxedOrderSLL[i].Head!=NULL)
                                ERR_IF(kFuncOK!=
                                        ConsumeIntoGenericInput(from,
                                               GI_RelaxedOrderSLL[i].Head,
                                                howmany,
                                                false// no reset if not the
                                                        //expected one
                                                        ),
                                        return kFuncFailed);
                }//else was ourtype
        }//for1

        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
InitGenericInput()
{//this sets all the combinations upon which the conversion from multiple input
//to generic input happens.

//temp
        KEY_TYPE *newkey=NULL;
        OneGenericInputTransducer_st *newtrans=NULL;
        LAME_PROGRAMMER_IF(AllLowLevelInputs[kKeyboardInputType]==NULL,
                        return kFuncFailed);
        void *damnbugs=NULL;

#define NEWKT(_stuff_,_wha_,...)                             \
        newtrans=NULL;                                          \
        newtrans=new OneGenericInputTransducer_st;              \
        ERR_IF(newtrans==NULL,                                  \
                       return kFuncFailed);                     \
        __VA_ARGS__;                                            \
                {EnumAllGI_t tmpi=_wha_;                        \
        ERR_IF(kFuncOK!=newtrans->Result.Assign(&tmpi),         \
                        return kFuncFailed);                    \
                }                                               \
        ERR_IF(kFuncOK!=                                        \
                GI_##_stuff_##OrderSLL[kKeyboardInputType].Append(newtrans),\
                                return kFuncFailed);

#define NEWK(_a_)                                             \
        damnbugs=NULL;                                          \
        ERR_IF(kFuncOK!=                                        \
                AllLowLevelInputs[kKeyboardInputType]->Alloc(damnbugs),\
                        return kFuncFailed);                    \
        (void *)newkey=damnbugs;                                \
        ERR_IF(newkey==NULL,                                    \
                        return kFuncFailed);                    \
                newkey->ScanCode=_a_;                           \
                ERR_IF(kFuncOK!=                                \
                                newtrans->Append(newkey),       \
                                return kFuncFailed);            \

#define NTE(_easier_,_akey_) \
        NEWKT(Strict, _easier_, NEWK(PRESS(_akey_)));

//       UnifiedInput_st us;
  //     us.type=kKeyboardInputType;

        //1st
        //us.data=newkey;
       //newtrans=new OneGenericInputTransducer_st;
              //  newkey=new KEY_TYPE;
                //ERR_IF(newkey==NULL,
                  //     return kFuncFailed);
        NEWKT(Relaxed,
                        kGI_NextSetOfValues,
                        NEWK(PRESS(KEY_TILDE));
                        NEWK(RELEASE(KEY_TILDE)));
        NEWKT(Strict,
                        kGI_Quit,
                        NEWK(PRESS(KEY_ESC)));
        NTE(kGI_CamSlideBackward,KEY_S);
        NTE(kGI_CamSlideForward,KEY_W);
        NTE(kGI_CamSlideDown,KEY_CAPSLOCK);
        NTE(kGI_CamSlideUp,KEY_TAB);
        NTE(kGI_CamSlideRight,KEY_D);
        NTE(kGI_CamSlideLeft,KEY_A);
        NTE(kGI_CamRollRight,KEY_E);
        NTE(kGI_CamRollLeft,KEY_Q);
        NTE(kGI_CamPitchDown,KEY_DOWN);
        NTE(kGI_CamPitchUp,KEY_UP);
        NTE(kGI_CamTurnRight,KEY_RIGHT);
        NTE(kGI_CamTurnLeft,KEY_LEFT);
        NTE(kGI_Aspect,KEY_G);
        NTE(kGI_FOV,KEY_F);
        NTE(kGI_Hold1KeyPress,KEY_LSHIFT);
        NEWKT(Strict,
                        kGI_Hold1KeyRelease,
                        NEWK(RELEASE(KEY_LSHIFT)));
        NTE(kGI_Hold1KeyPress,KEY_RSHIFT);
        NEWKT(Strict,
                        kGI_Hold1KeyRelease,
                        NEWK(RELEASE(KEY_RSHIFT)));



//last
#undef NTE
#undef NEWKT
#undef NEWK
//done
        PARANOID_IF(GI_StrictOrderSLL[kKeyboardInputType].HowManySoFar<=0,
                        return kFuncFailed);
        PARANOID_IF(GI_StrictOrderSLL[kKeyboardInputType].Head->Data->HowManySoFar<=0,
                        return kFuncFailed);
        PARANOID_IF(GI_RelaxedOrderSLL[kKeyboardInputType].HowManySoFar<=0,
                        return kFuncFailed);
        PARANOID_IF(GI_RelaxedOrderSLL[kKeyboardInputType].Head->Data->HowManySoFar<=0,
                        return kFuncFailed);


        //FIXME:feed from file
        return kFuncOK;
}

/*****************************************************************************/
EFunctionReturnTypes_t
DisposeGISLLArray(GI_SLLTransducersArray_st *which, const int input_type)
{
        //we need to dispose all Data elements, manually
        PARANOID_IF(NULL==which,
                        return kFuncFailed);
        GenericSingleLinkedList_st<OneGenericInputTransducer_st> *parser=
                which->Head;//head transducer from list of transducers
        for (int j=0;j<which->HowManySoFar;j++){
                //parsing transducers with 'j'
                ERR_IF(parser==NULL,
                                return kFuncFailed);

                OneGenericInputTransducer_st *transd=parser->Data;
                ERR_IF(transd==NULL,
                                return kFuncFailed);

                GenericSingleLinkedList_st<TRANSDUCER_S__TYPE> *trElem=transd->Head;

                for (int a=0;a<transd->HowManySoFar;a++) {
                        ERR_IF(trElem==NULL,
                                return kFuncFailed);
                        if (trElem->Data!=NULL) {
                                PARANOID_IF(AllLowLevelInputs[input_type]==NULL,
                                                return kFuncFailed);
                                ERR_IF(kFuncOK!=
                                     AllLowLevelInputs[input_type]->DeAlloc(trElem->Data),
                                     return kFuncFailed);
                        }//fi
                        trElem=trElem->Next;
                }//for3

                //kills all within 'transd'(incl. Head..Tail)
                //SAFE_delete(transd);//unsafe
                parser=parser->Next;
        }//for2
        return kFuncOK;
}
/*****************************************************************************/

EFunctionReturnTypes_t
DoneGenericInput()
{
        for (int i=0;i<kMaxInputTypes;i++) {
                ERR_IF(kFuncOK!=DisposeGISLLArray(&GI_StrictOrderSLL[i],i),
                        return kFuncFailed);
                ERR_IF(kFuncOK!=DisposeGISLLArray(&GI_RelaxedOrderSLL[i],i),
                        return kFuncFailed);
        }//for
        return kFuncOK;
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
                EnumAllGI_t tmpi=kGI_Undefined;
                WARN_IF(kFuncOK!=Result.Assign(&tmpi),);

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
        if (Head!=NULL)
                delete Head;/*Tail is done automagically*/
}
/*****************************************************************************/
EFunctionReturnTypes_t
OneGenericInputTransducer_st::Append(const TRANSDUCER_S__TYPE * a_Dat)
{
        //a COPY OF the CONTENTS of passed param IS NOT MADE
        PARANOID_IF(NULL==a_Dat,
                        return kFuncFailed);
/*        int input_type=a_Dat->type;

         LAME_PROGRAMMER_IF((input_type<0) ||(input_type>=kMaxInputTypes),
                        return kFuncFailed);
        PARANOID_IF(NULL==AllLowLevelInputs[input_type],
                        return kFuncFailed);
        TRANSDUCER_S__TYPE *tmp=NULL;
        //a thing gets allocated here which must and will be disposed upon
        //destruction of entire transducer
        ERR_IF(kFuncOK!=AllLowLevelInputs[input_type]->Alloc(tmp),
                        return kFuncFailed);
        PARANOID_IF(NULL==tmp,
                        return kFuncFailed);
        ERR_IF(kFuncOK!=AllLowLevelInputs[input_type]->CopyContents(
                                (const TRANSDUCER_S__TYPE*)a_Dat->data,tmp),
                        return kFuncFailed);
        */
        if (Head==NULL) {
                //first time
                Head=new GenericSingleLinkedList_st<TRANSDUCER_S__TYPE>(a_Dat);
                Tail=Head;
                WhosNext=Head;
                ERR_IF(Head==NULL,
                                return kFuncFailed);
        } else {
                //already has at least one stuff allocated
                //thus we append
                ERR_IF(kFuncOK!=Tail->New(a_Dat),
                                return kFuncFailed);
                Tail=Tail->Next;
                PARANOID_IF(Tail==NULL,
                                return kFuncFailed);
        }//else
        HowManySoFar++;
        return kFuncOK;
}
/*****************************************************************************/
bool
OneGenericInputTransducer_st::HasStarted()
{
        return ((WhosNext != NULL)&&(WhosNext != Head));
}
/*****************************************************************************/
EFunctionReturnTypes_t
OneGenericInputTransducer_st::RestartIfStarted()
{
        if (HasStarted()) {
                Reset();
        }//fi
        return kFuncOK;
}
/*****************************************************************************/
void
OneGenericInputTransducer_st::Reset()
{
        WhosNext=Head;//reset
}
/*****************************************************************************/
EFunctionReturnTypes_t
OneGenericInputTransducer_st::EatThis(const UnifiedInput_st *what,
                bool reset_when_failed)
{
        //here we compare *what with WhosNext, if they somehow match
        //then we move along, if that was Tail then we Reset after
        //pushing this GenericInput to the buffer
        int result;
        PARANOID_IF((what->type >=kMaxInputTypes) || (what->type<0),
                return kFuncFailed);
        PARANOID_IF(WhosNext==NULL,
                        return kFuncFailed);
        PARANOID_IF(WhosNext->Data==NULL,
                        return kFuncFailed);
        ERR_IF(kFuncOK!=AllLowLevelInputs[what->type]->Compare(
                                what->data,
                                WhosNext->Data,
                                result),
                        return kFuncFailed);
        //FIXME:perhaps provide a * or ? like A_*A~ any num of keys
        //between A pressed and A released; with a certain limit tho
        if (result==0) {//equal
                PARANOID_IF((WhosNext==Tail)&&(WhosNext->Next!=NULL),
                                return kFuncFailed);
                WhosNext=WhosNext->Next;
                if (WhosNext==NULL) {//das wars Tail
                        //then this is one generic input completed
                        ERR_IF(kFuncOK!=PushToBuffer(),
                                        return kFuncFailed);
                        Reset();
                }//fi
        }//fi
        else {//not equal
                //reset only if we're StrictOrder
                if (reset_when_failed)
                        Reset();
                LostInputsBecauseTheyDidntMatch++;
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
OneGenericInputTransducer_st::PushToBuffer()
{
        ERR_IF(kFuncOK!=GenericInputBuffer.CopyIntoBuffer(&Result),
                        return kFuncFailed);
        return kFuncOK;
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
{
        if (Head!=NULL)
                delete Head;/*Tail is done automagically*/
}
/*****************************************************************************/
EFunctionReturnTypes_t
GI_SLLTransducersArray_st::Append(
                const OneGenericInputTransducer_st * a_Dat)
{
        //a COPY OF the CONTENTS of passed param IS NOT MADE, so a_Dat must
        //be preallocated before calling this func, and must not be disposed
        //outside this func

        PARANOID_IF(NULL==a_Dat,
                        return kFuncFailed);
/*        OneGenericInputTransducer_st *tmp=new OneGenericInputTransducer_st;
        PARANOID_IF(NULL==tmp,
                        return kFuncFailed);
        *tmp=*a_Dat;
        tmp=(OneGenericInputTransducer_st *)a_Dat;//FIXME:
        PARANOID_IF(tmp->Result!=a_Dat->Result,
                        return kFuncFailed);
        PARANOID_IF(tmp->HowManySoFar != a_Dat->HowManySoFar,
                        return kFuncFailed);
        PARANOID_IF(tmp->Head != a_Dat->Head,
                        return kFuncFailed);*/
        //done
        if (Head==NULL) {
                //first time
                Head=new GenericSingleLinkedList_st<OneGenericInputTransducer_st>(a_Dat);
                Tail=Head;
                ERR_IF(Head==NULL,
                                return kFuncFailed);
        } else {
                //already has at least one stuff allocated
                //thus we append
                ERR_IF(kFuncOK!=Tail->New(a_Dat),
                                return kFuncFailed);
                PARANOID_IF(Tail==NULL,
                                return kFuncFailed);
                Tail=Tail->Next;
                PARANOID_IF(Tail==NULL,
                                return kFuncFailed);
        }//else
        HowManySoFar++;
        return kFuncOK;
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
