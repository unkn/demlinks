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

/*
 *    Inspired from an example program for the Allegro library, excamera.c
 *    by Shawn Hargreaves and modified by Francisco Pires.
 */

#include "consts.h"
#include "camera.h"


//constructor
TCamera::TCamera()
{
        //defaults
        need_refresh=true;

        SetFOVLimit(16,127);
        SetFOV(48);
        SetAspectLimit(0.1,2);
        SetAspect(4.0f/3.0f);//aka 1.33f remember 4/3 or 16/9??

        SetPosHasLimits(0,0,0,0,0,0);//nolimits

        SetPos(0,-2,-4);

        SetUpVectorHasLimits(0,0,0,0,0,0);//none
        SetUpVector(0,-1,0);

        SetFrontVectorHasLimits(0,0,0,0,0,0);//nolimits
        SetFrontVector(0,0,1);
}

//destructor
TCamera::~TCamera()
{
}

void
TCamera::SetFrontVectorLimits(
                float xl, float xh,
                float yl, float yh,
                float zl, float zh)
{
        xfront_low_limit=xl;
        xfront_high_limit=xh;
        yfront_low_limit=yl;
        yfront_low_limit=yl;
        zfront_high_limit=zh;
        zfront_high_limit=zh;
}

void
TCamera::SetUpVectorLimits(
                float xl, float xh,
                float yl, float yh,
                float zl, float zh)
{
        xup_low_limit=xl;
        xup_high_limit=xh;
        yup_low_limit=yl;
        yup_low_limit=yl;
        zup_high_limit=zh;
        zup_high_limit=zh;
}


void
TCamera::SetFrontVectorHasLimits(
                bool xl, bool xh,
                bool yl, bool yh,
                bool zl, bool zh)
{
        xfront_has_low_limit=xl;
        xfront_has_high_limit=xh;
        yfront_has_low_limit=yl;
        yfront_has_low_limit=yl;
        zfront_has_high_limit=zh;
        zfront_has_high_limit=zh;
}

void
TCamera::SetUpVectorHasLimits(
                bool xl, bool xh,
                bool yl, bool yh,
                bool zl, bool zh)
{
        xup_has_low_limit=xl;
        xup_has_high_limit=xh;
        yup_has_low_limit=yl;
        yup_has_low_limit=yl;
        zup_has_high_limit=zh;
        zup_has_high_limit=zh;
}


void
TCamera::SetFrontVector(float xf,float yf, float zf)
{
        float_SetInLimits(&xfront,xf,
                        xfront_low_limit, xfront_high_limit,
                        xfront_has_low_limit, xfront_has_high_limit);
        float_SetInLimits(&yfront,yf,
                        yfront_low_limit, yfront_high_limit,
                        yfront_has_low_limit, yfront_has_high_limit);
        float_SetInLimits(&zfront,zf,
                        zfront_low_limit, zfront_high_limit,
                        zfront_has_low_limit, zfront_has_high_limit);
}

void
TCamera::SetUpVector(float xu, float yu, float zu){
        float_SetInLimits(&xup,xu,
                        xup_low_limit, xup_high_limit,
                        xup_has_low_limit, xup_has_high_limit);
        float_SetInLimits(&yup,yu,
                        yup_low_limit, yup_high_limit,
                        yup_has_low_limit, yup_has_high_limit);
        float_SetInLimits(&zup,zu,
                        zup_low_limit, zup_high_limit,
                        zup_has_low_limit, zup_has_high_limit);
}


void
TCamera::SetPosHasLimits(
                bool xl, bool xh,//true=limit for these guys
                bool yl, bool yh,//call whenever
                bool zl, bool zh)
{
        xpos_has_low_limit=xl;
        xpos_has_high_limit=xh;

        ypos_has_low_limit=yl;
        ypos_has_high_limit=yh;

        zpos_has_low_limit=zl;
        zpos_has_high_limit=zh;
}

void
TCamera::SlideVert(float delta_slide)
//+0.20f
{
        if (delta_slide) {
                float xu,yu,zu;
                GetUpVector(&xu,&yu,&zu);
                IncPos(xu*delta_slide,yu*delta_slide,zu*delta_slide);
        }//fi
}


void
TCamera::SlideHoriz(float delta_slide)
//+0.20f
//positive is left, with initial cam pos
{
        if (delta_slide) {
                float xnorm,ynorm,znorm;
                GetNormVector(&xnorm,&ynorm,&znorm);
                IncPos(
                        xnorm*delta_slide,
                        ynorm*delta_slide,
                        znorm*delta_slide);
        }//fi
}

void
TCamera::Advance(float delta_thrust)
//0.5f
{
        if (delta_thrust) {
                float xf,yf,zf;
                GetFrontVector(&xf,&yf,&zf);
                IncPos(
                        xf*delta_thrust,
                        yf*delta_thrust,
                        zf*delta_thrust);
        }//fi
}



void
TCamera::Roll(float delta_roll)
{
        if (delta_roll){
                MATRIX_f tmp_matrix;
   /* rotate the up vector around the in-front vector by the delta_roll angle */   /*void get_vector_rotation_matrix_f(MATRIX_f *m, float x, y, z, float a);
    *      Constructs a transformation matrix which will rotate points around
    *           the specified x,y,z vector by the specified angle (given in
    *                binary, 256 degrees to a circle format).
    */
   //a delta_roll by 'delta_roll' degrees around the front vector:
                get_vector_rotation_matrix_f(
                //rotate left/rigth Q/E around front vector
                   &tmp_matrix,//relative to TCamera rotation
                   xfront,
                   yfront,
                   zfront,
                //rotates around the front vector(thus relativ2camera)
                   delta_roll*128.0/M_PI//0 deg transformed from radians
                );
                //that 128.0=180deg

   /*void apply_matrix_f(const MATRIX_f *m, float x, y, z, *xout, *yout, *zout);    *      Multiplies the point (x, y, z) by the transformation matrix m,
    *           storing the result in (*xout, *yout, *zout).
    *           */
   //applying that delta_roll to the UP-vector
        apply_matrix_f(&tmp_matrix,
                   xup,yup,zup,
                   //xup,yup,zup,
                   //0, -1, 0,//the upvector
                   &xup, &yup, &zup);//...gets rotated too
                __tIFnok(SetNeedRefresh());
        }//fi
}//func


void
TCamera::Turn(float delta_heading)
{
        if (delta_heading) {
                MATRIX_f tmp_matrix;
                get_vector_rotation_matrix_f(
                   &tmp_matrix,
                   xup,
                   yup,
                   zup,//rotates around the UP vector
                delta_heading*128.0/M_PI//0 deg transformed from radians
                );
                //that 128.0=180deg
                apply_matrix_f(&tmp_matrix,
                   xfront,yfront,zfront,
                   //0, -1, 0,//the upvector
                   &xfront, &yfront, &zfront);//...gets rotated too
                delta_heading=0;
                __tIFnok(SetNeedRefresh());
        }//fi delta_heading
}//func

void
TCamera::Pitch(float delta_pitch)
{

        if (delta_pitch) {
                MATRIX_f tmp_matrix;
        //calc. normalized vector, which is perperndicular on both
        //and then rotate both up and front vectors around the norm vector
        float xnorm,ynorm,znorm;
        GetNormVector(&xnorm,&ynorm,&znorm);
        //gen rotated matrix, around the norm vector
        get_vector_rotation_matrix_f(&tmp_matrix,
                        xnorm,
                        ynorm,
                        znorm,
                        delta_pitch*128.0/M_PI);

        //rotate both up and front vectors
        apply_matrix_f(&tmp_matrix,
                        xfront,yfront,zfront,
                        &xfront,&yfront,&zfront);
        apply_matrix_f(&tmp_matrix,
                        xup,yup,zup,
                        &xup,&yup,&zup);

        delta_pitch=0;
                __tIFnok(SetNeedRefresh());
        }//fi delta_pitch
}//func

void
TCamera::Update()
{
   /* build the TCamera matrix */
   get_camera_matrix_f(&matrix,
                   //constructs TCamera matrix:
                       xpos, ypos, zpos,        /* TCamera position */
                    xfront, yfront, zfront,/* in-front vector=viewer direction*/                       xup, yup, zup,           /* up vector */
                       fov,                     /* field of view */
                       aspect);                 /* aspect ratio */

   /* negY is to top
    * negX is to left
    * negZ is from screen to your eyes(out of screen)
    */
}//func


MATRIX_f *
TCamera::GetMatrix()
{
        return &matrix;
}

MATRIX_f *
TCamera::GetUpdatedMatrix()
{
        Update();
        return &matrix;
}

void
TCamera::GetPos(float *x, float *y, float *z)
{
        *x=xpos;
        *y=ypos;
        *z=zpos;
}

void
TCamera::float_SetInLimits(
                float *what,float to_what, float low, float high,
                bool has_low_limit, bool has_high_limit)
{
        float saved=*what;
        if ((has_low_limit)&&(to_what <= low))
                *what=low;
        else {
                if ((has_high_limit)&&(to_what >= high))
                        *what = high;
                else
                        *what = to_what;
        }
        if (saved!=to_what) //means change occured
                __tIFnok(SetNeedRefresh());
}

void
TCamera::int_SetInLimits(int *what, int to_what, int low, int high)
{
        int saved=*what;
        if (to_what <= low)
                *what=low;
        else {
                if (to_what >= high)
                        *what = high;
                else
                        *what = to_what;
        }//else
        if (saved!=to_what) //means change occured
                __tIFnok(SetNeedRefresh());
}

void
TCamera::IncPos(float byx, float byy, float byz)
{
        float x,y,z;
        GetPos(&x,&y,&z);
        SetPos(x+byx,y+byy,z+byz);
}

void
TCamera::SetPos(float x, float y, float z)
{
        float_SetInLimits(&xpos,x,xpos_low_limit,xpos_high_limit,
                        xpos_has_low_limit, xpos_has_high_limit);
        float_SetInLimits(&ypos,y,ypos_low_limit,ypos_high_limit,
                        ypos_has_low_limit, ypos_has_high_limit);
        float_SetInLimits(&zpos,z,zpos_low_limit,zpos_high_limit,
                        zpos_has_low_limit, zpos_has_high_limit);
        __tIFnok(SetNeedRefresh());
}

void
TCamera::SetPosLimits(
                float xl, float xh,
                float yl, float yh,
                float zl, float zh)
{
        xpos_low_limit=xl;
        xpos_high_limit=xh;
        ypos_low_limit=yl;
        ypos_high_limit=yh;
        zpos_low_limit=zl;
        zpos_high_limit=zh;
}



void
TCamera::GetNormVector(float *xnorm, float *ynorm, float *znorm)
{
        cross_product_f(
                xup,yup,zup,
                xfront,yfront,zfront,
                xnorm,ynorm,znorm);
}

void
TCamera::GetFrontVector(float *xf,float *yf, float *zf)
{
        *xf=xfront;
        *yf=yfront;
        *zf=zfront;
}

void
TCamera::GetUpVector(float *xu, float *yu, float *zu)
{
        *xu=xup;
        *yu=yup;
        *zu=zup;
}

float
TCamera::GetAspect()
{
        return aspect;
}

void
TCamera::SetAspect(float new_aspect)
{
        float_SetInLimits(&aspect,new_aspect,aspect_low_limit,aspect_high_limit,
                        true,true);
}


void
TCamera::SetAspectLimit(float low,float high)
{
        aspect_low_limit=low;
        aspect_high_limit=high;
}


void
TCamera::IncAspect(float by)
{
        SetAspect(GetAspect()+by);
}


int
TCamera::GetFOV()
{
        return fov;
}

void
TCamera::SetFOV(int newfov)
{
        int_SetInLimits(&fov,newfov,fov_low_limit,fov_high_limit);
}

void
TCamera::IncFOV(int by)
{
        SetFOV(GetFOV()+by);
}

void
TCamera::SetFOVLimit(int low,int high)
{
        fov_low_limit=low;
        fov_high_limit=high;
}


void
MProjectedCamera::SlideView(int x,int y){
        Prepare(GetX()+x,
                GetY()+y,
                GetW(),
                GetH());
}

void
MProjectedCamera::EnlargeView(int w,int h){
        Prepare(GetX(),
                GetY(),
                GetW()+w,
                GetH()+h);
}

void
MProjectedCamera::MarkState()
{
        last_active_state=active_state;
}

bool
MProjectedCamera::Activate()
{
        if (!active_state) {//only activate if wasn't active
                active_state=true;
                __tIFnok(SetNeedRefresh())
                return true;//changed
        }
        return false;//no change in state
}


bool
MProjectedCamera::Revert2State()
{
        if (active_state != last_active_state) {
                active_state=last_active_state;
                return true;//changed
        }
        return false;//no change
}

function
MProjectedCamera::Toggle()
{
        active_state = ! active_state;
        _OK;
}

bool
MProjectedCamera::Deactivate()
{
        if (active_state) {//only deactivate if it was active
                active_state=false;
                return true;//changed
        }
        return false;//no change in state
}

