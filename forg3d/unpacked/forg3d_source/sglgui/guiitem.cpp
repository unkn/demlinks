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

// Inits the attributes to default values
GUIITEM::GUIITEM(void)
{
  id=0;
  focus=false;
  visible=true;
  active=true;
  text="";
  x=0;
  y=0;
  w=GUIITEM_DEFAULT_SIZE;
  h=GUIITEM_DEFAULT_SIZE;
  transparency=1;
  font=0;
  borderColor[0]=0.6;
  borderColor[1]=0.6;
  borderColor[2]=0.6;
  fontColor[0]=0;
  fontColor[1]=0;
  fontColor[2]=0;
  fillColor1[0]=0.937;
  fillColor1[1]=0.937;
  fillColor1[2]=0.937;
  fillColor2[0]=0.843;
  fillColor2[1]=0.843;
  fillColor2[2]=0.843;
  texture1=0;
  texture2=0;
  border=true;
}

// Destructor
GUIITEM::~GUIITEM()
{
  // Empty
}

// QUERY METHODS ------------------------------------------
int GUIITEM::getId(void) const
{
  return id;
}

// Returns the coordinate of left edge of the item
int GUIITEM::getLeft(void) const
{
  return x;
}

// Returns the coordinate of right edge of the item
int GUIITEM::getRight(void) const
{
  return x+w;
}

// Returns the coordinate of top edge of the item
int GUIITEM::getTop(void) const
{
  return y;
}

// Returns the coordinate of bottom edge of the item
int GUIITEM::getBottom(void) const
{
  return y+h;
}

// Returns the witdh of the item
int GUIITEM::getWidth(void) const
{
  return w;
}

// Returns the height of the object
int GUIITEM::getHeight(void) const
{
  return h;
}

// Returns the text of the item
// Not all items have meaningfull text
const char *GUIITEM::getText(void) const
{
  return text.c_str();
}

// Returns true if the item is visible
bool GUIITEM::isVisible(void) const
{
  return visible;
}

// Returns true if the item is active
bool GUIITEM::isActive(void) const
{
  return  active;
}

// Returns the texture
GLuint GUIITEM::getTexture1(void) const
{
  return texture1;
}

// Returns the texture
GLuint GUIITEM::getTexture2(void) const
{
  return texture2;
}

// Returns true if the border is visible
bool GUIITEM::getBorderVisibility(void)
{
  return border;
}


// UPDATE MEDHODS------------------------------------------

// Sets the position of the item
void GUIITEM::setPosition(int x, int y)
{
  this->x=x;
  this->y=y;
}

// Sets the size of the item
void GUIITEM::setSize(int w, int h)
{
  if (w<GUIITEM_MIN_SIZE) w=GUIITEM_MIN_SIZE;
  if (h<GUIITEM_MIN_SIZE) h=GUIITEM_MIN_SIZE;

  if (w>GUIITEM_MAX_SIZE) w=GUIITEM_MAX_SIZE;
  if (h>GUIITEM_MAX_SIZE) h=GUIITEM_MAX_SIZE;

  this->w=w;
  this->h=h;
}

// Sets the transparency of the item
void GUIITEM::setTransparency(float a)
{
  if (a < 0.0f) a = 0.0f;
  if (a > 1.0f) a = 1.0f;
  transparency=a;
}

// Sets the color of the border of the item
void GUIITEM::setBorderColor(float r, float g, float b)
{
  borderColor[0]=r;
  borderColor[1]=g;
  borderColor[2]=b;
}

// Sets the first fill color of the item
void GUIITEM::setFillColor1(float r, float g, float b)
{
  fillColor1[0]=r;
  fillColor1[1]=g;
  fillColor1[2]=b;
}

// Sets the second fill color of the item
void GUIITEM::setFillColor2(float r, float g, float b)
{
  fillColor2[0]=r;
  fillColor2[1]=g;
  fillColor2[2]=b;
}

// Sets the font color of the item
void GUIITEM::setFontColor(float r, float g, float b)
{
  fontColor[0]=r;
  fontColor[1]=g;
  fontColor[2]=b;
}

// Sets the font of the item
void GUIITEM::setFont(const FONT *font)
{
  if (font) this->font=font;
}

// Sets the text of the item
void GUIITEM::setText(const char *text)
{
  if (text) this->text=text;
}

// Sets weather or not the item is visible
void GUIITEM::setVisibility(bool visible)
{
  this->visible=visible;
}

// Set weather or not the item is active
void GUIITEM::setActivity(bool active)
{
  this->active=active;
}

// Sets the texture
void GUIITEM::setTexture1(GLuint texture)
{
  texture1=texture;
}

// Sets the texture
void GUIITEM::setTexture2(GLuint texture)
{
  texture2=texture;
}

// Sets weather or not the border is visible
void GUIITEM::setBorderVisibility(bool border)
{
  this->border=border;
}

void GUIITEM::killFocus(void)
{
  focus=false;
}

void GUIITEM::preMouseButtonDown(int xPos, int yPos)
{
  // Empty
}

// Sets the scissorbox to match the item
// The scissorbox is assumed to match the container
void GUIITEM::setScissor(void)
{
  GLint sbox[4];
  glGetIntegerv(GL_SCISSOR_BOX, sbox);
  glScissor(sbox[0]+x, sbox[1]+sbox[3]-y-h, w, h);
}

const FONT *GUIITEM::getFont(void)
{
  return font;      
}
