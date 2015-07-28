#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <string>
#include <vector>

using namespace std;

#include "sglguih.h"
#include "info.h"
#include "infocontainer.h"
#include "node.h"
#include "edge.h"
#include "mathstuff.h"

// Constructor
EDGE::EDGE()
{
  visible=true;
  showTag=true;
  width=1;
  color[0]=0.5;
  color[1]=0.5;
  color[2]=0.5;  
  node1=0;
  node2=0;
  springConstant=1;
  directed=false;
}

// Creates a new LINK
EDGE *EDGE::create(NODE *n1, NODE *n2)
{
  // Safety check
  if (!n1 || !n2) return 0;

  // Allocate memory
  EDGE *e = new EDGE();
  if (!e) return 0;
  
  // set attributes
  e->node1=n1;
  e->node2=n2;
  n1->edge.push_back(e);
  n2->edge.push_back(e);

  // Return the created link      
  return e;   
}
 
// Returns the firt node of the edge
NODE *EDGE::getNode1(void)
{
  return node1;     
}

// REturns the second node of th edge
NODE *EDGE::getNode2(void)
{
  return node2;     
}

// Returns the spring constant of the edge
float EDGE::getSpringConstant(void) const
{
  return springConstant;      
}

// Returns the color of the edge
const float *EDGE::getColor(void) const
{
  return color;
}
 
// Returns the tag of the edge
const char *EDGE::getTag(void) const 
{
  return tag.c_str();      
}

// REturn the witdth of the edge
int EDGE::getWidth(void) const
{
  return width;    
}

// REturn true if the edge is visible
bool EDGE::getVisibility(void) const
{
  return visible;     
}

// Returns true if the tag of the edge is visible
bool EDGE::getTagVisibility(void) const
{
  return visible;     
}

// Returns true if the edge is directed
bool EDGE::getDirected(void) const
{
  return directed;     
}
 
 
// Sets the spring constant of the edge
void EDGE::setSpringConstant(float k)
{
  if (k<0.1) k=0.1;
  if (k>10) k=10;
  springConstant=k;
}

// Sets the color of the link
void EDGE::setColor(float r, float g, float b)
{
  if (r<0) r=0;
  if (g<0) g=0;
  if (b<0) b=0;
  if (r>1) r=1;
  if (g>1) g=1;
  if (b>1) b=1;

  color[0]=r;      
  color[1]=g;      
  color[2]=b;        
}

// setts the tag of the edge
void EDGE::setTag(const char *tag)
{
  this->tag=tag;     
}

// setsw the width of the edge
void EDGE::setWidth(int w)
{
  if (w<1) w=1;
  if (w>10) w=10;
  width=w;     
}

// Sets the visibility of the edge
void EDGE::setVisibility(bool visible)
{
  this->visible=visible;     
}

// Sets the tag visibility of the edge
void EDGE::setTagVisibility(bool visible)
{
  showTag=visible;     
}

// Sets weather or not the dege is directed
void EDGE::setDirected(bool directed)
{
  this->directed=directed;     
}

// Swaps the edge direction
void EDGE::swapDirection(void)
{
  NODE *temp=node1;
  node1=node2;
  node2=temp;
}

