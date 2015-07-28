#ifndef __combobox_h__
#define __combobox_h__

class SCROLLBAR;

class COMBOBOX : public GUIITEM, public EVENTINTERFACE
{
  private:
    vector<string> listItem;
    bool open;
    int maxNumberOfVisibleStrings;
    int scroll;
    int selectedString;
    SCROLLBAR *scrollbar;

    bool isOnActive(int xPos, int yPos);
    bool isOnList(int xPos, int yPos);
    bool isOnString(int xPos, int yPos, int index);
    void setUpScrollbar(void);
    void setScissor(void);

    COMBOBOX();
    ~COMBOBOX();
    
    static void scrollbarCallback(GUIITEM *gi, void *data);
  public:
    static COMBOBOX *create(int id, const FONT *font,
                            int x, int y, int w, int h,
                            void (*selectCallback)(GUIITEM *comboBox,
                                                   void *userData),
                            void *userData);

    // Procedure methods
    void render(void);

    // Query methods
    int getNumberOfStrings(void) const;
    const char *getSelectedString(void) const;
    int getSelectedStringIndex(void) const;
    const char *getString(int index) const;
    int getMaxNumberOfVisibleStrings(void) const;
    int getNumberOfVisibleStrings(void) const;
    SCROLLBAR *getScrollbar(void);

    // Update methods
    void setTransparency(float a);
    int addString(const char *text);
    void setString(int index, const char *text);
    void removeString(int index);
    void removeAll(void);
    void selectString(int index);
    void killFocus(void);
    void setMaxNumberOfVisibleStrings(int n);

    // Event handlers
    void preMouseButtonDown(int xPos, int yPos);
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};


#endif
