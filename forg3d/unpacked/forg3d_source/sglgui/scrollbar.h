#ifndef __scrollbar_h__
#define __scrollbar_h__

class SCROLLBAR : public GUIITEM, public EVENTINTERFACE
{
  private:
    int min, max, value, thumbSize;
    bool horizontal;
    bool drag;
    int grapPoint;
    int grapDelta;

    float getHandlePos(void);
    float getHandleSize(void);
    bool isOnActive(int xPos, int yPos);

    SCROLLBAR();
  public:
    static SCROLLBAR *create(int id, bool horizontal,
                             int x, int y, int w, int h,
                             int min, int max, int value, int thumbSize,
                             void (*scrollCallback)(GUIITEM *scrollBar, void *userData),
                             void *userData);
    ~SCROLLBAR();

    // Procedure methods
    void render(void);

    // Query methods
    int getValue(void) const;
    int getThumbSize(void) const;
    int getMin(void) const;
    int getMax(void) const;

    // Update methods
    void setValue(int value);
    void setThumbSize(int size);
    void setMax(int max);
    void setMin(int min);

    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};


#endif
