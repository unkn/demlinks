#ifndef __mainpanel_h__
#define __mainpanel_h__

class FORG3D;

class MAINPANEL
{
  private:
    FORG3D *h;
    PANEL *panel;
    CHECKBOX *showNodesCheckBox;
    CHECKBOX *showNodeTagsCheckBox;
    CHECKBOX *showLinksCheckBox;
    CHECKBOX *showLinkTagsCheckBox;
    CHECKBOX *litNodesCheckBox;
    CHECKBOX *specularCheckBox;
    SLIDER *redSlider;
    SLIDER *greenSlider;
    SLIDER *blueSlider;
    SLIDER *ntRedSlider;
    SLIDER *ntGreenSlider;
    SLIDER *ntBlueSlider;
    SLIDER *ltRedSlider;
    SLIDER *ltGreenSlider;
    SLIDER *ltBlueSlider;

    static void checkBoxCallback(GUIITEM *gi, void *userData);
    static void sliderCallback(GUIITEM *gi, void *userData);

    MAINPANEL();
  public:
    static MAINPANEL *MAINPANEL::create(FORG3D *h, const GLWINDOW *gw, SGLGUI *gui);

    void setVisibility(bool visible);
    void setPosition(int xPos, int yPos);   
};


#endif
