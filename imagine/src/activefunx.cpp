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
#include "pnotetrk.h"

/*****************************************************************************/
#include "activefunx.h"
#include "consts.h"

#include "camera.h"
#include "excamera.h"
#include "actions.h"
#include "flags.h"

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

#define SETME(_what_) \
        ERR_IF(Functions[_what_]!=NULL, \
                        return kFuncFailed); \
        Functions[_what_] = _what_##_;
/*****************************************************************************/


//functions:
/*****************************************************************************/
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_QuitProgram_()
{
        INFO(quit);
        ERR_IF(kFuncOK!=SetFlag(kF_QuitProgram),
                        return kFuncFailed);
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_Hold1KeyPress_()
{
        ERR_IF(kFuncOK!=SetFlag(kF_Hold1Key),
                        return kFuncFailed);
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_Hold1KeyRelease_()
{
        ERR_IF(kFuncOK!=ClearFlag(kF_Hold1Key),
                        return kFuncFailed);
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_FOV_()
{
        if (!Flag(kF_Hold1Key))
                FOR_ALL_ACTIVE_CAMS(cam->IncFOV(1))
        else
                FOR_ALL_ACTIVE_CAMS(cam->IncFOV(-1))
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_Aspect_()
{
                double frac, iptr;
FOR_ALL_ACTIVE_CAMS(
                frac = modf(cam->GetAspect()*10.0, &iptr);
        if (!Flag(kF_Hold1Key)) {//inc
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
        return kFuncOK;
}
/*****************************************************************************/
/*****************************************************************************/

/*****************************************************************************/

/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamSlideLeft_()
{
        if (!Flag(kF_Hold1Key)) {//left slide cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideHoriz(SLIDE_SPEED);
                );
        }
        else {//left slide view
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideView(-SLIDEVIEW_AMMOUNT,0);
                );
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamSlideRight_()
{
        if (!Flag(kF_Hold1Key)) {//right slide cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideHoriz(-SLIDE_SPEED);
                );
        }
        else {//right slide view
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideView(+SLIDEVIEW_AMMOUNT,0);
                );
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamSlideUp_()
{
        if (!Flag(kF_Hold1Key)) {//up slide cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideVert(SLIDE_SPEED););
        } else {//up slide view
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideView(0,-SLIDEVIEW_AMMOUNT);
                );
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamSlideDown_()
{
        if (!Flag(kF_Hold1Key)) {//down slide cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideVert(-SLIDE_SPEED););
        } else { //down slide view
                FOR_ALL_ACTIVE_CAMS(
                        cam->SlideView(0,+SLIDEVIEW_AMMOUNT);
                );
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamTurnLeft_()
{
        if (!Flag(kF_Hold1Key)) {//turn left cam
        FOR_ALL_ACTIVE_CAMS(
                cam->Turn(-TURN_SPEED););
        } else {//shrink left view
                FOR_ALL_ACTIVE_CAMS(
                        cam->EnlargeView(-ENLARGEVIEW_AMMOUNT,0);
                        );
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamTurnRight_()
{
        if (!Flag(kF_Hold1Key)) {//turn right cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->Turn(+TURN_SPEED););
        } else {//enlarge right view
                FOR_ALL_ACTIVE_CAMS(
                        cam->EnlargeView(ENLARGEVIEW_AMMOUNT,0);
                        );
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamPitchUp_()
{
        if (!Flag(kF_Hold1Key)) {//pitch up cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->Pitch(TURN_SPEED);
                        );
        } else {//shrink up view
                FOR_ALL_ACTIVE_CAMS(
                        cam->EnlargeView(0,-ENLARGEVIEW_AMMOUNT);
                        );
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamPitchDown_()
{
        if (!Flag(kF_Hold1Key)) {//pitch down cam
                FOR_ALL_ACTIVE_CAMS(
                        cam->Pitch(-TURN_SPEED);
                        );
        }else {//enlarge down view
                FOR_ALL_ACTIVE_CAMS(
                        cam->EnlargeView(0,ENLARGEVIEW_AMMOUNT);
                        );
        }//else
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamRollLeft_()
{
        FOR_ALL_ACTIVE_CAMS(
                        cam->Roll(+TURN_SPEED);
                        );
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamRollRight_()
{
        FOR_ALL_ACTIVE_CAMS(
                        cam->Roll(-TURN_SPEED);
                        );
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamSlideForward_()
{
        FOR_ALL_ACTIVE_CAMS(
                        cam->Advance(THRUST_SPEED);
                        );
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_CamSlideBackward_()
{
        FOR_ALL_ACTIVE_CAMS(
                        cam->Advance(-THRUST_SPEED);
                        );
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
kAI_Undefined_()
{
        ERR(this was never supposed to be called);
        return kFuncFailed;
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

//FIXME:temp
extern int global_select;
#define GLOBALMAX 5

EFunctionReturnTypes_t
kAI_NextSetOfValues_()
{
        global_select=(global_select+1)% GLOBALMAX;
        return kFuncOK;
}
/*****************************************************************************/
EFunctionReturnTypes_t
InitFunctions()
{
        for (int i=0;i<kMaxAIs;i++) {
                Functions[i]=NULL;//safer init
        }//for

//set part where the assignments go
        SETME(kAI_Undefined);
        SETME(kAI_QuitProgram);
        SETME(kAI_NextSetOfValues);
        SETME(kAI_CamSlideBackward);
        SETME(kAI_CamSlideForward);
        SETME(kAI_CamRollRight);
        SETME(kAI_CamRollLeft);
        SETME(kAI_CamPitchDown);
        SETME(kAI_CamPitchUp);
        SETME(kAI_CamTurnRight);
        SETME(kAI_CamTurnLeft);
        SETME(kAI_Aspect);
        SETME(kAI_FOV);
        SETME(kAI_Hold1KeyPress);
        SETME(kAI_Hold1KeyRelease);
        SETME(kAI_CamSlideDown);
        SETME(kAI_CamSlideUp);
        SETME(kAI_CamSlideRight);
        SETME(kAI_CamSlideLeft);
//end of set part
        //last:
        for (int i=0;i<kMaxAIs;i++) {
                //forgot to init some new action?
                ERR_IF(Functions[i]==NULL,
                                return kFuncFailed);
        }//for

        return kFuncOK;
}

/*****************************************************************************/
