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
#include "scrollbar.h"

// Constructor
SCROLLBAR::SCROLLBAR()
{
  min=0;
  max=100;
  value=0;
  thumbSize=10;
  horizontal=false;
  drag=false;
  grapPoint=0;
  grapDelta=0;
  fillColor1[0]=98;
  fillColor1[1]=97;
  fillColor1[2]=96;

  fillColor2[0]=0.78;
  fillColor2[1]=0.84;
  fillColor2[2]=0.99;
}

// destructor
SCROLLBAR::~SCROLLBAR()
{
  // Empty
}

// Creates a new scrollbar
SCROLLBAR *SCROLLBAR::create(int id, bool horizontal, int x, int y, int w, int h,
                             int min, int max, int value, int thumbSize,
                             void (*scrollCallback)(GUIITEM *scrollBar, void *userData),
                             void *userData)
{
  SCROLLBAR *sb=0;

  // Check for invalid input
  if (max<=min) max=min+1;
  if (value<min) value=min;
  if (value>max-1) value=max-1;
  if (thumbSize<1) thumbSize=1;
  if (value+thumbSize>max) thumbSize=max-value;

  // Allocate memory
  sb=new SCROLLBAR();
  if (!sb) return 0;

  // Copy data
  sb->id=id;
  sb->horizontal=horizontal;
  sb->setPosition(x, y);
  sb->setSize(w, h);
  sb->min=min;
  sb->max=max;
  sb->value=value;
  sb->thumbSize=thumbSize;
  sb->setCallback(scrollCallback);
  sb->setUserData(userData);


  // Return the created object
  return sb;
}

// Procedure methods

float SCROLLBAR::getHandlePos(void)
{
  return value*((float)(horizontal?w:h)/(float)(max-min));
}

float SCROLLBAR::getHandleSize(void)
{
  return thumbSize*((float)(horizontal?w:h)/(float)(max-min));
}

// Renders the scrollbar
void SCROLLBAR::render(void)
{
  if (visible)
  {
    // render base
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

    // Base border
    if (border)
    {
      glDisable(GL_TEXTURE_2D);
      glLineWidth(1);
      glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
      glBegin(GL_LINE_LOOP);
      glVertex2f(0, 0);
      glVertex2f(0, h);
      glVertex2f(w, h);
      glVertex2f(w, 0);
      glEnd();
    }

    // render handle
    float t1=getHandlePos();
    float t2=getHandleSize();

    if (horizontal)
    {
      // Handle base
      if (texture2)
      {
        glBindTexture(GL_TEXTURE_2D, texture2);
        glEnable(GL_TEXTURE_2D);
      }
      else glDisable(GL_TEXTURE_2D);

      glColor4f(fillColor2[0], fillColor2[1], fillColor2[2], transparency);
      glBegin(GL_QUADS);
      glTexCoord2f(0, 0);
      glVertex2f(t1, 0);
      glTexCoord2f(0, 1);
      glVertex2f(t1, h);
      glTexCoord2f(1, 1);
      glVertex2f(t1+t2, h);
      glTexCoord2f(1, 0);
      glVertex2f(t1+t2, 0);
      glEnd();

      // Handle border
      if (border)
      {
        glDisable(GL_TEXTURE_2D);
        glLineWidth(1);
        glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
        glBegin(GL_LINE_LOOP);
        glVertex2f(t1, 0);
        glVertex2f(t1, h);
        glVertex2f(t1+t2, h);
        glVertex2f(t1+t2, 0);
        glEnd();
      }
    }
    else
    {
      // Handle base
      if (texture2)
      {
        glBindTexture(GL_TEXTURE_2D, texture2);
        glEnable(GL_TEXTURE_2D);
      }
      else glDisable(GL_TEXTURE_2D);

      glColor4f(fillColor2[0], fillColor2[1], fillColor2[2], transparency);
      glBegin(GL_QUADS);
      glTexCoord2f(0, 0);
      glVertex2f(0, t1);
      glTexCoord2f(0, 1);
      glVertex2f(0, t1+t2);
      glTexCoord2f(1, 1);
      glVertex2f(w, t1+t2);
      glTexCoord2f(1, 0);
      glVertex2f(w, t1);
      glEnd();

      // Handle border
      if (border)
      {
        glDisable(GL_TEXTURE_2D);
        glLineWidth(1);
        glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
        glBegin(GL_LINE_LOOP);
        glVertex2f(0, t1);
        glVertex2f(0, t1+t2);
        glVertex2f(w, t1+t2);
        glVertex2f(w, t1);
        glEnd();
      }
    }
  }
}

// Query methods

int SCROLLBAR::getValue(void) const
{
  return value;
}

int SCROLLBAR::getThumbSize(void) const
{
  return thumbSize;
}

int SCROLLBAR::getMin(void) const
{
  return min;
}

int SCROLLBAR::getMax(void) const
{
  return max;
}


// Update methods

// Setting low value does not change the min value
// and cant be creater that hi value
void SCROLLBAR::setValue(int value)
{
  if (value<min) value=min;
  if (value>this->value+thumbSize-1) value=this->value+thumbSize-1;
  this->value=value;
}

// Setting hi value does not affect max value
void SCROLLBAR::setThumbSize(int size)
{
  if (size<1) size=1;
  if (size>max-value) size=max-value;
  thumbSize=size;
}

void SCROLLBAR::setMin(int min)
{
  if (min>value) min=value;
  this->min=min;
}

void SCROLLBAR::setMax(int max)
{
  if (max<value+thumbSize) max=value+thumbSize;
  this->max=max;
}


// Event handlers

bool SCROLLBAR::isOnActive(int xPos, int yPos)
{
  if (horizontal)
  {
    if (xPos>=getHandlePos() && xPos<=getHandlePos()+getHandleSize() &&
       yPos>=0 && yPos<=h)
      return true;
  }
  else
  {
    if (yPos>=getHandlePos() && yPos<=getHandlePos()+getHandleSize() &&
        xPos>=0 && xPos<=w)
      return true;
  }

  return false;
}

// Handles mouse button down event
bool SCROLLBAR::mouseButtonDown(int xPos, int yPos)
{
  if (visible && active)
  {
    if (isOnActive(xPos, yPos))
    {
      drag=true;
      grapPoint=horizontal?xPos:yPos;
      grapDelta=(int)((horizontal?xPos:yPos)-getHandlePos());
      return true;
    }
  }
  return false;
}

// Handles mouse button up event
void SCROLLBAR::mouseButtonUp(int xPos, int yPos)
{
  if (visible && active)
  {
    drag=false;
  }
}

// Handles mouse move event
void SCROLLBAR::mouseMove(bool button, int xPos, int yPos)
{
  if (visible && active && drag)
  {
    int v=(int)(((horizontal?xPos:yPos)-grapDelta)/((float)(horizontal?w:h)/(float)(max-min)));
    if (v<min) v=min;
    if (v>max-thumbSize) v=max-thumbSize;
    value=v;
    if (callback) callback(this, userData);
  }
}

void SCROLLBAR::keyDown(char asciiCode)
{
  // Empty
}
