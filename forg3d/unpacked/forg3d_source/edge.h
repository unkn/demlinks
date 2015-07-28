#ifndef __link_h__
#define __link_h__

class EDGE: public INFOCONTAINER
{
  private:
    string tag;
    bool visible;
    bool showTag;
    int width;
    float color[3];
    NODE *node1, *node2;
    float springConstant;
    bool directed;   

    EDGE();
  public:
    static EDGE *create(NODE *n1, NODE *n2);

    NODE *getNode1(void);
    NODE *getNode2(void);  
    float getSpringConstant(void) const;
    const float *getColor(void) const;
    const char *getTag(void) const;
    int getWidth(void) const;
    bool getVisibility(void) const;
    bool getTagVisibility(void) const;
    bool getDirected(void) const;

    void setSpringConstant(float k);              
    void setColor(float r, float g, float b);
    void setTag(const char *tag);
    void setWidth(int w);
    void setVisibility(bool visible);
    void setTagVisibility(bool visible);
    void setDirected(bool directed);
    void swapDirection(void);
    
  friend class SIMULATION;
};


#endif
