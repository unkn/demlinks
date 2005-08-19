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

#include <allegro.h>

#include "_gcdefs.h"
#include "pnotetrk.h"

#include "consts.h"

#include "fps.h"
#include "init.h"
#include "input.h"
#include "camera.h"
#include "excamera.h"
#include "flags.h"

//rest(x) when no input, reduces cpu cycles inside loop
#define IDLE_TIME_IN_LOOP 100

bool need_screen_refresh=true;//first time display screen



int main(void)
{
        EXIT_IF(kFuncOK != Init());

   while (!Flag(kF_QuitProgram)) {
        EXIT_IF(kFuncOK != Executant());


        bool need_screen_refresh=false;
        for (int i=0;i<NUM_CAMS;i++){
                if (cams[i].NeedsRefresh()) {
                        render(buffer,i);//update some part of screen
                        need_screen_refresh=true;//signal: need to show screen
                        cams[i].SetNoNeedRefresh();
                }//fi
        }//for
        if (need_screen_refresh) {//update it
                        #ifndef DISABLE_VSYNC
                        vsync();
                        #endif

                        acquire_screen();
                        blit(buffer, screen, 0, 0, 0, 0, SCREEN_W, SCREEN_H);
                        release_screen();

                        need_screen_refresh=false;
        }//fi screen changed
        else {//no input...just idle then.
                rest(IDLE_TIME_IN_LOOP);//give time slice
        }//else

      framecount++;


   }//while not quitting


   EXIT_IF(kFuncOK!=DeInit());

   //allegro_exit(); bad idea, since bugs inside

   INFO(normal exit);
   return 0;

}
END_OF_MAIN()

