#ifndef __editfield_h__
#define __editfield_h__

#define EDITFIELD_DEFAULT_MAX_LENGTH 256

class EDITFIELD : public GUIITEM, public EVENTINTERFACE
{
  private:
    int maxLength;
    int scroll;
    bool password;
    
    EDITFIELD();
    ~EDITFIELD();

    bool isOnActive(int xPos, int yPos);
  public:
    static EDITFIELD *create(int id, const char *text, const FONT *font,
                             int x, int y, int w, int h, int maxLength,
                             void (*editCallback)(GUIITEM *ef, void *userData),
                             void *userData);

    void render(void);
    
    // Quare methods
    int getMaxLength(void) const;

    // Update methods  
    void setText(const char *text);
    void setFocus(bool focus);

    // Event handlers
    void preMouseButtonDown(int xPos, int yPos);
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};

#endif
