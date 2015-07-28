#ifndef __nodepanel_h__
#define __nodepanel_h__

class NODEPANEL
{
  private:
    bool visible;
    NODE *node;
    SIMULATION *sim;
    PANEL *panel;
    LABEL *nameLabel;
    LISTBOX *infoListBox;
    CHECKBOX *showPropCheckBox;
    GROUPBOX *propGroupBox;
    EDITFIELD *keyEditField;
    EDITFIELD *dataEditField;
    BUTTON *setButton;
    BUTTON *addButton;
    BUTTON *delButton; 
    CHECKBOX *visibleCheckBox;
    CHECKBOX *showTagCheckBox;       
    EDITFIELD *tagEditField;
    COMBOBOX *modelComboBox;
    LABEL *sizeLabel;
    SLIDER *sizeSlider;
    SLIDER *redSlider;
    SLIDER *greenSlider;
    SLIDER *blueSlider;
    LABEL *massLabel;
    SLIDER *massSlider;
    LABEL *chargeLabel;
    SLIDER *chargeSlider;
    LABEL *idLabel;
    EDITFIELD *idEditField;
    BUTTON *setIdButton;

    static void checkBoxCallback(GUIITEM *gi, void *userData);
    static void editFieldCallback(GUIITEM *gi, void *userData);
    static void buttonCallback(GUIITEM *gi, void *userData);
    static void listBoxCallback(GUIITEM *gi, void *userData);
    static void sliderCallback(GUIITEM *gi, void *userData);
    static void comboBoxCallback(GUIITEM *gi, void *userData);
              
  public:     
    NODEPANEL();               
    static NODEPANEL *create(GLWINDOW *gw, SGLGUI *gui);
    void setNode(SIMULATION *s, NODE *n);
    NODE *getNode(void);
    void setVisibility(bool visible);
};


#endif
