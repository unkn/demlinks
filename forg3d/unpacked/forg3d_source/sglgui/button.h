#ifndef __button_h__
#define __button_h__

class BUTTON : public GUIITEM, public EVENTINTERFACE
{
  private:
    float roundness;
    bool down;

    bool isOnActive(int xPos, int yPos);

    BUTTON();
    ~BUTTON();
  public:
    static BUTTON *create(int id, const char *text, const FONT *font,
                          int x, int y, int w, int h,
                          void (*pushCallback)(GUIITEM *button, void *userdata),
                          void *userData);

    // procedure methods
    void render(void);

    // Query methods
    float getRoundness(void) const;

    // Update methods
    void setRoundness(float r);
    void autoSize(void);

    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};

#endif
