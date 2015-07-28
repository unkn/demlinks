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

#include "nodepanel.h"

// Nodepanel guiitem IDs
#define NODEPANEL_TAG_LABEL 1
#define NODEPANEL_TAG_EDITFIELD 2
#define NODEPANEL_MODEL_LABEL 3
#define NODEPANEL_MODEL_COMBOBOX 4
#define NODEPANEL_COLOR_GROUPBOX 5
#define NODEPANEL_RED_SLIDER 6
#define NODEPANEL_GREEN_SLIDER 7
#define NODEPANEL_BLUE_SLIDER 8
#define NODEPANEL_MASS_SLIDER 9
#define NODEPANEL_CHARGE_SLIDER 10
#define NODEPANEL_INFO_LISTBOX 11
#define NODEPANEL_SIZE_SLIDER 12
#define NODEPANEL_SIZE_LABEL 13
#define NODEPANEL_MASS_LABEL 14
#define NODEPANEL_NAME_LABEL 15
#define NODEPANEL_PROPERTIES_GROUPBOX 16
#define NODEPANEL_SHOWPROP_CHECKBOX 17
#define NODEPANEL_KEY_EDITFIELD 18
#define NODEPANEL_DATA_EDITFIELD 19
#define NODEPANEL_SET_BUTTON 20
#define NODEPANEL_ADD_BUTTON 21
#define NODEPANEL_DEL_BUTTON 22
#define NODEPANEL_VISIBLE_CHECKBOX 23
#define NODEPANEL_SHOWTAG_CHECKBOX 24
#define NODEPANEL_CHARGE_LABEL 25
#define NODEPANEL_ID_LABEL 26
#define NODEPANEL_ID_EDITFIELD 27
#define NODEPANEL_SETID_BUTTON 28

// Callback function for editfield(s)
void NODEPANEL::editFieldCallback(GUIITEM *gi, void *userData)
{
  EDITFIELD *ef = (EDITFIELD *)gi;
  NODEPANEL *np=(NODEPANEL *)userData;
  NODE *n=np->getNode();

  // Safety check
  if (!n) return;

  // Only one editfield in the panel
  if (ef->getId()==NODEPANEL_TAG_EDITFIELD)
  {
    n->setTag(ef->getText());
    np->nameLabel->setText(ef->getText());
  }
}

// Callback funktion for combobox
void NODEPANEL::comboBoxCallback(GUIITEM *gi, void *userData)
{
  COMBOBOX *cb = (COMBOBOX *)gi;
  NODEPANEL *np=(NODEPANEL *)userData;
  NODE *n=np->getNode();

  // Safety check
  if (!n) return;

  // Only one editfield in the panel
  if (cb->getId()==NODEPANEL_MODEL_COMBOBOX)
  {
    n->setModel(cb->getSelectedStringIndex());
  }
}

// Callback funktion for sliders in the nodepanel
void NODEPANEL::sliderCallback(GUIITEM *gi, void *userData)
{
  SLIDER *s = (SLIDER *)gi;
  NODEPANEL *np=(NODEPANEL *)userData;
  NODE *n=np->getNode();
  static char buffer[256];

  // Safety check
  if (!n) return;

  // Select correct slider
  switch (s->getId())
  {
    case NODEPANEL_SIZE_SLIDER:
    {
      n->setSize(0.1*s->getValue());
      snprintf(buffer, 256, "Size (%.1f):", n->getSize());
      np->sizeLabel->setText(buffer);
    }
    break;

    case NODEPANEL_RED_SLIDER:
    {
      n->setColor(s->getValue()/255.0, n->getColor()[1], n->getColor()[2]);
    }
    break;

    case NODEPANEL_GREEN_SLIDER:
    {
      n->setColor(n->getColor()[0], s->getValue()/255.0, n->getColor()[2]);
    }
    break;

    case NODEPANEL_BLUE_SLIDER:
    {
      n->setColor(n->getColor()[0], n->getColor()[1], s->getValue()/255.0);
    }
    break;

    case NODEPANEL_MASS_SLIDER:
    {
      n->setInverseMass(1.0/(0.1*s->getValue()));
      snprintf(buffer, 256, "Mass (%.1f):", 1.0/n->getInverseMass());
      np->massLabel->setText(buffer);
    }
    break;

    case NODEPANEL_CHARGE_SLIDER:
    {
      n->setCharge(0.1*s->getValue());
      snprintf(buffer, 256, "Charge (%.1f):", n->getCharge());
      np->chargeLabel->setText(buffer);
    }
    break;
  }
}

// Callback funktion for the listbox
void NODEPANEL::listBoxCallback(GUIITEM *gi, void *userData)
{
  LISTBOX *lb = (LISTBOX *)gi;
  NODEPANEL *np=(NODEPANEL *)userData;
  NODE *n=np->getNode();

  if (lb->getId()==NODEPANEL_INFO_LISTBOX)
  {
    int i=lb->getSelectedStringIndex();
    if (i>=0)
    {
      np->keyEditField->setText(n->getInfoKey(i));
      np->dataEditField->setText(n->getInfoData(i));
    }
    else
    {
      np->keyEditField->setText("");
      np->dataEditField->setText("");
    }
  }
}


// Callback function for the checkboxes
void NODEPANEL::checkBoxCallback(GUIITEM *gi, void *userData)
{
  CHECKBOX *cb = (CHECKBOX *)gi;
  NODEPANEL *np=(NODEPANEL *)userData;
  NODE *n=np->getNode();

  // Safety check
  if (!n) return;

  // select checkbox
  switch (cb->getId())
  {
    case NODEPANEL_SHOWPROP_CHECKBOX:
    {
      if (cb->isChecked())
      {
        np->panel->setSize(200, 436);
        np->propGroupBox->setVisibility(true);
      }
      else
      {
        np->panel->setSize(200, 160);
        np->propGroupBox->setVisibility(false);
      }
    }
    break;

    case NODEPANEL_VISIBLE_CHECKBOX:
    {
      n->setVisibility(cb->isChecked());
    }
    break;

    case NODEPANEL_SHOWTAG_CHECKBOX:
    {
      n->setTagVisibility(cb->isChecked());
    }
    break;
  }
}

// Callback function for buttons
void NODEPANEL::buttonCallback(GUIITEM *gi, void *userData)
{
  BUTTON *b = (BUTTON *)gi;
  NODEPANEL *np=(NODEPANEL *)userData;

  // witch button called this function?
  switch (b->getId())
  {
    // The add-button
    case NODEPANEL_ADD_BUTTON:
    {
      if (strlen(np->keyEditField->getText())>0 &&
          strlen(np->dataEditField->getText())>0)
      {
        np->node->addInfo(np->keyEditField->getText(),
                          np->dataEditField->getText());
        np->setNode(np->sim, np->node); // ?
      }
    }
    break;
    // The set-button
    case NODEPANEL_SET_BUTTON:
    {
      int i=np->infoListBox->getSelectedStringIndex();
      if (i>=0 &&
          strlen(np->keyEditField->getText())>0 &&
          strlen(np->dataEditField->getText())>0)
      {
        np->node->setInfoKey(i, np->keyEditField->getText());
        np->node->setInfoData(i, np->dataEditField->getText());
        np->setNode(np->sim, np->node);  // ?
      }
    }
    break;
    // The del-button
    case NODEPANEL_DEL_BUTTON:
    {
      int i=np->infoListBox->getSelectedStringIndex();
      if (i>=0)
      {
        np->node->deleteInfo(i);
        np->setNode(np->sim, np->node);  // ?
      }
    }
    break;
    // The set id button
    case NODEPANEL_SETID_BUTTON:
    {
      NODE *tn = np->sim->getNodeById(np->idEditField->getText());
      if (!tn)
      {
        np->node->setId(np->idEditField->getText());         
      }
      else
      {
        if (tn!=np->node) np->idEditField->setText("ID already in use");    
      }
    }
    break;
  }
}



// Constructor. Just initializes variables.
// Use create-method to create an instance
NODEPANEL::NODEPANEL()
{
  visible=true;
  node=0;
  sim=0;
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
  modelComboBox=0;
  sizeLabel=0;
  sizeSlider=0;
  redSlider=0;
  greenSlider=0;
  blueSlider=0;
  massLabel=0;
  massSlider=0;
  chargeLabel=0;
  chargeSlider=0;
  idLabel=0;
  idEditField=0;
  setIdButton=0;
  
}

// Creates a new nodepanel. Call only once!
NODEPANEL *NODEPANEL::create(GLWINDOW *gw, SGLGUI *gui)
{
  NODEPANEL *np=0;

  // Allocate memory
  np=new NODEPANEL();

  // Create the panel
  np->panel=gui->addPanel(3, "Node", gui->defaultFont(),
                        true, true, 0, 20, 200, 160);
  np->panel->setTransparency(0.85);
  np->panel->setVisibility(false);

  // Add name label
  np->nameLabel=np->panel->addLabel(NODEPANEL_NAME_LABEL, "Name",
                                    gui->defaultFont(), 4, 22);

  // Add info listbox
  np->infoListBox=np->panel->addListBox(NODEPANEL_INFO_LISTBOX, false, false,
                                        gui->defaultFont(), 4, 40, 192, 100,
                                        listBoxCallback, np);

  // Add the show properties checkbox
  np->showPropCheckBox=
    np->panel->addCheckBox(NODEPANEL_SHOWPROP_CHECKBOX, false,
                           "Show properties", gui->defaultFont(),
                           4, 142, 16, 16, checkBoxCallback, np);

  // Add properties groupbox
  np->propGroupBox=
    np->panel->addGroupBox(NODEPANEL_PROPERTIES_GROUPBOX, "Properties",
                           gui->defaultFont(), 4, 168, 192, 262);
  np->propGroupBox->setVisibility(false);

  // Add key label
  np->propGroupBox->addLabel(0, "Key:", gui->defaultFont(), 2, 8);
  np->keyEditField=
    np->propGroupBox->addEditField(NODEPANEL_KEY_EDITFIELD, "", gui->defaultFont(),
                                   30, 7, 160, 18, 256, 0, 0);

  // Add data label
  np->propGroupBox->addLabel(0, "Data:", gui->defaultFont(), 2, 28);
  np->dataEditField=
    np->propGroupBox->addEditField(NODEPANEL_DATA_EDITFIELD, "", gui->defaultFont(),
                                   30, 27, 160, 18, 256, 0, 0);

  // Add set button
  np->setButton=
    np->propGroupBox->addButton(NODEPANEL_SET_BUTTON, "Set",
                                gui->defaultFont(), 10, 47, 50, 18,
                                buttonCallback, np);
  // Add add button
  np->addButton=
    np->propGroupBox->addButton(NODEPANEL_ADD_BUTTON, "Add",
                                gui->defaultFont(), 70, 47, 50, 18,
                                buttonCallback, np);

  // Add del button
  np->delButton=
    np->propGroupBox->addButton(NODEPANEL_DEL_BUTTON, "Del",
                                gui->defaultFont(), 130, 47, 50, 18,
                                buttonCallback, np);

  // Add "visible"-checkbox
  np->visibleCheckBox=
    np->propGroupBox->addCheckBox(NODEPANEL_VISIBLE_CHECKBOX, true,
                                  "Visible", gui->defaultFont(),
                                   4, 68, 16, 16, checkBoxCallback, np);

  // Add show tag checkbox
  np->showTagCheckBox=
    np->propGroupBox->addCheckBox(NODEPANEL_SHOWTAG_CHECKBOX, true,
                                  "Show tag", gui->defaultFont(),
                                   100, 68, 16, 16, checkBoxCallback, np);

  // add tag label
  np->propGroupBox->addLabel(NODEPANEL_TAG_LABEL, "Tag:", gui->defaultFont(), 4, 88);

  // tag edit field
  np->tagEditField=np->propGroupBox->addEditField(NODEPANEL_TAG_EDITFIELD,
                                           "", gui->defaultFont(),
                                           40, 87, 150, 18, 256,
                                           editFieldCallback, np);

  // Add model label
  np->propGroupBox->addLabel(NODEPANEL_MODEL_LABEL, "Model:", gui->defaultFont(), 4, 107);


  // Add size label
  np->sizeLabel=
    np->propGroupBox->addLabel(NODEPANEL_SIZE_LABEL, "Size ():",
                               gui->defaultFont(), 4, 128);

  // Add size slider
  np->sizeSlider=
    np->propGroupBox->addSlider(NODEPANEL_SIZE_SLIDER, true, 70, 128, 110, 16,
                                1, 100, 10, sliderCallback, np);


  // Add color groupbox
  GROUPBOX *gb=np->propGroupBox->addGroupBox(NODEPANEL_COLOR_GROUPBOX,
                                             "Color", gui->defaultFont(),
                                             4, 150, 184, 50);
  // Add the color sliders
  np->redSlider=
    gb->addSlider(NODEPANEL_RED_SLIDER, true, 4, 6, 176, 12,
                            0, 255, 255, sliderCallback, np);
  np->redSlider->setFillColor2(1, 0, 0);

  np->greenSlider=
    gb->addSlider(NODEPANEL_GREEN_SLIDER, true, 4, 20, 176, 12,
                                0, 255, 255, sliderCallback, np);
  np->greenSlider->setFillColor2(0, 1, 0);

  np->blueSlider=
    gb->addSlider(NODEPANEL_BLUE_SLIDER, true, 4, 34, 176, 12,
                                0, 255, 255, sliderCallback, np);
  np->blueSlider->setFillColor2(0, 0, 1);

  // Add charge label
  np->chargeLabel=
    np->propGroupBox->addLabel(NODEPANEL_CHARGE_LABEL, "Charge ():",
                               gui->defaultFont(), 4, 202);

  // Add charge label
  np->chargeSlider=
    np->propGroupBox->addSlider(NODEPANEL_CHARGE_SLIDER, true, 80, 202, 100, 16,
                                1, 100, 10, sliderCallback, np);

  // Add mass label
  np->massLabel=
    np->propGroupBox->addLabel(NODEPANEL_MASS_LABEL, "Mass ():",
                               gui->defaultFont(), 4, 222);

  // Add mass label
  np->massSlider=
    np->propGroupBox->addSlider(NODEPANEL_MASS_SLIDER, true, 80, 222, 100, 16,
                                1, 100, 10, sliderCallback, np);
                                
  // Add Id label
  np->idLabel=
    np->propGroupBox->addLabel(NODEPANEL_ID_LABEL, "ID:",
                               gui->defaultFont(), 4, 242);
    
  // Add Id edit field
  np->idEditField=
    np->propGroupBox->addEditField(NODEPANEL_ID_EDITFIELD, "", gui->defaultFont(),
                                   30, 241, 120, 18, 256, 0, 0);

  // Add set id button
  np->setIdButton=
    np->propGroupBox->addButton(NODEPANEL_SETID_BUTTON, "Set",
                                gui->defaultFont(), 160, 241, 25, 18,
                                buttonCallback, np);
                              
                                

  // Add model select combobox
  np->modelComboBox=np->propGroupBox->addComboBox(NODEPANEL_MODEL_COMBOBOX,
                                           gui->defaultFont(),
                                           40, 107, 150, 18,
                                           comboBoxCallback, np);
  np->modelComboBox->addString("Tetrahedron");
  np->modelComboBox->addString("Cube");
  np->modelComboBox->addString("Octahedron");
  np->modelComboBox->addString("Dodecahedron");
  np->modelComboBox->addString("Icosahedron");


  // Return the created panel
  return np;
}

//
void NODEPANEL::setNode(SIMULATION *s, NODE *n)
{
  static char buffer[256];

  sim=s;
  node=n;
  
  if (n)
  {
    nameLabel->setText(n->getTag());

    infoListBox->removeAll();
    for (int i=0; i<n->getNumberOfInfos(); i++)
      infoListBox->addString(n->getInfo(i));

    keyEditField->setText("");
    dataEditField->setText("");

    visibleCheckBox->setCheck(n->getVisibility());
    showTagCheckBox->setCheck(n->getTagVisibility());

    tagEditField->setText(n->getTag());

    modelComboBox->selectString(n->getModel());

    snprintf(buffer, 256, "Size (%.1f):", n->getSize());
    sizeLabel->setText(buffer);

    sizeSlider->setValue((int)(10*n->getSize()));

    redSlider->setValue((int)(255*n->getColor()[0]));
    greenSlider->setValue((int)(255*n->getColor()[1]));
    blueSlider->setValue((int)(255*n->getColor()[2]));

    snprintf(buffer, 256, "Mass (%.1f):", 1.0/n->getInverseMass());
    massLabel->setText(buffer);
    massSlider->setValue((int)(10*(1.0/n->getInverseMass())));

    snprintf(buffer, 256, "Charge (%.1f):", n->getCharge());
    chargeLabel->setText(buffer);
    chargeSlider->setValue((int)(10*n->getCharge()));
    
    idEditField->setText(n->getId());

    if (visible) panel->setVisibility(true);
    else panel->setVisibility(false);
  }
  else
  {
    panel->setVisibility(false);
  }
}

NODE *NODEPANEL::getNode(void)
{
  return node;
}


void NODEPANEL::setVisibility(bool visible)
{
  this->visible=visible;

  if (visible && node) panel->setVisibility(true);
  else panel->setVisibility(false);
}
