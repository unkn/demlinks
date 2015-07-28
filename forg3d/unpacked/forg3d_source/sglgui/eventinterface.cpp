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

EVENTINTERFACE::EVENTINTERFACE()
{
  userData=0;
  callback=0;                                
}

void EVENTINTERFACE::setUserData(void *userData)
{
  this->userData=userData;     
}

void EVENTINTERFACE::setCallback(void (*callback)(GUIITEM *item, void *userData))
{
  this->callback=callback;     
}
 
void *getUserData(void *userData)
{
  return userData;     
}
