#ifndef __simulationpanel_h__
#define __simulationpanel_h__

class SIMULATIONPANEL
{
  private:
    SIMULATION *sim;
    PANEL *panel;
    LABEL *nodesLabel;
    LABEL *linksLabel;
    BUTTON *startstopButton;
    BUTTON *randomizeButton;
    LABEL *electricLabel;
    SLIDER *electricSlider;
    LABEL *springLabel;
    SLIDER *springSlider;
    LABEL *dampingLabel;
    SLIDER *dampingSlider;
    LABEL *maxTimeStepLabel;
    SLIDER *maxTimeStepSlider;
    LABEL *integratorLabel;
    COMBOBOX *integratorComboBox;

    SIMULATIONPANEL();          
    static void sliderCallback(GUIITEM *gi, void *userData);
    static void buttonCallback(GUIITEM *gi, void *userData);
    static void comboBoxCallback(GUIITEM *gi, void *userData);
  public:      
    static SIMULATIONPANEL *create(const GLWINDOW *gw, SGLGUI *gui);      
    void useSimulation(SIMULATION *sim);
    void setFps(float fps);
    void setVisibility(bool visible);
    void setPosition(int xPos, int yPos);
};


#endif
