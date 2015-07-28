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
#include "checkbox.h"


// Creates a new checkbox
CHECKBOX *CHECKBOX::create(int id, bool checked,
                           const char *text, const FONT *font,
                           int x, int y, int w, int h,
                           void (*checkCallback)(GUIITEM *checkBox,
                                                 void *userData),
                           void *userData)
{
  // Check for invalid input
  if (!font || !text) return 0;

  // Allocate memory
  CHECKBOX *cb = new CHECKBOX();
  if (!cb) return 0;

  // set attributes
  cb->id=id;
  cb->setCheck(checked);
  cb->setText(text);
  cb->setFont(font);
  cb->setPosition(x, y);
  cb->setSize(w, h);
  cb->setCallback(checkCallback);
  cb->setUserData(userData);

  // Return the created object
  return cb;
}

// Constructor
CHECKBOX::CHECKBOX()
{
  fillColor1[0]=1.0;
  fillColor1[1]=1.0;
  fillColor1[2]=1.0;
  fillColor2[0]=0.0;
  fillColor2[1]=0.75;
  fillColor2[2]=0.0;
  borderColor[0]=0.11;
  borderColor[1]=0.32;
  borderColor[2]=0.50;

  checked=false;
  round=false;
}

// Destructor
CHECKBOX::~CHECKBOX()
{
  // Empty
}

// Query methods

// Returns true is the box is checked
bool CHECKBOX::isChecked(void) const
{
  return checked;
}

// Update methods

void CHECKBOX::setCheck(bool check)
{
  checked=check;
}

void CHECKBOX::invertCheck(void)
{
  checked=checked?false:true;
}

// Renders an oval
static void oval(GLenum mode, float x, float y, float rx, float ry)
{
  int tesselation=(int)(0.5f*(2.0f*M_PI*(rx>ry?rx:ry)));
  if (tesselation<2) tesselation=2;

  glBegin(mode);
  for (int i=0; i<tesselation; i++)
  {
    float a=((float)i/(float)tesselation)*2.0f*M_PI;
    glTexCoord2f(0.5f+0.5f*cosf(a), 0.5f+0.5f*sinf(a));
    glVertex2f(x+rx*cosf(a), y+ry*sinf(a));
  }
  glEnd();
}


// Renders the button
void CHECKBOX::render(void)
{
  if (visible)
  {
    // base
    if (texture1)
    {
      glBindTexture(GL_TEXTURE_2D, texture1);
      glEnable(GL_TEXTURE_2D);
    }
    else glDisable(GL_TEXTURE_2D);

    glColor4f(fillColor1[0], fillColor1[1], fillColor1[2], transparency);
    if (round)
    {
      oval(GL_POLYGON, 0.5f*w, 0.5f*h, 0.5f*w, 0.5f*h);
    }
    else
    {
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
    }

    // Marker
    if (checked)
    {
      if (texture2)
      {
        glBindTexture(GL_TEXTURE_2D, texture2);
        glEnable(GL_TEXTURE_2D);
      }
      else
      {
        glDisable(GL_TEXTURE_2D);
      }
      glColor4f(fillColor2[0], fillColor2[1], fillColor2[2], transparency);
      
      if (round)
      {
        oval(GL_POLYGON, 0.5f*w, 0.5f*h, 0.3f*w, 0.3f*h);
      }
      else
      {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(0.2*w, 0.2*h);
        glTexCoord2f(0, 1);
        glVertex2f(0.2*w, 0.8*h);
        glTexCoord2f(1, 1);
        glVertex2f(0.8*w, 0.8*h);
        glTexCoord2f(1, 0);
        glVertex2f(0.8*w, 0.2*h);
        glEnd();
      }
    }

    // Border
    if (border)
    {
      glDisable(GL_TEXTURE_2D);
      glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
      if (round)
      {
        oval(GL_LINE_LOOP, 0.5f*w, 0.5f*h, 0.5f*w, 0.5f*h);
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

    // Render text
    glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
    glEnable(GL_TEXTURE_2D);
    font->printVCentered(text.c_str(), w+2, h/2);
  }
}

bool CHECKBOX::isRound(void) const
{
  return round;
}

void CHECKBOX::setRoundness(bool round)
{
  this->round=round;  
}


// Event handlers------------------------------------------

bool CHECKBOX::isOnActive(int xPos, int yPos)
{
  if (xPos>= 0 && xPos <=w && yPos>=0 && yPos<=h)
    return true;
  return false;
}

// Handles mouse button down event
bool CHECKBOX::mouseButtonDown(int xPos, int yPos)
{
  if (visible && active)
  {
    if (isOnActive(xPos, yPos))
    {
      invertCheck();
      if (callback)
      {
        callback(this, userData);
      }
      return true;
    }
  }
  return false;
}

// Handles mouse button up Event
void CHECKBOX::mouseButtonUp(int xPos, int yPos)
{
  // Empty
}

// Handles mouse move event
void CHECKBOX::mouseMove(bool button, int xPos, int yPos)
{
  // EMpty
}

void CHECKBOX::keyDown(char asciiCode)
{
  // Empty
}
