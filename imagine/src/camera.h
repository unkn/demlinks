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

#ifndef CAMERA___H
#define CAMERA___H

#include <allegro.h>
#include "pnotetrk.h"


#define HOW_MANY_CAMS 2 //u'll get this num at power of two, numcams
#define NUM_CAMS ((HOW_MANY_CAMS*HOW_MANY_CAMS)) //don't change this for now


class TCamera {//motion is relative to camera
private:
        int     fov_low_limit,
                fov_high_limit;
        float   aspect_low_limit,
                aspect_high_limit;

        float   xpos_low_limit,
                xpos_high_limit,
                ypos_low_limit,
                ypos_high_limit,
                zpos_low_limit,
                zpos_high_limit;
        bool    xpos_has_low_limit,
                xpos_has_high_limit,
                ypos_has_low_limit,
                ypos_has_high_limit,
                zpos_has_low_limit,
                zpos_has_high_limit;

        float   xup_low_limit,
                xup_high_limit,
                yup_low_limit,
                yup_high_limit,
                zup_low_limit,
                zup_high_limit;

        bool xup_has_low_limit, xup_has_high_limit,
             yup_has_low_limit, yup_has_high_limit,
             zup_has_low_limit, zup_has_high_limit;

        float xfront_low_limit, xfront_high_limit,
              yfront_low_limit, yfront_high_limit,
              zfront_low_limit, zfront_high_limit;
        bool xfront_has_low_limit, xfront_has_high_limit,
             yfront_has_low_limit, yfront_has_high_limit,
             zfront_has_low_limit, zfront_has_high_limit;

        void int_SetInLimits(int *what, int to_what, int low, int high);
        void float_SetInLimits(float *what,float to_what, float low, float high,
                        bool has_low_limit, bool has_high_limit);

protected:
        MATRIX_f matrix;
private:
        int fov;//field of view
        float aspect;//aspect ratio
        float xpos,ypos,zpos;//camera position
        //X positive is from left to right
        //Y negative is from bottop to top
        //Z negative from screen to your eyes;
        //left hand rule here, all fingers point to negative; thumb is left

//        float delta_heading, delta_pitch, delta_roll;//angles
        float xup,yup,zup;//up vector; from origin; unit vector
        float xfront,yfront,zfront;//front vector; from origin; unit vector
        //unit vector means has a length of one
        bool need_refresh;
public:

        function SetNeedRefresh(){ need_refresh=true; _OK;};
        function SetNoNeedRefresh() { need_refresh=false; _OK;};
        bool NeedsRefresh()const { return need_refresh; };

        TCamera();
        ~TCamera();
        void Update();
        MATRIX_f *GetMatrix();
        MATRIX_f *GetUpdatedMatrix();

        //positive roll is leftwards
        void Roll(float delta_roll);//angle
        //positive delta_heading is right
        void Turn(float delta_heading);//angle
        //positive pitch is up
        void Pitch(float delta_pitch);//angle

        //right hand rule here, all fingers point to positive, thumb is left
        //positive delta_thrust means go forward
        void Advance(float delta_thrust);//like and IncFrontVector
        //positive delta_slide means left
        void SlideHoriz(float delta_slide);//like an IncNormVector
        //positive delta_slide means up
        void SlideVert(float delta_slide);//like an IncUpVector

        //this is perpendiculat on both up and front vector, thus deduced
        //it's a unit vector
        void GetNormVector(float *xnorm, float *ynorm, float *znorm);

        //front vector is a unit vector too; points ahead
        void GetFrontVector(float *xf,float *yf, float *zf);
        void SetFrontVector(float xf,float yf, float zf);
        void SetFrontVectorHasLimits(//true means has its limit activated
                        bool xl, bool xh,
                        bool yl, bool yh,
                        bool zl, bool zh);
        void SetFrontVectorLimits(
                        float xl, float xh,
                        float yl, float yh,
                        float zl, float zh);



        //up vector is a unit vector too;
        //points upwards while looking ahead(front vec.)
        void GetUpVector(float *xu, float *yu, float *zu);
        void SetUpVector(float xu, float yu, float zu);
        void SetUpVectorHasLimits(
                        bool xl, bool xh,
                        bool yl, bool yh,
                        bool zl, bool zh);
        void SetUpVectorLimits(
                        float xl, float xh,
                        float yl, float yh,
                        float zl, float zh);


        //position of the camera in 3D space
        void GetPos(float *x, float *y, float *z);
        void SetPos(float x, float y, float z);
        void IncPos(float byx, float byy, float byz);
        void SetPosLimits(//yes limit
                        float xl, float xh,
                        float yl, float yh,
                        float zl, float zh);
        void SetPosHasLimits(
                        bool xl, bool xh,//true=limit for these guys
                        bool yl, bool yh,//call whenever
                        bool zl, bool zh);


        //aspect of the projection (from 3D to 2D) whenever that happens
        float GetAspect();
        void SetAspect(float new_aspect);
        void IncAspect(float by);
        void SetAspectLimit(float low,float high);

        //field of view, angle in which the eye(camera) sees the 3D world
        void SetFOVLimit(int low,int high);
        int  GetFOV();
        void SetFOV(int newfov);
        void IncFOV(int by);
};

class MProjectedCamera:public TCamera{
private:
        bool last_active_state;
        bool active_state;

        int proj_x,proj_y;
        int proj_w,proj_h;
public:
        MProjectedCamera(){
                Deactivate();
                MarkState();
        };
        ~MProjectedCamera(){};
        void MarkState();//save current as last_active_state
        bool Activate();//make active(if wasn't)
        bool Deactivate();//deactivate(if was active)
        bool Revert2State();//to last_active_state
        function Toggle();
        bool IsActive(){ return active_state; };
        int GetX(){ return proj_x;};
        int GetY(){ return proj_y;};
        int GetW(){ return proj_w;};
        int GetH(){ return proj_h;};

        void Prepare(int x,int y,int w,int h){
                proj_x=x;
                proj_y=y;
                proj_w=w;
                proj_h=h;
                SetNeedRefresh();
        };
        function
        Select(BITMAP *bmp){//select camera
                set_projection_viewport(proj_x, proj_y, proj_w, proj_h);
                set_clip_rect(bmp,
                                proj_x,
                                proj_y,
                                proj_x+proj_w-1,
                                proj_y+proj_h-1);
                _OK;
        };
        function
        Deselect(BITMAP *bmp){
                set_clip_rect(bmp, 0, 0, bmp->w, bmp->h);
                _OK;
        };
        void SlideView(int x, int y);
        void EnlargeView(int w,int h);
};//class


extern CAMERA_CLASS *cams[NUM_CAMS];
extern int current_cam;


#endif
