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
* Description:.
*
****************************************************************************/
#include "allegro.h"

#include "_gcdefs.h"
#include "pnotetrk.h"
#include "globaltimer.h"


volatile GLOBAL_TIMER_TYPE gTimer;
GLOBAL_TIMER_TYPE gSpeedRegulator;//game cycles ;)


void TimerIncrement(void)
{
        gTimer=(gTimer +1 ) % GLOBALTIMER_WRAPSAROUND_AT;//++;//%=TICKS_OF_GLOBALTIMER;
}
END_OF_FUNCTION(TimerIncrement);


function
InstallGlobalTimer()
{
        LOCK_FUNCTION(TimerIncrement);

        LOCK_VARIABLE(gTimer);
        gTimer=gSpeedRegulator=INIT_GLOBALTIMER;//safer start =0
        /*If you call this routine without having first
         *      installed the timer module, install_timer() will be called
         *           automatically.
         */
        __tIF(0 != install_int_ex(TimerIncrement, TICKS_OF_GLOBALTIMER));
        _OK;
}

function
UnInstallGlobalTimer()
{
        remove_int(TimerIncrement);
        _OK;
}

