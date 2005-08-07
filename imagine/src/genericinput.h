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

#ifndef GENERICINPUT_H
#define GENERICINPUT_H
/*****************************************************************************/
#include <allegro.h>
#include "timedinput.h"

/*
#define OFS_TYPE int
#define MAX_INPUTS_IN_REPLAY_BUFFER (OFS_TYPE(200))
#define MAX_KEYS_IN_REPLAY_BUFFER (100)
#define MAX_MOUSE_IN_REPLAY_BUFFER (200)
class TReplayBuffer {
private:
INPUT_TYPE InputReplayBuffer[MAX_INPUTS_IN_REPLAY_BUFFER];
KEY_TYPE KeyReplayBuffer[MAX_KEYS_IN_REPLAY_BUFFER];
MOUSE_TYPE MouseReplayBuffer[MAX_MOUSE_IN_REPLAY_BUFFER];

OFS_TYPE Ihowmany,Khowmany,Mhowmany;

        EFunctionReturnTypes_t Add2Input(const int a_Type);
        EFunctionReturnTypes_t Add2Key(const KEY_TYPE *from);
        EFunctionReturnTypes_t Add2Mouse(const MOUSE_TYPE *from);
public:
        TReplayBuffer();
        ~TReplayBuffer();

        EFunctionReturnTypes_t SaveBuffer();//append
        EFunctionReturnTypes_t LoadBuffer();//reload
        EFunctionReturnTypes_t PutInput(const int a_Type,
                        const void *from);
};
*/
/*****************************************************************************/
/*****************************************************************************/
enum {
        kTrigger_ActivateAction=0,
        kTrigger_DeActivateAction 
                //please no more additions here, since it depends on bool aka
                //two values
//last
        ,kMaxTriggers
};

enum {
        kInputInt=0,
        kKeyInt,
        kMouseInt,
        //last:
        kMax_Int
};
enum {
        kOfsType,
        kHowManyType,
        //last:
        kMax_Types
};
struct Act_st {
        INPUT_TYPE CombiInputBuf[MAX_INPUT_EVENTS_BUFFERED];
        //int CombiVars[kHowManyType][kInputInt];
        //int CombiVars[kOfsType][kInputInt];//the ofs which is next to be tested

        KEY_TYPE CombiKeyBuf[MAX_KEYS_BUFFERED];
        //int CombiVars[kHowManyType][kKeyInt];
        //int CombiVars[kOfsType][kKeyInt];

        MOUSE_TYPE CombiMouseBuf[MAX_MOUSE_EVENTS_BUFFERED];
        //int CombiVars[kHowManyType][kMouseInt];
        //int CombiVars[kOfsType][kMouseInt];
        int CombiVars[kMax_Types][kMax_Int];
//funx:
        EFunctionReturnTypes_t GoNextInputType() {
                this->CombiVars[kOfsType][kInputInt]++;//prepare next part of combination
                PARANOID_IF(this->CombiVars[kOfsType][kInputInt] > this->CombiVars[kHowManyType][kInputInt],
                                return kFuncFailed;);
                return kFuncOK;
        };
        bool CombiCompleted() {
                return (this->CombiVars[kOfsType][kInputInt] == this->CombiVars[kHowManyType][kInputInt]);
        };
        bool CombiIsThisIntDone(int which_int) {
         return (this->CombiVars[kOfsType][which_int] == this->CombiInputBuf[this->CombiVars[kOfsType][kInputInt]].how_many);
        };
        EFunctionReturnTypes_t GoNext(int which_int) {
                this->CombiVars[kOfsType][which_int]++;//try to go next
                PARANOID_IF(this->CombiVars[kOfsType][which_int] >
                                this->CombiInputBuf[this->CombiVars[kOfsType][kInputInt]].how_many,
                                return kFuncFailed;);
                return kFuncOK;
        };
        Act_st(){//constructor
                for (int i=0;i<kMax_Types;i++) {
                        for (int j=0;j<kMax_Int;j++) {
                                CombiVars[i][j]=0;
                        }//for2
                }//for
                for(int i=0;i<MAX_INPUT_EVENTS_BUFFERED;i++) {
                        CombiInputBuf[i].type=kNoInputType;
                        CombiInputBuf[i].how_many=0;
                }//for2
        };

        Act_st &
        Act_st::operator=(const Act_st & source)
        {
        if (&source==this)
                return *this;
                for (int i=0;i<kMax_Types;i++) {
                        for (int j=0;j<kMax_Int;j++) {
                                CombiVars[i][j]=source.CombiVars[i][j];
                        }//for2
                }//for
/*        CombiVars[kHowManyType][kInputInt]=source.CombiVars[kHowManyType][kInputInt];
        CombiVars[kHowManyType][kKeyInt]=source.CombiVars[kHowManyType][kKeyInt];
        CombiVars[kHowManyType][kMouseInt]=source.CombiVars[kHowManyType][kMouseInt];*/
        for (int i=0;i<MAX_INPUT_EVENTS_BUFFERED;i++) {
                CombiInputBuf[i]=source.CombiInputBuf[i];
        }
        for (int i=0;i<MAX_KEYS_BUFFERED;i++) {
                CombiKeyBuf[i]=source.CombiKeyBuf[i];
        }
        for (int i=0;i<MAX_MOUSE_EVENTS_BUFFERED;i++) {
                CombiMouseBuf[i]=source.CombiMouseBuf[i];
        }
        return *this;
        }

};
//don't use this in your programs:
class TAnyAction {
protected:
        void (*RunAsActive)(void);
        void (*RunAsNotActive)(void);

        //bool fIsEnabled[kMaxTriggers];//aka is enabled for x trigger
        bool fIsActive;//aka action is running
        bool fConstructed;//false if within constructor
        bool fState;//used as true to signal that a combination was recognized


        EFunctionReturnTypes_t _AddInput(int which_trigger,int type);

        void
        TAnyAction::ZeroOffsets(Act_st *act);

        EFunctionReturnTypes_t TriggerIndexWithinBounds(int which){
                ERR_IF(which < 0,
                                return kFuncFailed;);
                ERR_IF(which >=kMaxTriggers,
                                return kFuncFailed;);
                return kFuncOK;
        };
public:
        Act_st Act[kMaxTriggers];

        /*EFunctionReturnTypes_t DisableTrigger(int which){
                ERR_IF(kFuncOK != TriggerIndexWithinBounds(which),
                                return kFuncFailed);
                fIsEnabled[which]=false;
                return kFuncOK;
        };
        EFunctionReturnTypes_t EnableTrigger(int which){
                ERR_IF(kFuncOK != TriggerIndexWithinBounds(which),
                                return kFuncFailed);
                fIsEnabled[which]=true;
                return kFuncOK;
        };*/

        //Act_st *getAct(int k){return &Act[k];};

        /*
        EFunctionReturnTypes_t SetCombiKeyBuf(
                        int a_Input2ActAction,KEY_TYPE from[],int howmany);
        EFunctionReturnTypes_t SetCombiMouseBuf(
                        int a_Input2ActAction,MOUSE_TYPE from[],int howmany);
        EFunctionReturnTypes_t SetCombiInputBuf(
                        int a_Input2ActAction,INPUT_TYPE from[],int howmany);
*/

        void ClearState() {fState=false;};
        bool HasStateChanged() {return fState;};
        void SetStateChanged() {fState=true;};
        
        EFunctionReturnTypes_t SetFunx(
                                void (*func_active)(void),
                                void (*func_not_active)(void)){
                PARANOID_IF(func_active==NULL,
                        return kFuncFailed;);
                //PARANOID_IF(func_not_active==NULL,
                  //      return kFuncFailed;);

                RunAsActive=func_active;
                RunAsNotActive=func_not_active;

                return kFuncOK;
        };

        TAnyAction();//constructor
        virtual ~TAnyAction();

        //run the func asoc. with action only if active
        //this is run as long a action is active
        EFunctionReturnTypes_t PerformActive(){
                //if (IsActive()) {
                        PARANOID_IF(NULL==RunAsActive,return kFuncFailed);
                        RunAsActive();

                        //deactivate after one run, if no action on non-active
                        //this means that if there's no way to deactivate the
                        //action, no procedure, then we deactivate it now 
                        //this is prolly a toggle key
                        //if (RunAsNotActive==NULL)
                          //      SetNotActive();
                //}
                return kFuncOK;
        };

        //run it when it becomes non-active; this is run only once
        EFunctionReturnTypes_t PerformNotActive(){
                //if (!IsActive()) {
                        PARANOID_IF(NULL==RunAsNotActive,return kFuncFailed);
                        RunAsNotActive();
                //}
                return kFuncOK;
        };



        //the combination of inputs which triggers the action
        EFunctionReturnTypes_t AddKey(int which_trigger,const KEY_TYPE *from);
        EFunctionReturnTypes_t AddMouse(int which_trigger,const MOUSE_TYPE *from);

        bool IsActive();
        void SetActive();
        void SetNotActive();

        EFunctionReturnTypes_t
        HandleKey(const KEY_TYPE *from,const KEY_TYPE *existing,bool * result);

        EFunctionReturnTypes_t
        HandleMouse(const MOUSE_TYPE *from,Act_st *which_act,bool * result);

        virtual EFunctionReturnTypes_t HandleInput(const int a_Type,
                        const void *from,
                        const int which_trigger,
                        bool *result);

        EFunctionReturnTypes_t
        CompareKeys(
                        const KEY_TYPE *one,
                        const KEY_TYPE *two,
                        int *result);

};
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
#endif
