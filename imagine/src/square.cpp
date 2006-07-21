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

#define DISCREETE_CLEARENCE
#include "consts.h"
#include "camera.h"
#include "excamera.h"
#include "fps.h"
#include "square.h"
#include "timedinput.h"
#include "actions.h"
#include "globaltimer.h"
#include "input.h"


#define GRID_SIZEX    MAX_KEYS_BUFFERED

//temp
int global_select=3;

/* render a tile of the grid which is centered on x and z(world-space)
   with center vertex x,y,z
 * considering camera position */
void draw_square(BITMAP *bmp, MATRIX_f *camera, int x, int y,int z)
{
   V3D_f _v[4], _vout[8], _vtmp[8];//array of 8 structs V3D_f
   V3D_f *v[4], *vout[8], *vtmp[8];//_array_of_8_pointers_ to V3D_f structs
   int flags[4], out[8];
   int tmp_vertex_iter, vertex_count;

   for (tmp_vertex_iter=0; tmp_vertex_iter<4; tmp_vertex_iter++)
      v[tmp_vertex_iter] = &_v[tmp_vertex_iter];//initialize pointers

   for (tmp_vertex_iter=0; tmp_vertex_iter<8; tmp_vertex_iter++) {
      vout[tmp_vertex_iter] = &_vout[tmp_vertex_iter];//init other pointers
      vtmp[tmp_vertex_iter] = &_vtmp[tmp_vertex_iter];
   }

   /* set up four vertices with the world-space position of the tile */
   v[0]->x = x - GRID_SIZEX/2;
   v[0]->z = z;
   v[0]->y = y - GRID_SIZEY/2;

   v[1]->x = x - GRID_SIZEX/2 + TILE_SIZE;
   v[1]->z = z;
   v[1]->y = y - GRID_SIZEY/2;

   v[2]->x = x - GRID_SIZEX/2 + TILE_SIZE;
   v[2]->z = z;
   v[2]->y = y - GRID_SIZEY/2 + TILE_SIZE;

   v[3]->x = x - GRID_SIZEX/2;
   v[3]->z = z;
   v[3]->y = y - GRID_SIZEY/2 + TILE_SIZE;
/*
   v[0]->x = x - GRID_SIZEX/2;
   v[0]->y = y;
   v[0]->z = z - GRID_SIZEY/2;

   v[1]->x = x - GRID_SIZEX/2 + TILE_SIZE;
   v[1]->y = y;
   v[1]->z = z - GRID_SIZEY/2;

   v[2]->x = x - GRID_SIZEX/2 + TILE_SIZE;
   v[2]->y = y;
   v[2]->z = z - GRID_SIZEY/2 + TILE_SIZE;

   v[3]->x = x - GRID_SIZEX/2;
   v[3]->y = y;
   v[3]->z = z - GRID_SIZEY/2 + TILE_SIZE;
*/
           /*v[0]->u=0;
           v[0]->v=0;
           v[1]->u=0;
           v[1]->v=tex->h;
           v[2]->u=tex->w;
           v[2]->v=tex->h;
           v[3]->u=tex->w;
           v[3]->v=0;*/



   /* apply the camera matrix, translating world space -> view space
    * still 3D here !! */
   for (tmp_vertex_iter=0; tmp_vertex_iter<4; tmp_vertex_iter++) {
           //see EACH vertex thru camera eyes
      apply_matrix_f(camera,
                      v[tmp_vertex_iter]->x,
                      v[tmp_vertex_iter]->y,
                      v[tmp_vertex_iter]->z,
                     &v[tmp_vertex_iter]->x,
                     &v[tmp_vertex_iter]->y,
                     &v[tmp_vertex_iter]->z);

      flags[tmp_vertex_iter] = 0;

      /* set flags if this vertex is off the edge of the screen */
      if (v[tmp_vertex_iter]->x < -v[tmp_vertex_iter]->z)
         flags[tmp_vertex_iter] |= 1;
      else if (v[tmp_vertex_iter]->x > v[tmp_vertex_iter]->z)
         flags[tmp_vertex_iter] |= 2;

      if (v[tmp_vertex_iter]->y < -v[tmp_vertex_iter]->z)
         flags[tmp_vertex_iter] |= 4;
      else if (v[tmp_vertex_iter]->y > v[tmp_vertex_iter]->z)
         flags[tmp_vertex_iter] |= 8;

      if (v[tmp_vertex_iter]->z < 0.1)//beyond us? aka out of the screen toward
//the viewer
         flags[tmp_vertex_iter] |= 16;
   }

   /* quit if all vertices are off the same edge of the screen */
   if (flags[0] & flags[1] & flags[2] & flags[3])
      return;

   if (flags[0] | flags[1] | flags[2] | flags[3]) {
      /* clip if any vertices are off the edge of the screen */

           /*
     The routine will correctly interpolate u, v, and tmp_vertex_iter in the vertex
     structure.

     Returns the number of vertices after clipping is done.
            */
      vertex_count = clip3d_f(POLYTYPE_FLAT,
                      0.1,
                      0.1,
                      4, //num vertices
                      (AL_CONST V3D_f **)v,//polygon
                    vout,//output goes in here.
                    vtmp,//internal
                    out);//internal

      if (vertex_count <= 0)
         return;
   }//other if
   else {
      /* no need to bother clipping this one */
      vout[0] = v[0];
      vout[1] = v[1];
      vout[2] = v[2];
      vout[3] = v[3];

      vertex_count = 4;
   }
//vertex_count may be more than 4
/*
This function persp_project_f(...)
     projects from the normalized viewing pyramid, which has a camera
     at the origin and facing along the positive z axis. The x axis
     runs left/right, y runs up/down, and z increases with depth into
     the screen. The camera has a 90 degree field of view, ie. points
     on the planes x=z and -x=z will map onto the left and right edges
     of the screen, and the planes y=z and -y=z map to the top and
     bottom of the screen. If you want a different field of view or
     camera location, you should transform all your objects with an
     appropriate viewing matrix, eg. to get the effect of panning the
     camera 10 degrees to the left, rotate all your objects 10 degrees
     to the right.
 */
   /* project view space -> screen space */
   for (tmp_vertex_iter=0; tmp_vertex_iter<vertex_count; tmp_vertex_iter++)
      persp_project_f(
                      vout[tmp_vertex_iter]->x,//3dx
                      vout[tmp_vertex_iter]->y,//3dy
                      vout[tmp_vertex_iter]->z,//3dz
                     &vout[tmp_vertex_iter]->x,//2dx
                     &vout[tmp_vertex_iter]->y);//2dy

   /* set the color odd ones one color; even ones other color*/
   //only first vertex is needed to have .tmp_vertex_iter=color when using polygon3d...
   vout[0]->c = ((x + y) & 1) ? makecol((x+1)*16, 63*255/(x+1), 128*(y+1)) : makecol(64*y+(x+1)*(y+1), 32*x+(x+1)*(y+1)/3, 128+255/(y+1));

   /* render the polygon */
   polygon3d_f(bmp,
                   POLYTYPE_FLAT,
                   //POLYTYPE_PTEX,
                   //tex,
                   NULL/*texture*/,
                   vertex_count, vout);
}


int compar(const V3D_f *first,const V3D_f *second) {
        if (first->z < second->z)
                return +1;
        else if (first->z > second->z)
                        return -1;
                else return 0;//eq
}

function
draw_squareS(BITMAP *bmp,MATRIX_f *camera)
{
   /* draw the grid of squares */
        int z=0;
   for (int x=0; x<GRID_SIZEX; x++)
      for (int y=0; y<GRID_SIZEY; y++)
         draw_square(bmp,
                         camera,
                         x,
                         y,
                         z);



        V3D_f tempex[GRID_SIZEX*GRID_SIZEY+1];

        int ofs=-1;
   for (int x=0; x<GRID_SIZEX; x++)
      for (int y=0; y<GRID_SIZEY; y++) {
              ofs++;
      apply_matrix_f(camera,
                      x-GRID_SIZEX/2+TILE_SIZE/2
                      ,y-GRID_SIZEY/2+TILE_SIZE/2
                      ,z
                      ,
                      &tempex[ofs].x,
                      &tempex[ofs].y,
                      &tempex[ofs].z
                    );
      tempex[ofs].u=x;
      tempex[ofs].v=y;
      }//fors

   //sort by depth
   int tar=ofs+1;
   //FIXME: this sort messes up the order of textcells displayed on squares
   qsort(&tempex,tar,sizeof(V3D_f),(int (*)(const void*, const void*))compar);

   for (int ofs=0;ofs<tar;ofs++) {
      persp_project_f(
                      tempex[ofs].x,//3dx
                      tempex[ofs].y,//3dy
                      tempex[ofs].z,//3dz
                     &tempex[ofs].x,//2dx
                     &tempex[ofs].y);//2dy

#define easy2(_cond_,_shit_,...)  {\
        if ((_cond_)&&(ofs>=(initial)*GRID_SIZEX)         \
                &&(ofs<(initial+1)*GRID_SIZEX)) {       \
                        textprintf_centre_ex(bmp,font, \
                        (int)tempex[ofs].x, \
                        (int)tempex[ofs].y, \
                        makecol(255,255,255),makecol(0,0,0), \
                        _shit_,__VA_ARGS__ ); \
        } \
        initial++; \
}

#define easy(_shit_,...)  {\
        if ((ofs>=(initial)*GRID_SIZEX)         \
                &&(ofs<(initial+1)*GRID_SIZEX)) {       \
                        textprintf_centre_ex(bmp,font, \
                        (int)tempex[ofs].x, \
                        (int)tempex[ofs].y, \
                        makecol(255,255,255),makecol(0,0,0), \
                        _shit_,__VA_ARGS__ ); \
        } \
        initial++; \
}

#define easy3(_shit_,...) { \
                now++;initial--; \
                easy2(ofs==now,_shit_,__VA_ARGS__); \
}

        if (tempex[ofs].z>0.1) {//in our view not behind us
switch (global_select) {
case 0: {
// this is ok:
                textprintf_centre_ex(bmp,font,
                (int)tempex[ofs].x,
                (int)tempex[ofs].y,
                   /*makecol((int)tempex[ofs].z*32 % 255,
                           (int)tempex[ofs].z*8 % 255,
                           255),*/
                        makecol(255,255,255),
                   makecol(0,0,0),
                   "%.0f/%.0f",
                   tempex[ofs].u,
                   tempex[ofs].v);
break;}
case 1: {

                KEY_TYPE into;
                int ind=ofs % MAX_KEYS_BUFFERED;
                into.ScanCode=gKeyBuf[ind].ScanCode;
#ifdef ENABLE_TIMED_INPUT
                into.TimeDiff=gKeyBuf[ind].TimeDiff;
#endif

                int initial=0;
                easy2(ofs==7,"COUNT%d",AllLowLevelInputs[kKeyboardInputType]->HowManyInBuffer());

                easy2((ofs % GRID_SIZEY) == gKeyBufHead,
                        "KHEAD%d",gKeyBufHead);

#ifdef ENABLE_TIMED_INPUT
                easy("%d",into.TimeDiff);
#endif
                easy("%s%s",
                        GetKeyName(&into),
                        ISPRESSED(into.ScanCode)?"_":"~"
                        );

                easy2((ofs % GRID_SIZEY) == gKeyBufTail,
                        "KTAIL%d",gKeyBufTail);

                easy2((ofs % GRID_SIZEY) == gMouseBufHead,
                        "MHEAD%d",gMouseBufHead);


                int indm= ofs % MAX_MOUSE_EVENTS_BUFFERED;
                //BUG here, def and assignment fails, they've to be split
                MOUSE_TYPE mous;
                mous=gMouseBuf[indm];
#ifdef ENABLE_TIMED_INPUT
                easy("%d",mous.TimeDiff);
#endif
                easy("%d",mous.MickeyX);
                easy("%d",mous.MickeyY);
                easy("%d",mous.Flags);

                easy2((ofs % GRID_SIZEY) == gMouseBufTail,
                        "MTAIL%d",gMouseBufTail);
break;}
case 2: {
                int initial=0;
                int now=(initial)*GRID_SIZEX;
                easy2(ofs==now,"LostKP/R:%d/%d",
                        gLostKeysPressed,
                        gLostKeysReleased);
                now+=2;initial--;
                easy2(ofs==now,"LostMouse:%d",
                                gLostMouseEvents);
                now+=2;initial--;

                easy2(ofs==now,"FPS:%d",fps);
                now++;initial--;
                easy2(ofs==now,"CAM:%d/%d",current_cam,NUM_CAMS-1);

        MProjectedCamera *mycam=&cams[current_cam];
                easy3("%s","AspectR");
                easy3("%.2f",mycam->GetAspect());
                easy3("%s","FOV");
                easy3("%d",mycam->GetFOV());
                initial+=1;
                now=(initial-1)*GRID_SIZEX-1;
                easy3("%s","ViewportW");
                easy3("%d",mycam->GetW());
                easy3("%s","ViewportH");
                easy3("%d",mycam->GetH());

   float xf,yf,zf,xu,yu,zu;
   mycam->GetFrontVector(&xf,&yf,&zf);
   mycam->GetUpVector(&xu,&yu,&zu);
      float xpos,ypos,zpos;
         mycam->GetPos(&xpos,&ypos,&zpos);


                initial+=1;
                now=(initial-1)*GRID_SIZEX-1;
                easy3("%s","POS");
                easy3("PX:%.2f",xpos);
                easy3("PY:%.2f",ypos);
                easy3("PZ:%.2f",zpos);
                initial+=1;
                now=(initial-1)*GRID_SIZEX-1;
                easy3("%s","UPvect");
                easy3("UVX:%.2f",xu);
                easy3("UVY:%.2f",yu);
                easy3("UVZ:%.2f",zu);
                initial+=1;
                now=(initial-1)*GRID_SIZEX-1;
                easy3("%s","FRvect");
                easy3("FVX:%.2f",xf);
                easy3("FVY:%.2f",yf);
                easy3("FVZ:%.2f",zf);
break;}
case 3: {
                int initial=1;
                int now=(initial-1)*GRID_SIZEX-1;
                //easy3("gCurrent_ExecuteActionTimer_Time:%d",
                  //              gCurrent_ExecuteActionTimer_Time);
                easy3("Size of GenericInputBuffer:%d",
                                GenericInputBuffer.GetSize());
                now=(initial-1)*GRID_SIZEX-1;
                easy2((ofs % GRID_SIZEY) == GenericInputBuffer.fHead,
                        "GHEAD%d",GenericInputBuffer.fHead);
                easy("%d",
                        GenericInputBuffer.Buffer[ofs % GRID_SIZEY]
                    );
#ifdef ENABLE_TIMED_INPUT
                easy("%d",
                        GenericInputBuffer.Buffer[ofs % GRID_SIZEY].TimeDiff
                        );
#endif
                easy2((ofs % GRID_SIZEY) == GenericInputBuffer.fTail,
                        "GTAIL%d",GenericInputBuffer.fTail);

                initial+=1;
                now=(initial-1)*GRID_SIZEX+1;
                easy3("gInputBufCount==%d",
                        gInputBufCount);

                initial+=1;
                now=(initial-1)*GRID_SIZEX+1;
                easy3("gMouseBufCount==%d",
                        gMouseBufCount);

                //initial+=1;
                now=(initial-1)*GRID_SIZEX+5;
                easy3("gKeyBufCount==%d",
                        gKeyBufCount);

                initial+=1;
                now=(initial-1)*GRID_SIZEX+1;
                easy3("ActionsInputBuffer.IsEmpty()==%d",
                        ActionsInputBuffer.IsEmpty());
/*                easy3("Size of ActionsInputBuffer:%d",
                                ActionsInputBuffer.GetSize());*/
                now=(initial-1)*GRID_SIZEX-1;
                easy2((ofs % GRID_SIZEY) == ActionsInputBuffer.fHead,
                        "AHEAD%d",ActionsInputBuffer.fHead);
                easy("%d",
                        ActionsInputBuffer.Buffer[ofs % GRID_SIZEY]
                    );
#ifdef ENABLE_TIMED_INPUT
                easy("%d",
                        ActionsInputBuffer.Buffer[ofs % GRID_SIZEY].TimeDiff
                        );
#endif
                easy2((ofs % GRID_SIZEY) == ActionsInputBuffer.fTail,
                        "ATAIL%d",ActionsInputBuffer.fTail);

                /*initial+=1;
                now=(initial-1)*GRID_SIZEX+1;
                easy3("SLLArray_st GI_StrictOrderSLL[kMaxInputTypes=%d]",
                                kMaxInputTypes);
                now=(initial-1)*GRID_SIZEX-1;
                easy2((ofs % GRID_SIZEY) == 5*(ofs % kMaxInputTypes),
                        "GI_StrictOrderSLL[%d].HowManySoFar=%d",
                                ofs % kMaxInputTypes,
                           GI_StrictOrderSLL[ofs % kMaxInputTypes].HowManySoFar);*/
break;}
case 4:{
                int initial=3;
                int now=(initial-1)*GRID_SIZEX+3;
                easy3("TICKS_OF_GLOBALTIMER:%ld",
                               TICKS_OF_GLOBALTIMER);
                initial++;
                now=(initial-1)*GRID_SIZEX+3;
                easy3("BPS_OF_GLOBALTIMER:%d",
                               BPS_OF_GLOBALTIMER);
                initial++;
                now=(initial-1)*GRID_SIZEX+3;
                easy3("gTimer:%d",
                               gTimer);
                initial++;
                now=(initial-1)*GRID_SIZEX+3;
                easy3("gSpeedRegulator:%d",
                               gSpeedRegulator);
break;}
default: {
        ERR(undefined yet);
         }
}//switch
        }//fi visible
      }//for
        _OK;
}//func


