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
#include <iostream>

using namespace std;

#include "glwindow.h"
#include "sglguih.h"
#include "info.h"
#include "infocontainer.h"
#include "node.h"
#include "edge.h"
#include "simulation.h"
#include "nodepanel.h"
#include "simulationpanel.h"
#include "mainpanel.h"
#include "edgepanel.h"
#include "forg3d.h"


int main(int argc, char *argv[])
{
  FORG3D *h=FORG3D::create((argc>1)?argv[1]:0);

  if (h)
  {
    h->run();
    delete h;
  }

  return 0;
}
