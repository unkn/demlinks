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

using namespace std;

#include "glwindow.h"
#include "sglguih.h"
#include "info.h"
#include "infocontainer.h"
#include "node.h"
#include "edge.h"
#include "simulation.h"

#include "simulationpanel.h"


// IDs for the gui components
#define SIMULATIONPANEL_NODES_LABEL 1
#define SIMULATIONPANEL_LINKS_LABEL 2
#define SIMULATIONPANEL_STARTSTOP_BUTTON 3
#define SIMULATIONPANEL_RANDOMIZE_BUTTON 4
#define SIMULATIONPANEL_ELECTRIC_LABEL 5
#define SIMULATIONPANEL_ELECTRIC_SLIDER 6
#define SIMULATIONPANEL_SPRING_LABEL 7
#define SIMULATIONPANEL_SPRING_SLIDER 8
#define SIMULATIONPANEL_DAMPING_LABEL 9
#define SIMULATIONPANEL_DAMPING_SLIDER 10
#define SIMULATIONPANEL_INTEGRATOR_LABEL 11
#define SIMULATIONPANEL_INTEGRATOR_COMBOBOX 12
#define SIMULATIONPANEL_MAXTIMESTEP_LABEL 13
#define SIMULATIONPANEL_MAXTIMESTEP_SLIDER 14

// Callback funktion for sliders
void SIMULATIONPANEL::sliderCallback(GUIITEM *gi, void *userData)
{
  SLIDER *slider = (SLIDER *)gi;
  static char buffer[256];
  SIMULATIONPANEL *sp=(SIMULATIONPANEL *)userData;

  switch (slider->getId())
  {
    case SIMULATIONPANEL_ELECTRIC_SLIDER:
    {
      sp->sim->setElectricConstant(0.1*slider->getValue());
      snprintf(buffer, 256, "Electric constant: %.1f", sp->sim->getElectricConstant());
      sp->electricLabel->setText(buffer);
    }
    break;

    case SIMULATIONPANEL_SPRING_SLIDER:
    {
      sp->sim->setSpringConstant(0.1*slider->getValue());
      snprintf(buffer, 256, "Spring constant: %.1f", sp->sim->getSpringConstant());
      sp->springLabel->setText(buffer);
    }
    break;

    case SIMULATIONPANEL_DAMPING_SLIDER:
    {
      sp->sim->setDampingConstant(0.1*slider->getValue());
      snprintf(buffer, 256, "Damping constant: %.1f", sp->sim->getDampingConstant());
      sp->dampingLabel->setText(buffer);
    }
    break;
    
    case SIMULATIONPANEL_MAXTIMESTEP_SLIDER:
    {
      sp->sim->setMaxTimeStep(slider->getValue());
      snprintf(buffer, 256, "Max time step: %d ms", sp->sim->getMaxTimeStep());
      sp->maxTimeStepLabel->setText(buffer);      
    }                                            
    break;
  }
}

// Callback funktion for buttons
void SIMULATIONPANEL::buttonCallback(GUIITEM *gi, void *userData)
{
  BUTTON *b = (BUTTON *)gi;
  SIMULATIONPANEL *sp=(SIMULATIONPANEL *)userData;

  // Select the butoon
  switch (b->getId())
  {
    case SIMULATIONPANEL_STARTSTOP_BUTTON:
    {
      if (sp->sim->getRunState())
      {
        b->setText("Start simulation");
        sp->sim->setRunState(false);
      }
      else
      {
        b->setText("Stop simulation");
        sp->sim->setRunState(true);
      }
    }
    break;

    case SIMULATIONPANEL_RANDOMIZE_BUTTON:
    {
      sp->sim->randomize();
    }
    break;

  }
}

// Callback function for the combobox
void SIMULATIONPANEL::comboBoxCallback(GUIITEM *gi, void *userData)
{
  COMBOBOX *cb = (COMBOBOX *)gi;
  SIMULATIONPANEL *sp=(SIMULATIONPANEL *)userData;

  if (cb->getId()==SIMULATIONPANEL_INTEGRATOR_COMBOBOX)
  {
    sp->sim->setIntegrator(cb->getSelectedStringIndex());
  }
}


// Constructor
SIMULATIONPANEL::SIMULATIONPANEL()
{
  sim=0;
  panel=0;
  nodesLabel=0;
  linksLabel=0;
  startstopButton=0;
  randomizeButton=0;
  electricLabel=0;
  electricSlider=0;
  springLabel=0;
  springSlider=0;
  dampingLabel=0;
  dampingSlider=0;
  maxTimeStepSlider=0;
}


// Creates anew simulation panel.
// Call only ones
SIMULATIONPANEL *SIMULATIONPANEL::create(const GLWINDOW *gw, SGLGUI *gui)
{
  SIMULATIONPANEL *sp=new SIMULATIONPANEL();

  // Create the panel
  sp->panel=gui->addPanel(2, "Simulation (0 fps)", gui->defaultFont(), true, true,
                          gw->getWidth()-200, gw->getHeight()-242, 200, 242);
  sp->panel->setTransparency(0.85);

  // Create node label
  sp->nodesLabel=sp->panel->addLabel(SIMULATIONPANEL_NODES_LABEL, "Nodes: 0",
                                     gui->defaultFont(), 4, 22);

  // Create links label
  sp->linksLabel=sp->panel->addLabel(SIMULATIONPANEL_LINKS_LABEL, "Links: 0",
                                     gui->defaultFont(), 104, 22);

  // Create start/stop button
  sp->startstopButton=sp->panel->addButton(SIMULATIONPANEL_STARTSTOP_BUTTON,
                                           "Start simulation", gui->defaultFont(),
                                           4, 61, 96, 18,
                                           buttonCallback, sp);

  // Create randomize button
  sp->randomizeButton=sp->panel->addButton(SIMULATIONPANEL_RANDOMIZE_BUTTON,
                                           "Randomize", gui->defaultFont(),
                                           104, 61, 80, 18,
                                           buttonCallback, sp);

  // Create electric constant label
  sp->electricLabel=sp->panel->addLabel(SIMULATIONPANEL_ELECTRIC_LABEL,
                                        "Electric constant:",
                                        gui->defaultFont(), 4, 82);

  // Create electric constant slider
  sp->electricSlider=sp->panel->addSlider(SIMULATIONPANEL_ELECTRIC_SLIDER, true,
                                          4, 105, 120, 10, 0, 10000, 1000,
                                          sliderCallback, sp);

  // Create spring constant label
  sp->springLabel=sp->panel->addLabel(SIMULATIONPANEL_SPRING_LABEL,
                                      "Spring constant:",
                                      gui->defaultFont(), 4, 122);

  // Create electric constant slider
  sp->springSlider=sp->panel->addSlider(SIMULATIONPANEL_SPRING_SLIDER, true,
                                        4, 145, 120, 10, 0, 300, 150,
                                        sliderCallback, sp);

  // Create damping constant label
  sp->dampingLabel=sp->panel->addLabel(SIMULATIONPANEL_DAMPING_LABEL,
                                       "Damping constant:",
                                       gui->defaultFont(), 4, 162);

  // Create damping constant slider
  sp->dampingSlider=sp->panel->addSlider(SIMULATIONPANEL_DAMPING_SLIDER, true,
                                         4, 185, 120, 10, 2, 100, 50,
                                         sliderCallback, sp);

  // Create max time step label
  sp->maxTimeStepLabel=sp->panel->addLabel(SIMULATIONPANEL_MAXTIMESTEP_LABEL,
                                       "Max time step:",
                                       gui->defaultFont(), 4, 202);

  // Create max time step slider
  sp->maxTimeStepSlider=sp->panel->addSlider(SIMULATIONPANEL_MAXTIMESTEP_SLIDER, true,
                                         4, 225, 120, 10, 10, 100, 50,
                                         sliderCallback, sp);


  // Create integrator label
  sp->integratorLabel=sp->panel->addLabel(SIMULATIONPANEL_INTEGRATOR_LABEL,
                                          "Integrator:",
                                          gui->defaultFont(), 4, 42);

  // Create integartor combobox
  sp->integratorComboBox=
    sp->panel->addComboBox(SIMULATIONPANEL_INTEGRATOR_COMBOBOX, gui->defaultFont(),
                           70, 40, 116, 18, comboBoxCallback, sp);
  sp->integratorComboBox->addString("Euler's method");
  sp->integratorComboBox->addString("Midpoint method");
  sp->integratorComboBox->addString("Runge Kutta method");


  // return the created simulation panel
  return sp;
}

//
void SIMULATIONPANEL::useSimulation(SIMULATION *sim)
{
  static char buffer[256];

  this->sim=sim;

  snprintf(buffer, 256, "Nodes: %d", sim->getNumberOfNodes());
  nodesLabel->setText(buffer);

  snprintf(buffer, 256, "Edges: %d", sim->getNumberOfEdges());
  linksLabel->setText(buffer);

  startstopButton->setText(sim->getRunState()?"Stop simulation":"Start simulation");

  snprintf(buffer, 256, "Electric constant: %.1f", sim->getElectricConstant());
  electricLabel->setText(buffer);

  electricSlider->setValue((int)(10*sim->getElectricConstant()));

  snprintf(buffer, 256, "Spring constant: %.1f", sim->getSpringConstant());
  springLabel->setText(buffer);

  springSlider->setValue((int)(10*sim->getSpringConstant()));

  snprintf(buffer, 256, "Damping constant: %.1f", sim->getDampingConstant());
  dampingLabel->setText(buffer);

  dampingSlider->setValue((int)(10*sim->getDampingConstant()));


  snprintf(buffer, 256, "Max time step: %d ms", sim->getMaxTimeStep());
  maxTimeStepLabel->setText(buffer);

  maxTimeStepSlider->setValue(sim->getMaxTimeStep());

  integratorComboBox->selectString(sim->getIntegrator());
}

// Sets fps shown in the header
void SIMULATIONPANEL::setFps(float fps)
{
  static char buffer[256];
  snprintf(buffer, 256, "Simulation (%.1f fps)", fps);
  panel->setText(buffer);
}

void SIMULATIONPANEL::setVisibility(bool visible)
{
  panel->setVisibility(visible);     
}


void SIMULATIONPANEL::setPosition(int xPos, int yPos)
{
  panel->setPosition(xPos, yPos);     
}
