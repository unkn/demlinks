#ifndef __listbox_h__
#define __listbox_h__

class SCROLLBAR;

class LISTBOX : public GUIITEM, public EVENTINTERFACE
{
  private:
    vector< pair<string, bool> > listItem;
    bool forceselect;
    bool multiselect;
    int scroll;
    bool isOnActive(int xPos, int yPos);
    bool isOnString(int xPos, int yPos, int id);
    SCROLLBAR *scrollbar;

    void setUpScrollbar(void);
    LISTBOX();
    ~LISTBOX();

    static void scrollCallback(GUIITEM *gi, void *data);
  public:
    static LISTBOX *create(int id, bool forceselect, bool multiselect,
                           const FONT *font,  int x, int y, int w, int h,
                           void (*selectCallback)(GUIITEM *listBox, void *userData),
                           void *userData);

    // Procedure methods
    void render(void);

    // Query methods
    int getNumberOfStrings(void) const;
    int getNumberOfSelectedStrings(void) const;
    const char *getSelectedString(void) const;
    int getSelectedStringIndex(void) const;
    const char *getString(int index) const;
    bool isStringSelected(int index) const;

    // Update methods
    void setSize(int w, int h);
    void setForceSelect(bool forceSelect);
    void setMultiSelect(bool multiSelect);
    int addString(const char *text);
    void setString(int index, const char *text);
    void removeString(int index);
    void removeAll(void);
    void setStringSelection(int index, bool selected);
    void invertStringSelection(int index);
    void selectAll(void);
    void unselectAll(void);
    void invertAll(void);

    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);

  friend void listBoxScrollCallback(SCROLLBAR *s, void *data);
};


#endif
