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
#include "label.h"
#include "rectangle.h"
#include "slider.h"
#include "button.h"
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
#include "panel.h"
#include "messagedialog.h"
#include "sglgui.h"

#include "defaultfont.h"

// Creates a new SGLGUI
SGLGUI *SGLGUI::create(int w, int h)
{
  SGLGUI *sglgui=0;

  // Allovate memory
  sglgui = new SGLGUI();
  if (!sglgui) return 0;
  
  // Set attributes
  sglgui->resize(w, h);

  // Create default font
  unsigned char *data=new unsigned char[4*256*256];
  for (int i=0; i<256*256; i++)
  {
    int j=4*i;
    data[j]=255;
    data[j+1]=255;
    data[j+2]=255;
    data[j+3]=255*defaultFontData[i];
  }
  
  sglgui->dFont=FONT::create(256, data, defaultFontWidth);
  if (!sglgui->dFont)
  {
    delete sglgui;
    return 0;
  }

  delete data;
  
  // Create default dialog
  sglgui->messageDialog = MESSAGEDIALOG::create("Message", "No message", sglgui->dFont);
  if (!sglgui->messageDialog)
  {
    delete sglgui;
    return 0;                   
  }
  sglgui->messageDialog->setVisibility(false);

  // Return the created object
  return sglgui;
}

// Constructor
SGLGUI::SGLGUI()
{
  w=800;
  h=600;
  cursorTexture=0;
  cursorSize=32;
  dFont=0;
  dCursorTexture=0;
  messageDialog=0;
  msgCallback=0;
  msgUserData=0;
}

// Destructor
SGLGUI::~SGLGUI()
{
  if (dFont) delete dFont;
}

// Renders the whole GUI
// w and h are the width and height of the window
void SGLGUI::render()
{
  // Set up viewport
  glViewport(0, 0, w, h);

  // Set projection
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  glOrtho(0, w, h, 0, -1, 1);

  // Set modelview matrix
  glMatrixMode(GL_MODELVIEW);
  glLoadIdentity();
  glTranslatef(0.375, 0.375, 0);

  // Set up blending
  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  glEnable(GL_BLEND);

  glScissor(0, 0, w, h);

  // Render items
  renderItems();
  
  // Render the default message box
  glScissor(0, 0, w, h);
  glPushMatrix();
  glTranslatef(messageDialog->getLeft(), messageDialog->getTop(), 0);
  messageDialog->render();
  glPopMatrix();

  // Render cursor
  if (cursorTexture)
  {
    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, cursorTexture);
    glColor4f(1,1,1,1);

    glBegin(GL_QUADS);
    glTexCoord2f(0, 0);
    glVertex2f(mx, my);

    glTexCoord2f(0, 1);
    glVertex2f(mx, my+cursorSize);

    glTexCoord2f(1, 1);
    glVertex2f(mx+cursorSize, my+cursorSize);

    glTexCoord2f(1, 0);
    glVertex2f(mx+cursorSize, my);
    glEnd();
  }

  // Restore default state
  glDisable(GL_TEXTURE_2D);
  glDisable(GL_BLEND);
  glDisable(GL_SCISSOR_TEST);
}



GLuint SGLGUI::getCursorTexture(void) const
{
  return cursorTexture;
}

int SGLGUI::getCursorSize(void) const
{
  return cursorSize;
}

const FONT *SGLGUI::defaultFont(void) const
{
  return dFont;
}

GLuint SGLGUI::defaultCursorTexture(void) const
{
  return dCursorTexture;
}


void SGLGUI::setCursorTexture(GLuint texture)
{
  cursorTexture=texture;
}

void SGLGUI::setCursorSize(int size)
{
  cursorSize=size;
}



// A quick and dirty way to show a message
void SGLGUI::showMessage(int w, int h,
                         const char *header, const char *message, 
                         const char *editField, bool browse,
                         const char *left, const char *right,
                         void (*callback)(MESSAGEDIALOG *md, BUTTON *b, void *userData),
                         void *userData)
{
  //
  msgCallback=callback;
  msgUserData=userData;

  // 
  messageDialog->setUp(w, h, header, message, editField, browse, left, right, 
                       messageDialogCallback, this);

  // Show the dialog
  messageDialog->setVisibility(true);
  messageDialog->setPosition((int)(0.5*this->w-0.5*messageDialog->getWidth()),
                             (int)(0.5*this->h-0.5*messageDialog->getHeight()));

  // Disable the gui
  for (int i=0; i<item.size(); i++)
  {
    item[i]->setActivity(false);
  }
}

// Sets the size the gui. The size MUST macth the size of the window.
void SGLGUI::resize(int w, int h)
{
  this->w=w;
  this->h=h;     
}


// Handles mousedown message
// xPos and yPos are mouse coordinates relative to the upper left corner of the window
// This medhod must be called when ever a mouse button is clicked!
bool SGLGUI::mouseButtonDown(int xPos, int yPos)
{
  mx=xPos;
  my=yPos;
  preMouseButtonDownItems(xPos, yPos);
  messageDialog->preMouseButtonDown(xPos, yPos);
  if (messageDialog->isVisible())
  {
    messageDialog->mouseButtonDown(xPos-messageDialog->getLeft(), 
                                   yPos-messageDialog->getTop());
    return true;
  }
  if (mouseButtonDownItems(xPos, yPos)) return true;
  return false;
}

// Handles mousebuttonup message
// xPos and yPos are mouse coordinates relative to the upper left corner of the screen
// This medhod must be called when ever mouse buttun is released
void SGLGUI::mouseButtonUp(int xPos, int yPos)
{
  mx=xPos;
  my=yPos;

  mouseButtonUpItems(xPos, yPos);
  messageDialog->mouseButtonUp(xPos-messageDialog->getLeft(), 
                               yPos-messageDialog->getTop()); 
}

// Handles mouse move message
void SGLGUI::mouseMove(bool button, int xPos, int yPos)
{
  mx=xPos;
  my=yPos;

  mouseMoveItems(button, xPos, yPos);
  messageDialog->mouseMove(button, xPos-messageDialog->getLeft(), yPos-messageDialog->getTop());
}

// Handles key down event
void SGLGUI::keyDown(char asciiCode)
{
  keyDownItems(asciiCode);
  messageDialog->keyDown(asciiCode);
}

// Callback function that is called when the ok-button in the messageDialog is pressed
void SGLGUI::messageDialogCallback(GUIITEM *gi, void *userData)
{
  SGLGUI *sglgui = (SGLGUI *)userData;
  
  // Hide the dialog
  sglgui->messageDialog->setVisibility(false);

  // Enable rest of the gui
  for (int i=0; i<sglgui->item.size(); i++)
  {
    sglgui->item[i]->setActivity(true);
  }
  
  // Call calback
  if (sglgui->msgCallback)
    sglgui->msgCallback(sglgui->messageDialog, (BUTTON *)gi, sglgui->msgUserData);
}
