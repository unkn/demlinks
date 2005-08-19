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
        LAME_PROGRAMMER_IF(from==NULL,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(start_from==NULL,
                        return kFuncFailed);
        LAME_PROGRAMMER_IF(howmany<=0,
                        return kFuncFailed);

       GenericSingleLinkedList_st<OneActionsInputTransducer_st> *ptr=start_from;       for (int i=0;i<howmany;i++) {//parse all genericinputs of this type
               //aka all transducers

               PARANOID_IF(ptr==NULL,//unsync-ed howmany
                               return kFuncFailed);

                OneActionsInputTransducer_st *data=ptr->Data;

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
TransformToActions(const GENERICINPUT_TYPE *from)
{//generate items in ActionBuffer, or cumulate 'from' until the combi of genericinputs is complete then genereate one item in ActionBuffer
        LAME_PROGRAMMER_IF(from==NULL,
                        return kFuncFailed);
        if (AI_StrictOrderSLL.Head!=NULL) {
                int howmany=AI_StrictOrderSLL.HowManySoFar;
                ERR_IF(kFuncOK!=ConsumeIntoActionsInput(
                                        from,
                                        AI_StrictOrderSLL.Head,
                                        howmany,
                                        true/*reset combi if failed*/
                                        ),
                                return kFuncFailed);
        }//fi
        if (AI_RelaxedOrderSLL.Head!=NULL) {
                int howmany=AI_RelaxedOrderSLL.HowManySoFar;
                ERR_IF(kFuncOK!=ConsumeIntoActionsInput(
                                        from,
                                        AI_RelaxedOrderSLL.Head,
                                        howmany,
                                        false/*don't reset combi track*/
                                        ),
                                return kFuncFailed);
        }//fi
        return kFuncOK;
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
EFunctionReturnTypes_t
InitActionsInput()
{
//temp
        GENERICINPUT_TYPE *newgi=NULL;
       OneActionsInputTransducer_st *newtrans=NULL;

#define NEWT(_stuff_,_wha_,...)                             \
       newtrans=NULL;                                   \
       newtrans=new OneActionsInputTransducer_st;       \
       ERR_IF(NULL==newtrans,                           \
                       return kFuncFailed);             \
        __VA_ARGS__;                                    \
        {                                               \
                EnumAllAI_t tmpi2=_wha_;                \
                ERR_IF(kFuncOK!=newtrans->Result.Assign(&tmpi2),\
                        return kFuncFailed);            \
        }                                               \
        AI_##_stuff_##OrderSLL.Append(newtrans);


#define NGI(_a_)                                      \
                newgi=NULL;                             \
                newgi=new GENERICINPUT_TYPE;            \
                ERR_IF(newgi==NULL,                     \
                       return kFuncFailed);             \
       {\
                EnumAllGI_t tmpi=_a_;                   \
                ERR_IF(kFuncOK!=newgi->Assign(&tmpi),   \
                                return kFuncFailed);    \
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
        NTE(Hold1KeyPress);
        NTE(Hold1KeyRelease);

//last:
#undef NTE
#undef NEWT
#undef NGI
        PARANOID_IF(AI_StrictOrderSLL.HowManySoFar<=0,
                        return kFuncFailed);
        PARANOID_IF(AI_StrictOrderSLL.Head->Data->HowManySoFar<=0,
                        return kFuncFailed);
        PARANOID_IF(AI_RelaxedOrderSLL.HowManySoFar<=0,
                        return kFuncFailed);
        PARANOID_IF(AI_RelaxedOrderSLL.Head->Data->HowManySoFar<=0,
                        return kFuncFailed);
//FIMXE: feed from file
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
DisposeAISLLArray(AI_SLLTransducersArray_st *which)
{
        //we need to dispose all Data elements, manually
        GenericSingleLinkedList_st<OneActionsInputTransducer_st> *parser=
                which->Head;//head transducer from list of transducers
        for (int j=0;j<which->HowManySoFar;j++){
                //parsing transducers with 'j'
                ERR_IF(parser==NULL,
                                return kFuncFailed);

                OneActionsInputTransducer_st *transd=parser->Data;
                ERR_IF(transd==NULL,
                                return kFuncFailed);

                GenericSingleLinkedList_st<GENERICINPUT_TYPE> *trElem=
                        transd->Head;

                for (int a=0;a<transd->HowManySoFar;a++) {
                        ERR_IF(trElem==NULL,
                                return kFuncFailed);
                        if (trElem->Data!=NULL) {
                                SAFE_delete(trElem->Data);
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
DoneActionsInput()
{
        ERR_IF(kFuncOK!=DisposeAISLLArray(&AI_StrictOrderSLL),
                return kFuncFailed);
        ERR_IF(kFuncOK!=DisposeAISLLArray(&AI_RelaxedOrderSLL),
                return kFuncFailed);
        return kFuncOK;
}
/*****************************************************************************/
OneActionsInputTransducer_st::OneActionsInputTransducer_st()
{//constructor
        Head=NULL;
        Tail=NULL;
        WhosNext=Head;
        HowManySoFar=0;
        EnumAllAI_t tmpi=kAI_Undefined;
        WARN_IF(kFuncOK!=Result.Assign(&tmpi),);
        LostInputsBecauseTheyDidntMatch=0;
}
/*****************************************************************************/
OneActionsInputTransducer_st::~OneActionsInputTransducer_st()
{
        if (Head!=NULL)
                delete Head;/*Tail is done automagically*/
}
/*****************************************************************************/
EFunctionReturnTypes_t
OneActionsInputTransducer_st::Append(const GENERICINPUT_TYPE * a_Dat)
{
        //a COPY OF the CONTENTS of passed param IS NOT MADE

        //GENERICINPUT_TYPE *tmp=new GENERICINPUT_TYPE;
        //*tmp=*a_Dat;
        //done
        if (Head==NULL) {
                //first time
                Head=new GenericSingleLinkedList_st<GENERICINPUT_TYPE>(a_Dat);
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
OneActionsInputTransducer_st::HasStarted()
{
        return ((WhosNext != NULL)&&(WhosNext != Head));
}
/*****************************************************************************/
EFunctionReturnTypes_t
OneActionsInputTransducer_st::RestartIfStarted()
{
        if (HasStarted()) {
                Reset();
        }//fi
        return kFuncOK;
}
/*****************************************************************************/
void
OneActionsInputTransducer_st::Reset()
{
        WhosNext=Head;//reset
}
/*****************************************************************************/
/*EFunctionReturnTypes_t
OneActionsInputTransducer_st::Compare(const GENERICINPUT_TYPE *one,
                const GENERICINPUT_TYPE *two,
                int *result)
{
        PARANOID_IF(one==NULL,
                        return kFuncFailed);
        PARANOID_IF(two==NULL,
                        return kFuncFailed);
        if (*one==*two)
                *result=0;//equat
        else
                *result=-1;//less than equal
        return kFuncOK;
}*/
/*****************************************************************************/
EFunctionReturnTypes_t
OneActionsInputTransducer_st::EatThis(const GENERICINPUT_TYPE *whichgi,
                bool reset_when_failed)
{
        //here we compare *what with WhosNext, if they somehow match
        //then we move along, if that was Tail then we Reset after
        //pushing this ActionsInput to the buffer
        int result;
        PARANOID_IF(WhosNext==NULL,
                        return kFuncFailed);
        PARANOID_IF(WhosNext->Data==NULL,
                        return kFuncFailed);
//FIXME:
        ERR_IF(kFuncOK!=WhosNext->Data->Compare(whichgi,&result),
                        return kFuncFailed);
/*        ERR_IF(kFuncOK!=AllLowLevelInputs[what->type]->Compare(
                                what->data,
                                WhosNext->Data,
                                result),
                        return kFuncFailed);*/
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
OneActionsInputTransducer_st::PushToBuffer()
{
        ERR_IF(kFuncOK!=ActionsInputBuffer.CopyIntoBuffer(&Result),
                        return kFuncFailed);
        return kFuncOK;
}
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/

