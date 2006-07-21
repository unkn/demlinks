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


#include "consts.h"
#include "_gcdefs.h"
#include "pnotetrk.h"

#include "camera.h"
#include "input.h"
#include "init.h"
#include "fps.h"
#include "flags.h"
#include "globaltimer.h"


BITMAP *buffer;



EFunctionReturnTypes_t
Init()
{
        InitNotifyTracker();
        __tIF(allegro_init() != 0);

        __tIFnok(InstallGlobalTimer());

        __tIFnok(InitInput());

        __tIFnok(InitFlags());

   if (set_gfx_mode(GFX_AUTODETECT, 800, 600, 0, 0) != 0) {
      if (set_gfx_mode(GFX_SAFE, 640, 480, 0, 0) != 0) {
        if (set_gfx_mode(GFX_SAFE, 320, 200, 0, 0) != 0) {
         set_gfx_mode(GFX_TEXT, 0, 0, 0, 0);
         allegro_message("Unable to set any graphic mode\n%s\n",
                         allegro_error);
         _FA(no graphix);
        }
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
                cams[i].SetPos(0,0,10*TILE_SIZE);
                cams[i].SetFrontVector(0,0,-1);
                cams[i].SetUpVector(-1,-0.4,0);
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

         _OK;
}

function
DeInit()
{
        __( destroy_bitmap(buffer); );

        __tIFnok(DeInitInput());

        __tIFnok(UnInstallGlobalTimer());
        _OK;
}

