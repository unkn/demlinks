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
#include "eventinterface.h"
#include "editfield.h"


EDITFIELD *EDITFIELD::create(int id, const char *text, const FONT *font,
                             int x, int y, int w, int h, int maxLength,
                             void (*editCallback)(GUIITEM *ef, void *userData),
                             void *userData)
{
  // Check for invalid input
  if (!font || !text) return 0;

  // Allocate memory
  EDITFIELD *ef = new EDITFIELD();
  if (!ef) return 0;

  // copy data
  ef->id=id;
  ef->setText(text);
  ef->setFont(font);
  ef->setPosition(x, y);
  ef->setSize(w, h);
  ef->maxLength=maxLength;
  //ef->setMaxLength(maxLength);
  ef->setCallback(editCallback);
  ef->setUserData(userData);

  // Return the created object
  return ef;
}

// Constructor
EDITFIELD::EDITFIELD()
{
  password=false;
  focus=false;
  maxLength=EDITFIELD_DEFAULT_MAX_LENGTH;
  scroll=0;
  fillColor1[0]=1;
  fillColor1[1]=1;
  fillColor1[2]=1;
  borderColor[0]=0;
  borderColor[1]=0;
  borderColor[2]=0;
}

// Destructor
EDITFIELD::~EDITFIELD()
{
  // Empty
}

//
void EDITFIELD::render(void)
{
  // Render only if the editfield is visible
  if (visible)
  {
    // set scissor box
    setScissor();

    // Render Bottom
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

    // Render Text
    glEnable(GL_SCISSOR_TEST);
    if (font)
    {
      glEnable(GL_TEXTURE_2D);
      glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
      font->printVCentered(text.c_str(), 2, h/2, 1, scroll, 0, w);
      if (focus)
      {
        glDisable(GL_TEXTURE_2D);
        float t=font->textWidth(text.c_str(), scroll)+2;
        glBegin(GL_LINES);
        glVertex2f(t, 2);
        glVertex2f(t, h-2);
        glEnd();
      }
    }
    glDisable(GL_SCISSOR_TEST);

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


int EDITFIELD::getMaxLength(void) const
{
  return maxLength;    
}

void EDITFIELD::setText(const char *text)
{
  GUIITEM::setText(text);     
  scroll=0;
}


// Event handlers


bool EDITFIELD::isOnActive(int xPos, int yPos)
{
  if (xPos>=0 && xPos<=w && yPos>=0 && yPos<=h) return true;
  return false;
}

void EDITFIELD::preMouseButtonDown(int xPos, int yPos)
{
  if (!isOnActive(xPos, yPos))
  {
    focus=false;     
    scroll=0;
  }    
}

bool EDITFIELD::mouseButtonDown(int xPos, int yPos)
{
  if (visible && active)
  {
    if (isOnActive(xPos, yPos))
    {
      if (!focus)
      {
        focus = true;
        // Scroll the text if it does not fit to the box
        // Can this cause an infinite loop? (only if w<=2 ?)
        while (font->textWidth(text.c_str(), scroll)+2>=w)
        {
          scroll++;
        }
      }
      return true;
    }
  }
  return false;
}

void EDITFIELD::mouseButtonUp(int xPos, int yPos)
{
  // Empty
}

void EDITFIELD::mouseMove(bool button, int xPos, int yPos)
{
  // Empty
}


void EDITFIELD::keyDown(char asciiCode)
{
  if (focus && font)
  {
    unsigned char c=asciiCode;

    // Add letters
    if ((c>=32 && c<=126) || c>=128)
    {
      if (text.size()<maxLength)
      {
        text+=c;
        // Scroll the text if it does not fit to the box
        // Can this cause an infinite loop? (only if w<=2 ?)
        while (font->textWidth(text.c_str(), scroll)+2>=w)
        {
          scroll++;
        }
        
        if (callback) callback(this, userData);
      }
    }

    // Backspace
    if (c==8 && text.size()>0)
    {
      // Remove the last letter from the string
      text.erase(text.size()-1, 1);

      // scroll the text
      while (scroll>0 && 
             font->textWidth(text.c_str(), scroll-1)+2<w)
      {
        scroll--;
      }
      
      if (callback) callback(this, userData);
    }
  }
}

