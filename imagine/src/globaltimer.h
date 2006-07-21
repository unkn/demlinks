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

#ifndef GLOBALTIMER_H
#define GLOBALTIMER_H

#include "pnotetrk.h"

//the following also counts on how frequent the continous actions are being executed, how many times per second. (aka fps (of execution))
#define BPS_OF_GLOBALTIMER (30) //beats per second 1000 means that the counter equals to miliseconds, since there are 1000 miliseconds in a second and the counter is increased each milisecond(that is 1000 beats per second)

#define TICKS_OF_GLOBALTIMER (BPS_TO_TIMER(BPS_OF_GLOBALTIMER))

//largest measurable interval between two inputs(or actions) : GLOBALTIMER_WRAPSAROUND_AT
//ie. if this value is 10 (seconds) then if you get a diff of 12 seconds it'll mod to 10 thus it'll be exactly like you got 2 seconds diff, so instead of a longer interval you get a shorter one.
#define GLOBALTIMER_WRAPSAROUND_AT_in_seconds (10) //in seconds (default=1)



//the longest interval between when the input is read into the input buffer AND
//when that buffer is emptied such that input in transformed into action(and the action gets executed eventually) this is GLOBALTIMER_WRAPSAROUND_AT
//now suppose you have a real slow computer, or updating screen takes longer than this interval, well then the following could happen, you press key A then key S at 1 second difference such that key A has timer 178 and key S has 178 but that's because the timer wrapped around at exactly 1 second; after this when the keys are read(moved) from input buffer to actions buffer for execution they will be executed at the same time, instead of 1 second difference;
//so if you expect that kind of lag increase this value say to max 10*BPS_OF_GLOBALTIMER
//this is the value at which the GLOBALTIMER wraps around
//also, this interval should not be less than the highest interval in which two polls for input are made, ie. from above, after pressing key A it will be read before key S gets into the input buffer(aka pressed) and execution for key A's assoctiated action begins already so by the time key S gets processed(after that one second) well the action got executed for that long too.
//it may take 20 mins between pressing A and pressing S but it won't take longer than this interval(GLOBALTIMER_WRAPSAROUND_AT) to replay them.
//the difference of timers between pressing key A and pressing key S won't be larger than this value(GLOBALTIMER_WRAPSAROUND_AT-1)
#define GLOBALTIMER_WRAPSAROUND_AT (GLOBALTIMER_WRAPSAROUND_AT_in_seconds*BPS_OF_GLOBALTIMER)

#define INIT_GLOBALTIMER 0 //don't change this let it =0

#define GLOBAL_TIMER_TYPE int

extern volatile GLOBAL_TIMER_TYPE gTimer;
extern GLOBAL_TIMER_TYPE gSpeedRegulator;//highly dependable on gTimer


function
InstallGlobalTimer();

function
UnInstallGlobalTimer();

#endif
