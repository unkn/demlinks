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
#include "console.h"

CONSOLE::LINE::LINE()
{
  color[0]=0.75;
  color[1]=0.75;
  color[2]=0.75;
}

// Constructor
CONSOLE::CONSOLE(void)
{
  numberOfLines=0;
  line=0;
  interactive=false;
  fillColor1[0]=0;
  fillColor1[1]=0;
  fillColor1[2]=0;
  fontColor[0]=0.75;
  fontColor[1]=0.75;
  fontColor[2]=0.75;
  maxLineWidth=256;
}

// Destructor
CONSOLE::~CONSOLE(void)
{
  if (line)
  {
    for (int i=0; i<numberOfLines; i++)
      delete line[i];
    delete line;
  }
}

//
CONSOLE *CONSOLE::create(int id, bool interactive,
                         const FONT *font, int numberOfLines,
                         int x, int y, int w, int h,
                         void (*commandCallback)(GUIITEM *console,
                                                 void *userData),
                         void *userData)
{
  // Check for invalid input
  if (!font) return 0;
  if (numberOfLines<1) numberOfLines=1;

  // Allocate Memory
  CONSOLE *c = new CONSOLE();
  if (!c) return 0;

  // Copy data
  c->id=id;
  c->setInteractivity(interactive);
  c->setFont(font);
  c->numberOfLines=numberOfLines;
  c->setPosition(x, y);
  c->setSize(w, h);
  c->setCallback(commandCallback);
  c->setUserData(userData);

  c->line = new LINE* [numberOfLines];
  for (int i=0; i<numberOfLines; i++)
    c->line[i]= new LINE();

  // Return the created object
  return c;
}

void CONSOLE::render(void)
{
  // Render only if the console is visible
  if (visible)
  {
    int fs = font->getSize();

    // Set the scissor box to match this item
    setScissor();

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

    // Render text lines
    glEnable(GL_SCISSOR_TEST);

    // If the console is interactive render it with the input line
    if (interactive)
    {
      glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
      font->print(">", 2, h-font->getSize());
      font->print(text.c_str(), 2+fs, h-fs);
      for (int i=0; i<numberOfLines; i++)
      {
        glColor4f(line[i]->color[0], line[i]->color[1], 
                  line[i]->color[2], transparency);
        font->print(line[i]->text.c_str(), 2, h-(i+2)*fs);
      }
    }
    else
    {
      for (int i=0; i<numberOfLines; i++)
      {
        glColor4f(line[i]->color[0], line[i]->color[1], 
                  line[i]->color[2], transparency);
        font->print(line[i]->text.c_str(), 2, h-(i+1)*fs);
      }
    }
    // If the console has focus allso render the cursor
    if (focus)
    {
      float d=font->textWidth(text.c_str())+fs+1;
      glDisable(GL_TEXTURE_2D);
      glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
      glBegin(GL_LINES);
      glVertex2f(d, h-2);
      glVertex2f(d+0.8*fs, h-2);
      glEnd();
    }
  }
  glDisable(GL_SCISSOR_TEST);

  // Border
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
}

// Returns the text of a line
const char *CONSOLE::getLine(int index) const
{
  if (index>=0 && index<numberOfLines)
    return line[index]->text.c_str();
  return 0;
}

// Returns the text of the line 0
const char *CONSOLE::getLatestLine(void) const
{
  return line[0]->text.c_str();
}

// Returns the color of the line of given index
void CONSOLE::getLineColor(int index, float color[3]) const
{
  if (index>=0 && index<numberOfLines)
  {
    color[0]=line[index]->color[0];
    color[1]=line[index]->color[1];
    color[2]=line[index]->color[2];
  }
}

// Returns the maxium line witdh of the console
int CONSOLE::getMaxLineWidth(void) const
{
  return maxLineWidth;
}

bool CONSOLE::isInteractive(void) const
{
  return interactive;
}


// Update methods

// Adds a new line to the boom of the console
void CONSOLE::addLine(const char *text, float r, float g, float b)
{
  LINE *s=line[numberOfLines-1];
  for (int i=numberOfLines-1; i>0; i--)
  {
    line[i]=line[i-1];
  }
  line[0]=s;

  line[0]->text=text;
  line[0]->color[0]=r;
  line[0]->color[1]=g;
  line[0]->color[2]=b;
}


// Sets the color of line with given index
void CONSOLE::setLineColor(int index, float r, float g, float b)
{
  if (index>=0 && index<numberOfLines)
  {
    line[index]->color[0]=r;
    line[index]->color[1]=g;
    line[index]->color[2]=b;
  }
}

// Set the maxium line width of the console
void CONSOLE::setMaxLineWidth(int width)
{
  if (width>0)
    maxLineWidth=width;
}

void CONSOLE::setInteractivity(bool interactive)
{
  this->interactive=interactive;
}

// Event handlers

bool CONSOLE::isOnActive(int xPos, int yPos)
{
  if (xPos>=0 && xPos<=w && yPos>=0 && yPos<=h) return true;
  return false;
}

void CONSOLE::preMouseButtonDown(int xPos, int yPos)
{
  focus=false;     
}

// Handles mouse button down event
bool CONSOLE::mouseButtonDown(int xPos, int yPos)
{
  if (interactive && isOnActive(xPos, yPos))
  {
    return true;
  }
  return false;
}

// Handles mouse button up event
void CONSOLE::mouseButtonUp(int xPos, int yPos)
{
  // Empty
}

void CONSOLE::mouseMove(bool button, int xPos, int yPos)
{
  // Empty
}

void CONSOLE::keyDown(char asciiCode)
{
  if (visible && active && focus && interactive)
  {
    unsigned char c=asciiCode;

    // If enter was pressed
    if (c==13 && text.size()>0)
    {
      addLine(text.c_str(), fontColor[0], fontColor[1], fontColor[2]);
      text="";
      if (callback) callback(this, userData);
    }

    // If backspace was pressed
    if (c==8 && text.size()>0)
    {
      text.erase(text.size()-1, 1);
    }

    // If a letter was typed
    if ((c>=32 && c<=126) || (c>=128 && c<=254))
    {
      if (text.size()<maxLineWidth) text+=asciiCode;
    }
  }
}
