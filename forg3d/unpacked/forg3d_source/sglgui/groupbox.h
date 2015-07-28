#ifndef __groupbox_h__
#define __groupbox_h__

class GROUPBOX : public GUIITEM, public ITEMCONTAINER
{
  private:  
    GROUPBOX();
    ~GROUPBOX();
  public:
    
    static GROUPBOX *create(int id, const char *text, const FONT *font,
                            int x, int y, int w, int h);

    // Prosedure methods
    void render(void);

    // Event handlers
    void preMouseButtonDown(int xPos, int yPos);
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);    
};

#endif
