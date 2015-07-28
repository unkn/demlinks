#ifndef __linkpanel_h__
#define __linkpanel_h__

class EDGEPANEL
{
  private:
    bool visible;
    EDGE *edge;
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
    LABEL *widthLabel;
    SLIDER *widthSlider;
    SLIDER *redSlider;
    SLIDER *greenSlider;
    SLIDER *blueSlider;
    LABEL *springConstantLabel;
    SLIDER *springConstantSlider;   
    CHECKBOX *directedCheckbox;
    BUTTON *swapDirButton;        
    EDGEPANEL();               
    static void checkBoxCallback(GUIITEM *gi, void *userData);
    static void editFieldCallback(GUIITEM *gi, void *userData);
    static void buttonCallback(GUIITEM *gi, void *userData);
    static void listBoxCallback(GUIITEM *gi, void *userData);
    static void sliderCallback(GUIITEM *gi, void *userData);
  public:     
    static EDGEPANEL *create(GLWINDOW *gw, SGLGUI *gui);
    void setEdge(EDGE *e);
    EDGE *getEdge(void);
    void setVisibility(bool visible);
};


#endif
