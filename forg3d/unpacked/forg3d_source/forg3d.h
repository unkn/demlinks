#ifndef __forg3d_h__
#define __forg3d_h__

#define FORG3D_NUMBER_OF_MODELS 5

class FORG3D
{
  private:
    bool showNodes;
    bool showEdges;
    bool showNodeTags;
    bool showEdgeTags;
    bool litNodes;
    float bgColor[3];
    float ntColor[3];
    float ltColor[3];

    GLWINDOW *gw;
    SIMULATION *sim;
    SGLGUI *gui;
    float focus[3];
    float orientation[4];

    float zoom;
    float fov;
    NODE *selectedNode;
    EDGE *selectedEdge;
    GLfloat viewMatrix[16];
    GLfloat projMatrix[16];
    GLint viewport[4];

    bool leftDrag;
    bool rightDrag;
    int mPos[2];

    GLuint model[FORG3D_NUMBER_OF_MODELS];
    GLuint coneModel;

    MENUBAR *menubar;
    MAINPANEL *mainPanel;
    SIMULATIONPANEL *simulationPanel;
    NODEPANEL *nodePanel;
    EDGEPANEL *edgePanel;

    NODE *selectNode(void);
    EDGE *selectEdge(void);
    void frame(void);

    FORG3D();
    void renderSimulation(void);
    static void screenshotCallback(MESSAGEDIALOG *md, BUTTON *b, void *userData);
    static void loadCallback(MESSAGEDIALOG *md, BUTTON *b, void *userData);
    static void saveCallback(MESSAGEDIALOG *md, BUTTON *b, void *userData);
  public:
    ~FORG3D();
    static FORG3D *create(const char *filename);
    void run(void);
    void render(GLWINDOW *gw);
    void logic(GLWINDOW *gw, float frameTime);
    void keyDown(GLWINDOW *gw, GLWKey key, char ascii);
    void keyUp(GLWINDOW *gw, GLWKey key);
    void mouseMove(GLWINDOW *gw);
    void resize(GLWINDOW *gw);

    const float *getBgColor(void) const;
    const float *getNtColor(void) const;
    const float *getLtColor(void) const;

    void setNodeVisibility(bool visible);
    void setEdgeVisibility(bool visible);
    void setNodeTagVisibility(bool visible);
    void setEdgeTagVisibility(bool visible);
    void setNodeLighting(bool lit);
    void setBgColor(float r, float g, float b);
    void setNtColor(float r, float g, float b);    
    void setLtColor(float r, float g, float b);    
    void setSpecular(bool specular);

    bool screenshot(const char *filename);
    
    static void menuCallback(MENU *m, int sid, void *userData);
};

#endif
