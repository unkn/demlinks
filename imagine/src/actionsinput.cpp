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
* Description:.
*
****************************************************************************/

#include "actionsinput.h"
#include "macros.h"

TBuffer<ACTIONSINPUT_TYPE> ActionsInputBuffer(10);
#ifdef ENABLE_TIMED_INPUT
GLOBAL_TIMER_TYPE gLastActionsInputTime;
#endif

//there's only one generic input (source)
AI_SLLTransducersArray_st AI_StrictOrderSLL;//head, may be NULL
AI_SLLTransducersArray_st AI_RelaxedOrderSLL;//head -//-

/*****************************************************************************/
EFunctionReturnTypes_t
ConsumeIntoActionsInput(
        const GENERICINPUT_TYPE *from,
        GenericSingleLinkedList_st<OneActionsInputTransducer_st> *start_from,
        const int howmany,
        bool reset_when_failed)
{
//this would compare "from" with INputs from all 'howmany' Transducers starting
//from "*start_from", each transducer has a list of combinations that when
//fullfilled would push that actionsinput into the ActionsInputBuffer
        __tIF(from==NULL);
        __tIF(start_from==NULL);
        __tIF(howmany<=0);

       GenericSingleLinkedList_st<OneActionsInputTransducer_st> *ptr=start_from;       for (int i=0;i<howmany;i++) {//parse all genericinputs of this type
               //aka all transducers

               __tIF(ptr==NULL);//unsync-ed howmany

                OneActionsInputTransducer_st *data=ptr->Data;

                __tIF(NULL==data);

                //see if tmp->WhosNext == *from
                __tIFnok(data->EatThis(from,reset_when_failed));

                //parse next item from the list of transducers
                ptr=ptr->Next;
        }//for
       _OK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
TransformToActions(const GENERICINPUT_TYPE *from)
{//generate items in ActionBuffer, or cumulate 'from' until the combi of genericinputs is complete then genereate one item in ActionBuffer

        __tIF(from==NULL);
        if (AI_StrictOrderSLL.Head!=NULL) {
                int howmany=AI_StrictOrderSLL.HowManySoFar;
                __tIFnok(ConsumeIntoActionsInput(
                                        from,
                                        AI_StrictOrderSLL.Head,
                                        howmany,
                                        true/*reset combi if failed*/
                                        )
                        );
        }//fi
        if (AI_RelaxedOrderSLL.Head!=NULL) {
                int howmany=AI_RelaxedOrderSLL.HowManySoFar;
                __tIFnok(ConsumeIntoActionsInput(
                                        from,
                                        AI_RelaxedOrderSLL.Head,
                                        howmany,
                                        false/*don't reset combi track*/
                                        )
                        );
        }//fi
        _OK;
}

/*****************************************************************************/
AI_SLLTransducersArray_st::AI_SLLTransducersArray_st()
{//constructor
        Head=NULL;
        Tail=NULL;
        HowManySoFar=0;
}
/*****************************************************************************/
AI_SLLTransducersArray_st::~AI_SLLTransducersArray_st()
{//destructor
        if (Head!=NULL)
                delete Head;/*Tail is done automagically*/
}
/*****************************************************************************/
EFunctionReturnTypes_t
AI_SLLTransducersArray_st::Append(
                        const OneActionsInputTransducer_st * a_Dat)
{
        //a COPY OF the CONTENTS of passed param IS NOT MADE
        //OneActionsInputTransducer_st *tmp=new OneActionsInputTransducer_st;
        //*tmp=*a_Dat;
        //done
        if (Head==NULL) {
                //first time
                Head=new GenericSingleLinkedList_st<OneActionsInputTransducer_st>(a_Dat);
                Tail=Head;
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
function
InitActionsInput()
{
//temp
        GENERICINPUT_TYPE *newgi=NULL;
       OneActionsInputTransducer_st *newtrans=NULL;

#define NEWT(_stuff_,_whatAction_,...)                             \
       newtrans=NULL;                                   \
       newtrans=new OneActionsInputTransducer_st;       \
       __tIF(NULL==newtrans)                           \
        __VA_ARGS__;/*what generic inputs are necessary to cause _whatAction_*/\
        {                                               \
                EnumAllAI_t tmpi2=_whatAction_;                \
                newtrans->Result=tmpi2;     \
        }                                               \
        AI_##_stuff_##OrderSLL.Append(newtrans);


#define NGI(_a_)                                      \
                newgi=NULL;                             \
                newgi=new GENERICINPUT_TYPE;            \
                __tIF(newgi==NULL)                     \
       {\
                EnumAllGI_t tmpi=_a_;                   \
               *newgi=tmpi;                 \
       }\
                newtrans->Append(newgi);

#define NTE(_easier_) \
       NEWT(Strict, kAI_##_easier_ , NGI(kGI_##_easier_));



        NEWT(Strict,
                        kAI_NextSetOfValues,
                        NGI(kGI_NextSetOfValues);
                        NGI(kGI_NextSetOfValues));
        NEWT(Relaxed,
                        kAI_QuitProgram,
                        NGI(kGI_Quit));


        NTE(CamRollRight_byMouse);

        NTE(CamSlideBackward);
        NTE(CamSlideForward);
        NTE(CamSlideDown);
        NTE(CamSlideUp);
        NTE(CamSlideRight);
        NTE(CamSlideLeft);
        NTE(CamRollRight);
        NTE(CamRollLeft);
        NTE(CamPitchDown);
        NTE(CamPitchUp);
        NTE(CamTurnRight);
        NTE(CamTurnLeft);
        NTE(Aspect);
        NTE(FOV);
        NTE(Hold1Key);

        NTE(Hold1Key_stop);
        NTE(CamSlideBackward_stop);
        NTE(CamSlideForward_stop);
        NTE(CamSlideDown_stop);
        NTE(CamSlideUp_stop);
        NTE(CamSlideRight_stop);
        NTE(CamSlideLeft_stop);
        NTE(CamRollRight_stop);
        NTE(CamRollLeft_stop);
        NTE(CamPitchDown_stop);
        NTE(CamPitchUp_stop);
        NTE(CamTurnRight_stop);
        NTE(CamTurnLeft_stop);
        NTE(Aspect_stop);
        NTE(FOV_stop);


//last:
#undef NTE
#undef NEWT
#undef NGI
        __tIF(AI_StrictOrderSLL.HowManySoFar<=0);
        __tIF(AI_StrictOrderSLL.Head->Data->HowManySoFar<=0);
        __tIF(AI_RelaxedOrderSLL.HowManySoFar<=0);
        __tIF(AI_RelaxedOrderSLL.Head->Data->HowManySoFar<=0);
//FIMXE: feed from file
        _OK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
DisposeAISLLArray(AI_SLLTransducersArray_st *which)
//this is done to deallocate memory(ie. on quit)
{
        //we need to dispose all Data elements, manually
        GenericSingleLinkedList_st<OneActionsInputTransducer_st> *parser=
                which->Head;//head transducer from list of transducers
        for (int j=0;j<which->HowManySoFar;j++){
                //parsing transducers with 'j'
                __tIF(parser==NULL);

                OneActionsInputTransducer_st *transd=parser->Data;
                __tIF(transd==NULL);

                GenericSingleLinkedList_st<GENERICINPUT_TYPE> *trElem=
                        transd->Head;

                for (int a=0;a<transd->HowManySoFar;a++) {
                        __tIF(trElem==NULL);
                        if (trElem->Data!=NULL) {
                                SAFE_delete(trElem->Data);
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
DoneActionsInput()
{
        __tIFnok(DisposeAISLLArray(&AI_StrictOrderSLL /*head*/));
        __tIFnok(DisposeAISLLArray(&AI_RelaxedOrderSLL /*head*/));
        _OK;
}
/*****************************************************************************/
OneActionsInputTransducer_st::OneActionsInputTransducer_st()
{//constructor
        Head=NULL;
        Tail=NULL;
        WhosNext=Head;
        HowManySoFar=0;
        Result=kAI_Undefined;
        LostInputsBecauseTheyDidntMatch=0;
}
/*****************************************************************************/
OneActionsInputTransducer_st::~OneActionsInputTransducer_st()
{
        if (Head!=NULL) {
                delete Head;/*Tail is done automagically*/
        }
}
/*****************************************************************************/
function
OneActionsInputTransducer_st::Append(const GENERICINPUT_TYPE * a_Dat)
{
        //a COPY OF the CONTENTS of passed param IS NOT MADE

        //GENERICINPUT_TYPE *tmp=new GENERICINPUT_TYPE;
        //*tmp=*a_Dat;
        //done
        if (Head==NULL) {
                //first time
                Head=new GenericSingleLinkedList_st<GENERICINPUT_TYPE>(a_Dat);
                __tIF(NULL == Head);
                Tail=Head;
                WhosNext=Head;
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
OneActionsInputTransducer_st::HasStarted()
{
        return ((WhosNext != NULL)&&(WhosNext != Head));
}
/*****************************************************************************/
EFunctionReturnTypes_t
OneActionsInputTransducer_st::RestartIfStarted()
{
        if (HasStarted()) {
                __tIFnok( Reset() );
        }//fi
        _OK;
}
/*****************************************************************************/
function //in an attempt to standardize function calls: __tIFnok( Reset() );
OneActionsInputTransducer_st::Reset()
{
        WhosNext=Head;//reset
        _OK;
}
/*****************************************************************************/
/*****************************************************************************/
EFunctionReturnTypes_t
OneActionsInputTransducer_st::EatThis(const GENERICINPUT_TYPE *whichgi,
                bool reset_when_failed)
{
        //here we compare *what with WhosNext, if they somehow match
        //then we move along, if that was Tail then we Reset after
        //pushing this ActionsInput to the buffer
        __tIF(WhosNext==NULL);
        __tIF(WhosNext->Data==NULL);
//FIXME:
/*        ERR_IF(kFuncOK!=AllLowLevelInputs[what->type]->Compare(
                                what->data,
                                WhosNext->Data,
                                result),
                        return kFuncFailed);*/
        if (*WhosNext->Data==*whichgi) {//equal
                __tIF((WhosNext==Tail)&&(WhosNext->Next!=NULL));
                WhosNext=WhosNext->Next;
                if (WhosNext==NULL) {//das wars Tail
#ifdef ENABLE_TIMED_INPUT
                        //then this is one generic input completed
                        GLOBAL_TIMER_TYPE td;
                        GLOBAL_TIMER_TYPE timenow;
                        timenow=whichgi->Time;
                        if (gLastActionsInputTime <= timenow) {
                                td=timenow -gLastActionsInputTime;
                        } else {
                                td=GLOBALTIMER_WRAPSAROUND_AT - gLastActionsInputTime+1+timenow;
                        }//else
                        Result.Time=timenow;
                        Result.TimeDiff=td;
                        gLastActionsInputTime=timenow;
#endif

                        __tIFnok(PushToBuffer());
                        __tIFnok( Reset() );
                }//fi
        }//fi
        else {//not equal
                //reset only if we're StrictOrder
                if (reset_when_failed)
                        __tIFnok( Reset() );
                LostInputsBecauseTheyDidntMatch++;
        }//else
        _OK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
OneActionsInputTransducer_st::PushToBuffer()
{
        __tIFnok(ActionsInputBuffer.CopyIntoBuffer(&Result));
        _OK;
}
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

