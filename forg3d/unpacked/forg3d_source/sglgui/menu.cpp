#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <math.h>
#include <string>
#include <vector>

using namespace std;

#include "font.h"
#include "guiitem.h"
#include "menu.h"


MENU::OPTION::OPTION()
{
  id=0;
  checked=false;
  checkable=false;
  menu=0;
}

MENU::OPTION::~OPTION()
{
  // Empty
}

MENU::MENU()
{
  selectCallback=0;
  userData=0;
  spacing=20;
  w=0;
  h=0;
  fillColor1[0]=1;
  fillColor1[1]=1;
  fillColor1[2]=1;
  borderColor[0]=0.67;
  borderColor[1]=0.66;
  borderColor[2]=0.60;
}

MENU::~MENU()
{
  // Empty             
}

MENU *MENU::create(int id, const FONT *font, int x, int y, int spacing,
                   void (*selectCallback)(MENU *menu, 
                                          int optionId, void *userData),
                   void *userData)
{
  MENU *m=0;             
           
  // Alloacte memory
  m=new MENU();
  if (!m) return 0;
  
  // Copy data
  m->id=id;
  m->font=font;
  m->x=x;
  m->y=y;
  m->spacing=spacing;
  m->selectCallback=selectCallback;
  m->userData=userData;  

  // Return the creted menu
  return m;
}

// Procedure methods
void MENU::render(void)
{
  if (visible)
  {
    int fs=font->getSize();

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

    // Render texts
    glEnable(GL_TEXTURE_2D);
    glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
    for (int i=0; i<option.size(); i++)
    {
      font->print(option[i].text.c_str(), 
                  fs, i*spacing+(spacing-fs)/2);     
    }
    
    // Render check marks and submenuarrows
    glDisable(GL_TEXTURE_2D);
    for (int i=0; i<option.size(); i++)
    {
      glColor4f(fontColor[0], fontColor[1], fontColor[2], transparency);
      if (option[i].checked)
      {
        glBegin(GL_LINE_STRIP);
        glVertex2f(0.25*fs, spacing*(i+0.25));
        glVertex2f(0.5*fs, spacing*(i+0.75));
        glVertex2f(0.75*fs, spacing*(i+0.25));
        glEnd();                      
      }
      if (option[i].menu)
      {
        glBegin(GL_TRIANGLES);
        glVertex2f(w-0.66*fs, spacing*(i+0.33));
        glVertex2f(w-0.66*fs, spacing*(i+0.66));
        glVertex2f(w-0.33*fs, spacing*(i+0.5));
        glEnd();                  
      }
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
 
    // Render submenus
    for (int i=0; i<option.size(); i++)
    {
      if (option[i].menu)
      {
        glPushMatrix();
        glTranslatef(option[i].menu->getLeft(), option[i].menu->getTop(), 0);
        option[i].menu->render();  
        glPopMatrix();
      }
    }
  }  
}

// Query methods

// Returns the spacing
int MENU::getSpacing(void)
{
  return spacing;       
}

// Returns the text of a option of with given id
const char *MENU::getOptionText(int id)
{
  for (int i=0; i<option.size(); i++)
  {
    if (option[i].id==id) return option[i].text.c_str();
  }     
  return 0;
}

// Return true if the option with given index is checked
bool MENU::isOptionChecked(int id)
{
  for (int i=0; i<option.size(); i++)
  {
    if (option[i].id==id) return option[i].checked;
  }     
  return false;
}


// Returns true the the fisrt option that has the given id is checkable
// false otherwise
bool MENU::isOptionCheckable(int id)
{
  for (int i=0; i<option.size(); i++)
  {
    if (option[i].id==id) return option[i].checkable;
  }    
  return false;
}


// Returns the sub menu of the option that has the given id.
// The there are meny options with the same id only 
// the first one that has a menu is returned
// If there are no option with given id or the option has a menu null is returned
MENU *MENU::getSubmenu(int id)
{
  for (int i=0; i<option.size(); i++)
  {
    if (option[i].id==id && option[i].menu) return option[i].menu;
  }    
  return 0;
}


// Update methods
void MENU::setSpacing(int spacing)
{
  this->spacing=spacing;     
}


void MENU::setSize(int w, int h)
{
  // Compute height
  h=spacing*option.size();

  // Compute width
  w=0;
  for (int i=0; i<option.size(); i++)
  {
    int ww=(int)(font->textWidth(option[i].text.c_str())+2*font->getSize()+2);
    if (ww>w) w=ww;
  }     
  
  this->w=w;
  this->h=h;
}

// Adds a new option to the menu
MENU *MENU::addOption(int id, const char *text, bool submenu, bool checkable)
{
  OPTION o;
  MENU *m=0;

  o.id=id;
  o.text=text;

  if (submenu)
  {
    m=create(id, font, w, h, spacing, 0, 0);
    o.menu=m;
    o.menu->setVisibility(false);
  }
  
  o.checkable=checkable;


  option.push_back(o);

  setSize(0, 0);

  if (m) m->setPosition(w, m->getTop());

  return o.menu;
}

void MENU::setOptionText(int id, const char *text)
{
  for (int i=0; i<option.size(); i++)
  {
    if (option[i].id==id)
    {  
      option[id].text=text; 
      setSize(0,0);           
    }
  }     
}

void MENU::setOptionCheck(int id, bool checked)
{
  for (int i=0; i<option.size(); i++)
  {
    if (option[i].id==id)
    {  
      option[i].checked=checked; 
    }
  }     
}

void MENU::setOptionCheckability(int id, bool checkable)
{
  for (int i=0; i<option.size(); i++)
  {
    if (option[i].id==id)
    {  
      option[i].checkable=checkable; 
    }
  }   
}



    
// Event handlers

bool MENU::isOnOption(int i, int xPos, int yPos)
{
  if (xPos>=0 && xPos<=w && yPos>=i*spacing && yPos<(i+1)*spacing)
  {
    return true;                
  }
  return false;
}

bool MENU::mouseButtonDown(int xPos, int yPos)
{
  if (visible && active)
  {
    // Handle submenu events
    for (int i=0; i<option.size(); i++)
    {
      if (option[i].menu)
      {
        if (option[i].menu->mouseButtonDown(xPos-option[i].menu->getLeft(), 
                                            yPos-option[i].menu->getTop()))
          return true;
      }        
    }

    // sdgfsdfsd
    for (int i=0; i<option.size(); i++)
    {
      if (isOnOption(i, xPos, yPos))
      {
        if (option[i].checkable) 
          option[i].checked=option[i].checked?false:true;

        if (selectCallback)
          selectCallback(this, option[i].id, userData);

        return true;                  
      }
    }
  }
  return false;     
}

void MENU::mouseButtonUp(int xPos, int yPos)
{
  // Empty     
}

// Handles mouse move event
void MENU::mouseMove(bool button, int xPos, int yPos)
{
  // Handle the event only if the menu visible and active
  if (visible && active)
  {
    // Handle submenu events
    for (int i=0; i<option.size(); i++)
    {
      if (option[i].menu)
        option[i].menu->mouseMove(button, xPos-option[i].menu->getLeft(), 
                                          yPos-option[i].menu->getTop()); 
        
    }

    //
    if (xPos>=0 && yPos>=0 && xPos<=w && yPos<=h)
    {
      for (int i=0; i<option.size(); i++)
      {
        if (option[i].menu)
        {
          if (isOnOption(i, xPos, yPos))
          {
            option[i].menu->setVisibility(true);
          }
          else
          {
            option[i].menu->setVisibility(false);      
          }
        }
      }
    }
  }
}

void MENU::keyDown(char asciiCode)
{
  // Empty     
}
