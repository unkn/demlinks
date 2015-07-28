#ifndef __progressbar_h__
#define __progressbar_h__

class PROGRESSBAR : public GUIITEM
{
  private:
    int max;
    int value;
    PROGRESSBAR();
    ~PROGRESSBAR();
  public:
    static PROGRESSBAR *create(int id, const FONT *font, 
                               int x, int y, int w, int h, 
                               int max, int value);

    void render(void);

    void setText(const char *text);
    void setValue(int value);
    void setMax(int max);
    
    int getValue(void);
    int getMax(void);

    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);    
};

#endif
