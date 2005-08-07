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

#include "allegro.h"
#include "math.h"


#include "common.h"
#include "input.h"
#include "init.h"
#include "fps.h"
#include "consts.h"


BITMAP *buffer;

#ifdef BMPCROSS
BITMAP *tex;
#endif



EFunctionReturnTypes_t
init() {

        InitNotifyTracker();
        EXIT_IF(allegro_init() != 0);

        EXIT_IF( kFuncFailed ==
                        InitInput());

   if (set_gfx_mode(GFX_AUTODETECT, 800, 600, 0, 0) != 0) {
      if (set_gfx_mode(GFX_SAFE, 640, 480, 0, 0) != 0) {
         set_gfx_mode(GFX_TEXT, 0, 0, 0, 0);
         allegro_message("Unable to set any graphic mode\n%s\n",
                         allegro_error);
         ERR(no graphix);
         return kFuncFailed;
      }
   }

   set_palette(desktop_palette);
   buffer = create_bitmap(SCREEN_W, SCREEN_H);
        install_fps();

                int sq=(int)sqrt(NUM_CAMS);
                int w=SCREEN_W/sq;
                int h=SCREEN_H/sq;
                int a=0;
                int b=0;
         for (int i=0;i<NUM_CAMS;i++){
                cams[i].Prepare(
                                a*w,
                                b*h,
                                w,
                                h
                                );
                a++;
                if (a>=sq) {
                        a=0;
                        b++;
                        if (b>=sq)
                                b=0;
                }//fi
        }//for cams
         cams[current_cam].Activate();

#ifdef BMPCROSS
#ifdef ITSPAL
        PALETTE pal;
#endif
   tex=load_bitmap("1.bmp",
#ifdef ITSPAL
                   pal
#else
                NULL
#endif
                   );
        if (!tex) {
                set_gfx_mode(GFX_TEXT, 0, 0, 0, 0);
                allegro_message("error loading bitmap\n%s\n",
                                              allegro_error);
                ERR(damn bitmap);
                   return kFuncFailed;
           }
#ifdef ITSPAL
        set_palette(pal);
#endif
#endif
        textprintf_centre_ex(screen,font,SCREEN_W/2,SCREEN_H/2,makecol(0,0,0),
                        -1,"just move the mouse or press a valid key ie. W");
        clear_to_color(buffer,makecol(0,0,0));
        return kFuncOK;
}

