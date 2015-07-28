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
#include "scrollbar.h"
#include "eventinterface.h"
#include "listbox.h"

void LISTBOX::scrollCallback(GUIITEM *gi, void *data)
{
  LISTBOX *lb=(LISTBOX *)data;   
  lb->scroll=((SCROLLBAR *)gi)->getValue();    
}

// constructor
LISTBOX::LISTBOX()
{
  forceselect=true;
  multiselect=false;
  scrollbar=0;
  fillColor1[0]=1;
  fillColor1[1]=1;
  fillColor1[2]=1;
  fillColor2[0]=0.8;
  fillColor2[1]=1;
  fillColor2[2]=1;
  borderColor[0]=0;
  borderColor[1]=0;
  borderColor[2]=0;
  scroll=0;
}

// Destructor
LISTBOX::~LISTBOX()
{
  if (scrollbar) delete scrollbar;
}

// Creates a new listbox
LISTBOX *LISTBOX::create(int id, bool forceselect, bool multiselect,
                         const FONT *font, int x, int y, int w, int h,
                         void (*selectCallback)(GUIITEM *listBox, 
                                                void *userData),
                         void *userData)
{
  // Check for invalid input
  if (!font) return 0;

  // Allocate memory
  LISTBOX *lb=new LISTBOX();
  if (!lb) return 0;

  // Copy data
  lb->id=id;
  lb->setForceSelect(forceselect);
  lb->setMultiSelect(multiselect);
  lb->setFont(font);
  lb->setPosition(x, y);
//  lb->setSize(w, h);  // Rysähtää!!!!
  lb->w=w;
  lb->h=h;
  lb->setCallback(selectCallback);
  lb->setUserData(userData);

  // Create scrollbar
  lb->scrollbar=SCROLLBAR::create(1, false, w-10, 0, 10, h, 0, 100, 0, 1, 
                                  scrollCallback, lb);
  lb->setUpScrollbar();


  // Return the created object
  return lb;
}

// 
void LISTBOX::setUpScrollbar(void)
{
  scroll=0;
  scrollbar->setPosition(w-10, 0);     
  scrollbar->setSize(10, h);
  scrollbar->setMin(0);
  scrollbar->setValue(0);
  scrollbar->setThumbSize(1);
  scrollbar->setMax(listItem.size());
  scrollbar->setValue(scroll);
  scrollbar->setThumbSize((int)(h/font->getSize()));
  if (scrollbar->getMax()>scrollbar->getThumbSize() && listItem.size()>0)
    scrollbar->setVisibility(true); 
  else scrollbar->setVisibility(false);
}



// Procedure methods
void LISTBOX::render(void)
{
  // Render only if the listbox is visible
  if (visible)
  {
    int fs=font->getSize();

    // Set scissor box to match this item
    setScissor();

    // Render strings
    glEnable(GL_SCISSOR_TEST);
    int n=(int)(h/fs+1);
    n=(n<listItem.size())?n:listItem.size();
    int i=0;
    for (i=0; i<n && i+scroll<listItem.size(); i++)
    {
      // Render back ground
      if (listItem[i+scroll].second)
      {
        if (texture2)
        {
          glBindTexture(GL_TEXTURE_2D, texture2);
          glEnable(GL_TEXTURE_2D);
        }
        else glDisable(GL_TEXTURE_2D);
        glColor4f(fillColor2[0], fillColor2[1], fillColor2[2], transparency);
      }
      else
      {
        if (texture1)
        {
          glBindTexture(GL_TEXTURE_2D, texture1);
          glEnable(GL_TEXTURE_2D);
        }
        else glDisable(GL_TEXTURE_2D);
        glColor4f(fillColor1[0], fillColor1[1], fillColor1[2], transparency);
      }
      glBegin(GL_QUADS);
      glTexCoord2f(0, (fs*i)/h);
      glVertex2f(0, fs*i);
      glTexCoord2f(0, (fs*(i+1))/h);
      glVertex2f(0, fs*(i+1));
      glTexCoord2f(1, (fs*(i+1))/h);
      glVertex2f(w, fs*(i+1));
      glTexCoord2f(1, (fs*i)/h);
      glVertex2f(w, fs*i);
      glEnd();

      // Render text 
      glEnable(GL_TEXTURE_2D);
      glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
      font->print(listItem[i+scroll].first.c_str(), 
                  2, fs*i, 1, 0, 0, w);
    }

    // Fill the part not covered by the strings
    if (h>i*fs)
    {
      if (texture1)
      {
        glBindTexture(GL_TEXTURE_2D, texture1);
        glEnable(GL_TEXTURE_2D);
      }
      else glDisable(GL_TEXTURE_2D);
      glColor4f(fillColor1[0], fillColor1[1], fillColor1[2], transparency);
      glBegin(GL_QUADS);
      glTexCoord2f(0, (fs*i)/h);
      glVertex2f(0, fs*i);
      glTexCoord2f(0, 1);
      glVertex2f(0, h);
      glTexCoord2f(1, 1);
      glVertex2f(w, h);
      glTexCoord2f(1, (fs*i)/h);
      glVertex2f(w, fs*i);
      glEnd();
    }
    glDisable(GL_SCISSOR_TEST);

    // Render border
    if (border)
    {
      glDisable(GL_TEXTURE_2D);
      glColor4f(borderColor[0], borderColor[1], borderColor[2], transparency);
      glBegin(GL_LINE_LOOP);
      glVertex2f(0,0);
      glVertex2f(0,h);
      glVertex2f(w,h);
      glVertex2f(w,0);
      glEnd();
    }
   

    // Render scrollbar
    glPushMatrix();
    glTranslatef(scrollbar->getLeft(), scrollbar->getTop(), 0);
    scrollbar->render();
    glPopMatrix();
  }
}

// Query methods

// Returns the number of strings in the listbox
int LISTBOX::getNumberOfStrings(void) const
{
  return listItem.size();
}

// Returns the nubmer of selected strings in teh box
int LISTBOX::getNumberOfSelectedStrings(void) const
{
  int count=0;
  for (int i=0; i<listItem.size(); i++)
  {
    if (listItem[i].second) count++;
  }
  return count;
}

// Returns the selected string
// If there are multible selected strings only first is returned
const char *LISTBOX::getSelectedString(void) const
{
  for (int i=0; i<listItem.size(); i++)
  {
    if (listItem[i].second) return listItem[i].first.c_str();
  }
  return 0;
}

// Returns the index of the selected string
int LISTBOX::getSelectedStringIndex(void) const
{
  for (int i=0; i<listItem.size(); i++)
  {
    if (listItem[i].second) return i;
  }
  return -1;
}

// Returns the string of given index
const char *LISTBOX::getString(int index) const
{
  if (index<0 || index>=listItem.size()) return 0;
  return listItem[index].first.c_str();
}

// Sets the text of strin g of given index
void LISTBOX::setString(int index, const char *text)
{
  if (text && index>=0 && index<listItem.size())
  {
    listItem[index].first=text;
  }
}

// Returns true if the string of given index is selected
bool LISTBOX::isStringSelected(int index) const
{
  if (index<0 || index>=listItem.size()) return false;

  return listItem[index].second;
}

// Update methods

// Adds a new item at the end of the list
int LISTBOX::addString(const char *text)
{
  if (!text) return -1;

  // Create the new string
  listItem.push_back(pair<string, bool>(text, false));
  int index=listItem.size()-1;

  // Uppdate scrollbar
  setUpScrollbar();

  // Return index of the craeted string
  return index;
}

// Removes the string at given index
void LISTBOX::removeString(int index)
{
  if (index>=0 && index<listItem.size())
  {
    // Erase the string
    listItem.erase(listItem.begin()+index);

    // Uppdate scrollbar
    scroll=0;
    setUpScrollbar();
  }
}

// removes all strings from the listbox
void LISTBOX::removeAll(void)
{
  listItem.clear();
  
  // Uppdate scrollbar
  scroll=0;
  setUpScrollbar();
}

// Sets string at given index to selected
void LISTBOX::setStringSelection(int index, bool selected)
{
  if (index>=0 && index<listItem.size())
  {
    listItem[index].second=selected;
  }
}

void LISTBOX::invertStringSelection(int index)
{
  if (index>=0 && index<listItem.size())
  {
    if (listItem[index].second)
      listItem[index].second=false;
    else
      listItem[index].second=true;
  }
}



// Selects all strings
void LISTBOX::setSize(int w, int h)
{


  GUIITEM::setSize(w, h);
     cout << "LIST SS SUPER\n";

  scroll=0;
       cout << "LIST SS scroll=0\n";
  
  setUpScrollbar();     
  
     cout << "LIST SS setup\n";  
}


void LISTBOX::selectAll(void)
{
  for (int i=0; i<listItem.size(); i++)
  {
    listItem[i].second=true;
  }
}

// Unseclets all
void LISTBOX::unselectAll()
{
  for (int i=0; i<listItem.size(); i++)
  {
    listItem[i].second=false;
  }
}

void LISTBOX::invertAll(void)
{
  for (int i=0; i<listItem.size(); i++)
  {
    listItem[i].second=listItem[i].second?false:true;
  }
}

void LISTBOX::setForceSelect(bool forceSelect)
{
  this->forceselect=forceSelect;     
}

void LISTBOX::setMultiSelect(bool multiSelect)
{
  this->multiselect=multiSelect;     
}


// Event handlers

bool LISTBOX::isOnActive(int xPos, int yPos)
{
  if (xPos>=0 && xPos<=w && yPos>=0 && yPos <=h) return true;
  return false;
}


bool LISTBOX::isOnString(int xPos, int yPos, int index)
{
  if (xPos>=0 && xPos<=w && 
      yPos>=font->getSize()*index && yPos<=font->getSize()*(index+1))
    return true;
  return false;
}


// Handles mouse button down event
bool LISTBOX::mouseButtonDown(int xPos, int yPos)
{
  // Proses only if the item is visble and active
  if (visible && active)
  {
    // Check first if the scrollbar wants to proses the event
    if (scrollbar->mouseButtonDown(xPos - scrollbar->getLeft(),
                                   yPos - scrollbar->getTop()))
    {
      return true;
    }
    // if not prosese it with listbox
    else
    {
      // If the click was on the listbox
      if (isOnActive(xPos, yPos))
      {
        // Loop all visible strings
        int n=(int)(h/font->getSize()+1);
        n=(n<listItem.size())?n:listItem.size();
        for (int i=0; i<n && i+scroll<listItem.size(); i++)
        {
          // If the cursor is on this string
          if (isOnString(xPos, yPos, i))
          {
            if (multiselect)
            {
              if (forceselect)
              {
                if (!isStringSelected(i+scroll))
                {
                  setStringSelection(i+scroll, true);
                }
                else
                {
                  if (getNumberOfSelectedStrings()>1)
                    setStringSelection(i+scroll, false);
                }
              }
              else
              {
                invertStringSelection(i+scroll);
              }
            }
            else
            {
              if (forceselect)
              {
                unselectAll();
                setStringSelection(i+scroll, true);
              }
              else
              {
                bool s=isStringSelected(i+scroll);
                unselectAll();
                if (!s) setStringSelection(i+scroll, true);
              }
            }
            if (callback) callback(this, userData);
          }
        }
        return true;
      }
    }
  }
  
  // The event was not prosessed
  return false;
}

//
void LISTBOX::mouseButtonUp(int xPos, int yPos)
{
  scrollbar->mouseButtonUp(xPos - scrollbar->getLeft(), yPos - scrollbar->getTop());
}

//
void LISTBOX::mouseMove(bool button, int xPos, int yPos)
{
  scrollbar->mouseMove(button, xPos - scrollbar->getLeft(), yPos - scrollbar->getTop());
}

void LISTBOX::keyDown(char asciiCode)
{
  scrollbar->keyDown(asciiCode);
}
