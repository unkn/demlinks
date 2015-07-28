#ifndef __simulation_h__
#define __simulation_h__

class SIMULATION
{
  private:
    string filename;
    bool running;
    int integrator;
    vector<NODE *> node;
    vector<EDGE *> edge;
    float electricConstant;
    float springConstant;
    float dampingConstant;
    float maxTimeStep;
    
    int tempSpace;
    float *value;
    float *result;
    float *temp;
    
    void updateTempSpace(void);
    void updateIndex(void);
    
    void integrateEuler(float *result, const float *initialValue,  
                        const int size, const float x, float *temp);
    void integrateMidpoint(float *result, const float *initialValue,                         
                           const int size, const float x, float *temp);
    void integrateRungeKutta(float *result, const float *initialValue,
                             const int size, const float x, float *temp);
    void derive(float *result, const float *value, const int size, const float x);
    
    
    SIMULATION();
  public:                  
    ~SIMULATION();               
    static SIMULATION *create(void);
    static SIMULATION *createFromFile(const char *filename, bool *error);  
               
    void renderNodes(GLuint *modelList);
    void renderEdges(GLuint coneModel); 
    void renderNodeTags(const FONT *font, 
                        const GLfloat vm[16], const GLfloat pm[16], const GLint vp[4]);
    void renderEdgeTags(const FONT *font,
                        const GLfloat vm[16], const GLfloat pm[16], const GLint vp[4]);
    void simulate(float timeStep, const NODE *exclude);
    void randomize();
    
    int getNumberOfNodes(void) const;
    int getNumberOfEdges(void) const;
    NODE *getNode(int index);
    EDGE *getEdge(int index);
    float getElectricConstant(void) const;
    float getSpringConstant(void) const;
    float getDampingConstant(void) const;
    bool getRunState(void) const;
    int getIntegrator(void) const;
    int getMaxTimeStep(void) const;
    const char *getFilename(void) const;
    bool saveToFile(const char *filename);
    NODE *getNodeById(const char *id);
    
    
    NODE *addNode(const char *id);
    NODE *addNode();    
    void deleteNode(NODE *n);
    EDGE *addEdge(NODE *n1, NODE *n2);
    void deleteEdge(EDGE *e);
    void setElectricConstant(float k);
    void setSpringConstant(float k);
    void setDampingConstant(float k);
    void setRunState(bool running);  
    void setIntegrator(int integrator);  
    void setMaxTimeStep(int timeStep);
};

#endif
