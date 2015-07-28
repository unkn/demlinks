#ifdef __APPLE__
  #include <OpenGL/gl.h>
  #include <OpenGL/glu.h>
#else
  #include <GL/gl.h>
  #include <GL/glu.h>
#endif
#include <string>
#include <vector>
#include <fstream>
#include <iostream>
#include <sstream>
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
// Just initializes variables. Use the create()-method to create an instance
SIMULATION::SIMULATION()
{
  running=false;
  integrator=1;
  electricConstant=250;
  springConstant=15;
  dampingConstant=5;
  maxTimeStep=0.1;
  tempSpace=0;
  value=0;
  result=0;
  temp=0;
}

// Destructor
SIMULATION::~SIMULATION()
{
  // Delete nodes
  for (int i=0; i<node.size(); i++)
    if (node[i]) delete node[i];

  // Delete links
  for (int i=0; i<edge.size(); i++)
    if (edge[i]) delete edge[i];
    
  // Free temporary storage space
  if (value) delete value;
  if (result) delete result;
  if (temp) delete temp;
}

// Creates a new empty SIMULATION
SIMULATION *SIMULATION::create(void)
{
  SIMULATION *s=0;

  // Allocate menory
  s = new SIMULATION();
  if (!s) return 0;
  
  s->filename="newfile.txt";

  // Return the created object
  return s;
}


// Creates a new simulation from a file
SIMULATION *SIMULATION::createFromFile(const char *filename, bool *error)
{
  SIMULATION *sim=0;
  NODE *node=0;
  EDGE *edge=0;
  string line;
  string part1, part2;
  float tf1, tf2, tf3;
  int ti;

  *error = false;

  // Try to open the file
  ifstream file(filename);
  if (!file.is_open()) return 0;

  // Create the simulation
  sim=create();
  if (!sim)
  {
    file.close();           
    return 0;
  }    

  // Remember the filename  
  sim->filename=filename;

  // Parse the content of the file
  int lineNumber=0;
  while (getline(file, line))
  {
    lineNumber++;

    // Find the split point
    int pos = line.find(":", 0);

    // If no split point found skip this line
    if (pos == string::npos) continue;

    // Split the string
    part1 = line.substr(0, pos);
    part2 = line.substr(pos+1, line.size()-pos-1);

    // new node creation
    if (part1 == "NODE")
    {
      if (part2.size()==0)
      {
        cerr << "ERROR: Node on line " << lineNumber << " has no id.\n";
        *error=true;      
      }
      else
      if (sim->getNodeById(part2.c_str()))
      {
        cerr << "ERROR: Node on line " << lineNumber << " already exists.\n";
        *error=true;                                       
      }
      else
      {      
        node=sim->addNode(part2.c_str());
        edge=0;
      }
    }
    else
    // New link creation
    if (part1 == "EDGE")
    {
      // Find the split point
      int cpos = part2.find(",", 0);
      if (cpos == string::npos)
      {
        cerr << "ERROR: No comma separating the node ids on line "
             << lineNumber << ".\n";
        *error=true;
      }
      else
      {
        // Parse the node idd
        string n1Id = part2.substr(0, cpos);
        string n2Id = part2.substr(cpos+1, part2.size()-cpos-1);
        
        // Find the nodes
        NODE *n1 = sim->getNodeById(n1Id.c_str());
        if (!n1)
        {
          cerr << "ERROR: Node \"" << n1Id.c_str() << "\" on line " 
               << lineNumber << " not found.\n";            
          *error=true;
        }
            
        NODE *n2 = sim->getNodeById(n2Id.c_str());
        if (!n2)
        {
          cerr << "ERROR: Node \"" << n2Id.c_str() << "\" on line " 
               << lineNumber << " not found.\n";            
          *error=true;
        }

        // Create the edge
        if (n1 && n2)
        {
          edge=sim->addEdge(n1, n2);
          node=0;
        }
      }
    }
    else
    // Tag attribute
    if (part1 == "TAG")
    {
      if (node) node->setTag(part2.c_str());
      if (edge) edge->setTag(part2.c_str());
    }
    else
    // Visible attribute
    if (part1 == "VISIBLE")
    {
      if (sscanf(part2.c_str(), "%d", &ti)==1)
      {
        if (node) node->setVisibility(ti);
        if (edge) edge->setVisibility(ti);
      }
      else
      {
        cerr << "ERROR: VISIBLE on line " << lineNumber << " has a bad attribute.\n";
        *error=true;
      }
    }
    else
    // Show tag attribute
    if (part1 == "SHOWTAG")
    {
      if (sscanf(part2.c_str(), "%d", &ti)==1)
      {
        if (node) node->setTagVisibility(ti);
        if (edge) edge->setTagVisibility(ti);
      }
      else
      {
        cerr << "ERROR: SHOWTAG on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }
    }
    else
    // Size attribute
    if (part1 == "SIZE")
    {
      if (sscanf(part2.c_str(), "%f", &tf1)==1)
      {
        if (node) node->setSize(tf1);
      }
      else
      {
        cerr << "ERROR: SIZE on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }
    }
    else
    // Position attribute
    if (part1 == "POSITION")
    {
      if (sscanf(part2.c_str(), "%f,%f,%f", &tf1, &tf2, &tf3)==3)
      {
        if (node) node->setPosition(tf1, tf2, tf3);
      }
      else
      {
        cerr << "ERROR: POSITION on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }
    }
    else
    // Color attribute
    if (part1 == "COLOR")
    {
      if (sscanf(part2.c_str(), "%f,%f,%f", &tf1, &tf2, &tf3)==3)
      {
        if (node) node->setColor(tf1, tf2, tf3);
        if (edge) edge->setColor(tf1, tf2, tf3);
      }
      else
      {
        cerr << "ERROR: COLOR on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }      
    }
    else
    // Charge attribute
    if (part1 == "CHARGE")
    {
      if (sscanf(part2.c_str(), "%f", &tf1)==1)
      {
        if (node) node->setCharge(tf1);
      }
      else
      {
        cerr << "ERROR: CHARGE on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }      
    }
    else
    // Mass attribute
    if (part1 == "MASS")
    {
      if (sscanf(part2.c_str(), "%f", &tf1)==1)
      {
        if (node && tf1>0) node->setInverseMass(1.0/tf1);
      }
      else
      {
        cerr << "ERROR: MASS on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }      
    }
    else
    // Width attribute
    if (part1 == "WIDTH")
    {
      if (sscanf(part2.c_str(), "%d", &ti)==1)
      {
        if (edge) edge->setWidth(ti);
      }
      else
      {
        cerr << "ERROR: WIDTH on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }      
    }
    else
    // Spring constant attribute
    if (part1 == "SCONST")
    {
      if (sscanf(part2.c_str(), "%f", &tf1)==1)
      {
        if (edge) edge->setSpringConstant(tf1);
      }
      else
      {
        cerr << "ERROR: SCONST on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }      
    }
    else
    // Spring constant attribute
    if (part1 == "DIRECTED")
    {
      if (sscanf(part2.c_str(), "%d", &ti)==1)
      {
        if (edge) edge->setDirected(ti);
      }
      else
      {
        cerr << "ERROR: DIRECTED on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }      
    }
    else  
    // Model attribute
    if (part1 == "MODEL")
    {
      if (node && part2.size()>0)
      {
        switch (part2[0])
        {
          case 'T':   // Tetrahedron
            node->setModel(0);
          break;
          case 'C':   // Cube
            node->setModel(1);
          break;
          case 'O':   // Octahedron
            node->setModel(2);
          break;
          case 'D':   // Dodecahedron
            node->setModel(3);
          break;
          case 'I':   // Icosahedron
            node->setModel(4);
          break;
          default:
            cerr << "ERROR: MODEL on line " << lineNumber << " has a bad attribute.\n";          
            *error=true;
          break;
        }
      }
    }
    else
    // Electric constant
    if (part1 == "SIMEC")
    {
      if (sscanf(part2.c_str(), "%f", &tf1)==1)
      {
        sim->setElectricConstant(tf1);
      }
      else
      {
        cerr << "ERROR: SIMEC on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }      
    }
    else
    // Spring constant    
    if (part1 == "SIMSC")
    {
      if (sscanf(part2.c_str(), "%f", &tf1)==1)
      {
        sim->setSpringConstant(tf1);
      }
      else
      {
        cerr << "ERROR: SIMSC on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }            
    }
    else
    // Damping constant
    if (part1 == "SIMDC")
    {
      if (sscanf(part2.c_str(), "%f", &tf1)==1)
      {
        sim->setDampingConstant(tf1);
      }
      else
      {
        cerr << "ERROR: SIMDC on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }            
    }
    else
    // Max time step
    if (part1 == "TIMESTEP")
    {
      if (sscanf(part2.c_str(), "%d", &ti)==1)
      {
        sim->setMaxTimeStep(ti);
      }
      else
      {
        cerr << "ERROR: TIMESTEP on line " << lineNumber << " has a bad attribute.\n";          
        *error=true;
      }            
    }
    else
    // Generic attribute
    {
      if (node) node->addInfo(part1.c_str(), part2.c_str());
      if (edge) edge->addInfo(part1.c_str(), part2.c_str());
    }
  }


  // Close the file
  file.close();

  // Return the created simulation
  return sim;
}



// Renders all nodes in the simulation
void SIMULATION::renderNodes(GLuint *modelList)
{
  // Render nodes
  int num=node.size();

  for (int i=0; i<num; i++)
  {
    NODE *n=node[i];

    // Render only if the node is visible
    if (n->visible)
    {
      // Push current modeview matrix
      glPushMatrix();

      // Compute model matrix
      glTranslatef(n->position[0], n->position[1], n->position[2]);
      glScalef(n->size, n->size, n->size);

      // Rendet the node
      glColor3fv(n->color);
      glCallList(modelList[n->modelIndex]);

      // Pop current modelview matrix
      glPopMatrix();
    }
  }
}


static void mmv(float out[4], const float matrix[16], const float in[4])
{
  for ( int i=0; i<4; i++)
  {
    out[i] = 
      in[0] * matrix[0*4+i] +
	  in[1] * matrix[1*4+i] +
	  in[2] * matrix[2*4+i] +
	  in[3] * matrix[3*4+i];
  }
}



// Renders the tags of all of the nodes in the simulation
void SIMULATION::renderNodeTags(const FONT *font, 
                                const GLfloat vm[16], 
                                const GLfloat pm[16], 
                                const GLint vp[4])
{
  int num=node.size();
  for (int i=0; i<num; i++)
  {
    NODE *n=node[i];

    // Render the tag only if it is visible
    if (n->showTag)
    {
      static float p[4], temp[4];
      
      p[0]=n->position[0];
      p[1]=n->position[1];
      p[2]=n->position[2];
      p[3]=1.0f;
      
      // Transform to eye space
      mmv(temp, vm, p);
      temp[0]+=n->size;
      mmv(p, pm, temp);
      if (p[3] == 0) continue;

      // Perspective division
      p[0]/=p[3];
      p[1]/=p[3];
      p[2]/=p[3];

      // map to range 0 - 1
      p[0] = p[0] * 0.5f + 0.5f;
      p[1] = p[1] * 0.5f + 0.5f;
      p[2] = p[2] * 0.5f + 0.5f;

      // Map to view port
      p[0] = p[0] * vp[2] + vp[0];
      p[1] = p[1] * vp[3] + vp[1];

      // Render the tag
      glPushMatrix();
 
      glTranslatef(p[0], (vp[3]-p[1]), -511*p[2]-1);

      font->printVCentered(n->tag.c_str(), 0, 0);

      glPopMatrix();
    }
  }
}



// Renders all the links in the simulation
void SIMULATION::renderEdges(GLuint coneModel)
{
  // Render links
  int num=edge.size();
  for (int i=0; i<num; i++)
  {
    EDGE *e=edge[i];

    // Render the link onl if it is visible
    if (e->visible)
    {
      // Render line
      glLineWidth(e->width);
      glBegin(GL_LINES);
      glColor3fv(e->color);
      glVertex3fv(e->node1->position);
      glVertex3fv(e->node2->position);
      glEnd();
      
      // Render arrow
      if (e->directed)
      {
        NODE *n=e->node2;
        float z[3]={0, 0, -1};
        float dir[0];
        float axis[3];
        const float *p=n->getPosition();
        makeVector(dir, e->node1->position, e->node2->position);
        normalizeVector(dir);
        crossProduct(axis, z, dir);
        normalizeVector(axis);
        float angle=57.29577*acosf(dotProduct(dir, z));
        glPushMatrix();
        glTranslatef(p[0]-n->size*dir[0], 
                     p[1]-n->size*dir[1],
                     p[2]-n->size*dir[2]);
        glRotatef(angle, axis[0], axis[1], axis[2]);
        glCallList(coneModel);
        glPopMatrix();
      }
    }
  }
}


// Renders link tags
void SIMULATION::renderEdgeTags(const FONT *font, 
                                const GLfloat vm[16], 
                                const GLfloat pm[16], 
                                const GLint vp[4])
{
  int num=edge.size();
  for (int i=0; i<num; i++)
  {
    EDGE *e=edge[i];

    if (e->showTag)
    {
      static float p[4], temp[4];
      const float *p1=e->node1->position;
      const float *p2=e->node2->position;
      
      p[0]=0.5f*(p1[0]+p2[0]);
      p[1]=0.5f*(p1[1]+p2[1]);
      p[2]=0.5f*(p1[2]+p2[2]);
      p[3]=1.0f;
      
      // Transform to eye space
      mmv(temp, vm, p);
      mmv(p, pm, temp);
      if (p[3] == 0) continue;

      // Perspective division
      p[0]/=p[3];
      p[1]/=p[3];
      p[2]/=p[3];

      // map to range 0 - 1
      p[0] = p[0] * 0.5f + 0.5f;
      p[1] = p[1] * 0.5f + 0.5f;
      p[2] = p[2] * 0.5f + 0.5f;

      // Map to view port
      p[0] = p[0] * vp[2] + vp[0];
      p[1] = p[1] * vp[3] + vp[1];

      // Render the tag
      glPushMatrix();
 
      glTranslatef(p[0], (vp[3]-p[1]), -511*p[2]-1);

      font->printCentered(e->tag.c_str(), 0, 0);

      glPopMatrix();
    }
  }
}




// Takes a time step forward
void SIMULATION::simulate(float timeStep, const NODE *exclude)
{
  int numberOfNodes=node.size();
  int size=6*numberOfNodes;

  // If the simulation is not running return immediately
  if (!running) return;
  
  // Make sure there are not too big leaps
  if (timeStep>maxTimeStep) timeStep=maxTimeStep;

  // Copy current state of the simulation as initial value
  float *nodeState=value;
  for (int i=0; i<numberOfNodes; i++, nodeState+=6)
  {
    NODE *n=node[i];
    nodeState[0]=n->position[0];
    nodeState[1]=n->position[1];
    nodeState[2]=n->position[2];  
    if (n!=exclude)
    {
      nodeState[3]=n->velocity[0];
      nodeState[4]=n->velocity[1];
      nodeState[5]=n->velocity[2];  
    }
    else
    {
      nodeState[3]=0;
      nodeState[4]=0;
      nodeState[5]=0;  
    } 
  }

  // Integrate to take a time step forward
  switch (integrator)
  {
    case 0:
      integrateEuler(result, value, size, timeStep, temp);
    break;
    case 1:
      integrateMidpoint(result, value, size, timeStep, temp);
    break;
    case 2:
      integrateRungeKutta(result, value, size, timeStep, temp);
    break;
  }
 
  // Copy the result back as current state of the simulation
  float *nodeResult=result;
  for (int i=0; i<numberOfNodes; i++, nodeResult+=6)
  {
    NODE *n=node[i];
    if (n!=exclude)
    {
      n->position[0]=nodeResult[0];
      n->position[1]=nodeResult[1];
      n->position[2]=nodeResult[2];
    }
    n->velocity[0]=nodeResult[3];
    n->velocity[1]=nodeResult[4];
    n->velocity[2]=nodeResult[5];            
  }
}

// Sets all nodes to random positions
void SIMULATION::randomize()
{
  for (int i=0; i<node.size(); i++)
  {
    node[i]->setPosition(randomNumber(0, 100),
                         randomNumber(0, 100),
                         randomNumber(0, 100));
    node[i]->setVelocity(0, 0, 0);
  }
}

// Returns the number of nodes in the simulation
int SIMULATION::getNumberOfNodes(void) const
{
  return node.size();
}

// Returns the number of links in the simulation
int SIMULATION::getNumberOfEdges(void) const
{
  return edge.size();
}

// Returns the node that has the given index
NODE *SIMULATION::getNode(int index)
{
  return node[index];
}

// Returns the link that has the given index
EDGE *SIMULATION::getEdge(int index)
{
  return edge[index];
}

// Returns the electric constant of the simulation
float SIMULATION::getElectricConstant(void) const
{
  return electricConstant;
}

// Returns the spring constant of the simulation
float SIMULATION::getSpringConstant(void) const
{
  return springConstant;
}

// Returns the damping constant of the simulation
float SIMULATION::getDampingConstant(void) const
{
  return dampingConstant;
}

// Adds a new node to the simulation
NODE *SIMULATION::addNode(void)
{
  static ostringstream id;
  
  // Find a free id
  int i=0;
  do
  {
    id.str("");
    id << i;
    i++;    
  }
  while (getNodeById(id.str().c_str()));
  
  // Create a node using that id
  return addNode(id.str().c_str());
}

// Adds a new node to the simulation
NODE *SIMULATION::addNode(const char *id)
{
  // Create the node
  NODE *n=NODE::create(id);
  if (!n) return 0;

  // Store the node to the list
  node.push_back(n);

  // Make sure there are enough storege space
  updateTempSpace();
  
  // rebuild the index
  updateIndex();

  // Return the created node
  return n;
}

// Deletes a node from the simulation and sets the pointer to null
void SIMULATION::deleteNode(NODE *n)
{
  int num=0;

  // Safety check
  if (!n) return;

  // Erase the node from the node list
  num=node.size();
  for (int i=0; i<num; i++)
  {
    if (node[i]==n)
    {
      node.erase(node.begin()+i);
      break;
    }
  }

  // Delete all links that are attached to the node
  while (!n->edge.empty())
  {
    deleteEdge(n->edge[0]);
  }

  //
  updateIndex();

  // Free memory
  delete n;
}


// Adds a new node to the simulation
EDGE *SIMULATION::addEdge(NODE *n1, NODE *n2)
{
  // Safety check
  if (!n1 || !n2 || n1==n2) return 0;

  // Check that the link does not exist!!!!
  int num=edge.size();
  for (int i=0; i<num; i++)
  {
    if (edge[i]->node1==n1 && edge[i]->node2==n2) return 0;
    if (edge[i]->node1==n2 && edge[i]->node2==n1) return 0;
  }

  // Create the link
  EDGE *e=EDGE::create(n1, n2);
  if (!e) return 0;

  // Store the link to the link list
  edge.push_back(e);

  // Return the created link
  return e;
}

// Deletes a link from the simulation
void SIMULATION::deleteEdge(EDGE *e)
{
  int num;

  // Safety check
  if (!e) return;

  // Erase the link from the link list
  num=edge.size();
  for (int i=0; i<num; i++)
  {
    if (edge[i]==e)
    {
      edge.erase(edge.begin()+i);
      break;
    }
  }

  // Erase the link from nodes
  num=  e->node1->edge.size();
  for (int i=0; i<num; i++)
  {
    if (e->node1->edge[i] == e)
    {
      e->node1->edge.erase(e->node1->edge.begin()+i);
      break;
    }
  }
  num=e->node2->edge.size();
  for (int i=0; i<num; i++)
  {
    if (e->node2->edge[i] == e)
    {
      e->node2->edge.erase(e->node2->edge.begin()+i);
      break;
    }
  }

  // Free allocated memory
  delete e;
}


// Sets the electric constant of the simulation to k
void SIMULATION::setElectricConstant(float k)
{
  electricConstant=k;
}

// Sets the spring constant of the simulation to k
void SIMULATION::setSpringConstant(float k)
{
  springConstant=k;
}

// Sets the damping constant of the simulation to k
void SIMULATION::setDampingConstant(float k)
{
  dampingConstant=k;
}

// Set wheather or not the simulation is running
void SIMULATION::setRunState(bool running)
{
  this->running=running;
}

// Sets the integrator to be used
void SIMULATION::setIntegrator(int integrator)
{
  this->integrator=integrator;
}

// Sets the maxium time step allowed in the simulation
void SIMULATION::setMaxTimeStep(int timeStep)
{
  if (timeStep>100) timeStep=100;
  if (timeStep<10) timeStep=10;
  maxTimeStep=0.001*timeStep;       
}

// Returns true if the simulation is running
bool SIMULATION::getRunState(void) const
{
  return running;
}

// Returns the (index of) integrator used
int SIMULATION::getIntegrator(void) const
{
  return integrator;
}

// Returns the maxium time step allowed in the simulation
int SIMULATION::getMaxTimeStep(void) const
{
  return (int)(1000*maxTimeStep);
}

// Integrates using euler method.
// Temp needs as meny elements as are in initial value.
void SIMULATION::integrateEuler(float *result, const float *initialValue,  
                                const int size, const float x, float *temp)
{
  int i;
  
  // Ask how much f'(0) is
  derive(temp, initialValue, size, 0);
  
  // Compute f(0)+x*f'(x)
  for ( i=0; i<size; i++ )    
    result[i]=initialValue[i]+x*temp[i];
}



// Integrates using the midpoint method
// temp needs 2 times as many elements as are in initial value
void SIMULATION::integrateMidpoint(float *result, const float *initialValue,                         
                                   const int size, const float x, float *temp)
{
  float *temp1=temp;
  float *temp2=temp+size;
  int i;
  float hx=0.5f*x;
 
  // Use the Euler method to compute f(x/2)
  // Ask how much f'(0) is
  derive(temp1, initialValue, size, 0);
  // Compute f(0)+(x/2)*f'(0)
  for (i=0; i<size; i++) temp2[i]=initialValue[i]+hx*temp1[i];
 
  // Apply the midpoint method
  // Ask how much f'(x/2) is when f(x/2) is known
  derive(temp1, temp2, size, hx);
  // Compute f(0)+x*f'(x/2)
  for (i=0; i<size; i++) result[i]=initialValue[i]+x*temp1[i];
}

// Integrates using the runge Kutta method
// Temp needs 6 times as meny elements as are in the initial value
void SIMULATION::integrateRungeKutta(float *result, const float *initialValue,
                                     const int size, const float x, float *temp)
{
  int i;
  float *temp1=temp;
  float *temp2=temp+size;
  float *k1=temp2+size;
  float *k2=k1+size;
  float *k3=k2+size;
  float *k4=k3+size;

  // Compute the result using 4 diferent methods and take weighted avarage

  // Method 1
  derive(temp1, initialValue, size, 0);
  for (i=0; i<size; i++) k1[i]=x*temp1[i];

  // Method 2
  for (i=0; i<size; i++) temp2[i]=initialValue[i]+0.5f*k1[i];
  derive(temp1, temp2, size, 0.5f*x);
  for (i=0; i<size; i++) k2[i]=x*temp1[i];

  // Method 3
  for (i=0; i<size; i++) temp2[i]=initialValue[i]+0.5f*k2[i];
  derive(temp1, temp2, size, 0.5f*x);
  for (i=0; i<size; i++) k3[i]=x*temp1[i];

  // Method 4
  for (i=0; i<size; i++) temp2[i]=initialValue[i]+k3[i];
  derive(temp1, temp2, size, x);
  for (i=0; i<size; i++) k4[i]=x*temp1[i];

  // Compute weighted avarage
  for (i=0; i<size; i++) result[i]=initialValue[i]+
                                   (1.0/6.0)*k1[i]+(1.0/3.0)*k2[i]+
                                   (1.0/3.0)*k3[i]+(1.0/6.0)*k4[i];
}


// Computes the derivative of the simulation system state vector
// This is needed for the numerical integrator
void SIMULATION::derive(float *result, const float *value, const int size, const float x)
{
  static const float epsilon=0.001;
  int numberOfNodes=size/6;
  float F[3];
  float v[3];
  float r, ir;
  float magnitude;
  
  float dc=dampingConstant;
  float sc=springConstant;
  float ec=electricConstant;
  
  // Compute the derivative of the state of all of the nodes
  const float *nodeState=value;
  float *nodeDerive=result;
  for (int i=0; i<numberOfNodes; i++, nodeState+=6, nodeDerive+=6)
  {
    NODE *n=node[i];

    // Init force accumulator
    F[0]=0; 
    F[1]=0; 
    F[2]=0;
    
    // Compute electric forces againts all of the other nodes ( O(n^2) )
    const float *nodeState2=value;
    for (int j=0; j<numberOfNodes; j++, nodeState2+=6)
    {
      NODE *n2 = node[j];

      // The node does not have electric force with it self
      if (i!=j)
      {
        // Make a vector from node 2 to node 1
        v[0]=nodeState[0]-nodeState2[0];
        v[1]=nodeState[1]-nodeState2[1];
        v[2]=nodeState[2]-nodeState2[2];
  
        // Compute distance and its inverse
        // This is the slowest part ( square root and division :( )
        r=sqrtf(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
        if (r<epsilon) continue;
        ir=1.0f/r;

        // Compute the magnitude of the electric force
        // Couloms law: f=(k*q1*q1)/(r*r) -> f=1/r*1/r*k*q1*q2
        // However the direction vector needs to be normalized 
        // so we add one more 1/r term
        magnitude=ir*ir*ir*ec*n->charge*n2->charge;
  
        // Add the force to the total
        F[0]+=magnitude*v[0];
        F[1]+=magnitude*v[1];
        F[2]+=magnitude*v[2];
      }
    }
  
    // Compute the spring forces 
    // O(n^2) in theory, but the number of links is usullly small
    int numberOfEdges = n->edge.size();
    for (int j=0; j<numberOfEdges; j++)
    {
      EDGE *e=n->edge[j];
    
      magnitude=e->springConstant*sc;  

      // Find the otherend node from the state vector
      if (n==e->node1) nodeState2 = &value[e->node2->index];
      else nodeState2 = &value[e->node1->index];

      // Add the force to the total
      F[0]+=magnitude*(nodeState2[0]-nodeState[0]);
      F[1]+=magnitude*(nodeState2[1]-nodeState[1]);
      F[2]+=magnitude*(nodeState2[2]-nodeState[2]);
    }
  
    // Compute damping force F=-kv
    F[0]-=dc*nodeState[3];
    F[1]-=dc*nodeState[4];
    F[2]-=dc*nodeState[5];  
    
    // Derivative of position is velocity s'(t)=v(t)
    nodeDerive[0]=nodeState[3];
    nodeDerive[1]=nodeState[4];
    nodeDerive[2]=nodeState[5];  

    // Derivative of velocity is acceleration v'(t)=a(t)
    // which according to Newton is a(t)=F/m or a(t)=(1/m)*F
    float im=n->inverseMass;
    nodeDerive[3]=im*F[0];
    nodeDerive[4]=im*F[1];
    nodeDerive[5]=im*F[2];
  }
}


// Makes sure there are enough temporary storage space allocated
void SIMULATION::updateTempSpace(void)
{
  if (node.size()>=tempSpace)
  {
    tempSpace+=100;
    
    if (value) delete value;
    if (result) delete result;
    if (temp) delete temp;
    
    value=new float [6*tempSpace];
    result=new float [6*tempSpace];
    temp=new float [6*6*tempSpace];       
  }     
}

// Computes the positions the nodes in the state vector
// Has to called every time the number or order of the nodes chances
void SIMULATION::updateIndex(void)
{
  for (int i=0; i<node.size(); i++) node[i]->index=6*i;
}

const char *SIMULATION::getFilename(void) const
{
  return filename.c_str();      
}

bool SIMULATION::saveToFile(const char *filename)
{
  char model[5]={'T','C','O','D','I'};

  // Try to open the file
  ofstream file(filename);
  if (!file.is_open()) return false;    

  // Write content
  
  // Write global params
  file << "SIMEC:" << electricConstant << "\n";
  file << "SIMSC:" << springConstant << "\n";
  file << "SIMDC:" << dampingConstant << "\n";
  file << "TIMESTEP:" << (int)(1000*maxTimeStep) << "\n";
  file << "\n\n";
  
  
  // Write nodes
  for (int i=0; i<node.size(); i++)
  {
    file << "NODE:" << node[i]->id << "\n";

    file << "TAG:" << node[i]->tag << "\n";

    file << "POSITION:" << node[i]->position[0] << ","
                        << node[i]->position[1] << ","
                        << node[i]->position[2] << "\n";

    file << "COLOR:" << node[i]->color[0] << ","
                     << node[i]->color[1] << ","
                     << node[i]->color[2] << "\n";
    
    file << "VISIBLE:" << node[i]->visible << "\n";
    
    file << "SHOWTAG:" << node[i]->showTag << "\n";
    
    file << "SIZE:" << node[i]->size << "\n";
    
    file << "CHARGE:" << node[i]->charge << "\n";
    
    file << "MASS:" << (1.0f/node[i]->inverseMass) << "\n";
    
    file << "MODEL:" << model[node[i]->modelIndex] << "\n";
  
    for (int j=0; j<node[i]->getNumberOfInfos(); j++)
    {
      file << node[i]->getInfoKey(j) << ":" << node[i]->getInfoData(j) << "\n";    
    }
  
    file << "\n";
  }
  file << "\n";

  // Write links
  for (int i=0; i<edge.size(); i++)
  {
    file << "EDGE:" << edge[i]->node1->id << "," 
                    << edge[i]->node2->id << "\n";

    
    file << "TAG:" << edge[i]->tag << "\n";
    
    file << "VISIBLE:" << edge[i]->visible << "\n";
    
    file << "SHOWTAG:" << edge[i]->showTag << "\n";

    file << "COLOR:" << edge[i]->color[0] << "," 
                     << edge[i]->color[1] << ","
                     << edge[i]->color[2] << "\n";

    file << "WIDTH:" << edge[i]->width << "\n";

    file << "SCONST:" << edge[i]->springConstant << "\n";

    file << "DIRECTED:" << edge[i]->directed << "\n";

    for (int j=0; j<edge[i]->getNumberOfInfos(); j++)
    {
      file << edge[i]->getInfoKey(j) << ":" << edge[i]->getInfoData(j) << "\n";    
    }

    file << "\n";
  } 


  // Close the file
  file.close();       

  this->filename=filename;
  return true;
}

// Returns the node that has the given id
NODE *SIMULATION::getNodeById(const char *id)
{
  int n = getNumberOfNodes();
  for (int i=0; i<n; i++)
  {
    if (node[i]->id==id) return node[i];
  }     
  return 0;   
}
