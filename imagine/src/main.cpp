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
#include "globaltimer.h"


//#define DISABLE_VSYNC //faster but may(should) flicker

//rest(x) when no input, reduces cpu cycles inside loop
//this also counts as the fastest response time after going idle; using mouse movements should keep fluidity
#define IDLE_TIME_IN_LOOP 100 //miliseconds
//ie . after releasing a key(and no other inputs are present) then pressing another(or same) key would wait at most this time (IDLE_TIME_IN_LOOP) before reacting (not considering processing time)


bool need_screen_refresh=true;//first time display screen


int main(void)
{
        __tIFnok (Init());//throw if kFuncOK!= Init() OR Init() threw exception

   while (!Flag(kF_QuitProgram)) {

        //here, transform all inputs into actions
        function state;
        __(state=MangleInputs());//if returns kFuncOK then there are actions in queue to be executed!
        if ((kFuncNoGenericInputs==state)
                ||(kFuncNoActions==state)){//this means there were low level inputs but were invalid to generate genericinputs; OR there were both but there were no actions executed(test this by pressing and releasing the grave "`" key)
                __tIFnok( cams[current_cam]->SetNeedRefresh() );//maybe to refresh on screen the keyboard input buffer
        }

        //game cycles, catching up to timer
        while (gTimer != gSpeedRegulator) {//are we behind schedule?
                //then, catch on
                //here, execute all actions, eventually disabling them(ie. one-time actions get executed once then disabled)

//using this idea there'd be a worst case lag of at most one second, ie. setting BPS_OF_EXECUTION==1 and pressing and releasing a key which does motion of 'chess' board would still move it, even if neither of the two got processed inside Executant(), this effect would be hated when keeping key down for like few seconds causing continous motion (each second), after releasing the key, there would still be one more motion which would follow at most 1 second apart after releasing the key.
#define BPS_OF_EXECUTION 10 //==times per second we exec continous actions;
#if (BPS_OF_GLOBALTIMER < BPS_OF_EXECUTION) || (BPS_OF_EXECUTION<=0)
#error "BPS_OF_GLOBALTIMER must be bigger than (or at most equal to) BPS_OF_EXECUTION, the latter must be at least ==1"
#endif

                if (gSpeedRegulator % (BPS_OF_GLOBALTIMER/BPS_OF_EXECUTION)==0){
                        __(Executant());
                }

               gSpeedRegulator=(gSpeedRegulator+1) % GLOBALTIMER_WRAPSAROUND_AT;
        }//while


        for (int i=0;i<NUM_CAMS;i++){
                __if (cams[i]->NeedsRefresh()) {
                        __tIFnok(render(buffer,i));//update some part of buffered screen
                        if (false==need_screen_refresh) {
                                need_screen_refresh=true;//signal: need to show buffered screen on monitor
                        }
                        __tIFnok(cams[i]->SetNoNeedRefresh());
                }__fi
        }//for
        if (need_screen_refresh) {//update it
                        #ifndef DISABLE_VSYNC
                        vsync();
                        #endif

                        acquire_screen();
                        __( blit(buffer, screen, 0, 0, 0, 0, SCREEN_W, SCREEN_H) );//who knows maybe it can throw (sure it can - if it believes so!)
                        release_screen();

                        need_screen_refresh=false;
        }//fi screen changed
        else {//no input...just idle then.
                rest(IDLE_TIME_IN_LOOP);//give time slice
        }//else

        framecount++;


   }//while not quitting


   __tIFnok(DeInit());

   INFO(normal exit);
   return EXIT_SUCCESS;

}
END_OF_MAIN()

