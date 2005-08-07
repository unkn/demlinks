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

#include <math.h>

#include "_gcdefs.h"

/*****************************************************************************/
#include "common.h"
#include "activefunx.h"
#include "pnotetrk.h"
#include "excamera.h"
//macros
#define FOR_ALL_ACTIVE_CAMS(a_statements)      \
{ \
        for (int i=0;i<NUM_CAMS;i++)            \
        {                                       \
                CAMERA_CLASS *cam=&cams[i];     \
                if (cam->IsActive()) {          \
                        a_statements;            \
                }                               \
        }              \
}

#define MOM_DECL(_what_) \
        activation_funcs[_what_]=_what_##_a_func; \
        deactivation_funcs[_what_]=nothing_func;



//functions:

/*****************************************************************************/
void nothing_func() {
        //supposed to do nothing
}
/*****************************************************************************/
void quit(void){
        INFO(quit);
        quit_flag=true;
}
/*****************************************************************************/
void  kHold1Key_a_func() {
        Hold1_Key=true;
}
/*****************************************************************************/
void  kHold1Key_d_func() {
        Hold1_Key=false;
}
/*****************************************************************************/
void kIncFOV_a_func() {
        if (!Hold1_Key)
                FOR_ALL_ACTIVE_CAMS(cam->IncFOV(1))
        else
                FOR_ALL_ACTIVE_CAMS(cam->IncFOV(-1))
}
/*****************************************************************************/
void kIncAspect_a_func() {
                double frac, iptr;
FOR_ALL_ACTIVE_CAMS(
                frac = modf(cam->GetAspect()*10.0, &iptr);
        if (!Hold1_Key) {//inc
                        if ((frac>0.59) && (frac<0.61))
                                cam->IncAspect(0.04f);
                        else
                                cam->IncAspect(0.03f);
        }//if
        else {//dec
                        if ((frac>0.99) || (frac<0.01))
                                cam->IncAspect(-0.04f);
                        else
                                cam->IncAspect(-0.03f);
        }//else
);
}
/*****************************************************************************/
/*****************************************************************************/

/*****************************************************************************/

/*****************************************************************************/
void kLeftSlideCam_a_func() {
        if (!Hold1_Key) {//left slide cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideHoriz(SLIDE_SPEED);
                );
        }
        else {//left slide view
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideView(-SLIDEVIEW_AMMOUNT,0);
                );
        }//else
}
/*****************************************************************************/
void kRightSlideCam_a_func() {
        if (!Hold1_Key) {//right slide cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideHoriz(-SLIDE_SPEED);
                );
        }
        else {//right slide view
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideView(+SLIDEVIEW_AMMOUNT,0);
                );
        }//else
}
/*****************************************************************************/
void kUpSlideCam_a_func() {
        if (!Hold1_Key) {//up slide cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideVert(SLIDE_SPEED););
        } else {//up slide view
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideView(0,-SLIDEVIEW_AMMOUNT);
                );
        }//else
}
/*****************************************************************************/
void kDownSlideCam_a_func() {
        if (!Hold1_Key) {//down slide cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideVert(-SLIDE_SPEED););
        } else { //down slide view
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideView(0,+SLIDEVIEW_AMMOUNT);
                );
        }//else
}
/*****************************************************************************/
void kLeftTurnCam_a_func() {
        if (!Hold1_Key) {//turn left cam
        FOR_ALL_ACTIVE_CAMS(
                cam->Turn(-TURN_SPEED););
        } else {//shrink left view
                FOR_ALL_ACTIVE_CAMS(
                        cam->EnlargeView(-ENLARGEVIEW_AMMOUNT,0);
                        );
        }//else
}
/*****************************************************************************/
void kRightTurnCam_a_func() {
        if (!Hold1_Key) {//turn right cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->Turn(+TURN_SPEED););
        } else {//enlarge right view
                FOR_ALL_ACTIVE_CAMS(
                        cam->EnlargeView(ENLARGEVIEW_AMMOUNT,0);
                        );
        }//else
}
/*****************************************************************************/
void kUpPitchCam_a_func() {
        if (!Hold1_Key) {//pitch up cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->Pitch(TURN_SPEED);
                        );
        } else {//shrink up view
                FOR_ALL_ACTIVE_CAMS(
                        cam->EnlargeView(0,-ENLARGEVIEW_AMMOUNT);
                        );
        }//else
}
/*****************************************************************************/
void kDownPitchCam_a_func() {
        if (!Hold1_Key) {//pitch down cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->Pitch(-TURN_SPEED);
                        );
        }else {//enlarge down view
                FOR_ALL_ACTIVE_CAMS(
                        cam->EnlargeView(0,ENLARGEVIEW_AMMOUNT);
                        );
        }//else
}
/*****************************************************************************/
void kLeftRollCam_a_func() {
        FOR_ALL_ACTIVE_CAMS(
                        cam->Roll(+TURN_SPEED);
                        );
}
/*****************************************************************************/
void kRightRollCam_a_func() {
        FOR_ALL_ACTIVE_CAMS(
                        cam->Roll(-TURN_SPEED);
                        );
}
/*****************************************************************************/
void kForwardSlideCam_a_func() {
        FOR_ALL_ACTIVE_CAMS(
                        cam->Advance(THRUST_SPEED);
                        );
}
/*****************************************************************************/
void kBackwardSlideCam_a_func() {
        FOR_ALL_ACTIVE_CAMS(
                        cam->Advance(-THRUST_SPEED);
                        );
}
/*****************************************************************************/
/*****************************************************************************/

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
EFunctionReturnTypes_t
InitFunx()
{
        for (int i=0;i<kAllocatedActions;i++)
                deactivation_funcs[i]=NULL;//safer init

        activation_funcs[kActQuit]=quit;
        activation_funcs[kToggleCurCam]=ToggleCurrentCam;
        activation_funcs[kChooseNextCam]=ChooseNextCam;
        activation_funcs[kHold1_Key]=kHold1Key_a_func;
        deactivation_funcs[kHold1_Key]=kHold1Key_d_func;

        MOM_DECL(kIncFOV);
        MOM_DECL(kIncAspect);
        //MOM_DECL(kLeftSlideView);
        //MOM_DECL(kRightSlideView);
        //MOM_DECL(kUpSlideView);
        //MOM_DECL(kDownSlideView);
        MOM_DECL(kLeftSlideCam);
        MOM_DECL(kRightSlideCam);
        MOM_DECL(kUpSlideCam);
        MOM_DECL(kDownSlideCam);
        MOM_DECL(kLeftTurnCam);
        MOM_DECL(kRightTurnCam);
        MOM_DECL(kUpPitchCam);
        MOM_DECL(kDownPitchCam);
        MOM_DECL(kLeftRollCam);
        MOM_DECL(kRightRollCam);
        MOM_DECL(kForwardSlideCam);
        MOM_DECL(kBackwardSlideCam);
        /*MOM_DECL(kEnlargeDown_View);
        MOM_DECL(kShrinkUp_View);
        MOM_DECL(kEnlargeRight_View);
        MOM_DECL(kShrinkLeft_View);*/



        return kFuncOK;
}

/*****************************************************************************/
