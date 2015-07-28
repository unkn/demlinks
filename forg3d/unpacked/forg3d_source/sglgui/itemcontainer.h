#ifndef __itemcontainer_h__
#define __itemcontainer_h__

class PANEL;
class GROUPBOX;

class ITEMCONTAINER
{
  protected:
    vector<GUIITEM *> item;
    void renderItems(void);
    bool mouseButtonDownItems(int xPos, int yPos);
    void mouseButtonUpItems(int xPos, int yPos);
    void mouseMoveItems(bool button, int xPos, int yPos);
    void keyDownItems(char asciiCode);
    void killFocusItems(void);
    void preMouseButtonDownItems(int xPos, int yPos);
    ITEMCONTAINER();
    ~ITEMCONTAINER();
  public:
    PANEL *addPanel(int id, const char *text, const FONT *font,
                    bool header, bool dragable, int x, int y, int w, int h);

    GROUPBOX *addGroupBox(int id, const char *text, const FONT *font,
                          int x, int y, int w, int h);

    BUTTON *addButton(int id, const char *text, const FONT *font,
                      int x, int y, int w, int h,
                      void (*pushCallback)(GUIITEM *button, void *userData),
                      void *userData);

    LISTBOX *addListBox(int id, bool forceselect, bool multiselect,
                        const FONT *font, int x, int y, int w, int h,
                        void (*selectCallback)(GUIITEM *listBox, void *userData),
                        void *userData);

    SCROLLBAR *addScrollbar(int id, bool horizontal,
                            int x, int y, int w, int h,
                            int min, int max, int lowValue, int highValue,
                            void (*scrollCallback)(GUIITEM *scrollBar,
                                                   void *userData),
                            void *userData);

    RECTANGLE *addRectangle(int id, int x, int y, int w, int h,
                            float r, float g, float b, GLuint texture);

    SLIDER *addSlider(int id, bool horizontal, int x, int y, int w, int h,
                      int min, int max, int value,
                      void (*scrollCallback)(GUIITEM *slider, void *userData),
                      void *userData);

    LABEL *addLabel(int id, const char *text, const FONT *font, int x, int y);

    CHECKBOX *addCheckBox(int id, bool checked,
                          const char *text, const FONT *font,
                          int x, int y, int w, int h,
                          void (*checkCallback)(GUIITEM *checkBox,
                                                void *userData),
                          void *userData);

    CONSOLE *addConsole(int id, bool interactive,
                        const FONT *font, int numberOfLines,
                        int x, int y, int w, int h,
                        void (*commandCallback)(GUIITEM *console,
                                                void *userData),
                        void *userData);

    COMBOBOX *addComboBox(int id, const FONT *font,
                          int x, int y, int w, int h,
                          void (*selectCallback)(GUIITEM *comboBox,
                                                 void *userData),
                          void *userData);

    EDITFIELD *addEditField(int id, const char *text, const FONT *font,
                            int x, int y, int w, int h, int maxLength,
                            void (*editCallback)(GUIITEM *ef, void *userData),
                            void *userData);

    PROGRESSBAR *addProgressBar(int id, const FONT *font,
                                int x, int y, int w, int h, 
                                int max, int value);
                                
    MENU *addMenu(int id, const FONT *font, int x, int y, int spacing,
                  void (*selectCallback)(MENU *menu, 
                                         int optionId, void *userData),
                  void *userData);
                  
    MENUBAR *addMenuBar(int id, const FONT *font, 
                        int x, int y, int w, int h,
                        void (*selectCallback)(MENU *menu, 
                                               int optionId, void *userData),
                        void *userData);

                                

    GUIITEM *getItem(int id);
    int getFreeId(void);
    void bringToTop(int id);
   // void sendToBottom(int id);
};

#endif
