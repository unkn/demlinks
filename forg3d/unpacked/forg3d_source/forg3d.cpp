#include <SDL/SDL.h>
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
#include <fstream>
#include <iostream>
#include <FreeImage.h>

using namespace std;

#include "glwindow.h"
#include "sglguih.h"
#include "info.h"
#include "infocontainer.h"
#include "node.h"
#include "edge.h"
#include "simulation.h"
#include "mathstuff.h"
#include "nodepanel.h"
#include "simulationpanel.h"
#include "mainpanel.h"
#include "edgepanel.h"
#include "forg3d.h"
#include "polyhedron.h"

// Menu item IDs
#define MENU_FILE_SAVE 1
#define MENU_FILE_LOAD 2
#define MENU_FILE_EXIT 3
#define MENU_VIEW_MAINPANEL 4
#define MENU_VIEW_SIMULATIONPANEL 5
#define MENU_VIEW_NODEPANEL 6
#define MENU_VIEW_LINKPANEL 7
#define MENU_FILE_NEW 8
#define MENU_FILE_SCREENSHOT 9


// Render callback funktion
static void render(GLWINDOW *gw, void *userData)
{
  // Pointer to the main program comes from userdata param
  FORG3D *forg3d=(FORG3D *)userData;
  if (forg3d) forg3d->render(gw);
}

// Logic callback funktion
static void logic(GLWINDOW *gw, float frameTime, void *userData)
{
  FORG3D *forg3d=(FORG3D *)userData;
  if (forg3d) forg3d->logic(gw, frameTime);
}

// Keydown callback for glwindow
static void keyDown(GLWINDOW *gw, GLWKey key, char ascii, void *userData)
{
  // Pointer to the main program comes from userData param
  FORG3D *forg3d=(FORG3D *)userData;
  if (forg3d) forg3d->keyDown(gw, key, ascii);
}

// Key up callback for glwindow
static void keyUp(GLWINDOW *gw, GLWKey key, void *userData)
{
  FORG3D *forg3d=(FORG3D *)userData;
  if (forg3d) forg3d->keyUp(gw, key);
}

// Mouse move callback for glwindow
static void mouseMove(GLWINDOW *gw, void *userData)
{
  // Pointer to the main program comes from
  FORG3D *forg3d=(FORG3D *)userData;
  if (forg3d) forg3d->mouseMove(gw);
}

static void resize(GLWINDOW *gw, void *userData)
{
  // Pointer to the main program comes from
  FORG3D *forg3d=(FORG3D *)userData;
  if (forg3d) forg3d->resize(gw);
}


// Callback funktion for the menu
void FORG3D::menuCallback(MENU *m, int sid, void *userData)
{
  FORG3D *h=(FORG3D *)userData;

  // Whitch menu option was las selected
  switch (sid)
  {
    // File->new was selected
    case MENU_FILE_NEW:
    {
      h->selectedNode=0;
      h->selectedEdge=0;
      h->nodePanel->setNode(0, 0);
      h->edgePanel->setEdge(0);
      SIMULATION *tempSim = SIMULATION::create();
      if (h->sim) delete h->sim;
      h->sim = tempSim;
      h->simulationPanel->useSimulation(h->sim);
      h->gui->showMessage(150, 100, "Message", "New simulation created.", 0, false, 0, "OK", 0, 0);
    }
    break;
    // File->save was selected
    case MENU_FILE_SAVE:
    {
      h->gui->showMessage(250, 130, "Save", "File name:",
                          h->sim->getFilename(), true,
                          "Cancel", "Save", saveCallback, h);
    }
    break;
    // File->load was selected
    case MENU_FILE_LOAD:
    {
      h->gui->showMessage(250, 130, "Load", "File name:",
                          "", true,
                          "Cancel", "Load",
                          loadCallback, h);
    }
    break;
    // File->screenshot was selected
    case MENU_FILE_SCREENSHOT:
    {
      h->gui->showMessage(250, 130, "Screenshot", "File name:",
                          "screenshot.png", false,
                          "Cancel", "Ok",
                          screenshotCallback, h);
    }
    break;
    // File->exit was selected
    case MENU_FILE_EXIT:
    {
      h->gw->exitMainLoop();
    }
    break;
    // View->mainpanel was selected
    case MENU_VIEW_MAINPANEL:
    {
      h->mainPanel->setVisibility(m->isOptionChecked(sid));
    }
    break;
    // View->simulation panel was selected
    case MENU_VIEW_SIMULATIONPANEL:
    {
      h->simulationPanel->setVisibility(m->isOptionChecked(sid));
    }
    break;
    // View->node panel was selected
    case MENU_VIEW_NODEPANEL:
    {
      h->nodePanel->setVisibility(m->isOptionChecked(sid));
    }
    break;
    // View->link panel was selected
    case MENU_VIEW_LINKPANEL:
    {
      h->edgePanel->setVisibility(m->isOptionChecked(sid));
    }
    break;
  }
}


// Constructor
// Just initializes variables
// Use create to create an instance
FORG3D::FORG3D()
{
  showNodes=true;
  showEdges=true;
  showNodeTags=false;
  showEdgeTags=false;
  litNodes=true;
  bgColor[0]=1;
  bgColor[1]=1;
  bgColor[2]=1;
  ntColor[0]=0;
  ntColor[1]=0;
  ntColor[2]=0;
  ltColor[0]=0;
  ltColor[1]=0;
  ltColor[2]=0;

  gw=0;
  gui=0;
  sim=0;
  focus[0]=50;
  focus[1]=50;
  focus[2]=50;
  orientation[0]=1;
  orientation[1]=0;
  orientation[2]=0;
  orientation[3]=0;

  zoom=50;
  fov=60;
  selectedNode=0;
  selectedEdge=0;

  leftDrag=false;
  rightDrag=false;
  mPos[0]=0;
  mPos[1]=1;

  menubar=0;
  mainPanel=0;
  simulationPanel=0;
  nodePanel=0;
  edgePanel=0;

  for (int i=0; i<FORG3D_NUMBER_OF_MODELS; i++)
    model[i]=0;
  coneModel=0;
}

// Destructor
FORG3D::~FORG3D()
{
  for (int i=0; i<FORG3D_NUMBER_OF_MODELS; i++)
    if (model[i]) glDeleteLists(model[i], 1);
  if (coneModel) glDeleteLists(coneModel, 1);
  if (nodePanel) delete nodePanel;
  if (simulationPanel) delete simulationPanel;
  if (mainPanel) delete mainPanel;
  if (edgePanel) delete edgePanel;
  if (gui) delete gui;
  if (sim) delete sim;
  if (gw) delete gw;
}


// Creates a new FORG3D
// Call only once!
FORG3D *FORG3D::create(const char *filename)
{
  // Allocate memory
  FORG3D *h = new FORG3D();
  if (!h) return 0;

  // create the window
  h->gw = GLWINDOW::create("FORG3D", 800, 600, false, true, false);
  if (!h->gw)
  {
    delete h;
    return 0;
  }
  h->gw->setUserData(h);
  h->gw->setRenderCallback(::render);
  h->gw->setLogicCallback(::logic);
  h->gw->setKeyDownCallback(::keyDown);
  h->gw->setKeyUpCallback(::keyUp);
  h->gw->setMouseMoveCallback(::mouseMove);
  h->gw->setLogicCallback(::logic);
  h->gw->setResizeCallback(::resize);


  // Set opengl states
  float white[4]={0.75, 0.75, 0.75, 1};
  glViewport(0, 0, h->gw->getWidth(), h->gw->getHeight());
  glShadeModel(GL_FLAT);
  glEnable(GL_COLOR_MATERIAL);
  glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, white);
  glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 8);
  glEnable(GL_LIGHT0);
  glEnable(0x803A/*GL_RESCALE_NORMAL_EXT*/);
  glAlphaFunc(GL_GREATER, 0.5);

  // Create 3D-models
  h->model[0]=buildTetrahedron();
  h->model[1]=buildCube();
  h->model[2]=buildOctahedron();
  h->model[3]=buildDodecahedron();
  h->model[4]=buildIcosahedron();
  h->coneModel=buildCone();

  // Create the simulation
  bool error;
  if (filename)
    h->sim=SIMULATION::createFromFile(filename, &error);
  else
    h->sim=SIMULATION::create();
  if (!h->sim)
  {
    delete h;
    return 0;
  }
  h->frame();

  // Create the gui
  h->gui=SGLGUI::create(h->gw->getWidth(), h->gw->getHeight());
  if (!h->gui)
  {
    delete h;
    return 0;
  }

  h->mainPanel=MAINPANEL::create(h, h->gw, h->gui);
  h->simulationPanel=SIMULATIONPANEL::create(h->gw, h->gui);
  h->simulationPanel->useSimulation(h->sim);
  h->nodePanel=NODEPANEL::create(h->gw, h->gui);
  h->edgePanel=EDGEPANEL::create(h->gw, h->gui);

  // create menu bar
  h->menubar=h->gui->addMenuBar(10, h->gui->defaultFont(), 0, 0, 800, 20, menuCallback, h);
//  h->menubar->setTransparency(0.75);
  MENU *m=h->menubar->addMenu(1, "File");
  m->addOption(MENU_FILE_NEW, "New", false, false);
  m->addOption(MENU_FILE_SAVE, "Save", false, false);
  m->addOption(MENU_FILE_LOAD, "Load", false, false);
  m->addOption(MENU_FILE_SCREENSHOT, "Screenshot", false, false);
  m->addOption(MENU_FILE_EXIT, "Exit", false, false);
  m=h->menubar->addMenu(2, "View");
  m->addOption(MENU_VIEW_MAINPANEL, "Main panel", false, true);
  m->addOption(MENU_VIEW_SIMULATIONPANEL, "Simulation panel", false, true);
  m->addOption(MENU_VIEW_NODEPANEL, "Node panel", false, true);
  m->addOption(MENU_VIEW_LINKPANEL, "Edge panel", false, true);
  m->setOptionCheck(MENU_VIEW_MAINPANEL, true);
  m->setOptionCheck(MENU_VIEW_SIMULATIONPANEL, true);
  m->setOptionCheck(MENU_VIEW_NODEPANEL, true);
  m->setOptionCheck(MENU_VIEW_LINKPANEL, true);

  // Return the created object
  return h;
}

// Starts the program
void FORG3D::run(void)
{
  // Just enter to the main loop
  gw->mainLoop();
}



void FORG3D::renderSimulation(void)
{
  static float rotM[16];

  // Set up opengl states
  glEnable(GL_DEPTH_TEST);
  glDepthFunc(GL_LEQUAL);
  glEnable(GL_CULL_FACE);
  glClearColor(bgColor[0], bgColor[1], bgColor[2], 1);
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


  // Render nodes and links


  // Set up perspective projection matrix
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  gluPerspective(fov, (float)gw->getWidth()/(float)gw->getHeight(), 1, 512);

  // Set up view matrix
  glMatrixMode(GL_MODELVIEW);
  glLoadIdentity();
  glTranslatef(0, 0, -zoom);
  normalizeQuaternion(orientation);
  generateQuaternionMatrix(rotM, orientation);
  glMultMatrixf(rotM);
  glTranslatef(-focus[0], -focus[1], -focus[2]);

  // Store the matrices for later use
  glGetFloatv(GL_PROJECTION_MATRIX, projMatrix);
  glGetFloatv(GL_MODELVIEW_MATRIX, viewMatrix);
  glGetIntegerv(GL_VIEWPORT, viewport);


  // Render nodes
  if (showNodes)
  {
    if (litNodes) glEnable(GL_LIGHTING);
    else glDisable(GL_LIGHTING);
    sim->renderNodes(model);
    glDisable(GL_LIGHTING);
  }

  // Render links
  glDisable(GL_CULL_FACE);
  if (showEdges)
  {
    sim->renderEdges(coneModel);
  }


  // Render tags


  // Set up orthographic projection matrix
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  glOrtho(0, gw->getWidth(), gw->getHeight(), 0, 1, 512);

  // Set up view matrix
  glMatrixMode(GL_MODELVIEW);
  glLoadIdentity();


  glEnable(GL_TEXTURE_2D);
  glEnable(GL_ALPHA_TEST);
  glDisable(GL_CULL_FACE);

  // Render node tags
  if (showNodeTags)
  {
    glColor3fv(ntColor);
    sim->renderNodeTags(gui->defaultFont(), viewMatrix, projMatrix, viewport);
  }

  // Render link tags
  if (showEdgeTags)
  {
    glColor3fv(ltColor);
    sim->renderEdgeTags(gui->defaultFont(), viewMatrix, projMatrix, viewport);
  }

  glEnable(GL_CULL_FACE);
  glDisable(GL_ALPHA_TEST);
  glDisable(GL_TEXTURE_2D);
}


// Renders the main program
void FORG3D::render(GLWINDOW *gw)
{
  // Render the simulation
  renderSimulation();


  glMatrixMode(GL_PROJECTION);
  glLoadMatrixf(projMatrix);

  // Set up view matrix
  glMatrixMode(GL_MODELVIEW);
  glLoadMatrixf(viewMatrix);


  // Render selected node and link
  glDisable(GL_DEPTH_TEST);
  if (selectedNode)
  {
    glPointSize(10);
    glColor3f(1, 0, 0);
    glBegin(GL_POINTS);
    glVertex3fv(selectedNode->getPosition());
    glEnd();
  }
  if (selectedEdge)
  {
    glLineWidth(1);
    glColor3f(1, 0, 0);
    glBegin(GL_LINES);
    glVertex3fv(selectedEdge->getNode1()->getPosition());
    glVertex3fv(selectedEdge->getNode2()->getPosition());
    glEnd();
  }


  // Render the GUI
  glDisable(GL_DEPTH_TEST);
  glDisable(GL_CULL_FACE);
  gui->render();
}

// Handles main program logic
void FORG3D::logic(GLWINDOW *gw, float frameTime)
{
  static float fpsUpdate=0;

  fpsUpdate+=frameTime;
  if (fpsUpdate>=1)
  {
    fpsUpdate=0;
    simulationPanel->setFps(gw->getFps());
  }


  sim->simulate(frameTime, rightDrag?selectedNode:0);
}

// Returns the node that is under the mouse cursor
// Or null if no node is under the mouse
NODE *FORG3D::selectNode(void)
{
  float rayDirection[3];
  float rayOrigin[3];
  NODE *closestNode=0;
  float closestDistance=0;

  // Get ray for picking
  gw->getMouseRay(viewMatrix, projMatrix, rayOrigin, rayDirection);

  int num=sim->getNumberOfNodes();

  for (int i=0; i<num; i++)
  {
    float distance=0;
    NODE *n=sim->getNode(i);

    if (raySphereIntersection(rayOrigin, rayDirection,
                              n->getPosition(), n->getSize(),
                              &distance))
    {
      // If this was the closest node so far store it
      if (distance<closestDistance || !closestNode)
      {
        closestDistance=distance;
        closestNode=n;
      }
    }
  }

  // Return the closest found node
  return closestNode;
}

// Returns the link that is currently under the mouse
// Or null if no link is under the mouse cursor
EDGE *FORG3D::selectEdge(void)
{
  float rayDirection[3];
  float rayOrigin[3];
  EDGE *closestEdge=0;
  float closestDistance=0;

  gw->getMouseRay(viewMatrix, projMatrix, rayOrigin, rayDirection);

  int num=sim->getNumberOfEdges();

  for (int i=0; i<num; i++)
  {
    float distance=0;
    EDGE *e=sim->getEdge(i);

    if (rayFiniteCylinderIntersection(rayOrigin, rayDirection,
                                      e->getNode1()->getPosition(),
                                      e->getNode2()->getPosition(),
                                      0.5,
                                      &distance))
    {
      if (distance<closestDistance || !closestEdge)
      {
        closestDistance=distance;
        closestEdge=e;
      }
    }
  }

  return closestEdge;
}


// Handels the key down event
void FORG3D::keyDown(GLWINDOW *gw, GLWKey key, char ascii)
{
  // Pass the event to the GUI first
  if (key==GLWK_MOUSE_BUTTON1)
  {
    if (gui->mouseButtonDown(gw->getMouseX(), gw->getMouseYUpper()))
      return;
  }
  if (ascii!=0) gui->keyDown(ascii);

  // If delete was pressed delete the selected node/link
  if (key==GLWK_DELETE)
  {
    if (selectedNode)
    {
      sim->deleteNode(selectedNode);
      selectedNode=0;
      nodePanel->setNode(0, 0);
      simulationPanel->useSimulation(sim);
    }
    if (selectedEdge)
    {
      sim->deleteEdge(selectedEdge);
      selectedEdge=0;
      edgePanel->setEdge(0);
      simulationPanel->useSimulation(sim);
    }
  }

  // Left mouse button was clicked
  if (key==GLWK_MOUSE_BUTTON1)
  {
    // If control was held during the click add a new node
    if (gw->getKeyState(GLWK_RCTRL) || gw->getKeyState(GLWK_LCTRL))
    {
      float rayDirection[3];
      float rayOrigin[3];
      float pos[3];
      float normal[3]={viewMatrix[2], viewMatrix[6], viewMatrix[10]};
      gw->getMouseRay(viewMatrix, projMatrix, rayOrigin, rayDirection);
      rayPlaneIntersection(rayOrigin, rayDirection,
                           focus, normal, pos);
      NODE *nn=sim->addNode();
      simulationPanel->useSimulation(sim);
      nn->setPosition(pos[0], pos[1], pos[2]);

    }
    else
    // If shift was held during click add a new edge
    if ((gw->getKeyState(GLWK_RSHIFT) || gw->getKeyState(GLWK_LSHIFT))
        && selectedNode)
    {
      NODE *n=selectNode();
      if (n && n!=selectedNode)
      {
        sim->addEdge(selectedNode, n);
        simulationPanel->useSimulation(sim);
      }
    }
    // No shift or control was held just select a node
    else
    {
      selectedNode=0;
      selectedEdge=0;
      selectedNode=selectNode();
      if (selectedNode)
      {
        rightDrag=true;
        mPos[0]=gw->getMouseX();
        mPos[1]=gw->getMouseYUpper();
      }
      else
      {
        selectedEdge=selectEdge();
      }

      nodePanel->setNode(sim, selectedNode);
      edgePanel->setEdge(selectedEdge);
    }
  }

  // Middle mouse button was pressed
  // Focus the camera to the selected node/link
  if (key==GLWK_MOUSE_BUTTON2)
  {
    if (selectedNode)
    {
      selectedNode->getPosition(focus);
    }
    if (selectedEdge)
    {
      const float *p1=selectedEdge->getNode1()->getPosition();
      const float *p2=selectedEdge->getNode2()->getPosition();
      focus[0]=0.5*p1[0]+0.5*p2[0];
      focus[1]=0.5*p1[1]+0.5*p2[1];
      focus[2]=0.5*p1[2]+0.5*p2[2];
    }
  }

  // Right mouse button was pressed
  if (key==GLWK_MOUSE_BUTTON3)
  {
    leftDrag=true;
    mPos[0]=gw->getMouseX();
    mPos[1]=gw->getMouseYUpper();
  }
  if (key==GLWK_MOUSE_BUTTON4) zoom+=10;
  if (zoom>1000) zoom=1000;
  if (key==GLWK_MOUSE_BUTTON5) zoom-=10;
  if (zoom<10) zoom=10;
}

// Handels keyup event
void FORG3D::keyUp(GLWINDOW *gw, GLWKey key)
{
  if (key==GLWK_MOUSE_BUTTON1)
  {
    rightDrag=false;
  }

  if (key==GLWK_MOUSE_BUTTON3)
  {
    leftDrag=false;
  }

  // Pass the event to the gui
  if (key==GLWK_MOUSE_BUTTON1)
    gui->mouseButtonUp(gw->getMouseX(), gw->getMouseYUpper());
}

// Handels mouse move event
void FORG3D::mouseMove(GLWINDOW *gw)
{
  // A node was dragged
  if (rightDrag && selectedNode)
  {
    float rayDirection[3];
    float rayOrigin[3];
    float pos[3];
    float normal[3]={viewMatrix[2], viewMatrix[6], viewMatrix[10]};
    gw->getMouseRay(viewMatrix, projMatrix, rayOrigin, rayDirection);
    rayPlaneIntersection(rayOrigin, rayDirection,
                         selectedNode->getPosition(), normal, pos);
    selectedNode->setPosition(pos[0], pos[1], pos[2]);
  }

  //
  if (leftDrag)
  {
    int tempPos[2];
    float up[3]={0,1,0};
    float right[3]={1,0,0};
    tempPos[0]=gw->getMouseX();
    tempPos[1]=gw->getMouseYUpper();


    if (gw->getKeyState(GLWK_RSHIFT))
    {
      float dx=mPos[0]-tempPos[0];
      float dy=mPos[1]-tempPos[1];
      float e=0.01;
      focus[0]+=e*dx*zoom*viewMatrix[0];
      focus[1]+=e*dx*zoom*viewMatrix[4];
      focus[2]+=e*dx*zoom*viewMatrix[8];
      focus[0]-=e*dy*zoom*viewMatrix[1];
      focus[1]-=e*dy*zoom*viewMatrix[5];
      focus[2]-=e*dy*zoom*viewMatrix[9];
    }
    else
    {
      float rotQ[4];
      float tempQ[4];
      quaternionFromAxisAndAngle(rotQ, up, tempPos[0]-mPos[0]);
      multQuaternion(tempQ, rotQ, orientation);
      quaternionFromAxisAndAngle(rotQ, right, tempPos[1]-mPos[1]);
      multQuaternion(orientation, rotQ, tempQ);
    }
    mPos[0]=tempPos[0];
    mPos[1]=tempPos[1];
  }

  // pass the event to the gui
  gui->mouseMove(gw->getKeyState(GLWK_MOUSE_BUTTON1),
                 gw->getMouseX(), gw->getMouseYUpper());
}

// Sets the positions of the panels when resize occures
void FORG3D::resize(GLWINDOW *gw)
{
  glViewport(0, 0, gw->getWidth(), gw->getHeight());
  gui->resize(gw->getWidth(), gw->getHeight());
  menubar->setSize(gw->getWidth(), 20);
 /*nodePanel->setPosition(0,0);
  linkPanel->setPosition(0,0);*/
  mainPanel->setPosition(gw->getWidth()-200, 20);
  simulationPanel->setPosition(gw->getWidth()-200, gw->getHeight()-242);
}

//
void FORG3D::setNodeVisibility(bool visible)
{
  showNodes=visible;
}

void FORG3D::setEdgeVisibility(bool visible)
{
  showEdges=visible;
}

void FORG3D::setNodeTagVisibility(bool visible)
{
  showNodeTags=visible;
}

void FORG3D::setEdgeTagVisibility(bool visible)
{
  showEdgeTags=visible;
}

void FORG3D::setNodeLighting(bool lit)
{
  litNodes=lit;
}

// Sets background color
void FORG3D::setBgColor(float r, float g, float b)
{
  bgColor[0]=r;
  bgColor[1]=g;
  bgColor[2]=b;
}

// Sets the node tag color
void FORG3D::setNtColor(float r, float g, float b)
{
  ntColor[0]=r;
  ntColor[1]=g;
  ntColor[2]=b;
}

// Sets the link tag color
void FORG3D::setLtColor(float r, float g, float b)
{
  ltColor[0]=r;
  ltColor[1]=g;
  ltColor[2]=b;
}

// Returns the back ground color of the program
const float *FORG3D::getBgColor(void) const
{
  return bgColor;
}

// Returns the node tag color
const float *FORG3D::getNtColor(void) const
{
  return ntColor;
}

// Return the link tag color
const float *FORG3D::getLtColor(void) const
{
  return ltColor;
}

// Set weather or not specular light should be used
void FORG3D::setSpecular(bool specular)
{
  if (specular)
  {
    float color[4]={0.75, 0.75, 0.75, 1};
    glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, color);
    glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 8);
  }
  else
  {
    float color[4]={0, 0, 0, 1};
    glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, color);
    glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
  }
}


void FreeImageErrorHandler(FREE_IMAGE_FORMAT fif, const char *message)
{
  cout << FreeImage_GetFormatFromFIF(fif) << "\n";
  cout << message << "\n";
}


// Uses the freeimage library to save a screenshot
bool FORG3D::screenshot(const char *filename)
{
  #ifndef WIN32
  static bool init = false;
  if (!init)
  {
    FreeImage_Initialise();
    init = true;
  }
  #endif

  FreeImage_SetOutputMessage(FreeImageErrorHandler);

  // Allocate temp memory
  int size = 3*gw->getWidth()*gw->getHeight();
  unsigned char *data = new unsigned char [size];
  if (!data) return false;

  // Render the simulation without the gui
  renderSimulation();


  // Read back the rendered data
  glReadPixels(0, 0, gw->getWidth(), gw->getHeight(),
               0x80E0/*GL_BGR_EXT*/, GL_UNSIGNED_BYTE, data);


  // Use the data to make an image
  FIBITMAP *bitmap =
    FreeImage_ConvertFromRawBits(data, gw->getWidth(), gw->getHeight(),
                                 3*gw->getWidth(), 24,
                                 FI_RGBA_RED_MASK, 
                                 FI_RGBA_GREEN_MASK,
                                 FI_RGBA_BLUE_MASK, true);
  if (!bitmap)
  {
    delete data;
    return false;
  }

  // Delete temporary storage
  delete data;


  // Save the image to a file
  if (!FreeImage_Save(FIF_PNG, bitmap, filename))
  {
    FreeImage_Unload(bitmap);
    return false;
  }

  // Delete the bitmap temporary storage
  FreeImage_Unload(bitmap);

  // Return succes
  return true;
}

void FORG3D::screenshotCallback(MESSAGEDIALOG *md, BUTTON *b, void *userData)
{
  FORG3D *h=(FORG3D *)userData;

  if (b->getId()==MESSAGEDIALOG_RIGHT_ID)
  {
    if (h->screenshot(md->getEditField()->getText()))
      h->gui->showMessage(150, 100, "Message", "Screenshot taken.", 0, false, 0, "OK", 0, 0);
    else
      h->gui->showMessage(150, 100, "ERROR", "Screenshot failed!", 0, false, 0, "OK", 0, 0);
  }
}

void FORG3D::loadCallback(MESSAGEDIALOG *md, BUTTON *b, void *userData)
{
  FORG3D *h=(FORG3D *)userData;

  if (b->getId()==MESSAGEDIALOG_RIGHT_ID)
  {
    bool error = false;
    SIMULATION *temp = SIMULATION::createFromFile(md->getEditField()->getText(), &error);
    if (temp)
    {
      if (h->sim) delete h->sim;
      h->sim=temp;
      h->selectedNode=0;
      h->selectedEdge=0;
      h->nodePanel->setNode(0, 0);
      h->edgePanel->setEdge(0);
      h->simulationPanel->useSimulation(h->sim);
      h->frame();
      if (error)
        h->gui->showMessage(260, 120, "Warning", 
                            "Simulation loaded, but there where some errors.\nSee stderr for details.", 
                            0, false, 0, "OK", 0, 0);
      else
        h->gui->showMessage(150, 100, "Message", "Simulation loaded.", 
                            0, false, 0, "OK", 0, 0);      
    }
    else
    {
      h->gui->showMessage(150, 100, "ERROR", "Load failed!", 0, false, 0, "OK", 0, 0);
    }
  }
}


void FORG3D::saveCallback(MESSAGEDIALOG *md, BUTTON *b, void *userData)
{
  FORG3D *h=(FORG3D *)userData;

  if (b->getId()==MESSAGEDIALOG_RIGHT_ID)
  {
    if (h->sim->saveToFile(md->getEditField()->getText()))
      h->gui->showMessage(150, 100, "Message", "Save complete.", 0, false, 0, "OK", 0, 0);
    else
      h->gui->showMessage(150, 100, "ERROR", "Save failed!", 0, false, 0, "OK", 0, 0);
  }
}

// Focuses the camera to the center point of the simulation
void FORG3D::frame(void)
{
  focus[0]=0;
  focus[1]=0;
  focus[2]=0;
  int n = sim->getNumberOfNodes();
  if (n<1) return;

  for (int i=0; i<n; i++)
  {
    const NODE *n = sim->getNode(i);
    const float *p=n->getPosition();
    focus[0]+=p[0];
    focus[1]+=p[1];
    focus[2]+=p[2];
  }
  focus[0]/=n;
  focus[1]/=n;
  focus[2]/=n;
}
