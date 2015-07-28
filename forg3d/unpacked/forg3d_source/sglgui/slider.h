#ifndef __slider_h__
#define __slider_h__

class SLIDER : public GUIITEM, public EVENTINTERFACE
{
  private:
    bool horizontal;
    int min, max, value;
    bool drag;
    int grapDelta;
    float getHandlePos(void);
    
    bool isOnActive(int xPos, int yPos);
    SLIDER(),
    ~SLIDER();
  public:
    static SLIDER *create(int id, bool horizontal, 
                          int x, int y, int w, int h, 
                          int min, int max, int value,
                          void (*scrollCallback)(GUIITEM *slider, void *userData),
                          void *userData);

    // Query methos
    int getValue(void);

    // Update methods
    void setValue(int value);

    void render(void);
    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};

#endif
