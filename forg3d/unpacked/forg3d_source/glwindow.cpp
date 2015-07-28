#include <SDL/SDL.h>

#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif

#include <cstdlib>
#include <math.h>
#include <iostream>

using namespace std;

#include "glwindow.h"

// Constructor
GLWINDOW::GLWINDOW()
{
  width = 0;
  height = 0;
  bpp = 0;
  done=false;
  active=false;
  fps=0;
  for (int i=0; i<GLWK_LAST; i++) key[i]=false;
  mousePos[0]=0;
  mousePos[1]=0;
  sdlSurface=0;
  flags=0;

  logicCallback=0;
  renderCallback=0;
  mouseMoveCallback=0;
  keyDownCallback=0;
  keyUpCallback=0;
  userData=0;
}

// Destructor
GLWINDOW::~GLWINDOW()
{
  SDL_Quit();
}

// Creates a new GLWINDOW
GLWINDOW *GLWINDOW::create(const char *title, int w, int h, 
                           bool fs, bool rs, bool stencil)
{
  GLWINDOW *gw = 0;

  // Allocate memory
  gw = new GLWINDOW();

  // Information about the current video settings.
  const SDL_VideoInfo* info = NULL;

  // First, initialize SDL
  putenv("SDL_VIDEO_CENTERED=1");
  if( SDL_Init( SDL_INIT_EVERYTHING ) < 0 )
  {
    cerr << "ERROR: Video initialization failed: " << SDL_GetError() << ".\n";
    SDL_Quit();
    return 0;
  }
  SDL_EnableUNICODE(1);
  SDL_EnableKeyRepeat(SDL_DEFAULT_REPEAT_DELAY, SDL_DEFAULT_REPEAT_INTERVAL);


  // Get the video information. 
  info = SDL_GetVideoInfo( );
  if(!info)
  {
    cerr << "ERROR: Video query failed:" << SDL_GetError() << ".\n";
    SDL_Quit();
    return 0;
  }

  gw->width = w;
  gw->height = h;
  gw->bpp = info->vfmt->BitsPerPixel;

  SDL_GL_SetAttribute( SDL_GL_RED_SIZE, 8 );
  SDL_GL_SetAttribute( SDL_GL_GREEN_SIZE, 8 );
  SDL_GL_SetAttribute( SDL_GL_BLUE_SIZE, 8 );
  SDL_GL_SetAttribute( SDL_GL_DEPTH_SIZE, 24 );
  if (stencil) SDL_GL_SetAttribute( SDL_GL_STENCIL_SIZE, 8 );
  SDL_GL_SetAttribute( SDL_GL_DOUBLEBUFFER, 1 );
  gw->flags = SDL_OPENGL | (fs?SDL_FULLSCREEN:(rs?SDL_RESIZABLE:0));

  // Set the video mode
  if( (gw->sdlSurface=SDL_SetVideoMode( gw->width, gw->height, gw->bpp, gw->flags )) == 0 )
  {
    cerr << "ERROR: Video mode set failed: " << SDL_GetError() << ".\n";
    SDL_Quit();
    return 0;
  }

  // return the created object
  SDL_WM_SetCaption(title, title);
  gw->active=true;
  return gw;
}



// Loop that runs until exitMainLoop is called
// At each round logic and render functions are called
void GLWINDOW::mainLoop(void)
{
  // Our SDL event placeholder.
  SDL_Event event;
  int frames=0;
  Uint32 fpsTime=0;

  done=false;

  Uint32 startTime=SDL_GetTicks();
  while (!done)
  {
    // Habdle events
    while(SDL_PollEvent(&event))
    {
      switch (event.type)
      {
      /*  case SDL_VIDEOEXPOSE:
        {
          if (renderCallback) renderCallback(this, userData);
        }
        break;*/

        //
        case SDL_ACTIVEEVENT:
        {
          if (event.active.state==SDL_APPACTIVE)
          {
            if (event.active.gain==0) active=false;
            else active=true;
          }
        }
        break;
        
        case SDL_VIDEORESIZE:
        {
          width=event.resize.w;
          height=event.resize.h;
          #ifdef WIN32
          sdlSurface->w=width;
          sdlSurface->h=height;
          #endif
          #ifndef WIN32
          sdlSurface = SDL_SetVideoMode( event.resize.w, event.resize.h, bpp, flags );
          #endif
          if (resizeCallback) resizeCallback(this, userData);
        }
        
        // A keyboard key was pressed
        case SDL_KEYDOWN:
        {
          if (event.key.keysym.sym<=322)
          {
            key[event.key.keysym.sym]=true;
            if (keyDownCallback)
              keyDownCallback(this, (GLWKey)event.key.keysym.sym, 
                              event.key.keysym.unicode<256?event.key.keysym.unicode:0, 
                              userData);
          }
        }
        break;

        // A keyboard key was released
        case SDL_KEYUP:
        {
          if (event.key.keysym.sym<=322)
          {
            key[event.key.keysym.sym]=false;
            if (keyUpCallback)
              keyUpCallback(this, (GLWKey)event.key.keysym.sym, userData);
          }
        }
        break;

        // The mouse was moved
        case SDL_MOUSEMOTION:
        {
          mousePos[0]=event.motion.x;
          mousePos[1]=event.motion.y;
          if (mouseMoveCallback)
            mouseMoveCallback(this, userData);
        }
        break;

        // Mouse button was pressed
        case SDL_MOUSEBUTTONDOWN:
        {
          int i=GLWK_MOUSE_BUTTON1+event.button.button-1;
          key[i]=true;
          if (keyDownCallback)
            keyDownCallback(this, (GLWKey)i, 0, userData);
        }
        break;

        // Mouse button was releaed
        case SDL_MOUSEBUTTONUP:
        {
          int i=GLWK_MOUSE_BUTTON1+event.button.button-1;
          key[i]=false;
          if (keyUpCallback)
            keyUpCallback(this, (GLWKey)i, userData);
        }
        break;

        // Quit signal
        case SDL_QUIT:
        {
          done=true;
        }
        break;
      }
    }

    // Run user code
    if (active)
    {
      // logic
      Uint32 time = SDL_GetTicks();
      Uint32 deltaTime=time-startTime;
      if (deltaTime>0)
      {
        if (logicCallback)
          logicCallback(this, 0.001*deltaTime, userData);
        startTime=time;
      }

      // Render
      if (renderCallback) renderCallback(this, userData);
      SDL_GL_SwapBuffers( );
      frames++;
      fpsTime+=deltaTime;
      if (fpsTime>1000)
      {
        fps=(float)frames/(0.001*fpsTime);
        fpsTime=0;
        frames=0;                        
      }
    }
    else
    {
      startTime = SDL_GetTicks();
    }
  }
}

// Exits from the glWindow rendering loop
void GLWINDOW::exitMainLoop(void)
{
  done=true;
}


bool GLWINDOW::getKeyState(GLWKey key) const
{
  return this->key[key];
}


int GLWINDOW::getCursorVisibility(void) const
{
  return SDL_ShowCursor(SDL_QUERY)==SDL_ENABLE;
}



//
void GLWINDOW::setLogicCallback(void (*logicCallback)(GLWINDOW *gw, float frameTime, void *userData))
{
  this->logicCallback=logicCallback;
}

//
void GLWINDOW::setRenderCallback(void (*renderCallback)(GLWINDOW *gw, void *userData))
{
  this->renderCallback=renderCallback;
}

//
void GLWINDOW::setKeyDownCallback(void (*keyDownCallback)(GLWINDOW *gw, GLWKey key, char ascii, void *userData))
{
  this->keyDownCallback=keyDownCallback;
}

//
void GLWINDOW::setKeyUpCallback(void (*keyUpCallback)(GLWINDOW *gw, GLWKey key, void *userData))
{
  this->keyUpCallback=keyUpCallback;
}

//
void GLWINDOW::setMouseMoveCallback(void (*mouseMoveCallback)(GLWINDOW *gw, void *userData))
{
  this->mouseMoveCallback=mouseMoveCallback;
}

void GLWINDOW::setResizeCallback(void (*resizeCallback)(GLWINDOW *gw, void *userData))
{
  this->resizeCallback=resizeCallback;
}


void GLWINDOW::setCursorVisibility(bool visible)
{
  SDL_ShowCursor(visible?SDL_ENABLE:SDL_DISABLE);
}

void GLWINDOW::setUserData(void *userData)
{
  this->userData=userData;     
}


// Returns the width of the window
int GLWINDOW::getWidth(void) const
{
  return width;
}

// Returns the height of the window
int GLWINDOW::getHeight(void) const
{
  return height;
}

// Returns the position of the mouse cursor relative
// to the lower left corner of the window
void GLWINDOW::getMousePositionUpper(int mpos[2]) const
{
  mpos[0]=mousePos[0];
  mpos[1]=mousePos[1];
}

// Returns the position of the mouse cursor relative
// to the upper left corner of the window
void GLWINDOW::getMousePositionLower(int mpos[2]) const
{
  mpos[0]=mousePos[0];
  mpos[1]=height-mousePos[1];
}

// Returns the X position of the mouse cursor
int GLWINDOW::getMouseX(void) const
{
  return mousePos[0];
}

int GLWINDOW::getMouseYLower(void) const
{
  return height-mousePos[1];
}


int GLWINDOW::getMouseYUpper(void) const
{
  return mousePos[1];
}

void *GLWINDOW::getUserData(void)
{
  return userData;     
}


// Return a mouse ray based on the given modelview and projection matrix
void GLWINDOW::getMouseRay(const float viewMatrix[16],
                           const float projMatrix[16],
                           float position[3], float direction[3]) const
{
  int mpos[2];
  static double viewMat[16];
  static double projMat[16];
  static GLint viewport[4];
  static double mouseRay[3];

  mpos[0]=getMouseX();
  mpos[1]=getMouseYLower();

  if (projMatrix)
  {
    for (int i=0; i<16; i++)
      projMat[i]=projMatrix[i];
  }
  else
  {
    glGetDoublev(GL_PROJECTION_MATRIX, projMat);
  }

  if (viewMatrix)
  {
    for (int i=0; i<16; i++)
      viewMat[i]=viewMatrix[i];
  }
  else
  {
    glGetDoublev(GL_MODELVIEW_MATRIX, viewMat);
  }
  
  glGetIntegerv(GL_VIEWPORT, viewport);

  gluUnProject(mpos[0], mpos[1], 0,
               viewMat, projMat, viewport,
               &mouseRay[0], &mouseRay[1], &mouseRay[2]);

  position[0]=mouseRay[0];
  position[1]=mouseRay[1];
  position[2]=mouseRay[2];

  gluUnProject(mpos[0], mpos[1], 1,
               viewMat, projMat, viewport,
               &mouseRay[0], &mouseRay[1], &mouseRay[2]);

  direction[0]=mouseRay[0]-position[0];
  direction[1]=mouseRay[1]-position[1];
  direction[2]=mouseRay[2]-position[2];

  float len=sqrt(direction[0]*direction[0]+
                 direction[1]*direction[1]+
                 direction[2]*direction[2]);
  if (len>0)
  {
    direction[0]/=len;
    direction[1]/=len;
    direction[2]/=len;    
  }
}

float GLWINDOW::getFps(void) const
{
  return fps;      
}

