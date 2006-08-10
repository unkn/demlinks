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
#include "square.h"

//macros
#define FOR_ALL_ACTIVE_CAMS(a_statements)      \
{ \
        for (int i=0;i<NUM_CAMS;i++)            \
        {                                       \
                CAMERA_CLASS *cam=cams[i];     \
                if (cam->IsActive()) {          \
                        a_statements;            \
                }                               \
        }              \
}

#define SETME(_what_) \
        __tIF(Functions[_what_] != NULL); \
        Functions[_what_] = _what_##_;

#define SETME4(_what_,_a_) \
        __tIF(Functions[_what_##_a_]!=NULL); \
        Functions[_what_##_a_] = _what_##_;

#define SETME2(_what_) \
        __tIF(Functions[_what_]!=NULL); \
        Functions[_what_] = _what_##_; \
        __tIF(Functions[_what_##_stop]!=NULL); \
        Functions[_what_##_stop] = none_;

#define SETME3(_what_) \
        __tIF(Functions[_what_]!=NULL); \
        Functions[_what_] = _what_##_; \
        __tIF(Functions[_what_##_stop]!=NULL); \
        Functions[_what_##_stop] = _what_##_stop_;


/*****************************************************************************/


//functions:
/*****************************************************************************/
function
none_()
{
        _OK;
}
/*****************************************************************************/
function
kAI_QuitProgram_()
{
        INFO(quit);
        __fIFnok(SetFlag(kF_QuitProgram));
        _OK;
}
/*****************************************************************************/
function
kAI_Hold1Key_()
{
        __fIFnok(SetFlag(kF_Hold1Key));
        _OK;
}
/*****************************************************************************/
function
kAI_Hold1Key_stop_()
{
        __fIFnok(ClearFlag(kF_Hold1Key));
        _OK;
}
/*****************************************************************************/
function
kAI_FOV_()
{
        if (!Flag(kF_Hold1Key))
                FOR_ALL_ACTIVE_CAMS(cam->IncFOV(1))
        else
                FOR_ALL_ACTIVE_CAMS(cam->IncFOV(-1))
        _OK;
}
/*****************************************************************************/
function
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
        _OK;
}
/*****************************************************************************/
/*****************************************************************************/

/*****************************************************************************/

/*****************************************************************************/
function
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
        _OK;
}
/*****************************************************************************/
function
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
        _OK;
}
/*****************************************************************************/
function
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
        _OK;
}
/*****************************************************************************/
function
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
        _OK;
}
/*****************************************************************************/
function
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
        _OK;
}
/*****************************************************************************/
function
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
        _OK;
}
/*****************************************************************************/
function
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
        _OK;
}
/*****************************************************************************/
function
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
        _OK;
}
/*****************************************************************************/
function
kAI_CamRollLeft_()
{
        FOR_ALL_ACTIVE_CAMS(
                        cam->Roll(+TURN_SPEED);
                        );
        _OK;
}
/*****************************************************************************/
function
kAI_CamRollRight_()
{
        FOR_ALL_ACTIVE_CAMS(
                        cam->Roll(-TURN_SPEED);
                        );
        _OK;
}
/*****************************************************************************/
function
kAI_CamSlideForward_()
{
        FOR_ALL_ACTIVE_CAMS(
                        cam->Advance(THRUST_SPEED);
                        );
        _OK;
}
/*****************************************************************************/
function
kAI_CamSlideBackward_()
{
        FOR_ALL_ACTIVE_CAMS(
                        cam->Advance(-THRUST_SPEED);
                        );
        _OK;
}
/*****************************************************************************/
function
kAI_Undefined_()
{
        _FA(this was never supposed to be called);
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


function
kAI_NextSetOfValues_()
{
        //global_select=(global_select+1)% GLOBALMAX;
        SET_NEXT_ROTATION(global_select,GLOBALMAX);
        __tIFnok( cams[current_cam]->SetNeedRefresh() );
        _OK;
}
/*****************************************************************************/
function
InitFunctions()
{
        for (int i=0;i<kMaxAIs;i++) {
                Functions[i]=NULL;//safer init
        }//for

//set part where the assignments go
        SETME(kAI_Undefined);
        SETME(kAI_QuitProgram);
        SETME(kAI_NextSetOfValues);

        SETME3(kAI_Hold1Key);

        SETME4(kAI_CamRollRight,_byMouse);

        SETME2(kAI_CamSlideBackward);
        SETME2(kAI_CamSlideForward);
        SETME2(kAI_CamRollRight);
        SETME2(kAI_CamRollLeft);
        SETME2(kAI_CamPitchDown);
        SETME2(kAI_CamPitchUp);
        SETME2(kAI_CamTurnRight);
        SETME2(kAI_CamTurnLeft);
        SETME2(kAI_Aspect);
        SETME2(kAI_FOV);
        SETME2(kAI_CamSlideDown);
        SETME2(kAI_CamSlideUp);
        SETME2(kAI_CamSlideRight);
        SETME2(kAI_CamSlideLeft);

//end of set part
        //last:
        for (int i=0;i<kMaxAIs;i++) {
                //forgot to init some new action?
                __tIF(Functions[i]==NULL);
        }//for

        _OK;
}

/*****************************************************************************/
