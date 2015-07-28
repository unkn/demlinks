#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <string>
#include <vector>
#include <iostream>

using namespace std;

#include "font.h"
#include "guiitem.h"
#include "eventinterface.h"
#include "label.h"
#include "editfield.h"
#include "checkbox.h"
#include "rectangle.h"
#include "slider.h"
#include "button.h"
#include "listbox.h"
#include "scrollbar.h"
#include "console.h"
#include "combobox.h"
#include "progressbar.h"
#include "menu.h"
#include "menubar.h"
#include "itemcontainer.h"
#include "groupbox.h"
#include "panel.h"


// Constructor
ITEMCONTAINER::ITEMCONTAINER()
{
  // Empty
}

// Destructor
ITEMCONTAINER::~ITEMCONTAINER()
{
  // Delete all items
  for (int i=0; i<item.size(); i++)
  {
    delete item[i];
  }

  // Empty the container
  item.clear();
}


// Renders all items in the container
void ITEMCONTAINER::renderItems(void)
{
  // Store current scissor box
  GLint sbox[4];
  glGetIntegerv(GL_SCISSOR_BOX, sbox);

  // Loop all items
  for(int i=0; i<item.size(); i++)
  {
    // Restore scissor box
    glScissor(sbox[0], sbox[1], sbox[2], sbox[3]);

    // Render the item
    glPushMatrix();
    glTranslatef(item[i]->getLeft(), item[i]->getTop(), 0);
    item[i]->render();
    glPopMatrix();
  }
}

// Returs a item that has the given id
GUIITEM *ITEMCONTAINER::getItem(int id)
{
  for (int i=0; i<item.size(); i++)
  {
    if (item[i]->getId()==id) return item[i];
  }

  return 0;
}


// Adds a new panel to the container
PANEL *ITEMCONTAINER::addPanel(int id, const char *text, const FONT *font,
                               bool header, bool dragable,
                               int x, int y, int w, int h)
{
  PANEL *p = PANEL::create(id, text, font, header, dragable, x, y, w, h);
  item.push_back(p);
  return p;
}

// Adds a new checkBox
GROUPBOX *ITEMCONTAINER::addGroupBox(int id, const char *text, const FONT *font,
                                     int x, int y, int w, int h)
{
  GROUPBOX *gb = GROUPBOX::create(id, text, font, x, y, w, h);
  item.push_back(gb);
  return gb;
}

// Adds a new rectangle to the container
RECTANGLE *ITEMCONTAINER::addRectangle(int id, int x, int y, int w, int h,
                                       float r, float g, float b, GLuint texture)
{
  RECTANGLE *re = RECTANGLE::create(id, x, y, w, h, r, g, b, texture);
  item.push_back(re);
  return re;
}

// adds a new slider to the container
SLIDER *ITEMCONTAINER::addSlider(int id, bool horizontal,
                                 int x, int y, int w, int h,
                                 int min, int max, int value,
                                 void (*scrollCallback)(GUIITEM *slider,
                                                        void *userData),
                                 void *userData)
{
  SLIDER *s = SLIDER::create(id, horizontal, x, y, w, h, min, max, value,
                             scrollCallback, userData);
  item.push_back(s);
  return s;
}

// Adds a new button to the container
BUTTON *ITEMCONTAINER::addButton(int id, const char *text, const FONT *font,
                                 int x, int y, int w, int h,
                                 void (*pushCallback)(GUIITEM *button,
                                                     void *userData),
                                 void *userData)
{
  BUTTON *b = BUTTON::create(id, text, font, x, y, w, h, pushCallback, userData);
  item.push_back(b);
  return b;
}

// Adds a new button to the container
LABEL *ITEMCONTAINER::addLabel(int id, const char *text, const FONT *font,
                               int x, int y)
{
  LABEL *l = LABEL::create(id, text, font, x, y);
  item.push_back(l);
  return l;
}


// Adds a new checkbox
CHECKBOX *ITEMCONTAINER::addCheckBox(int id, bool checked,
                                     const char *text, const FONT *font,
                                     int x, int y, int w, int h,
                                     void (*checkCallback)(GUIITEM *checkBox,
                                                           void *userData),
                                     void *userData)
{
  CHECKBOX *cb = CHECKBOX::create(id, checked, text, font, x, y, w, h,
                                  checkCallback, userData);
  item.push_back(cb);
  return cb;
}

// Adds a new listbox
LISTBOX *ITEMCONTAINER::addListBox(int id, bool forceselect, bool multiselect,
                                   const FONT *font, int x, int y, int w, int h,
                                   void (*selectCallback)(GUIITEM *listBox,
                                                          void *userData),
                                   void *userData)
{
  LISTBOX *lb = LISTBOX::create(id, forceselect, multiselect, font, x, y, w, h,
                                selectCallback, userData);
  item.push_back(lb);
  return lb;
}

// Adds a new scrollbar
SCROLLBAR *ITEMCONTAINER::addScrollbar(int id, bool horizontal,
                                       int x, int y, int w, int h,
                                       int min, int max,
                                       int lowValue, int highValue,
                                       void (*scrollCallback)(GUIITEM *scrollBar,
                                                              void *userData),
                                       void *userData)
{
  SCROLLBAR *sb = SCROLLBAR::create(id, horizontal, x, y, w, h,
                                    min, max, lowValue, highValue,
                                    scrollCallback, userData);
  item.push_back(sb);
  return sb;
}

// Adss a new console
CONSOLE *ITEMCONTAINER::addConsole(int id, bool interactive,
                                   const FONT *font, int numberOfLines,
                                   int x, int y, int w, int h,
                                   void (*commandCallback)(GUIITEM *console,
                                                           void *userData),
                                   void *userData)
{
  CONSOLE *c = CONSOLE::create(id, interactive, font, numberOfLines, x, y, w, h,
                               commandCallback, userData);
  item.push_back(c);
  return c;
}


COMBOBOX *ITEMCONTAINER::addComboBox(int id, const FONT *font,
                                     int x, int y, int w, int h,
                                     void (*selectCallback)(GUIITEM *comboBox,
                                                             void *userData),
                                     void *userData)
{
  COMBOBOX *cb = COMBOBOX::create(id, font, x, y, w, h, selectCallback, userData);
  item.push_back(cb);
  return cb;
}

EDITFIELD *ITEMCONTAINER::addEditField(int id, const char *text, const FONT *font,
                                       int x, int y, int w, int h, int maxLength,
                                       void (*editCallback)(GUIITEM *ef, void *userData),
                                       void *userData)
{
  EDITFIELD *ef=EDITFIELD::create(id, text, font, x, y, w, h, maxLength, 
                                  editCallback, userData);
  item.push_back(ef);
  return ef;
}

PROGRESSBAR *ITEMCONTAINER::addProgressBar(int id, const FONT *font, 
                                           int x, int y, int w, int h, 
                                           int max, int value)
{
  PROGRESSBAR *pb=PROGRESSBAR::create(id, font, x, y, w, h, max, value);
  item.push_back(pb);
  return pb;                
}


MENU *ITEMCONTAINER::addMenu(int id, const FONT *font, int x, int y, int spacing,
                             void (*selectCallback)(MENU *menu, int optionId, 
                                                    void *userData),
                             void *userData)
{
  MENU *m=MENU::create(id, font, x, y, spacing, selectCallback, userData);
  item.push_back(m);
  return m;
}             

MENUBAR *ITEMCONTAINER::addMenuBar(int id, const FONT *font, 
                                   int x, int y, int w, int h,
                                   void (*selectCallback)(MENU *menu, int optionId, 
                                                          void *userData),
                                   void *userData)

{
  MENUBAR *mb=MENUBAR::create(id, font, x, y, w, h, selectCallback, userData);
  item.push_back(mb);
  return mb;
}
                




// Handles mousedown event
bool ITEMCONTAINER::mouseButtonDownItems(int xPos, int yPos)
{
  // Loop all items (in reverse order so top ones are handled first)
  for (int i=item.size()-1; i>=0; i--)
  {
    if (item[i]->mouseButtonDown(xPos - item[i]->getLeft(), yPos - item[i]->getTop()))
    {
      item[i]->focus=true;
      return true;
    }
  }
  return false;
}

// Handles mouseup event
void ITEMCONTAINER::mouseButtonUpItems(int xPos, int yPos)
{
  // Loop all items (in reverse order so top ones are handled first)
  for (int i=item.size()-1; i>=0; i--)
  {
    item[i]->mouseButtonUp(xPos - item[i]->getLeft(), yPos - item[i]->getTop());
  }
}

// Handles mouse move events
void ITEMCONTAINER::mouseMoveItems(bool button, int xPos, int yPos)
{
  // Loop all items (in reverse order so top ones are handled first)
  for (int i=item.size()-1; i>=0; i--)
  {
    item[i]->mouseMove(button, xPos - item[i]->getLeft(), yPos - item[i]->getTop());
  }
}

// Handles key down events
void ITEMCONTAINER::keyDownItems(char asciiCode)
{
  // Loop all items (in reverse order so top ones are handled first)
  for (int i=item.size()-1; i>=0; i--)
  {
    item[i]->keyDown(asciiCode);
  }
}


void ITEMCONTAINER::killFocusItems(void)
{
  for (int i=0; i<item.size(); i++)
  {
    item[i]->killFocus();
  }
}


void ITEMCONTAINER::preMouseButtonDownItems(int xPos, int yPos)
{
  for (int i=0; i<item.size(); i++)
  {
    item[i]->preMouseButtonDown(xPos - item[i]->getLeft(), 
                                yPos - item[i]->getTop());
  }    
}

// Moves a item to the top most
void ITEMCONTAINER::bringToTop(int id)
{
  // If there are not enough items return
  if (item.size()<2) return;

  // Find the desired item
  for (int i=0; i<item.size(); i++)
  {
    if (item[i]->getId()==id)
    {
      for (int j=i; j<item.size()-1; j++)
      {
        GUIITEM *g=item[j];
        item[j]=item[j+1];
        item[j+1]=g;        
      }
      return;
    }          
  }
}

