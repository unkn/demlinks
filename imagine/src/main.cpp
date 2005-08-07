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

#ifdef KEYDEBUG
        #define DISCREETE_CLEARENCE
        #include "timedinput.h"
#endif

#include "common.h"
#include "init.h"
#include "input.h"
#include "excamera.h"
#include "pnotetrk.h"

volatile bool quit_flag=false;
bool need_screen_refresh=true;//first time display screen


#ifdef KEYDEBUG
void doom()
{
                KEY_TYPE into;
      //          MOUSE_TYPE intomouse;

                acquire_screen();
                clear_to_color(screen,makecol(0,0,0));

                //keybd input
                textprintf_ex(screen,font,
                                gKeyBufHead*8,0,
                                makecol(255,255,255),makecol(0,0,0),
                                "H%d",gKeyBuf[gKeyBufHead].Time);
                for (int i=0; i<MAX_KEYS_BUFFERED; i++) {
                        into.ScanCode=gKeyBuf[i].ScanCode;
                        into.Time=gKeyBuf[i].Time;
                        textprintf_ex(screen,font,
                                i*8,8,
                                makecol(255,255,255),makecol(0,0,0),
                                "%s",GetKeyName(&into));
                }//for
                textprintf_ex(screen,font,
                                gKeyBufTail*8,16,
                                makecol(255,255,255),makecol(0,0,0),
                                "T%d",gKeyBuf[gKeyBufTail].Time);
                textprintf_ex(screen,font,
                                gKeyBufTail*8,24,
                                makecol(255,255,255),makecol(0,0,0),
                                "LostPresses/Releases : %d/%d",
                                gLostKeysPressed,gLostKeysReleased);
                //mouse input
                textprintf_ex(screen,font,
                                gMouseBufHead*8*4,32,
                                makecol(255,255,255),makecol(0,0,0),
                                "H%d",gMouseBuf[gMouseBufHead].Time);
                for (int i=0; i<MAX_MOUSE_EVENTS_BUFFERED; i++) {
                        volatile MOUSE_TYPE *shit=&gMouseBuf[i];
                        textprintf_ex(screen,font,
                                i*8*4,40,
                                makecol(255,255,255),makecol(0,0,0),
                                "%d",shit->Flags);
                }//for
                textprintf_ex(screen,font,
                                gMouseBufTail*8*4,48,
                                makecol(255,255,255),makecol(0,0,0),
                                "T%d",gMouseBuf[gMouseBufTail].Time);
                textprintf_ex(screen,font,
                                0,56,
                                makecol(255,255,255),makecol(0,0,0),
                                "LostMouseEvents : %d",
                                gLostMouseEvents);

                //common input
                textprintf_ex(screen,font,
                                gInputBufHead*8*4,64,
                                makecol(255,255,255),makecol(0,0,0),
                                "H%d",gInputBuf[gInputBufHead].type);
                for (int i=0; i<MAX_INPUT_EVENTS_BUFFERED; i++) {
                        volatile INPUT_TYPE *shit=&gInputBuf[i];
                        textprintf_ex(screen,font,
                                i*8*4,72,
                                makecol(255,255,255),makecol(0,0,0),
                                "%d",shit->how_many);
                }//for
                textprintf_ex(screen,font,
                                gInputBufTail*8*4,80,
                                makecol(255,255,255),makecol(0,0,0),
                                "T%d",gInputBuf[gInputBufTail].type);
                textprintf_ex(screen,font,
                                0,88,
                                makecol(255,255,255),makecol(0,0,0),
                                "LostInputEvents key/mouse : %d/%d",
                                gLostInput[kKeyboardInputType],
                                gLostInput[kMouseInputType]
                                );

                textprintf_ex(screen,font,
                                0,100,
                                makecol(255,255,255),makecol(0,0,0),
                                "HowManyInputsInBuffer() : %d",
                                HowManyInputsInBuffer()
                                );

                release_screen();

}
#endif

int main(void)
{
        EXIT_IF(kFuncOK != init());

   while (!quit_flag) {
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

#ifdef KEYDEBUG
        doom();
        ShowAllNotifications();
#endif


   }//while not quitting

   destroy_bitmap(buffer);
#ifdef BMPCROSS
   destroy_bitmap(tex);
#endif
   //allegro_exit();
   return 0;

}
END_OF_MAIN()

