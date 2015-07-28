#ifdef WIN32
#include <windows.h>
#endif

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
#include "panel.h"

#include "messagedialog.h"

MESSAGEDIALOG::MESSAGEDIALOG()
{
  messageLabel=0;
  editField=0;
  leftButton=0;
  rightButton=0;
  browseButton=0;                          
}


MESSAGEDIALOG::~MESSAGEDIALOG()
{
  // Empty                               
}

MESSAGEDIALOG *MESSAGEDIALOG::create(const char *header, const char *message, const FONT *font)
{
  // Allocate memory
  MESSAGEDIALOG *md = new MESSAGEDIALOG();
  if (!md) return 0;
 
  // Set attributes
  md->setFont(font);
  md->setText(header);  
  
  // Create  gui components
  md->messageLabel = md->addLabel(MESSAGEDIALOG_LABEL_ID, message, font, 50, 30);

  md->leftButton = md->addButton(MESSAGEDIALOG_LEFT_ID, "Cancel", font, 
                               60, 60, 50, 30, 0, 0);
                               
  md->rightButton = md->addButton(MESSAGEDIALOG_RIGHT_ID, "OK", font, 
                                   60, 60, 50, 30, 0, 0);

  md->browseButton = md->addButton(MESSAGEDIALOG_BROWSE_ID, "...", font, 
                                   60, 60, 50, 30, browseCallback, md);

  md->editField = md->addEditField(MESSAGEDIALOG_EDITFIELD_ID, "", font,
                                   60, 60, 100, 20, 256, 0, 0);


  // Return the created object
  return md;
}

LABEL *MESSAGEDIALOG::getMessageLabel()
{
  return messageLabel;      
}

BUTTON *MESSAGEDIALOG::getLeftButton(void)
{
  return leftButton;       
}

BUTTON *MESSAGEDIALOG::getRightButton(void)
{
  return rightButton;       
}

EDITFIELD *MESSAGEDIALOG::getEditField(void)
{
  return editField;
}





void MESSAGEDIALOG::setUp(int w, int h,
                          const char *header, const char *message, 
                          const char *editField, bool browse,
                          const char *left, const char *right,
                          void (*buttonCallback)(GUIITEM *gi, void *userData),
                          void *userData)
{
  #ifndef WIN32
    browse=false;
  #endif

  setSize(w, h);

  // Set header                          
  setText(header?header:"");
 
  // Set message label 
  if (message)
  {
    messageLabel->setVisibility(true);                
    messageLabel->setText(message);
    messageLabel->setPosition(font->getSize(), getHeaderSize()+font->getSize());
  }
  else messageLabel->setVisibility(false);
  
  // Set edit field
  if (editField)
  {
    if (browse)
    {
      browseButton->setVisibility(true);
      browseButton->autoSize();
    }
    
    this->editField->setText(editField);
    this->editField->setVisibility(true);
    this->editField->setPosition(font->getSize(), (int)(0.5f*h-0.5f*(this->editField->getHeight()-getHeaderSize())));

    if (browse)
    {
      this->editField->setSize((int)(w-2*font->getSize())-4-browseButton->getWidth(), (int)(1.33f*this->editField->getFont()->getSize()));
      browseButton->setPosition(w-4-browseButton->getWidth(), (int)(0.5f*h-0.5f*(this->editField->getHeight()-getHeaderSize())));
      browseButton->setSize(browseButton->getWidth(), this->editField->getHeight());
    }
    else
    {
      this->editField->setSize((int)(w-2*font->getSize()), (int)(1.33f*this->editField->getFont()->getSize()));
      browseButton->setVisibility(false);
    }
  }
  else
  {
    this->editField->setVisibility(false);    
    browseButton->setVisibility(false);
  }

 
  // Set left button
  if (left)
  {
    leftButton->setVisibility(true);
    leftButton->setText(left);
    leftButton->autoSize();
    leftButton->setCallback(buttonCallback);
    leftButton->setUserData(userData);
  }
  else
  {
    leftButton->setVisibility(false);      
  }
  
  // Set right button
  if (right)rightButton->setText(right);
  else rightButton->setText("OK");
  rightButton->autoSize();
  rightButton->setCallback(buttonCallback);
  rightButton->setUserData(userData);


  if (left)
  {
    leftButton->setPosition(w/2-leftButton->getWidth()-2, 
                            getHeight()-font->getSize()/2-
                            rightButton->getHeight());
    rightButton->setPosition(w/2+2, 
                             getHeight()-font->getSize()/2-
                             rightButton->getHeight());
  }
  else
  {
    rightButton->setPosition(w/2-rightButton->getWidth()/2, 
                             getHeight()-font->getSize()/2-
                             rightButton->getHeight());    
  }
}                          


void MESSAGEDIALOG::browseCallback(GUIITEM *gi, void *userData)
{
  MESSAGEDIALOG *md=(MESSAGEDIALOG *)userData;

  if (gi->getId()==MESSAGEDIALOG_BROWSE_ID)
  {
    #ifdef WIN32

    static char buffer[256];
    memset(buffer, 0, 256);
    OPENFILENAME ofn;
    memset(&ofn, 0, sizeof(OPENFILENAME));
    ofn.lStructSize=sizeof(OPENFILENAME);
    ofn.lpstrFile=buffer;
    ofn.nMaxFile=256;
    ofn.lpstrTitle="Select a file";
    ofn.Flags=OFN_HIDEREADONLY | OFN_NOCHANGEDIR;
        
    if (GetOpenFileName(&ofn))
    {
      md->editField->setText(buffer);
    }

    #endif
  }
}






