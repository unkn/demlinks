#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <string>
#include <vector>

using namespace std;

#include "font.h"
#include "guiitem.h"
#include "progressbar.h"

// creates a new progressbar
PROGRESSBAR *PROGRESSBAR::create(int id, const FONT *font, 
                                 int x, int y, int w, int h, 
                                 int max, int value)
{
  PROGRESSBAR *pb=0;

  // Allocate memory
  pb=new PROGRESSBAR();
  if (!pb) return 0;

  // Copy attributes
  pb->id=id;
  pb->setFont(font);
  pb->setPosition(x, y);
  pb->setSize(w, h);
  pb->setMax(max);
  pb->setValue(value);

  // Return the created object
  return pb;
}

// Constructor
PROGRESSBAR::PROGRESSBAR()
{
  max=100;
  value=0;
  fillColor1[0]=0.71;
  fillColor1[1]=0.4;
  fillColor1[2]=0.4;
  fillColor2[0]=0.9;
  fillColor2[1]=0.7;
  fillColor2[2]=0.7;
}

// Destructor
PROGRESSBAR::~PROGRESSBAR()
{
  // Empty
}

// Renders the progresbar
void PROGRESSBAR::render(void)
{
  if (visible)
  {
    float t=w*((float)value/(float)max);

    // Render base    
    if (texture2)
    {
      glBindTexture(GL_TEXTURE_2D, texture2);
      glEnable(GL_TEXTURE_2D);
    }
    else glDisable(GL_TEXTURE_2D);   
    glColor4f(fillColor2[0], fillColor2[1], fillColor2[2], transparency);
    glBegin(GL_QUADS);
    glTexCoord2f(0, 0);
    glVertex2f(0, 0);
    glTexCoord2f(0, 1);
    glVertex2f(0, h);
    glTexCoord2f(t/w, 1);
    glVertex2f(t, h);
    glTexCoord2f(t/w, 0);
    glVertex2f(t, 0);
    glEnd();
    
    if (texture1)
    {
      glBindTexture(GL_TEXTURE_2D, texture1);
      glEnable(GL_TEXTURE_2D);
    }
    else glDisable(GL_TEXTURE_2D);
    glColor4f(fillColor1[0], fillColor1[1], fillColor1[2], transparency);
    glBegin(GL_QUADS);
    glTexCoord2f(t/w, 0);
    glVertex2f(t, 0);
    glTexCoord2f(t/w, 1);
    glVertex2f(t, h);
    glTexCoord2f(1, 1);
    glVertex2f(w, h);
    glTexCoord2f(1, 0);
    glVertex2f(w, 0);
    glEnd();

    // render text
    if (font)
    {
      glEnable(GL_TEXTURE_2D);
      glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
      font->printCentered(text.c_str(), w/2, h/2);                   
    }

    // Render border
    if (border)
    {
      glDisable(GL_TEXTURE_2D);
      glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
      glBegin(GL_LINE_LOOP);
      glVertex2f(0, 0);
      glVertex2f(0, h);
      glVertex2f(w, h);
      glVertex2f(w, 0);
      glEnd();
    }
  }
}


int PROGRESSBAR::getValue(void)
{
  return value;    
}

int PROGRESSBAR::getMax(void)
{
  return max;    
}


void PROGRESSBAR::setText(const char *text)
{    
  // Empty       
}

void PROGRESSBAR::setValue(int value)
{
  static char buffer[8]; // Only 5 needed but just in case
  if (value<0) value=0;
  if (value>max) value=max;
  this->value=value;     
  sprintf(buffer, "%d%%", (int)(100*(float)value/(float)max));
  text=buffer;
}

void PROGRESSBAR::setMax(int max)
{
  if (max<1) max=1;
  this->max=max;
  if (value<max) value=max;
}


// Event handlers
bool PROGRESSBAR::mouseButtonDown(int xPos, int yPos)
{
  return false;
}

void PROGRESSBAR::mouseButtonUp(int xPos, int yPos)
{
  // Empty    
}

void PROGRESSBAR::mouseMove(bool button, int xPos, int yPos)
{
  // Empty     
}

void PROGRESSBAR::keyDown(char asciiCode)
{
  // Empty
}
