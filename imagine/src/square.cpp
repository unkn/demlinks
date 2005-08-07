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

#include "consts.h"
#include "square.h"

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
   v[0]->x = x - GRID_SIZE/2;
   v[0]->y = y;
   v[0]->z = z - GRID_SIZE/2;

   v[1]->x = x - GRID_SIZE/2 + TILE_SIZE;
   v[1]->y = y;
   v[1]->z = z - GRID_SIZE/2;

   v[2]->x = x - GRID_SIZE/2 + TILE_SIZE;
   v[2]->y = y;
   v[2]->z = z - GRID_SIZE/2 + TILE_SIZE;

   v[3]->x = x - GRID_SIZE/2;
   v[3]->y = y;
   v[3]->z = z - GRID_SIZE/2 + TILE_SIZE;

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
   vout[0]->c = ((x + z) & 1) ? makecol((x+1)*16, 63*255/(x+1), 128*(z+1)) : makecol(64*z+(x+1)*(z+1), 32*x+(x+1)*(z+1)/3, 128+255/(z+1));

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

void draw_squareS(BITMAP *bmp,MATRIX_f *camera)
{
   /* draw the grid of squares */
        int y=0;
   for (int x=0; x<GRID_SIZE; x++)
      for (int z=0; z<GRID_SIZE; z++)
         draw_square(bmp,
                         camera,
                         x,
                         y,
                         z);



V3D_f tempex[GRID_SIZE*GRID_SIZE+1];

        int ofs=-1;
   for (int x=0; x<GRID_SIZE; x++)
      for (int z=0; z<GRID_SIZE; z++) {
              ofs++;
      apply_matrix_f(camera,
                      x-GRID_SIZE/2+TILE_SIZE/2
                      ,y
                      ,z-GRID_SIZE/2+TILE_SIZE/2
                      ,
                      &tempex[ofs].x,
                      &tempex[ofs].y,
                      &tempex[ofs].z
                    );
      tempex[ofs].u=x;
      tempex[ofs].v=z;
      }//fors

   //sort by depth
   int tar=ofs;
   qsort(&tempex,tar+1,sizeof(V3D_f),(int (*)(const void*, const void*))compar);

   for (int ofs=0;ofs<tar+1;ofs++) {
      persp_project_f(
                      tempex[ofs].x,//3dx
                      tempex[ofs].y,//3dy
                      tempex[ofs].z,//3dz
                     &tempex[ofs].x,//2dx
                     &tempex[ofs].y);//2dy

      /*rectfill(bmp,
                      xf-2,
                      yf-2,
                      xf+2,
                      yf+2,
                      makecol(255,0,0));*/
        if (tempex[ofs].z>0.1)//in our view not behind us
                textprintf_centre_ex(bmp,font,
                (int)tempex[ofs].x,
                (int)tempex[ofs].y,
                   makecol(255,
                           255,
                           255),
                   makecol(0,0,0),
                   "%.0f/%.0f",
                   tempex[ofs].u,
                   tempex[ofs].v);
      }//for
}//func


