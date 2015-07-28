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

#include "edgepanel.h"

// Nodepanel guiitem IDs
#define EDGEPANEL_TAG_LABEL 1
#define EDGEPANEL_TAG_EDITFIELD 2
#define EDGEPANEL_COLOR_GROUPBOX 5
#define EDGEPANEL_RED_SLIDER 6
#define EDGEPANEL_GREEN_SLIDER 7
#define EDGEPANEL_BLUE_SLIDER 8
#define EDGEPANEL_INFO_LISTBOX 11
#define EDGEPANEL_WIDTH_SLIDER 12
#define EDGEPANEL_WIDTH_LABEL 13
#define EDGEPANEL_NAME_LABEL 15
#define EDGEPANEL_PROPERTIES_GROUPBOX 16
#define EDGEPANEL_SHOWPROP_CHECKBOX 17
#define EDGEPANEL_KEY_EDITFIELD 18
#define EDGEPANEL_DATA_EDITFIELD 19
#define EDGEPANEL_SET_BUTTON 20
#define EDGEPANEL_ADD_BUTTON 21
#define EDGEPANEL_DEL_BUTTON 22
#define EDGEPANEL_VISIBLE_CHECKBOX 23
#define EDGEPANEL_SHOWTAG_CHECKBOX 24
#define EDGEPANEL_SPRINGCONSTANT_LABEL 25
#define EDGEPANEL_SPRINGCONSTANT_SLIDER 26
#define EDGEPANEL_DIRECTED_CHECKBOX 27
#define EDGEPANEL_SWAPDIR_BUTTON 28

// Callback function for editfield(s)
void EDGEPANEL::editFieldCallback(GUIITEM *gi, void *userData)
{
  EDITFIELD *ef= (EDITFIELD *)gi;
  EDGEPANEL *ep=(EDGEPANEL *)userData;
  EDGE *e=ep->getEdge();

  // Safety check
  if (!e) return;

  // Only one editfield in the panel
  if (ef->getId()==EDGEPANEL_TAG_EDITFIELD)
  {
    e->setTag(ef->getText());
    ep->nameLabel->setText(ef->getText());
  }
}


// Callback funktion for sliders in the nodepanel
void EDGEPANEL::sliderCallback(GUIITEM *gi, void *userData)
{
  SLIDER *s = (SLIDER *)gi;
  EDGEPANEL *ep=(EDGEPANEL *)userData;
  EDGE *e=ep->getEdge();
  static char buffer[256];

  // Safety check
  if (!e) return;

  // Select correct slider
  switch (s->getId())
  {
    case EDGEPANEL_WIDTH_SLIDER:
    {
      e->setWidth(s->getValue());
      snprintf(buffer, 256, "Width (%d):", e->getWidth());
      ep->widthLabel->setText(buffer);
    }
    break;

    case EDGEPANEL_RED_SLIDER:
    {
      e->setColor(s->getValue()/255.0, e->getColor()[1], e->getColor()[2]);
    }
    break;

    case EDGEPANEL_GREEN_SLIDER:
    {
      e->setColor(e->getColor()[0], s->getValue()/255.0, e->getColor()[2]);
    }
    break;

    case EDGEPANEL_BLUE_SLIDER:
    {
      e->setColor(e->getColor()[0], e->getColor()[1], s->getValue()/255.0);
    }
    break;

    case EDGEPANEL_SPRINGCONSTANT_SLIDER:
    {
      e->setSpringConstant(0.1*s->getValue());
      snprintf(buffer, 256, "Spring constant (%.1f):", e->getSpringConstant());
      ep->springConstantLabel->setText(buffer);
    }
    break;
  }
}

// Callback funktion for the listbox
void EDGEPANEL::listBoxCallback(GUIITEM *gi, void *userData)
{
  LISTBOX *lb = (LISTBOX *)gi;
  EDGEPANEL *ep=(EDGEPANEL *)userData;
  EDGE *e=ep->getEdge();

  if (lb->getId()==EDGEPANEL_INFO_LISTBOX)
  {
    int i=lb->getSelectedStringIndex();
    if (i>=0)
    {
      ep->keyEditField->setText(e->getInfoKey(i));
      ep->dataEditField->setText(e->getInfoData(i));
    }
    else
    {
      ep->keyEditField->setText("");
      ep->dataEditField->setText("");
    }
  }
}


// Callback function for the checkboxes
void EDGEPANEL::checkBoxCallback(GUIITEM *gi, void *userData)
{
  CHECKBOX *cb = (CHECKBOX *)gi;
  EDGEPANEL *ep=(EDGEPANEL *)userData;
  EDGE *e=ep->getEdge();

  // Safety check
  if (!e) return;
 
  // Whitch checkbox called this function
  switch (cb->getId())
  {
    case EDGEPANEL_SHOWPROP_CHECKBOX:
    {
      if (cb->isChecked())
      {
        ep->panel->setSize(200, 420);
        ep->propGroupBox->setVisibility(true);
      }
      else
      {
        ep->panel->setSize(200, 160);
        ep->propGroupBox->setVisibility(false);
      }
    }
    break;

    case EDGEPANEL_VISIBLE_CHECKBOX:
    {
      e->setVisibility(cb->isChecked());
    }
    break;

    case EDGEPANEL_SHOWTAG_CHECKBOX:
    {
      e->setTagVisibility(cb->isChecked());
    }
    break;
    
    case EDGEPANEL_DIRECTED_CHECKBOX:
    {
      e->setDirected(cb->isChecked());
    }
    break;
  }
}

// Callback funktion for the buttons
void EDGEPANEL::buttonCallback(GUIITEM *gi, void *userData)
{
  BUTTON *b = (BUTTON *)gi;
  EDGEPANEL *ep=(EDGEPANEL *)userData;

  // Witch button called this function
  switch (b->getId())
  {
    // The add button
    case (EDGEPANEL_ADD_BUTTON):
    {
      if (strlen(ep->keyEditField->getText())>0 &&
          strlen(ep->dataEditField->getText())>0)
      {
        ep->edge->addInfo(ep->keyEditField->getText(),
                          ep->dataEditField->getText());
        ep->setEdge(ep->edge); // ?
      }
    }
    break;
    // The set button
    case (EDGEPANEL_SET_BUTTON):
    {
      int i = ep->infoListBox->getSelectedStringIndex();
      if (i>=0 &&
          strlen(ep->keyEditField->getText())>0 &&
          strlen(ep->dataEditField->getText())>0)
      {
        ep->edge->setInfoKey(i, ep->keyEditField->getText());
        ep->edge->setInfoData(i, ep->dataEditField->getText());
        ep->setEdge(ep->edge);  // ?
      }
    }
    break;
    // The del button
    case (EDGEPANEL_DEL_BUTTON):
    {
      int i = ep->infoListBox->getSelectedStringIndex();
      if (i>=0)
      {
        ep->edge->deleteInfo(i);
        ep->setEdge(ep->edge);  // ?
      }
    }
    break;
    
    case EDGEPANEL_SWAPDIR_BUTTON:
    {
      ep->edge->swapDirection();
    }
    break;
  }
}



// Constructor
EDGEPANEL::EDGEPANEL()
{
  visible=true;
  edge=0;
  panel=0;
  nameLabel=0;
  infoListBox=0;
  showPropCheckBox=0;
  propGroupBox=0;
  keyEditField=0;
  dataEditField=0;
  setButton=0;
  addButton=0;
  delButton=0;
  visibleCheckBox=0;
  showTagCheckBox=0;
  tagEditField=0;
  widthLabel=0;
  widthSlider=0;
  redSlider=0;
  greenSlider=0;
  blueSlider=0;
  springConstantLabel=0;
  springConstantSlider=0;
  directedCheckbox=0;
  swapDirButton=0;
}

// Creates a new nodepanel. Call only once!
EDGEPANEL *EDGEPANEL::create(GLWINDOW *gw, SGLGUI *gui)
{
  EDGEPANEL *ep=0;

  // Allocate memory
  ep=new EDGEPANEL();

  // Create the panel
  ep->panel=gui->addPanel(4, "Edge", gui->defaultFont(),
                          true, true, 0, 20, 200, 160);
  ep->panel->setTransparency(0.85);
  ep->panel->setVisibility(false);

  // Add name label
  ep->nameLabel=ep->panel->addLabel(EDGEPANEL_NAME_LABEL, "Name",
                                    gui->defaultFont(), 4, 22);

  // Add info listbox
  ep->infoListBox=ep->panel->addListBox(EDGEPANEL_INFO_LISTBOX, false, false,
                                        gui->defaultFont(), 4, 40, 192, 100,
                                        listBoxCallback, ep);

  // Add the show properties checkbox
  ep->showPropCheckBox=
    ep->panel->addCheckBox(EDGEPANEL_SHOWPROP_CHECKBOX, false,
                           "Show properties", gui->defaultFont(),
                           4, 142, 16, 16, checkBoxCallback, ep);

  // Add properties groupbox
  ep->propGroupBox=
    ep->panel->addGroupBox(EDGEPANEL_PROPERTIES_GROUPBOX, "Properties",
                           gui->defaultFont(), 4, 168, 192, 248);
  ep->propGroupBox->setVisibility(false);

  // Add key label
  ep->propGroupBox->addLabel(0, "Key:", gui->defaultFont(), 2, 8);
  ep->keyEditField=
    ep->propGroupBox->addEditField(EDGEPANEL_KEY_EDITFIELD, "", gui->defaultFont(),
                                   30, 7, 160, 18, 256, 0, 0);

  // Add data label
  ep->propGroupBox->addLabel(0, "Data:", gui->defaultFont(), 2, 28);
  ep->dataEditField=
    ep->propGroupBox->addEditField(EDGEPANEL_DATA_EDITFIELD, "", gui->defaultFont(),
                                   30, 27, 160, 18, 256, 0, 0);

  // Add set button
  ep->setButton=
    ep->propGroupBox->addButton(EDGEPANEL_SET_BUTTON, "Set",
                                gui->defaultFont(), 10, 47, 50, 18,
                                buttonCallback, ep);
  // Add add button
  ep->addButton=
    ep->propGroupBox->addButton(EDGEPANEL_ADD_BUTTON, "Add",
                                gui->defaultFont(), 70, 47, 50, 18,
                                buttonCallback, ep);

  // Add del button
  ep->delButton=
    ep->propGroupBox->addButton(EDGEPANEL_DEL_BUTTON, "Del",
                                gui->defaultFont(), 130, 47, 50, 18,
                                buttonCallback, ep);

  // Add "visible"-checkbox
  ep->visibleCheckBox=
    ep->propGroupBox->addCheckBox(EDGEPANEL_VISIBLE_CHECKBOX, true,
                                  "Visible", gui->defaultFont(),
                                   4, 68, 16, 16, checkBoxCallback, ep);

  // Add show tag checkbox
  ep->showTagCheckBox=
    ep->propGroupBox->addCheckBox(EDGEPANEL_SHOWTAG_CHECKBOX, true,
                                  "Show tag", gui->defaultFont(),
                                   100, 68, 16, 16, checkBoxCallback, ep);

  // add tag label
  ep->propGroupBox->addLabel(EDGEPANEL_TAG_LABEL, "Tag:", gui->defaultFont(), 4, 88);

  // Add tag editfield
  ep->tagEditField=ep->propGroupBox->addEditField(EDGEPANEL_TAG_EDITFIELD,
                                           "", gui->defaultFont(),
                                           40, 87, 150, 18, 64,
                                           editFieldCallback, ep);


  // Add width label
  ep->widthLabel=
    ep->propGroupBox->addLabel(EDGEPANEL_WIDTH_LABEL, "Width ():",
                               gui->defaultFont(), 4, 108);

  // Add width slider
  ep->widthSlider=
    ep->propGroupBox->addSlider(EDGEPANEL_WIDTH_SLIDER, true, 70, 108, 110, 16,
                                1, 10, 1, sliderCallback, ep);

  // The color selector groupbox
  GROUPBOX *gb=ep->propGroupBox->addGroupBox(EDGEPANEL_COLOR_GROUPBOX,
                                             "Color", gui->defaultFont(),
                                             4, 130, 184, 50);
  // Color selector sliders
  ep->redSlider=
    gb->addSlider(EDGEPANEL_RED_SLIDER, true, 4, 6, 176, 12,
                  0, 255, 255, sliderCallback, ep);
  ep->redSlider->setFillColor2(1, 0, 0);

  ep->greenSlider=
    gb->addSlider(EDGEPANEL_GREEN_SLIDER, true, 4, 20, 176, 12,
                  0, 255, 255, sliderCallback, ep);
  ep->greenSlider->setFillColor2(0, 1, 0);

  ep->blueSlider=
    gb->addSlider(EDGEPANEL_BLUE_SLIDER, true, 4, 34, 176, 12,
                  0, 255, 255, sliderCallback, ep);
  ep->blueSlider->setFillColor2(0, 0, 1);

  // craete the spring constant label
  ep->springConstantLabel=
    ep->propGroupBox->addLabel(EDGEPANEL_SPRINGCONSTANT_LABEL, "Spring constant ():",
                               gui->defaultFont(), 4, 182);

  // Create teh spring constant slider
  ep->springConstantSlider=
    ep->propGroupBox->addSlider(EDGEPANEL_SPRINGCONSTANT_SLIDER, true, 10, 202, 156, 16,
                  1, 100, 10, sliderCallback, ep);

  // Create the directed checkbox
  ep->directedCheckbox=
    ep->propGroupBox->addCheckBox(EDGEPANEL_DIRECTED_CHECKBOX, true,
                                  "Directed", gui->defaultFont(),
                                   8, 224, 16, 16, checkBoxCallback, ep);
  // Add swap direction button
  ep->setButton=
    ep->propGroupBox->addButton(EDGEPANEL_SWAPDIR_BUTTON, "Swap direction",
                                gui->defaultFont(), 84, 223, 90, 18,
                                buttonCallback, ep);



  // Return the created panel
  return ep;
}

//
void EDGEPANEL::setEdge(EDGE *e)
{
  static char buffer[256];

  edge=e;
  if (e)
  {
    nameLabel->setText(e->getTag());

    infoListBox->removeAll();
    for (int i=0; i<e->getNumberOfInfos(); i++)
      infoListBox->addString(e->getInfo(i));

    keyEditField->setText("");
    dataEditField->setText("");

    visibleCheckBox->setCheck(e->getVisibility());
    showTagCheckBox->setCheck(e->getTagVisibility());

    tagEditField->setText(e->getTag());

    snprintf(buffer, 256, "Width (%d):", e->getWidth());
    widthLabel->setText(buffer);

    widthSlider->setValue(e->getWidth());

    redSlider->setValue((int)(255*e->getColor()[0]));
    greenSlider->setValue((int)(255*e->getColor()[1]));
    blueSlider->setValue((int)(255*e->getColor()[2]));

    snprintf(buffer, 256, "Spring constant (%.1f):", e->getSpringConstant());
    springConstantLabel->setText(buffer);

    springConstantSlider->setValue((int)(10*e->getSpringConstant()));

    directedCheckbox->setCheck(e->getDirected());

    if (visible) panel->setVisibility(true);
    else panel->setVisibility(false);
  }
  else
  {
    panel->setVisibility(false);
  }
}

EDGE *EDGEPANEL::getEdge(void)
{
  return edge;
}

void EDGEPANEL::setVisibility(bool visible)
{
  this->visible=visible;
  if (visible && edge) panel->setVisibility(true);     
  else panel->setVisibility(false);
}
