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
#include "rectangle.h"

// Creates a new rectangle
RECTANGLE *RECTANGLE::create(int id, int x, int y, int w, int h,
                             float r, float g, float b, GLuint texture)
{
  RECTANGLE *re=0;

  // Allocate memory
  re = new RECTANGLE();
  if (!re) return 0;

  // Copy info
  re->id=id;
  re->x=x;
  re->y=y;
  re->w=w;
  re->h=h;
  re->fillColor1[0]=r;
  re->fillColor1[1]=g;
  re->fillColor1[2]=b;
  re->texture1=texture;

  return re;
}

// Constructor
RECTANGLE::RECTANGLE()
{
  // Empty
}

// Destructor
RECTANGLE::~RECTANGLE()
{
  // Empty
}

// Renders the item
void RECTANGLE::render(void)
{
  // Render only if the item is visible
  if (visible)
  {
    // set states
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glEnable(GL_BLEND);

    // Base
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
}

// Event handlers------------------------------------------

// Handles mousemove event
bool RECTANGLE::mouseButtonDown(int xPos, int yPos)
{
  return false;
}

// Handle mouse up event
void RECTANGLE::mouseButtonUp(int xPos, int yPos)
{
  // EMpty
}

// Handle mouse move event
void RECTANGLE::mouseMove(bool button, int xPos, int yPos)
{
  // Empty
}

void RECTANGLE::keyDown(char asciiCode)
{
  // Empty
}

