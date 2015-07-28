#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <string>
#include <math.h>

using namespace std;

#include "font.h"
#include "guiitem.h"
#include "eventinterface.h"
#include "slider.h"

// Creates a new slider
SLIDER *SLIDER::create(int id, bool horizontal,
                       int x, int y, int w, int h,
                       int min, int max, int value,
                       void (*scrollCallback)(GUIITEM *slider, void *userData),
                       void *userData)
{
  // Make sure input is valid
  if (max<min)
  {
    int temp;
    temp=max;
    max=min;
    min=temp;
  }
  if (value<min) value=min;
  if (value>max) value=max;

  // Allocate memory
  SLIDER *s = new SLIDER();
  if (!s) return 0;

  // Copy data
  s->id=id;
  s->horizontal=horizontal;
  s->setPosition(x, y);
  s->setSize(w, h);
  s->min=min;
  s->max=max;
  s->value=value;
  s->setCallback(scrollCallback);
  s->setUserData(userData);

  // Return the created object
  return s;
}

// Constructor
SLIDER::SLIDER()
{
  horizontal=true;
  drag=false;
  min=0;
  max=100;
  value=50;
  fillColor1[0]=0.95;
  fillColor1[1]=0.95;
  fillColor1[2]=0.91;
  fillColor2[0]=0.29;
  fillColor2[1]=0.58;
  fillColor2[2]=0.91;
  borderColor[0]=0.71;
  borderColor[1]=0.77;
  borderColor[2]=0.80;
}

// Destructor
SLIDER::~SLIDER()
{
  // Empty
}

// QUERY methods-------------------------------------------

int SLIDER::getValue(void)
{
  return value;
}

// Update methods

//
void SLIDER::setValue(int value)
{
  this->value=value;
}

// Private!
float SLIDER::getHandlePos(void)
{
  if (horizontal)
    return w*((float)(value-min)/(float)(max-min));
  else
    return h*((float)(value-min)/(float)(max-min));
}


// Renders a slider
void SLIDER::render(void)
{
  // Render only if the item is visible
  if (visible)
  {
    float xx, yy, ww, hh;

    if (horizontal) { xx=0; yy=0.3333*h; ww=w; hh=0.3333*h; }
    else { xx=0.3333*w; yy=0; ww=0.3333*w; hh=h; }

    // Base
    if (texture1)
    {
      glBindTexture(GL_TEXTURE_2D, texture1);
      glEnable(GL_TEXTURE_2D);
    }
    else  glDisable(GL_TEXTURE_2D);

    glColor4f(fillColor1[0], fillColor1[1], fillColor1[2], transparency);
    glBegin(GL_QUADS);
    glTexCoord2f(0, 0);
    glVertex2f(xx, yy);
    glTexCoord2f(0, 1);
    glVertex2f(xx, yy+hh);
    glTexCoord2f(1, 1);
    glVertex2f(xx+ww, yy+hh);
    glTexCoord2f(1, 0);
    glVertex2f(xx+ww, yy);
    glEnd();
    glDisable(GL_TEXTURE_2D);

    // Base border
    if (border)
    {
      glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
      glLineWidth(1);
      glBegin(GL_LINE_LOOP);
      glVertex2f(xx, yy);
      glVertex2f(xx, yy+hh);
      glVertex2f(xx+ww, yy+hh);
      glVertex2f(xx+ww, yy);
      glEnd();
    }

    // Handle
    float t = getHandlePos();
    float r = 0.5*(horizontal?h:w);
    int tesselation=(int)(0.2*(2.0*M_PI*r));
    if (tesselation<2) tesselation=2;
    if (horizontal) { xx=t; yy=r; } else { xx=r; yy=t; }

    glColor4f(fillColor2[0], fillColor2[1], fillColor2[2], transparency);
    if (texture2)
    {
      glBindTexture(GL_TEXTURE_2D, texture2);
      glEnable(GL_TEXTURE_2D);
    }
    else glDisable(GL_TEXTURE_2D);

    glBegin(GL_POLYGON);
    for (int i=0; i<=tesselation; i++)
    {
      float a=((float)i/tesselation)*2.0*M_PI;
      glTexCoord2f(0.5+0.5*cos(a), 0.5+0.5*sin(a));
      glVertex2f(xx+r*cos(a), yy+r*sin(a));
    }
    glEnd();
    glDisable(GL_TEXTURE_2D);

    // Handle border
    if (border)
    {
      glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
      glBegin(GL_LINE_LOOP);
      for (int i=0; i<=tesselation; i++)
      {
        float a=((float)i/tesselation)*2.0*M_PI;
        glVertex2f(xx+r*cos(a), yy+r*sin(a));
      }
      glEnd();
    }
  }
}

// Event handlers------------------------------------------

bool SLIDER::isOnActive(int xPos, int yPos)
{
  float xx = horizontal?getHandlePos():(0.5*w);
  float yy = horizontal?(0.5*h):getHandlePos();
  float r = 0.5*(horizontal?h:w);

  if ((xPos-xx)*(xPos-xx)+(yPos-yy)*(yPos-yy)<=r*r)
    return true;

  return false;
}

// Handles mouse button down event
bool SLIDER::mouseButtonDown(int xPos, int yPos)
{
  if (visible && active)
  {
    if (isOnActive(xPos, yPos))
    {
      drag=true;
      grapDelta=(int)(horizontal?(xPos-getHandlePos()):(yPos-getHandlePos()));
      return true;
    }
  }
  return false;
}

// Handles mouseup event
void SLIDER::mouseButtonUp(int xPos, int yPos)
{
  drag = false;
}

// Handles mouse move ivent
void SLIDER::mouseMove(bool button, int xPos, int yPos)
{
  if (drag)
  {
    float fvalue=min+((horizontal?(xPos-grapDelta):(yPos-grapDelta)))*((float)(max-min)/(float)(horizontal?w:h));
    if (fvalue<min) fvalue=min;
    if (fvalue>max) fvalue=max;
    value=(int)(fvalue+0.5);
    if (callback)
     callback(this, userData);
  }
}

// Handles key down event
void SLIDER::keyDown(char asciiCode)
{
  // Empty
}

