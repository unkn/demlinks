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

#include "fps.h"

#include "_gcdefs.h"
#include "pnotetrk.h"
#include "consts.h"


volatile int fps;
volatile int framecount;

void fps_check(void)
{
   fps = framecount*FPS_INT;
   framecount = 0;
}
END_OF_FUNCTION(fps_check);


EFunctionReturnTypes_t
install_fps() {
        LOCK_FUNCTION(fps_check);
        LOCK_VARIABLE(fps);
        LOCK_VARIABLE(framecount);
        ERR_IF(0!=install_int_ex(fps_check, BPS_TO_TIMER(FPS_INT)),
                        return kFuncFailed);
        return kFuncOK;
}
