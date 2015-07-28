#ifndef __rectangle_h__
#define __rectangle_h__

class RECTANGLE : public GUIITEM
{
  private:
    RECTANGLE();
    ~RECTANGLE();
  public:
    static RECTANGLE *create(int id, int x, int y, int w, int h, 
                             float r, float g, float b, GLuint texture);

    void render(void);       

    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};


#endif
