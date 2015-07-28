#ifndef __menubar_h__
#define __menubar_h__

class MENUBAR : public GUIITEM
{
  private:
    bool open;
    vector<MENU *> menu;    
    void (*selectCallback)(MENU *menu, int optionId, void *userData);
    void *userData;
            
    int getActiveWidth();
    bool isOnActive(int xPos, int yPos);  
    bool isOnString(int index, int xPos, int yPos);
    MENUBAR();
  public:
    ~MENUBAR();
    static MENUBAR *create(int id, const FONT *font, 
                           int x, int y, int w, int h,
                           void (*selectCallback)(MENU *menu, 
                                               int optionId, void *userData),
                           void *userData);


    void render();
    
    void *getUserData(void);
    
    MENU *addMenu(int id, const char *text);
    void setUserData(void *userData);
    void setSelectCallback(void (*selectCallback)(MENU *menu, int optionId, void *userData));
    

    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};


#endif
