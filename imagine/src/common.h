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

#ifndef COMMON__H
#define COMMON__H

#include "consts.h"
#include "camera.h"
#include "genericinput.h"

extern CAMERA_CLASS cams[NUM_CAMS];
extern int current_cam;
extern volatile int fps;
extern volatile bool quit_flag;
extern BITMAP *buffer;
extern volatile int framecount;

//variables
enum {
        kActQuit=0,
        kToggleCurCam,
        kChooseNextCam,
        kHold1_Key,
        kIncFOV,
        kIncAspect,
        //kLeftSlideView,
        //kRightSlideView,
        //kUpSlideView,
        //kDownSlideView,
        kLeftSlideCam,
        kRightSlideCam,
        kUpSlideCam,
        kDownSlideCam,
        kLeftTurnCam,
        kRightTurnCam,
        kUpPitchCam,
        kDownPitchCam,
        kLeftRollCam,
        kRightRollCam,
        kForwardSlideCam,
        kBackwardSlideCam,
        //kEnlargeDown_View,
        //kShrinkUp_View,
        //kEnlargeRight_View,
        //kShrinkLeft_View,
//last
        kAllocatedActions
};
extern const char *ActionNames[kAllocatedActions];
extern const char *TriggerNames[kMaxTriggers];

extern const char *kCombiFileName;

extern TAnyAction *AllActions[kAllocatedActions];


extern void (*activation_funcs[kAllocatedActions])(void);
extern void (*deactivation_funcs[kAllocatedActions])(void);
extern bool Hold1_Key;

#endif
