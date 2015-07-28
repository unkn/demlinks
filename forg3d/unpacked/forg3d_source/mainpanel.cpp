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
#include "nodepanel.h"
#include "simulationpanel.h"
#include "mainpanel.h"
#include "edgepanel.h"
#include "forg3d.h"



#define MAINPANEL_SHOWNODES_CHECKBOX 1
#define MAINPANEL_SHOWNODETAGS_CHECKBOX 2
#define MAINPANEL_SHOWLINKS_CHECKBOX 3
#define MAINPANEL_SHOWLINKTAGS_CHECKBOX 4
#define MAINPANEL_LITNODES_CHECKBOX 5
#define MAINPANEL_SPECULAR_CHECKBOX 6
#define MAINPANEL_RED_SLIDER 7
#define MAINPANEL_GREEN_SLIDER 8
#define MAINPANEL_BLUE_SLIDER 9
#define MAINPANEL_BGCOLOR_GROUPBOX 10
#define MAINPANEL_NTRED_SLIDER 11
#define MAINPANEL_NTGREEN_SLIDER 12
#define MAINPANEL_NTBLUE_SLIDER 13
#define MAINPANEL_NTCOLOR_GROUPBOX 14
#define MAINPANEL_LTRED_SLIDER 15
#define MAINPANEL_LTGREEN_SLIDER 16
#define MAINPANEL_LTBLUE_SLIDER 17
#define MAINPANEL_LTCOLOR_GROUPBOX 18


// Callback funktion for chackboxes fot the main program panel
void MAINPANEL::checkBoxCallback(GUIITEM *gi, void *userData)
{
  CHECKBOX *cb = (CHECKBOX *)gi;
  MAINPANEL *mp=(MAINPANEL *)userData;

  switch (cb->getId())
  {
    // "Show nodes"-checkbox
    case MAINPANEL_SHOWNODES_CHECKBOX:
    {
      mp->h->setNodeVisibility(cb->isChecked());
    }
    break;
    // "Show node tags"-checkbox
    case MAINPANEL_SHOWNODETAGS_CHECKBOX:
    {
      mp->h->setNodeTagVisibility(cb->isChecked());
    }
    break;
    // "Show links" checkbox
    case MAINPANEL_SHOWLINKS_CHECKBOX:
    {
      mp->h->setEdgeVisibility(cb->isChecked());
    }
    break;
    // "Show link tags"-checkbox
    case MAINPANEL_SHOWLINKTAGS_CHECKBOX:
    {
      mp->h->setEdgeTagVisibility(cb->isChecked());
    }
    break;
    // "Lit nodes"-checkbox
    case MAINPANEL_LITNODES_CHECKBOX:
    {
      mp->h->setNodeLighting(cb->isChecked());
    }
    break;
    // "specular"-checkbox
    case MAINPANEL_SPECULAR_CHECKBOX:
    {
      mp->h->setSpecular(cb->isChecked());
    }
    break;
  }
}

// Callback funktion for the sliders in the main panel
void MAINPANEL::sliderCallback(GUIITEM *gi, void *userData)
{
  SLIDER *s = (SLIDER *)gi;
  MAINPANEL *mp=(MAINPANEL *)userData;


  switch (s->getId())
  {
    case MAINPANEL_RED_SLIDER:
    {
      mp->h->setBgColor(s->getValue()/255.0, mp->h->getBgColor()[1], mp->h->getBgColor()[2]);
    }
    break;

    case MAINPANEL_GREEN_SLIDER:
    {
      mp->h->setBgColor(mp->h->getBgColor()[0], s->getValue()/255.0, mp->h->getBgColor()[2]);
    }
    break;

    case MAINPANEL_BLUE_SLIDER:
    {
      mp->h->setBgColor(mp->h->getBgColor()[0], mp->h->getBgColor()[1], s->getValue()/255.0);
    }
    break;


    case MAINPANEL_NTRED_SLIDER:
    {
      mp->h->setNtColor(s->getValue()/255.0, mp->h->getNtColor()[1], mp->h->getNtColor()[2]);
    }
    break;

    case MAINPANEL_NTGREEN_SLIDER:
    {
      mp->h->setNtColor(mp->h->getNtColor()[0], s->getValue()/255.0, mp->h->getNtColor()[2]);
    }
    break;

    case MAINPANEL_NTBLUE_SLIDER:
    {
      mp->h->setNtColor(mp->h->getNtColor()[0], mp->h->getNtColor()[1], s->getValue()/255.0);
    }
    break;
    

    case MAINPANEL_LTRED_SLIDER:
    {
      mp->h->setLtColor(s->getValue()/255.0, mp->h->getLtColor()[1], mp->h->getLtColor()[2]);
    }
    break;

    case MAINPANEL_LTGREEN_SLIDER:
    {
      mp->h->setLtColor(mp->h->getLtColor()[0], s->getValue()/255.0, mp->h->getLtColor()[2]);
    }
    break;

    case MAINPANEL_LTBLUE_SLIDER:
    {
      mp->h->setLtColor(mp->h->getLtColor()[0], mp->h->getLtColor()[1], s->getValue()/255.0);
    }
    break;

  }
}



// Constructor
MAINPANEL::MAINPANEL()
{
  panel=0;
  showNodesCheckBox=0;
  showNodeTagsCheckBox=0;
  showLinksCheckBox=0;
  showLinkTagsCheckBox=0;
  litNodesCheckBox=0;
  specularCheckBox=0;
  redSlider=0;
  greenSlider=0;
  blueSlider=0;  
}


// Creates a new MAINPANEL
MAINPANEL *MAINPANEL::create(FORG3D *h, const GLWINDOW *gw, SGLGUI *gui)
{
  MAINPANEL *mp=new MAINPANEL();
  if (!mp) return 0;

  mp->h=h;

  // Create panel
  mp->panel=gui->addPanel(1, "Main program", gui->defaultFont(),
                          true, true, gw->getWidth()-200, 20, 200, 300);
  mp->panel->setTransparency(0.85);

  // Create "show nodes"-checkbox
  mp->showNodesCheckBox=
    mp->panel->addCheckBox(MAINPANEL_SHOWNODES_CHECKBOX, true,
                           "Show nodes", gui->defaultFont(),
                           4, 22, 16, 16, checkBoxCallback, mp);

  // Create "show node tags"-checkbox
  mp->showNodeTagsCheckBox=
    mp->panel->addCheckBox(MAINPANEL_SHOWNODETAGS_CHECKBOX,  false,
                           "Show node tags", gui->defaultFont(), 100, 22, 16, 16,
                           checkBoxCallback, mp);

  // Create "show links"-checkbox
  mp->showLinksCheckBox=
    mp->panel->addCheckBox(MAINPANEL_SHOWLINKS_CHECKBOX, true,
                           "Show edges", gui->defaultFont(), 4, 42, 16, 16,
                           checkBoxCallback, mp);

  // Create "show link tags"-checkbox
  mp->showLinkTagsCheckBox=
    mp->panel->addCheckBox(MAINPANEL_SHOWLINKTAGS_CHECKBOX, false,
                           "Show edge tags", gui->defaultFont(), 100, 42, 16, 16,
                           checkBoxCallback, mp);

  // Create "Lit nodes"-checkbox
  mp->litNodesCheckBox=
    mp->panel->addCheckBox(MAINPANEL_LITNODES_CHECKBOX, true,
                           "Lit nodes", gui->defaultFont(), 4, 62, 16, 16,
                           checkBoxCallback, mp);

  // Create "Specular"-checkbox
  mp->specularCheckBox=
    mp->panel->addCheckBox(MAINPANEL_SPECULAR_CHECKBOX, true,
                           "Specular", gui->defaultFont(), 100, 62, 16, 16,
                           checkBoxCallback, mp);

  GROUPBOX *gb=mp->panel->addGroupBox(MAINPANEL_BGCOLOR_GROUPBOX,
                                      "Background color", gui->defaultFont(),
                                      4, 88, 192, 38);

  mp->redSlider=
    gb->addSlider(MAINPANEL_RED_SLIDER, true, 8, 6, 176, 9,
                  0, 255, 255, sliderCallback, mp);
  mp->redSlider->setFillColor2(1, 0, 0);

  mp->greenSlider=
    gb->addSlider(MAINPANEL_GREEN_SLIDER, true, 8, 16, 176, 9,
                  0, 255, 255, sliderCallback, mp);
  mp->greenSlider->setFillColor2(0, 1, 0);

  mp->blueSlider=
    gb->addSlider(MAINPANEL_BLUE_SLIDER, true, 8, 26, 176, 9,
                  0, 255, 255, sliderCallback, mp);
  mp->blueSlider->setFillColor2(0, 0, 1);

  gb=mp->panel->addGroupBox(MAINPANEL_NTCOLOR_GROUPBOX,
                            "Node tag color", gui->defaultFont(),
                            4, 134, 192, 38);

  mp->ntRedSlider=
    gb->addSlider(MAINPANEL_NTRED_SLIDER, true, 8, 6, 176, 9,
                  0, 255, 0, sliderCallback, mp);
  mp->ntRedSlider->setFillColor2(1, 0, 0);

  mp->ntGreenSlider=
    gb->addSlider(MAINPANEL_NTGREEN_SLIDER, true, 8, 16, 176, 9,
                  0, 255, 0, sliderCallback, mp);
  mp->ntGreenSlider->setFillColor2(0, 1, 0);

  mp->ntBlueSlider=
    gb->addSlider(MAINPANEL_NTBLUE_SLIDER, true, 8, 26, 176, 9,
                  0, 255, 0, sliderCallback, mp);
  mp->ntBlueSlider->setFillColor2(0, 0, 1);



  gb=mp->panel->addGroupBox(MAINPANEL_LTCOLOR_GROUPBOX,
                            "Edge tag color", gui->defaultFont(),
                            4, 180, 192, 38);

  mp->ltRedSlider=
    gb->addSlider(MAINPANEL_LTRED_SLIDER, true, 8, 6, 176, 9,
                  0, 255, 0, sliderCallback, mp);
  mp->ltRedSlider->setFillColor2(1, 0, 0);

  mp->ltGreenSlider=
    gb->addSlider(MAINPANEL_LTGREEN_SLIDER, true, 8, 16, 176, 9,
                  0, 255, 0, sliderCallback, mp);
  mp->ltGreenSlider->setFillColor2(0, 1, 0);

  mp->ltBlueSlider=
    gb->addSlider(MAINPANEL_LTBLUE_SLIDER, true, 8, 26, 176, 9,
                  0, 255, 0, sliderCallback, mp);
  mp->ltBlueSlider->setFillColor2(0, 0, 1);

  // Create renderer label
  mp->panel->addLabel(1, "Renderer:", gui->defaultFont(), 4, 226);
  LISTBOX *lb=mp->panel->addListBox(1, false, false, gui->defaultFont(), 4, 246, 192, 50, 0, 0);
  lb->addString((const char *)glGetString(GL_RENDERER));
  lb->addString((const char *)glGetString(GL_VENDOR));
  lb->addString((const char *)glGetString(GL_VERSION));
  lb->setFillColor2(1,1,1);

  // Return the created panel
  return mp;
}

void MAINPANEL::setVisibility(bool visible)
{
  panel->setVisibility(visible);     
}

void MAINPANEL::setPosition(int xPos, int yPos)
{
  panel->setPosition(xPos, yPos);     
}
