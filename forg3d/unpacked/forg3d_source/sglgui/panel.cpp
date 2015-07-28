#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <string>
#include <vector>
#include <math.h>

using namespace std;

#include "font.h"
#include "guiitem.h"
#include "eventinterface.h"
#include "rectangle.h"
#include "slider.h"
#include "button.h"
#include "label.h"
#include "checkbox.h"
#include "listbox.h"
#include "scrollbar.h"
#include "console.h"
#include "combobox.h"
#include "editfield.h"
#include "progressbar.h"
#include "menu.h"
#include "menubar.h"
#include "itemcontainer.h"
#include "panel.h"

// Creates a panel with given size
PANEL *PANEL::create(int id, const char *text, const FONT *font,
                     bool header, bool dragable,
                     int x, int y, int w, int h)
{
  PANEL *p=0;

  // Allocate memory
  p = new PANEL();
  if (!p) return 0;

  // Copy values
  p->id=id;
  p->text=text;
  p->font=font;
  p->header=header;
  p->dragable=dragable;
  p->x=x;
  p->y=y;
  p->w=w;
  p->h=h;

  // Return the created panel
  return p;
}

// Inits a panel
PANEL::PANEL(void)
{
  header=true;
  headerSize=PANEL_DEFAULT_HEADER_SIZE;
  fontColor[0]=1;
  fontColor[1]=1;
  fontColor[2]=1;
  fillColor1[0]=0.93;
  fillColor1[1]=0.91;
  fillColor1[2]=0.90;
  fillColor2[0]=0.0;
  fillColor2[1]=0.33;
  fillColor2[2]=0.90;
  borderColor[0]=0;
  borderColor[1]=0.33;
  borderColor[2]=0.90;
  dragable=true;
  drag=false;
  grapPoint[0]=0;
  grapPoint[1]=0;
}

// Destructor
PANEL::~PANEL()
{
 // Empty
}


// PROCEDURE methods---------------------------------------

// Renders the panel
void PANEL::render(void)
{
  // Render only if the panel is visible
  if (visible)
  {
    // Set the scissor box to match this item
    setScissor();
    glDisable(GL_SCISSOR_TEST);

    float r = 0.33*headerSize;
    if (w < 2*r) r = 0.5*w;

    int tesselation = (int)(0.2f*(0.25*2.0f*M_PI*r));
    if (tesselation < 2) tesselation=2;

    // If the panel has header
    if (header)
    {
      // Render header
      if (texture2)
      {
        glBindTexture(GL_TEXTURE_2D, texture2);
        glEnable(GL_TEXTURE_2D);
      }
      else glDisable(GL_TEXTURE_2D);

      glColor4f(fillColor2[0], fillColor2[1], fillColor2[2], transparency);
      glBegin(GL_POLYGON);
      for (int i=0; i<=tesselation; i++)
      {
        float a=((float)i/(float)tesselation)*0.5f*M_PI+0.5f*M_PI;
        float x=r+r*cosf(a);
        float y=r-r*sinf(a);
        glTexCoord2f(x/w, y/headerSize);
        glVertex2f(x, y);
      }
      glTexCoord2f(0, 1);
      glVertex2f(0, headerSize);
      glTexCoord2f(1, 1);
      glVertex2f(w, headerSize);
      for (int i=0; i<=tesselation; i++)
      {
        float a=((float)i/(float)tesselation)*0.5f*M_PI;
        float x=w-r+r*cosf(a);
        float y=r-r*sinf(a);
        glTexCoord2f(x/w, y/headerSize);
        glVertex2f(x, y);
      }

      glVertex2f(w-r, 0);
      glVertex2f(r, 0);
      glEnd();

      // Render base
      if (texture1)
      {
        glBindTexture(GL_TEXTURE_2D, texture1);
        glEnable(GL_TEXTURE_2D);
      }
      else glDisable(GL_TEXTURE_2D);

      glColor4f(fillColor1[0], fillColor1[1], fillColor1[2], transparency);
      glBegin(GL_QUADS);
      glTexCoord2f(0, 0);
      glVertex2f(0, headerSize);
      glTexCoord2f(0, 1);
      glVertex2f(0, h);
      glTexCoord2f(1, 1);
      glVertex2f(w, h);
      glTexCoord2f(1, 0);
      glVertex2f(w, headerSize);
      glEnd();
      glDisable(GL_TEXTURE_2D);
    }
    else
    {
      // Render base
      if (texture1)
      {
        glBindTexture(GL_TEXTURE_2D, texture1);
        glEnable(GL_TEXTURE_2D);
      }
      else glDisable(GL_TEXTURE_2D);

      glColor4f(fillColor1[0], fillColor1[1], fillColor1[2], transparency);
      glBegin(GL_QUADS);
      glTexCoord2f(0, 0);
      glVertex2f(0, 0);
      glTexCoord2f(0, 1);
      glVertex2f(0, h);
      glTexCoord2f(1, 1);
      glVertex2f(w, h);
      glTexCoord2f(1, 0);
      glVertex2f(w, 0);
      glEnd();
      glDisable(GL_TEXTURE_2D);
    }

    // Print title
    if (header)
    {
      glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
      glEnable(GL_TEXTURE_2D);
      font->printCentered(text.c_str(), w/2, headerSize/2);
    }

    // Render border
    if (border)
    {
      glLineWidth(1);
      glDisable(GL_TEXTURE_2D);
      glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
      if (header)
      {
        glBegin(GL_LINE_LOOP);
        for (int i=0; i<=tesselation; i++)
        {
          float a=((float)i/(float)tesselation)*0.5f*M_PI+0.5f*M_PI;
          glVertex2f(r+r*cosf(a), r-r*sinf(a));
        }
        glVertex2f(0, h);
        glVertex2f(w, h);
        for (int i=0; i<=tesselation; i++)
        {
          float a=((float)i/(float)tesselation)*0.5f*M_PI;
          glVertex2f(w-r+r*cosf(a), r-r*sinf(a));
        }
        glVertex2f(w-r, 0);
        glVertex2f(r, 0);
        glEnd();
      }
      else
      {
        glBegin(GL_LINE_LOOP);
        glVertex2f(0, 0);
        glVertex2f(0, h);
        glVertex2f(w, h);
        glVertex2f(w, 0);
        glEnd();
      }
    }

    // Render items
    renderItems();
  }
}




// QUERY methods-------------------------------------------

// Returns the size of th header
int PANEL::getHeaderSize(void) const
{
  return headerSize;
}

// returns true if the header is visible
bool PANEL::isHeaderVisible(void) const
{
  return header;
}

// UPDATE methods -----------------------------------------

void PANEL::setSize(int w, int h)
{
  GUIITEM::setSize(w, h);
  if (headerSize > this->h) headerSize = this->h;
}

// Sets weather or not the panel can be dragged
void PANEL::setDragability(bool dragable)
{
  this->dragable=dragable;
}

// Set weather or not the panel has visible header
void PANEL::setHeaderVisibility(bool header)
{
  this->header=header;
}

void PANEL::setHeaderSize(int size)
{
  if (size < PANEL_MIN_HEADER_SIZE) size = PANEL_MIN_HEADER_SIZE;
  if (size > h) size = h;

  headerSize=size;
}

void PANEL::killFocus()
{
  killFocusItems();
  focus=false;
}

// Event handlers------------------------------------------

bool PANEL::isOnActive(int xPos, int yPos)
{
  if (xPos>=0 && xPos<=w && yPos>=0 && yPos<=h)
    return true;
  return false;
}

void PANEL::preMouseButtonDown(int xPos, int yPos)
{
  preMouseButtonDownItems(xPos, yPos);
}

// Handles mousemove event
bool PANEL::mouseButtonDown(int xPos, int yPos)
{
  if (visible && active)
  {
    if (!mouseButtonDownItems(xPos, yPos))
    {
      if (isOnActive(xPos, yPos))
      {
        if (dragable)
        {
          grapPoint[0]=xPos+x;
          grapPoint[1]=yPos+y;
          drag=true;
        }
        return true;
      }
    }
    else return true;
  }
  return false;
}

// Handle mouse up event
void PANEL::mouseButtonUp(int xPos, int yPos)
{
  if (visible && active)
  {
    mouseButtonUpItems(xPos, yPos);

    if (drag) drag=false;
  }
}

// Handle mouse move event
void PANEL::mouseMove(bool button, int xPos, int yPos)
{
  if (visible && active)
  {
    mouseMoveItems(button, xPos, yPos);

    if (drag && button)
    {
      int xx, yy;
      xx=xPos+x;
      yy=yPos+y;
      setPosition(x+(xx-grapPoint[0]), y+(yy-grapPoint[1]));
      grapPoint[0]=xx;
      grapPoint[1]=yy;
    }
  }
}

// Handles keydown event
void PANEL::keyDown(char asciiCode)
{
  if (visible && active)
  {
    keyDownItems(asciiCode);
  }
}
