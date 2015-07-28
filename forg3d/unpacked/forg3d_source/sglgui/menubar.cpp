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
#include "menu.h"
#include "menubar.h"

// Constructor
MENUBAR::MENUBAR()
{
  open=false;
  fillColor1[0]=0.93;
  fillColor1[1]=0.91;
  fillColor1[2]=0.85;
  borderColor[0]=1;     
  borderColor[1]=1;     
  borderColor[2]=1;     
  userData=0;
  selectCallback=0;
}

// Destructor
MENUBAR::~MENUBAR()
{
  for (int i=0; i<menu.size(); i++)
  {
    delete menu[i];
  }
}

// Creates a new menu bar
MENUBAR *MENUBAR::create(int id, const FONT *font, int x, int y, int w, int h,
                        void (*selectCallback)(MENU *menu, 
                                               int optionId, void *userData),
                        void *userData)
{
  MENUBAR *mb=0;
  
  // Allocate memory
  mb=new MENUBAR();
  if (!mb) return 0;
  
  // Set attributes
  mb->id=id;
  mb->setFont(font);
  mb->setPosition(x, y);
  mb->setSize(w, h);
  mb->setSelectCallback(selectCallback);
  mb->setUserData(userData);                    
                    
  // Return the created menubar                               
  return mb;                         
}                           
                           

// Renders the menubar
void MENUBAR::render(void)
{
  if (visible)
  {
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
    
    // Render strings
    glEnable(GL_TEXTURE_2D);
    glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
    for (int i=0; i<menu.size(); i++)
    {
      font->printVCentered(menu[i]->getText(), 
                           menu[i]->getLeft()+font->getSize()/2, h/2);
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
    
    // Render menus 
    if (open)
    {
      for (int i=0; i<menu.size(); i++)
      {
        glPushMatrix();
        glTranslatef(menu[i]->getLeft(), menu[i]->getTop(), 0);
        menu[i]->render();
        glPopMatrix();
      }
    }
  }     
}


int MENUBAR::getActiveWidth()
{
  if (menu.size()==0) return 0;
  return (int)(menu.back()->getLeft()+
               font->textWidth(menu.back()->getText())+font->getSize());
}

void *MENUBAR::getUserData(void)
{
  return userData;     
}


// Adds a new menu to the menu bar and returns it
MENU *MENUBAR::addMenu(int id, const char *text)
{
  // Create the menu
  MENU *m=MENU::create(id, font, getActiveWidth(), h, 20, selectCallback, userData);
  if (!m) return 0;

  // Store the created menu
  m->setText(text);
  menu.push_back(m);

  // Return the created menu
  return m;
}

void MENUBAR::setUserData(void *userData)
{
  this->userData=userData;     
}

void MENUBAR::setSelectCallback(void (*selectCallback)(MENU *menu, int optionId, void *userData))
{
  this->selectCallback=selectCallback;     
}



// 

bool MENUBAR::isOnActive(int xPos, int yPos)
{
  if (xPos>=0 && xPos<=w && yPos>=0 && yPos<=h) return true;
  return false;
}

bool MENUBAR::isOnString(int index, int xPos, int yPos)
{
  if (index<0 && index>=menu.size()) return false;

  if (yPos>=0 && yPos<=h && 
      xPos>=menu[index]->getLeft() && 
      xPos<menu[index]->getLeft()+
           font->textWidth(menu[index]->getText())+font->getSize())
  {
    return true;    
  }

  return false;     
}

// 
bool MENUBAR::mouseButtonDown(int xPos, int yPos)
{
  // React only if the menu bar is visible and active
  if (visible && active)
  {
    // Handle menus, only if the menu bar is open
    if (open)
    {
      for (int i=0; i<menu.size(); i++)
      {
        if (menu[i]->mouseButtonDown(xPos-menu[i]->getLeft(), 
                                     yPos-menu[i]->getTop())) 
        {
          open=false;
          return true;
        }
      }
    }

    // React only if the cursor was over the menubar
    if (isOnActive(xPos, yPos))
    {
      if (!open)
      {
        open=false;
        for (int i=0; i<menu.size(); i++)
        {
          if (isOnString(i, xPos, yPos))
          {
            menu[i]->setVisibility(true);
            open=true;     
          }
          else menu[i]->setVisibility(false);
        }
      }
      else open=false;
      return true;          
    }          
  }
  
  return false; 
}

void MENUBAR::mouseButtonUp(int xPos, int yPos)
{
  // Empty     
}

void MENUBAR::mouseMove(bool button, int xPos, int yPos)
{
  // Habdle the event only if the menubar is visible and active
  if (visible && active && open)
  {
    // Handle menu event
    for (int i=0; i<menu.size(); i++)
    {
      menu[i]->mouseMove(button, xPos-menu[i]->getLeft(), 
                                 yPos-menu[i]->getTop());                
    }              
    
    //
    if (isOnActive(xPos, yPos))
    {
      for (int i=0; i<menu.size(); i++)
      {
        if (isOnString(i, xPos, yPos))         
          menu[i]->setVisibility(true);
        else
          menu[i]->setVisibility(false);
      }   
    }
              
  }
}

void MENUBAR::keyDown(char asciiCode)
{
  // Empty     
}
