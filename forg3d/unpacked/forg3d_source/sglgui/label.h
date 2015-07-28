#ifndef __label_h__
#define __label_h__

class LABEL : public GUIITEM
{
  private:  
    LABEL();
    ~LABEL();

    bool isOnActive(int xPos, int yPos);
  public:
    // 
    static LABEL *create(int id, const char *text, const FONT *font, 
                         int x, int y);

    // Prosedure methods
    void render(void);   

    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};

#endif
