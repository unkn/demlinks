#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <string>

#include "font.h"

FONT *FONT::create(int textureSize,
                   const unsigned char *data,
                   const int characterWidth[256])
{
  // Check for invalid input
  if (!data) return 0;

  // Allocate memory
  FONT *f = new FONT();
  if (!f) return 0;

  // Compute size
  f->size=textureSize/16;

  // Copy character widths
  for (int i=0; i<256; i++)
  {
    if (characterWidth) f->characterWidth[i]=characterWidth[i];
    else f->characterWidth[i]=f->size;
  }

  // Copy texture "name"
  glGenTextures(1, &f->texture);
  if (!f->texture)
  {
    delete f;
    return 0;
  }
  glBindTexture(GL_TEXTURE_2D, f->texture);
  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 
               textureSize, textureSize, 0, 
               GL_RGBA, GL_UNSIGNED_BYTE, data);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

  // generate lists
  f->listBase = glGenLists(256);

  // Build lists
  for (int i=0; i<256; i++)
  {
    float tx=(i%16)/16.0f;
    float ty=(i/16)/16.0f;

    glNewList(f->listBase+i, GL_COMPILE);

    glBegin(GL_QUADS);
    glTexCoord2f(tx, ty);
    glVertex2f(0, 0);
    glTexCoord2f(tx, ty+1.0/16.0);
    glVertex2f(0, f->size);
    glTexCoord2f(tx+1.0/16.0, ty+1.0/16.0);
    glVertex2f(f->size, f->size);
    glTexCoord2f(tx+1.0/16.0, ty);
    glVertex2f(f->size, 0);
    glEnd();

//    glTranslatef(f->characterWidth[i], 0, 0);

    glEndList();
  }

  // Return the created font object
  return f;
}

// Constructor
FONT::FONT()
{
  listBase=0;
  texture=0;
}

// Destructor
FONT::~FONT()
{
  if (listBase)
    glDeleteLists(listBase, 256);
}

// Prints text using the font
void FONT::print(const char *text, 
                 int x, int y, int scale, 
                 int first, int maxCount, 
                 int maxLength, bool obeyNewLine) const
{
  // Safety check
  if (!text) return;

  // Bind font texture
  glBindTexture(GL_TEXTURE_2D, texture);

  // Store current modelview matrix
  glPushMatrix();

  // Translate text to correct position
  glTranslatef(x, y, 0);
  glScalef(scale, scale, scale);

  int w = 0;
  int max = maxCount+first;
  for (int i = first; maxCount == 0 || i < max; i++)
  {
    unsigned char letter = (unsigned char)text[i];

    if (letter == 0) break;
    
    if (obeyNewLine && letter=='\n')
    {
      glTranslatef(-w, size, 0);
      w = 0;
      continue;
    }

    // Print the letter
    glCallList(listBase+letter);
    glTranslatef(characterWidth[letter], 0, 0);
    
    // If max length was met stop printing
    w += characterWidth[letter];
    if (maxLength > 0 && w >= maxLength) break;
  }

  // Restore the modelview matrix
  glPopMatrix();
}

// Same as print but x and y are the coordinates of the center of the text
void FONT::printCentered(const char *text, 
                         int x, int y, int scale,
                         int first, int maxCount, 
                         int maxLength, bool obeyNewLine) const
{
  print(text, 
        x-(scale*textWidth(text))/2, 
        y-(scale*textHeight(text))/2,
        scale, first, maxCount, maxLength, obeyNewLine);
}

void FONT::printVCentered(const char *text, 
                          int x, int y, int scale,
                          int first, int maxCount, 
                          int maxLength, bool obeyNewLine) const
{
  print(text, x, y-(scale*textHeight(text))/2, scale,
        first, maxCount, maxLength, obeyNewLine);
}


void FONT::printHCentered(const char *text, 
                          int x, int y, int scale,
                          int first, int maxCount, 
                          int maxLength, bool obeyNewLine) const
{
  print(text, x-(scale*textWidth(text))/2, scale,
        first, maxCount, maxLength, obeyNewLine);
}


// Returns the width of the text
int FONT::textWidth(const char *text, 
                    int first, int maxCount, bool obeyNewLine) const
{
  int width = 0;
  int maxWidth = 0 ;

  int max = maxCount + first;
  for (int i = first; i < max || maxCount == 0; i++)
  {
    unsigned char letter = (unsigned char)text[i];

    if (letter == 0) return (width > maxWidth)?width:maxWidth;

    if (obeyNewLine && letter=='\n')
    {
      if (width > maxWidth) maxWidth = width;
      width=0;
      continue;
    }

    width += characterWidth[letter];
  }

  return (width > maxWidth)?width:maxWidth;
}


// Returns the width of the text
int FONT::textHeight(const char *text, 
                     int first, int maxCount, bool obeyNewLine) const
{
  if (!obeyNewLine) return size;

  int h = size;
  int max = maxCount + first;
  for (int i = first; i < max || maxCount == 0; i++)
  {
    unsigned char letter = (unsigned char)text[i];

    if (letter == 0) return h;

    if (letter=='\n') h+=size;
  }

  return h;
}



// Returns the size of the font
int FONT::getSize(void) const
{
  return size;
}




