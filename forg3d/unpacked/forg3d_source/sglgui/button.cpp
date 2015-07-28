#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <math.h>
#include <string>

using namespace std;

#include "font.h"
#include "guiitem.h"
#include "eventinterface.h"
#include "button.h"

// Creates a new button
BUTTON *BUTTON::create(int id, const char *text, const FONT *font,
                       int x, int y, int w, int h,
                       void (*pushCallback)(GUIITEM *button, void *userData),
                       void *userData)
{
  // Check for invalid input
  if (!font || !text) return 0;

  // Allocate memory
  BUTTON *b = new BUTTON();
  if (!b) return 0;

  // set attributes
  b->id=id;
  b->setText(text);
  b->setFont(font);
  b->setPosition(x, y);
  b->setSize(w, h);
  b->setCallback(pushCallback);
  b->setUserData(userData);

  // Return the created button
  return b;
}

// Constructor
BUTTON::BUTTON()
{
  roundness=0.25;
  down=false;
  fillColor1[0]=0.95;
  fillColor1[1]=0.95;
  fillColor1[2]=0.93;
  fillColor2[0]=0.89;
  fillColor2[1]=0.88;
  fillColor2[2]=0.85;
  borderColor[0]=0;
  borderColor[1]=0.24;
  borderColor[2]=0.45;
}

// Destructor
BUTTON::~BUTTON()
{
  // Empty
}




// Renders the button
void BUTTON::render(void)
{
  // Render only if the button is visible
  if (visible)
  {
    // Compute corner radius
    float r=0;
    if (h<w) r = roundness*0.5*h;
    else r = roundness*0.5*w;

    // Compute tesselation
    int tesselation = (int)(0.2f*(0.25*2.0f*M_PI*r));
    if (tesselation < 2) tesselation=2;

    // Render base
    if (down)
    {
      glColor4f(fillColor2[0], fillColor2[1], fillColor2[2], transparency);
      if (texture2)
      {
        glBindTexture(GL_TEXTURE_2D, texture2);
        glEnable(GL_TEXTURE_2D);
      }
      else glDisable(GL_TEXTURE_2D);
    }
    else
    {
      glColor4f(fillColor1[0], fillColor1[1], fillColor1[2], transparency);
      if (texture1)
      {
        glBindTexture(GL_TEXTURE_2D, texture1);
         glEnable(GL_TEXTURE_2D);
      }
      else  glDisable(GL_TEXTURE_2D);
    }

    // Draw the polygon
    glBegin(GL_POLYGON);
    // bottom left corner
    for (int i=0; i<=tesselation; i++)
    {
      float a=((float)i/(2*tesselation))*M_PI+M_PI;
      float xx=r+r*cosf(a);
      float yy=h-r-r*sinf(a);
      glTexCoord2f(xx/w, yy/h);
      glVertex2f(xx, yy);
    }
    // bottom right corner
    for (int i=0; i<=tesselation; i++)
    {
      float a=((float)i/(2*tesselation))*M_PI-(M_PI/2.0f);
      float xx=w-r+r*cosf(a);
      float yy=h-r-r*sinf(a);
      glTexCoord2f(xx/w, yy/h);
      glVertex2f(xx, yy);
    }
    // top right corner
    for (int i=0; i<=tesselation; i++)
    {
      float a=((float)i/(2*tesselation))*M_PI;
      float xx=w-r+r*cosf(a);
      float yy=r-r*sinf(a);
      glTexCoord2f(xx/w, yy/h);
      glVertex2f(xx, yy);
    }
    // top left corner
    for (int i=0; i<=tesselation; i++)
    {
      float a=((float)i/(2*tesselation))*M_PI+(M_PI/2.0f);
      float xx=r+r*cosf(a);
      float yy=r-r*sinf(a);
      glTexCoord2f(xx/w, yy/h);
      glVertex2f(xx, yy);
    }
    glEnd();

    // Border
    if (border)
    {
      glLineWidth(1);
      glDisable(GL_TEXTURE_2D);
      glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
      glBegin(GL_LINE_LOOP);
      // bottom left corner
      for (int i=0; i<=tesselation; i++)
      {
        float a=((float)i/(2*tesselation))*M_PI+M_PI;
        glVertex2f(r+r*cosf(a), h-r-r*sinf(a));
      }
      // bottom right corner
      for (int i=0; i<=tesselation; i++)
      {
        float a=((float)i/(2*tesselation))*M_PI-(M_PI/2.0f);
        glVertex2f(w-r+r*cosf(a), h-r-r*sinf(a));
      }
      // top right corner
      for (int i=0; i<=tesselation; i++)
      {
        float a=((float)i/(2*tesselation))*M_PI;
        glVertex2f(w-r+r*cosf(a), r-r*sinf(a));
      }
      // top left corner
      for (int i=0; i<=tesselation; i++)
      {
        float a=((float)i/(2*tesselation))*M_PI+(M_PI/2.0f);
        glVertex2f(r+r*cosf(a), r-r*sinf(a));
      }
      glEnd();
    }

    // Render text
    
    glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
    glEnable(GL_TEXTURE_2D);
    font->printCentered(text.c_str(), w/2, h/2);
  }
}


// QUERY methods-------------------------------------------

// returns the roundness of the button
float BUTTON::getRoundness(void) const
{
  return roundness;
}

// UPDATE methods------------------------------------------

// Sets the roundness off the button
void BUTTON::setRoundness(float r)
{
  if (r<0.0f) r=0.0f;
  if (r>1.0f) r=1.0f;
  roundness=r;
}

void BUTTON::autoSize(void)
{
  w=(int)(font->textWidth(text.c_str())+2*font->getSize());
  h=(int)(1.5f*font->getSize());     
}


// Event handlers------------------------------------------

bool BUTTON::isOnActive(int xPos, int yPos)
{
  float r=0;
  if (h<w) r = roundness*0.5*h;
  else r = roundness*0.5*w;

  if (xPos >= 0 && xPos <= w && yPos >=r && yPos <= h-r) return true;
  if (xPos >= r && xPos <= w-r && yPos >=0 && yPos <= h) return true;
  if (sqrt((xPos-r)*(xPos-r)+(yPos-r)*(yPos-r))<=r) return true;
  if (sqrt((xPos-(w-r))*(xPos-(w-r))+(yPos-r)*(yPos-r))<=r) return true;
  if (sqrt((xPos-r)*(xPos-r)+(yPos-(h-r))*(yPos-(h-r)))<=r) return true;
  if (sqrt((xPos-(w-r))*(xPos-(w-r))+(yPos-(h-r))*(yPos-(h-r)))<=r) return true;

  return false;
}

// Handles mouse down event
bool BUTTON::mouseButtonDown(int xPos, int yPos)
{
  if (visible && active)
  {
    if (isOnActive(xPos, yPos))
    {
      down=true;
      return true;
    }
  }
  return false;
}

// Handles mouse up event
void BUTTON::mouseButtonUp(int xPos, int yPos)
{
  if (down)
  {
    down=false;
    if (callback && isOnActive(xPos, yPos))
      callback(this, userData);
  }
}

// Handles mouse move event
void BUTTON::mouseMove(bool button, int xPos, int yPos)
{
  if (down && !button) down=false;
}

// Handles key down event
void BUTTON::keyDown(char asciiCode)
{
  // Empty
}


