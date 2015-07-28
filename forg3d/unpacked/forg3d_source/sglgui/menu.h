#ifndef __menu_h__
#define __menu_h__

class MENU : public GUIITEM
{
  private:
    class OPTION
    {
      friend class MENU;
      private:
        int id;
        string text;
        bool checked;
        bool checkable;
        MENU *menu;
      public:
        OPTION();
        ~OPTION();
    };

    vector<OPTION> option;
    int spacing;
    void (*selectCallback)(MENU *menu, int optionId, void *userData);
    void *userData;

    MENU();
    
    bool isOnOption(int i, int xPos, int yPos);
  public:
    ~MENU();
    static MENU *create(int id, const FONT *font, int x, int y, int spacing,
                        void (*selectCallback)(MENU *menu, 
                                               int optionId, void *userData),
                        void *userData);

    // Procedure methods
    void render(void);
    
    // Query methods
    int getSpacing(void);
    const char *getOptionText(int id);
    bool isOptionChecked(int id);
    bool isOptionCheckable(int id);
    MENU *getSubmenu(int id);
    
    // Update methods
    void setSpacing(int spacing);
    void setSize(int w, int h);
    MENU *addOption(int id, const char *text, bool submenu, bool checkable);
    void setOptionText(int id, const char *text);
    void setOptionCheck(int id, bool checked);
    void setOptionCheckability(int id, bool checkable);
    
    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};

#endif
