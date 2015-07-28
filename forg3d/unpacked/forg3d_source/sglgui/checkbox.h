#ifndef __checkbox_h__
#define __checkbox_h__

class CHECKBOX : public GUIITEM, public EVENTINTERFACE
{
  private:
    bool checked;
    bool round;
    bool isOnActive(int xPos, int yPos);

    CHECKBOX();
    ~CHECKBOX();
  public:

    static CHECKBOX *create(int id,  bool checked,
                            const char *text, const FONT *font,
                            int x, int y, int w, int h,
                            void (*checkCallback)(GUIITEM *checkBox,
                                                  void *userData),
                            void *userData);

    void render(void);

    // Query methods
    bool isChecked(void) const;
    bool isRound(void) const;

    // Update methods
    void setCheck(bool check);
    void invertCheck(void);
    void setRoundness(bool round);

    // Event handlers
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};


#endif
