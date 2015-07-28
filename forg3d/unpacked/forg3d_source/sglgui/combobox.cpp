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
#include "combobox.h"
#include "scrollbar.h"


void COMBOBOX::scrollbarCallback(GUIITEM *gi, void *data)
{
  COMBOBOX *cb=(COMBOBOX *)data;
  cb->scroll=((SCROLLBAR *)gi)->getValue();
}

// Constructor
COMBOBOX *COMBOBOX::create(int id, const FONT *font,
                           int x, int y, int w, int h,
                           void (*selectCallback)(GUIITEM *comboBox,
                                                  void *userData),
                           void *userData)
{
  // Check for invalid input
  if (!font) return 0;

  // Allocate memory
  COMBOBOX *cb=new COMBOBOX();
  if (!cb) return 0;

  // Copy data
  cb->id=id;
  cb->setFont(font);
  cb->setPosition(x, y);
  cb->w=w;
  cb->h=h;
  //cb->setSize(w, h);
  cb->setCallback(selectCallback);
  cb->setUserData(userData);

  // Create scrollbar
  cb->scrollbar=
    SCROLLBAR::create(1, false, w-10, h, 10, 100,
                      0, 100, 50, 20, scrollbarCallback, cb);
  cb->setUpScrollbar();

  // Return the created combobox
  return cb;
}

// Constructor
COMBOBOX::COMBOBOX()
{
  open=false;
  selectedString=-1;
  maxNumberOfVisibleStrings=8;
  scroll=0;
  fillColor1[0]=1;
  fillColor1[1]=1;
  fillColor1[2]=1;
  fillColor2[0]=1;
  fillColor2[1]=1;
  fillColor2[2]=1;
  borderColor[0]=0.5;
  borderColor[1]=0.62;
  borderColor[2]=0.73;
}

// Destructor
COMBOBOX::~COMBOBOX()
{
  delete scrollbar;
}


// Procedure methods
// Sets the scissorbox to match the item
// The scissorbox is assumed to match the container
void COMBOBOX::setScissor(void)
{
  GLint sbox[4];
  glGetIntegerv(GL_SCISSOR_BOX, sbox);
  float hh=h+font->getSize()*getNumberOfVisibleStrings();
  glScissor(sbox[0]+x, (int)(sbox[1]+sbox[3]-y-hh), w, (int)hh);
}


void COMBOBOX::render(void)
{
  // Render the box only if it is visible
  if (visible)
  {
    int fs=font->getSize();

    // Set the scissor box to match this item
    setScissor();            
              
    // render base
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

    // Text
    if (font && selectedString>=0 && selectedString<listItem.size())
    {
      glEnable(GL_SCISSOR_TEST);
      glEnable(GL_TEXTURE_2D);
      glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
      font->printVCentered(listItem[selectedString].c_str(), 
                           2, h/2, 1, 0, 0, w);
      glDisable(GL_SCISSOR_TEST);
    }

    // Render arrow
    glDisable(GL_TEXTURE_2D);
    glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
    glBegin(GL_TRIANGLES);
    glVertex2f(w-0.5*fs, 0.66*h);
    glVertex2f(w-0.1*fs, 0.33*h);
    glVertex2f(w-0.9*fs+1, 0.33*h);
    glEnd();

    // border
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
      glBegin(GL_LINES);
      glVertex2f(w-fs, 0);
      glVertex2f(w-fs, h);      
      glEnd();
    }

    // render selections and only if they are visible
    if (open)
    {
      // List base
      if (texture2)
      {
        glBindTexture(GL_TEXTURE_2D, texture2);
        glEnable(GL_TEXTURE_2D);
      }
      else glDisable(GL_TEXTURE_2D);

      glColor4f(fillColor2[0], fillColor2[1], fillColor2[2], transparency);
      glBegin(GL_QUADS);
      glTexCoord2f(0, 0);
      glVertex2f(0, h);
      glTexCoord2f(0, 1);
      glVertex2f(0, h+getNumberOfVisibleStrings()*fs);
      glTexCoord2f(1, 1);
      glVertex2f(w, h+getNumberOfVisibleStrings()*fs);
      glTexCoord2f(1, 0);
      glVertex2f(w, h);
      glEnd();

      // strings
      glEnable(GL_SCISSOR_TEST);
      glEnable(GL_TEXTURE_2D);
      glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
      for (int i=0; i<getNumberOfVisibleStrings(); i++)
      {
        font->print(listItem[scroll+i].c_str(), 
                    2, h+i*fs, 1, 0, 0, w);
      }
      glDisable(GL_SCISSOR_TEST);
  

      // List outline
      if (border)
      {
        glDisable(GL_TEXTURE_2D);
        glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
        glBegin(GL_LINE_LOOP);
        glVertex2f(0, h);
        glVertex2f(0, h+getNumberOfVisibleStrings()*fs);
        glVertex2f(w, h+getNumberOfVisibleStrings()*fs);
        glVertex2f(w, h);
        glEnd();
      }

      // Render scrollbar
      glPushMatrix();
      glTranslatef(scrollbar->getLeft(), scrollbar->getTop(), 0);
      scrollbar->render();
      glPopMatrix();
    }
  }
}

// Query methods

SCROLLBAR *COMBOBOX::getScrollbar(void)
{
  return scrollbar;
}

int COMBOBOX::getNumberOfStrings(void) const
{
  return listItem.size();
}

const char *COMBOBOX::getSelectedString(void) const
{
  if (selectedString>=0 && selectedString<listItem.size())
    return listItem[selectedString].c_str();
  return 0;
}

int COMBOBOX::getSelectedStringIndex(void) const
{
  if (selectedString>=0 && selectedString<listItem.size())
    return selectedString;
  return -1;
}

const char *COMBOBOX::getString(int index) const
{
  if (index>=0 && index<listItem.size())
    return listItem[index].c_str();
  return 0;
}

int COMBOBOX::getMaxNumberOfVisibleStrings(void) const
{
  return maxNumberOfVisibleStrings;    
}

int COMBOBOX::getNumberOfVisibleStrings(void) const
{
  return (listItem.size()<maxNumberOfVisibleStrings)?listItem.size():maxNumberOfVisibleStrings;
}


// Update methods

void COMBOBOX::setTransparency(float a)
{
  GUIITEM::setTransparency(a);
  scrollbar->setTransparency(a);
}

// Adds a new string to the combobox
// Returns the index of the added string
int COMBOBOX::addString(const char *text)
{
  if (text)
  {
    listItem.push_back(text);
    setUpScrollbar();
    return listItem.size()-1;
  }
  return -1;
}

// Set the content of a string
void COMBOBOX::setString(int index, const char *text)
{
  if (index>=0 && index<listItem.size())
  {
    listItem[index]=text;
  }
}

// Deletes a string from the combobox
void COMBOBOX::removeString(int index)
{
  if (index>=0 && index<listItem.size())
  {
    selectedString=-1;
    listItem.erase(listItem.begin()+index);
    setUpScrollbar();
  }
}

// deletes all strings from the combobox
void COMBOBOX::removeAll(void)
{
  listItem.clear();
  setUpScrollbar();
}

void COMBOBOX::selectString(int index)
{
  if (index>=0 && index<listItem.size()) selectedString=index;
}

void COMBOBOX::killFocus(void)
{
  focus=false;
  open=false;
}

void COMBOBOX::setMaxNumberOfVisibleStrings(int n)
{
  if (n<1) n=1;
  maxNumberOfVisibleStrings=n;     
}

// Event handlers
bool COMBOBOX::isOnActive(int xPos, int yPos)
{
  if (xPos>=0 && xPos<=w && yPos>=0 && yPos<=h) return true;
  return false;
}

bool COMBOBOX::isOnList(int xPos, int yPos)
{
  if (!open) return false;
  if (xPos>=0 && xPos<=w &&
      yPos>h && yPos<=h+getNumberOfVisibleStrings()*font->getSize()) return true;
  return false;
}

bool COMBOBOX::isOnString(int xPos, int yPos, int index)
{
  if (xPos>=0 && xPos<=w &&
      yPos>=h+index*font->getSize() &&
      yPos<=h+(index+1)*font->getSize()) return true;
  return false;
}

void COMBOBOX::setUpScrollbar(void)
{
  scroll=0;
  scrollbar->setPosition(w-10, h);     
  scrollbar->setSize(10, (int)(font->getSize()*getNumberOfVisibleStrings()));
  scrollbar->setMin(0);
  scrollbar->setValue(0);
  scrollbar->setThumbSize(1);
  scrollbar->setMax(listItem.size());
  scrollbar->setValue(scroll);
  scrollbar->setThumbSize(getNumberOfVisibleStrings());
  if (scrollbar->getMax()>scrollbar->getThumbSize() && listItem.size()>0)
    scrollbar->setVisibility(true); 
  else scrollbar->setVisibility(false); 
}

// Handles mouse button down event
void COMBOBOX::preMouseButtonDown(int xPos, int yPos)
{
  if (!isOnList(xPos, yPos) && !isOnActive(xPos, yPos)) open=false;
}

// Handles mousebutton down event
bool COMBOBOX::mouseButtonDown(int xPos, int yPos)
{
  if (visible && active)
  {
    // If the cursor is on the box
    if (isOnActive(xPos, yPos))
    {
      open=open?false:true;
      return true;
    }
    // If not it can still be on the list
    else
    {
      if (isOnList(xPos, yPos))
      {
        if (!scrollbar->mouseButtonDown(xPos-scrollbar->getLeft(), 
                                        yPos-scrollbar->getTop()))
        {
          open=false;
          for (int i=0; i<listItem.size(); i++)
          {
            if (isOnString(xPos, yPos, i))
            {
              selectedString=i+scroll;
              if (callback)
                callback(this, userData);
            }
          }
        }
        return true;
      }
    }
  }
  return false;
}

// Handles mouse button up event
void COMBOBOX::mouseButtonUp(int xPos, int yPos)
{
  scrollbar->mouseButtonUp(xPos-scrollbar->getLeft(), yPos-scrollbar->getTop());
}

// Handles mouse move event
void COMBOBOX::mouseMove(bool button, int xPos, int yPos)
{
  scrollbar->mouseMove(button, xPos-scrollbar->getLeft(), yPos-scrollbar->getTop());
}

// Handles key down event
void COMBOBOX::keyDown(char asciiCode)
{
  scrollbar->keyDown(asciiCode);
}

