#ifndef __sglgui_h__
#define __sglgui_h__

class SGLGUI : public ITEMCONTAINER
{
  private:
    int w, h;
    int mx, my;
    GLuint cursorTexture;
    int cursorSize;

    FONT *dFont;
    GLuint dCursorTexture;
    
    MESSAGEDIALOG *messageDialog;
    void (*msgCallback)(MESSAGEDIALOG *md, BUTTON *b, void *userData);
    void *msgUserData;

    SGLGUI();

    static void messageDialogCallback(GUIITEM *gi, void *userData);
  public:
    ~SGLGUI();
    static SGLGUI *create(int w, int h);

    void render();    

    GLuint getCursorTexture(void) const;
    int getCursorSize(void) const;
    const FONT *defaultFont(void) const;
    GLuint defaultCursorTexture(void) const;

    void setCursorTexture(GLuint texture);
    void setCursorSize(int size);
    
    void showMessage(int w, int h,
                     const char *header, const char *message, 
                     const char *editField, bool browse,
                     const char *left, const char *right,
                     void (*callback)(MESSAGEDIALOG *md, BUTTON *b, void *userData),
                     void *userData); 
    

    // Event handlers
    void resize(int w, int h);
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};


#endif
