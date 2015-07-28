#ifndef __node_h__
#define __node_h__

class SIMULATION;
class EDGE;

class NODE : public INFOCONTAINER
{
  private:
    int index;          
    string id;
    string tag;
    bool visible;
    bool showTag;
    float size;
    float position[3];
    float velocity[3];
    float color[3];
    float charge;
    float inverseMass;   
    
    int modelIndex;

    vector<EDGE *> edge;
    
    NODE();
  public:
    ~NODE();
    static NODE *create(const char *id);
    
    void setTag(const char *tag);
    void setPosition(float x, float y, float z);
    void setVelocity(float x, float y, float z);
    void setModel(int model);
    void setCharge(float charge);
    void setSize(float size);
    void setColor(float r, float g, float b);
    void setInverseMass(float im);
    void setVisibility(bool visible);
    void setTagVisibility(bool visible);
    void setId(const char *id);
  
    const char *getTag(void) const;
    float getCharge(void) const;
    const float *getPosition(void) const;
    void getPosition(float pos[3]) const;
    const float *getVelocity(void) const;
    float getInverseMass(void) const;
    float getSize(void) const;
    const float *getColor(void) const;
    int getModel(void) const;
    bool getVisibility(void) const;
    bool getTagVisibility(void) const;
    const char *getId(void) const;


  friend class EDGE;
  friend class SIMULATION;
};

#endif
