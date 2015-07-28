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
#include "groupbox.h"

// Creates a panel with given size
GROUPBOX *GROUPBOX::create(int id, const char *text, const FONT *font,
                           int x, int y, int w, int h)
{
  // Check for invalid input
  if (!font || !text) return 0;

  // Allocate memory
  GROUPBOX *gb = new GROUPBOX();
  if (!gb) return 0;

  // Copy values
  gb->id=id;
  gb->text=text;
  gb->font=font;
  gb->x=x;
  gb->y=y;                       
  gb->w=w;
  gb->h=h;          

  // Return the created panel 
  return gb;
}

// Inits a panel
GROUPBOX::GROUPBOX()
{
  fontColor[0]=0;
  fontColor[1]=0.27;
  fontColor[2]=0.84;
  borderColor[0]=0.82;
  borderColor[1]=0.82;
  borderColor[2]=0.75;
}

// Destructor
GROUPBOX::~GROUPBOX()
{
 // Empty
}


// PROCEDURE methods---------------------------------------

// Renders the panel
void GROUPBOX::render(void)
{     
  // Render only if the groupbox is visible
  if (visible)
  {
    // Set the scissor box to match this item
    setScissor();
    glDisable(GL_SCISSOR_TEST);

    float r =0.5*font->textWidth(text.c_str());

    // Renderborder
    glDisable(GL_TEXTURE_2D);
    glLineWidth(1);
    glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
    glBegin(GL_LINES);
    glVertex2f(0, 0);
    glVertex2f(0, h);
    glVertex2f(0, h);
    glVertex2f(w, h);
    glVertex2f(w, h);
    glVertex2f(w, 0);
    if (r<0.5*w)
    {
      glVertex2f(0, 0);
      glVertex2f(0.5*w-r-1, 0);
      glVertex2f(w, 0);
      glVertex2f(0.5*w+r+1, 0);
    }
    glEnd();
    
    // Render title
    glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
    glEnable(GL_TEXTURE_2D);
    font->printCentered(text.c_str(), w/2, 0);
    
    // Render items
    renderItems();
  } 
}





// Event handlers------------------------------------------

void GROUPBOX::preMouseButtonDown(int xPos, int yPos)
{
  preMouseButtonDownItems(xPos, yPos);
}

// Handles mousemove event
bool GROUPBOX::mouseButtonDown(int xPos, int yPos)
{
  if (visible && active)
  {
    return mouseButtonDownItems(xPos, yPos);
  }
  return false;
}

// Handle mouse up event
void GROUPBOX::mouseButtonUp(int xPos, int yPos)
{
  if (visible && active)
  {
    mouseButtonUpItems(xPos, yPos);
  }  
}

// Handle mouse move event
void GROUPBOX::mouseMove(bool button, int xPos, int yPos)
{
  if (visible && active)
  {
    mouseMoveItems(button, xPos, yPos);
  }
}

void GROUPBOX::keyDown(char asciiCode)
{
  if (visible && active)
  {
    keyDownItems(asciiCode);            
  }
}
