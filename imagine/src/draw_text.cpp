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

#include "common.h"
#include "draw_text.h"

void
draw_text(BITMAP *bmp, int cur_cam) {
   //     MProjectedCamera *mycam=&cams[cur_cam];
   /* overlay some text */
/*   set_clip_rect(bmp, 0, 0, bmp->w, bmp->h);
   textprintf_ex(bmp, font, 0,  0, makecol(255,255,255), -1,
                 "Viewport width: %d (l/L changes)", mycam->GetW());
   textprintf_ex(bmp, font, 0,  8, makecol(255,255,255), -1,
                 "Viewport height: %d (k/K changes)", mycam->GetH());
   textprintf_ex(bmp, font, 0, 16, makecol(255,255,255), -1,
                 "Field of view: %d (f/F changes)", mycam->GetFOV());
   textprintf_ex(bmp, font, 0, 24, makecol(255,255,255), -1,
                 "Aspect ratio: %.2f (j/J changes)", mycam->GetAspect());

   float xpos,ypos,zpos;
   mycam->GetPos(&xpos,&ypos,&zpos);

   textprintf_ex(bmp, font, 0, 32, makecol(255,255,255), -1,
                 "X position: %.2f (m/M changes)", xpos);
   textprintf_ex(bmp, font, 0, 40, makecol(255,255,255), -1,
                 "Y position: %.2f (n/N changes)", ypos);
   textprintf_ex(bmp, font, 0, 48, makecol(255,255,255), -1,
                 "Z position: %.2f (b/B changes)", zpos);
   textprintf_ex(bmp, font, 0, 56, makecol(255,255,255), -1,
                 "Heading: %.2f deg (left/right changes)", DEG(heading));
   textprintf_ex(bmp, font, 0, 64, makecol(255,255,255), -1,
                 "Pitch: %.2f deg (up/down changes)", DEG(pitch));
   textprintf_ex(bmp, font, 0, 72, makecol(255,255,255), -1,
                 "Roll: %.2f deg (*128.0/M_PI=%.2f) (q/e changes)",
                 DEG(roll),
                 roll*128.0/M_PI);
   float xf,yf,zf,xu,yu,zu;
   mycam->GetFrontVector(&xf,&yf,&zf);
   mycam->GetUpVector(&xu,&yu,&zu);

   textprintf_ex(bmp, font, 0, 80, makecol(255,255,255), -1,
                 "Front vector: %.2f, %.2f, %.2f", xf, yf, zf);
   textprintf_ex(bmp, font, 0, 88, makecol(255,255,255), -1,
                 "Up vector: %.2f, %.2f, %.2f", xu, yu, zu);*/
//   textprintf_ex(bmp, font, 0, 96, makecol(255,255,255), -1,
  //               "Frames per second: %d", fps);
   //textprintf_ex(bmp, font, 0, 104, makecol(255,255,255), -1,
     //            "Using camera number: (%d)%d/%d", current_cam,cur_cam,NUM_CAMS);

}

