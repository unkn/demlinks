#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <string>

using namespace std;

#include "font.h"
#include "guiitem.h"
#include "label.h"

LABEL *LABEL::create(int id, const char *text, const FONT *font, int x, int y)
{
  LABEL *l=0;
  
  // Allocate memory
  l = new LABEL();
  if (!l) return 0;
  
  // Copy data
  l->id=id;
  l->text=text;
  l->font=font;
  l->x=x;
  l->y=y;
       
  // Return the created object
  return l;
}

// Constructor
LABEL::LABEL()
{
  // Empty
}

// Destructor
LABEL::~LABEL()
{
  // Empty
}


// Prosedure methods
void LABEL::render(void)
{
  if (visible)
  {
    glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
    glEnable(GL_TEXTURE_2D);
    font->print(text.c_str(), 0, 0, 1, 0, 0, 0, true);
  }     
}

// Event handlers

bool LABEL::mouseButtonDown(int xPos, int yPos)
{
  return false;
}

void LABEL::mouseButtonUp(int xPos, int yPos)
{
  // Empty     
}

void LABEL::mouseMove(bool button, int xPos, int yPos)
{
  // Empty
}

void LABEL::keyDown(char asciiCode)
{
  if (visible && active && focus)
  {
    if (asciiCode==8 && text.size()>0)
    {
      text.erase(text.size()-1, 1);         
    }
    if (asciiCode>=32 && asciiCode<=126) text+=asciiCode;
  }
}
