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

#include <stdio.h>

#include <allegro.h>

#include "_gcdefs.h"

#include "common.h"
#include "timedinput.h"//both mouse and keyboard united
#include "pnotetrk.h"
//#include "genericinput.h"
#include "combifile.h"
#include "input.h"

/*
enum {
        kActQuit=0
//last
        ,kAllocatedActions
}; 

const char *kCombiFileName="combinations.dat";

TAnyAction *AllActions[kAllocatedActions];//TODO:must be a double linked list

void (*activation_funcs[kAllocatedActions])(void);
void (*deactivation_funcs[kAllocatedActions])(void);
*/

/*
enum {
        kKeyDone=0,
        kKeyConsecutive,
        kKeyNonConsecutive,

        //last
        kMaxKeys
};
SCANCODE_TYPE gSomeValidKeys[kMaxKeys];
void
SomeInit() {
        gSomeValidKeys[kKeyDone]=PRESS(KEY_D);
        gSomeValidKeys[kKeyConsecutive]=PRESS(KEY_C);
        gSomeValidKeys[kKeyNonConsecutive]=PRESS(KEY_N);
}
*/
EFunctionReturnTypes_t
InitFunx(){return kFuncOK;};
void dummy(){};
/*
bool
IsValidKey(
                SCANCODE_TYPE a_ScanThis,
                SCANCODE_TYPE a_ManyValidKeys[],
                int a_Max)
{
        for (int i=0;i<a_Max;i++)
                if (a_ManyValidKeys[i] == a_ScanThis)
                        return true;
        return false;
}*/

void
showkey(KEY_TYPE *from)
{
        printf("%s%s",GetKeyName(from),
                ISPRESSED(from->ScanCode)?"_":"~");
        fflush(stdout);
}

int main()
{
        InitNotifyTracker();
        EXIT_IF(allegro_init() != 0);

        EXIT_IF( kFuncFailed ==
                InstallTimedInput(kRealKeyboard | kRealKeyboardTimer |
                        kRealMouse | kRealMouseTimer,
                        1000,200)
               );

/*        if (set_gfx_mode(GFX_AUTODETECT, 800, 600, 0, 0) != 0) {
                ERR_IF(set_gfx_mode(GFX_SAFE, 640, 480, 0, 0) != 0,
                        set_gfx_mode(GFX_TEXT, 0, 0, 0, 0);
                        allegro_message("Unable to set any graphic mode\n%s\n",
                                allegro_error);
                        return 3;
                        );
        }

        set_palette(desktop_palette);
*/

        printf("initializing...\n");
//endkey
                KEY_TYPE into;
                ClearKeyBuffer();
               printf("WAIT! You must press and release the key which will end recording");
                fflush(stdout);
                rest(1000);//1 sec
                ClearKeyBuffer();
                printf("\npress key now(don't forget to release it):\n");

                //only accept presses
                into.ScanCode=RELEASE(KEY_A);//dummy
                while (PRESS(into.ScanCode) != into.ScanCode) {
                        while (kFuncFailed == RemoveNextKeyFromBuffer(&into)) {
                                rest(200);
                        }//while
                }//while
                showkey(&into);//pressed key
                KEY_TYPE endk=into;
                endk.ScanCode=RELEASE(into.ScanCode);
                //only accept release of the prev. pressed key
                //into is now press
                while (into.ScanCode != endk.ScanCode) {
                        while (kFuncFailed == RemoveNextKeyFromBuffer(&into)) {
                                rest(200);//do this if no key to get from buf
                        }//while
                }//1st while
                showkey(&into);//released key
                printf("\nWAIT! ");
                printf("the key is:");
                showkey(&endk);
                rest(1000);
                ClearKeyBuffer();
                
                printf("\nok, whenever u wanna stop recording press&release that key!\n");

        //SomeInit();
/*        for (int i=0;i<MAX_ACTIONS;i++) {
                EXIT_IF( ! (AllActions[i]=new TAction));
        }
        EXIT_IF(kFuncFailed ==
                AllActions[0]->SetActionType(kConsecutiveActionType)
               );
        */
        int allocnow=0;
        while (allocnow < kAllocatedActions) {
                EXIT_IF( ! (AllActions[allocnow]=new TAnyAction));
                EXIT_IF(kFuncOK !=
                        AllActions[allocnow]->SetFunx(
                                dummy,
                                dummy));
        int kact=0;
        while (kact < kMaxTriggers) {
                printf("Action[%d=%s][%d=%s]: ",allocnow,
                                ActionNames[allocnow],
                                kact,
                                TriggerNames[kact]);
              /*do {
                printf("Action[%d]: ",allocnow);
                printf("Consecutive/noNconsecutive/Done ? ");
                fflush(stdout);
                while (kFuncFailed == RemoveNextKeyFromBuffer(&into)) {
                        rest(200);//max response time; ie. after pressing a key
                }//while
                showkey(&into);
                printf("\n");
              } while ( !IsValidKey(into.ScanCode, gSomeValidKeys, kMaxKeys) );

              if (gSomeValidKeys[kKeyDone] != into.ScanCode) {
                      //if not done yet then:
                EXIT_IF( ! (AllActions[allocnow]=new TAction));

                 if (into.ScanCode == gSomeValidKeys[kKeyConsecutive]) {
                     EXIT_IF(kFuncOK !=
                        AllActions[allocnow]->SetActionType(kConsecutiveActionType,activation_funcs[allocnow],deactivation_funcs[allocnow]));
                } else //fi
                if (into.ScanCode == gSomeValidKeys[kKeyNonConsecutive]) {
                        //AllActions[allocnow]->SetActionType(kNonConsecutiveActionType);
                        ERR(non-consec not yet defined in other cpp files);
                }//fi
*/
               //preparing to record the keycombination which starts the action
                printf("recording begun...\n");
                fflush(stdout);
                into.ScanCode=PRESS(into.ScanCode);
                while (into.ScanCode != endk.ScanCode) {
                        while (kFuncFailed == RemoveNextKeyFromBuffer(&into)) {
                                rest(200);//do this if no key to get from buf
                        }//while
                        if (PRESS(into.ScanCode)!=PRESS(endk.ScanCode)) {
                                showkey(&into);
                                ERR_IF(kFuncOK!=
                                        AllActions[allocnow]->AddKey(kact,&into),
                                        return kFuncFailed);
                        }
                }//while
                ClearKeyBuffer();
                printf("done with this combi!\n");
                rest(500);
/*                Act_st tmp=AllActions[allocnow]->Act[kact];
                printf("[%d/%d] CombiVars[kHowManyType][kInputInt]=%d!\n",allocnow,kact,
                                tmp.CombiVars[kHowManyType][kInputInt]);
*/
                kact++;
        }//while kact
                printf("done with this action!\n");
                rest(500);
                //try next
                allocnow++;
              //} else break;//fi
        }//while allocnow
        printf("allocated actions / kAllocatedActions = %d/%d\n",
                        allocnow,
                        kAllocatedActions
                        );
        TFacileFile temp;
        EXIT_IF(kFuncOK !=
                temp.SaveAllInOne(kCombiFileName,kAllocatedActions,kMaxTriggers));

/*        for (int i=0;i<MAX_ACTIONS/2;i++) {
                EXIT_IF(kFuncFailed ==
                        AllActions[i]->SetActionType(kConsecutiveActionType)
                      );
        }
        for (int i=MAX_ACTIONS/2;i<MAX_ACTIONS;i++) {
                EXIT_IF(kFuncFailed ==
                        AllActions[i]->SetActionType(kNonConsecutiveActionType)
                        );
        }
        for (int i=0; i<MAX_ACTIONS; i++) {
                EXIT_IF(kFuncFailed ==
                        AllActions[i]->HandleInput(i)
                       );
        }
        for (int i=0; i<MAX_ACTIONS; i++) {
                EXIT_IF(kFuncFailed ==
                        AllActions[i]->SetActionType(kConsecutiveActionType)
                       );
        }
        for (int i=0; i<MAX_ACTIONS; i++) {
                EXIT_IF(kFuncFailed ==
                        AllActions[i]->HandleInput(i)
                       );
        }
        for (int i=0; i<MAX_ACTIONS; i++) {
                EXIT_IF(kFuncFailed ==
                        AllActions[i]->SetActionType(kNonConsecutiveActionType)
                       );
        }
        for (int i=0; i<MAX_ACTIONS; i++) {
                EXIT_IF(kFuncFailed ==
                        AllActions[i]->HandleInput(i)
                       );
        }*/
        for (int i=0; i<allocnow; i++) {
                printf("deleting actions[%d]...",i);
                fflush(stdout);
                TRAP(delete AllActions[i]);
                AllActions[i]=NULL;
                printf("done.\n");
        }

        for (int i=0;i<kAllocatedActions;i++) {
                activation_funcs[i]=dummy;
                deactivation_funcs[i]=dummy;
        }
        TFacileFile zfile;
        ERR_IF(kFuncOK != zfile.AllInOne(kCombiFileName,
                                kAllocatedActions,kMaxTriggers),
                        return kFuncFailed);
        for (int i=0;i<kAllocatedActions;i++) {
                for (int k=0;k<kMaxTriggers;k++) {
                        printf("Action[%d][%d](%d,%d,%d) ",i,k,
                                        AllActions[i]->Act[k].CombiVars[kHowManyType][kInputInt],
                                        AllActions[i]->Act[k].CombiVars[kHowManyType][kKeyInt],
                                        AllActions[i]->Act[k].CombiVars[kHowManyType][kMouseInt]
                                        );
                        for (int j=0;j<AllActions[i]->Act[k].CombiVars[kHowManyType][kKeyInt];j++){
                                showkey(&AllActions[i]->Act[k].CombiKeyBuf[j]);
                        }//for3
                        printf("\n");
                }//for2
        }//for1

        UnInstallTimedInput();

 //       set_gfx_mode(GFX_TEXT, 0, 0, 0, 0);
        ShutDownNotifyTracker();
        return EXIT_SUCCESS;
} END_OF_MAIN();


