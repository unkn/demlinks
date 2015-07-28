#ifndef __panel_h__
#define __panel_h__

#define PANEL_DEFAULT_SIZE 256
#define PANEL_DEFAULT_HEADER_SIZE 20
#define PANEL_MIN_HEADER_SIZE 2

class PANEL : public GUIITEM, public ITEMCONTAINER
{
  private:  
    bool dragable;
    bool header;
    int headerSize;
    bool drag;
    int grapPoint[2];

    bool isOnActive(int xPos, int yPos);

  protected:
    PANEL();
    ~PANEL();
  public:
    static PANEL *create(int id, const char *text, const FONT *font,
                         bool header, bool dragable, 
                         int x, int y, int w, int h);

    // Prosedure methods
    void render(void);

    // Query methods
    int getHeaderSize(void) const;
    bool isHeaderVisible(void) const;
 
    // Update methods
    void setSize(int w, int h);
    void setDragability(bool d);
    void setHeaderVisibility(bool header);
    void setHeaderSize(int size);
    void killFocus(void);

    // Event handlers
    void preMouseButtonDown(int xPos, int yPos);
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};

#endif
