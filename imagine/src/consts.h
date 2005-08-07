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

#ifndef CONSTS___H
#define CONSTS___H

#ifndef M_PI
   #define M_PI   3.1415926
#endif

/* display a nice 8x8 chessboard grid */
#define GRID_SIZE    8
#define TILE_SIZE    1.0f

/* convert radians to degrees */
#define DEG(n)    ((n) * 180.0 / M_PI)

/* how many times per second the fps will be checked */
#define FPS_INT 1

/* uncomment to disable waiting for vsync */
//#define DISABLE_VSYNC

#define MOTION_SPEED 0.20f
#define SLIDE_SPEED MOTION_SPEED
#define TURN_SPEED 0.10f
#define MOUSE_TURN_SPEED (TURN_SPEED/30.0f)
#define THRUST_SPEED 0.50f

//#define BMPCROSS
//#define ITSPAL //use palette of BMPCROSS loaded

#define CROSSHAIR_LEN 20
#define HOW_MANY_CAMS 4 //u'll get this num at power of two, numcams
#define NUM_CAMS ((HOW_MANY_CAMS*HOW_MANY_CAMS)) //don't change this for now
#define SLIDEVIEW_AMMOUNT 10
#define ENLARGEVIEW_AMMOUNT SLIDEVIEW_AMMOUNT

//rest(x) when no input, reduces cpu cycles inside loop
#define IDLE_TIME_IN_LOOP 100


#define CAMERA_CLASS MProjectedCamera

#endif
