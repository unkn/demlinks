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

#include "sglguih.h"
#include "info.h"
#include "infocontainer.h"
#include "node.h"
#include "edge.h"
#include "simulation.h"
#include "mathstuff.h"


// Constructor
NODE::NODE()
{
  index=0;            
  visible=true;
  showTag=true;
  size=1;
  color[0]=randomNumber(0, 1);
  color[1]=randomNumber(0, 1);;
  color[2]=randomNumber(0, 1);;
  position[0]=randomNumber(0, 100);
  position[1]=randomNumber(0, 100);
  position[2]=randomNumber(0, 100);
  velocity[0]=0;
  velocity[1]=0;
  velocity[2]=0;  
  charge=1;
  inverseMass=1;
  modelIndex=4;
}

// Destructor
NODE::~NODE()
{

}

// Creates a new node
NODE *NODE::create(const char *id)
{
  NODE *n=0;

  // Allocate memory
  n = new NODE();
  if (!n) return 0;

  // Set id
  n->id=id;
  
  // Return the created object
  return n;
}


// Set model of this node
void NODE::setModel(int modelIndex)
{
  if (modelIndex<0) modelIndex=0;
  if (modelIndex>4) modelIndex=4;
  this->modelIndex=modelIndex;
}

// Sets the tag text of the node
void NODE::setTag(const char *tag)
{
  this->tag=tag;     
}

// Sets the position of the node
void NODE::setPosition(float x, float y, float z)
{
  position[0]=x;
  position[1]=y;
  position[2]=z;     
}


void NODE::setVelocity(float x, float y, float z)
{
  velocity[0]=x;
  velocity[1]=y;
  velocity[2]=z;  
}

// Sets the charge of this node
void NODE::setCharge(float charge)
{
  if (charge<0.1) charge=0.1;
  if (charge>10) charge=10;
  this->charge=charge;   
}

// Sets the size of the node
void NODE::setSize(float size)
{
  if (size<0.1) size=0.1;
  if (size>10) size=10;
  this->size=size;     
}

// sets the color of the node
void NODE::setColor(float r, float g, float b)
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

// Sets 1/mass of the node
void NODE::setInverseMass(float im)
{
  if (im<0.1) im=0.1;
  if (im>10) im=10;

  inverseMass=im;
}

void NODE::setVisibility(bool visible)
{
  this->visible=visible;       
}

void NODE::setTagVisibility(bool visible)
{
  showTag=visible;     
}

// Returns the tag of the node
const char *NODE::getTag(void) const
{
  return tag.c_str();      
}

void NODE::setId(const char *id)
{
  this->id=id;     
}


// Returns the charge of this node
float NODE::getCharge(void) const
{
  return charge;      
}

// Returns the position of the node
const float *NODE::getPosition(void) const
{
  return position;      
}

// gives a copy og the postion of the node
void NODE::getPosition(float pos[3]) const
{
  pos[0]=position[0];
  pos[1]=position[1];
  pos[2]=position[2];
}

// Returns the velocity of the node
const float *NODE::getVelocity(void) const
{
  return velocity;      
}

// return the inverse of the mass of the node
float NODE::getInverseMass(void) const
{
  return inverseMass;      
}

// Returns the size of the node
float NODE::getSize(void) const
{
  return size;      
}

// Returns the color of the node
const float *NODE::getColor(void) const
{
  return color;      
}

// Returns (the index) the model of the node
int NODE::getModel(void) const
{
  return modelIndex;    
}



bool NODE::getVisibility(void) const
{
  return visible;     
}

bool NODE::getTagVisibility(void) const
{
  return showTag;     
}

const char *NODE::getId(void) const
{
  return id.c_str();      
}

