/****************************************************************************
*
*                             dmental links
*    Copyright (C) 2005-2006 AtKaaZ, AtKaaZ at users.sourceforge.net
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

#include "allegro.h"

#include "excamera.h"
#include "consts.h"

#include "square.h"

#include "camera.h"


//VARIABLES:
//
CAMERA_CLASS *cams[NUM_CAMS];//made it a pointer to prevent pre main() initialization(by constructors)
int current_cam=0;




#ifdef BMPCROSS
extern BITMAP *tex;
#endif


//END VARIABLES
void ChooseNextCam(){
        cams[current_cam]->Revert2State();
        current_cam = (current_cam+1) % NUM_CAMS;//choose next cam
        cams[current_cam]->MarkState();//remember how it was (active or not)
        cams[current_cam]->Activate();
}

void ToggleCurrentCam()
{
        cams[current_cam]->Toggle();//if ever toggled then
        cams[current_cam]->MarkState();//we keep it in mind
}

function
draw_all(BITMAP *bmp, CAMERA_CLASS *mycam);

/* draw everything */

function
render(BITMAP *bmp, int cur_cam)
{
        CAMERA_CLASS *mycam=cams[cur_cam];
        /* clear the background */
        //clear_to_color(bmp, makecol(0,0,0));
        rectfill(bmp,
                        mycam->GetX(),
                        mycam->GetY(),
                        mycam->GetX()+mycam->GetW()-1,
                        mycam->GetY()+mycam->GetH()-1,
                        makecol(0,0,0));


        /* set up the viewport region */

        rect(bmp,
                        mycam->GetX()-1,
                        mycam->GetY()-1,
                        mycam->GetX()+mycam->GetW(),
                        mycam->GetY()+mycam->GetH(),
                        makecol(255, 255,255));

        __tIFnok(mycam->Select(bmp));
        __tIFnok(draw_all(bmp,mycam));

        __tIFnok(mycam->Deselect(bmp));

        _OK;
}


void draw_cross(BITMAP *bmp, CAMERA_CLASS *mycam) {
//draw crosshair
        xor_mode(true);
        int ix=(CROSSHAIR_LEN)/2;
        int iy=(CROSSHAIR_LEN)/6;
        int x=mycam->GetX()+mycam->GetW()/2;
        int y=mycam->GetY()+mycam->GetH()/2;
        rectfill(bmp,
                        x-ix,
                        y-iy,
                        x+ix,
                        y+iy,
                        makecol(0,255,0));
        rectfill(bmp,
                        x-iy,
                        y-ix,
                        x+iy,
                        y+ix,
                        makecol(0,255,0));
        vline(bmp,
                        x,
                        y-ix,
                        y+ix,
                        makecol(0,255,0));//palette_color[6]);
        hline(bmp,
                        x-ix,
                        y,
                        x+ix,
                        makecol(0,255,0));//palette_color[6]);
        xor_mode(false);
#ifdef BMPCROSS
        draw_sprite(
                        bmp,tex,
                        w-(tex->w/2),
                        h-(tex->h/2)
                        );
#endif

}

function
draw_all(BITMAP *bmp, CAMERA_CLASS *mycam)
{
        draw_squareS(bmp,mycam->GetUpdatedMatrix());
        //draw_cross(bmp,mycam);
        _OK;
}


