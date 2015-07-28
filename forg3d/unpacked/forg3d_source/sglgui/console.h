#ifndef __console_h__
#define __console_h__

#define CONSOLE_DEFAULT_MAX_LINE_WIDTH 256

class CONSOLE : public GUIITEM, public EVENTINTERFACE
{
  private:

    class LINE
    {
      friend class CONSOLE;
      private:
        string text;
        float color[3];
        LINE();
    };

    bool interactive;
    int numberOfLines;
    int maxLineWidth;
    LINE **line;

    bool isOnActive(int xPos, int yPos);
    CONSOLE();
    ~CONSOLE();
  public:
    static CONSOLE *create(int id, bool interactive,
                           const FONT *font, int numberOfLines,
                           int x, int y, int w, int h,
                           void (*commandCallback)(GUIITEM *console,
                                                   void *userData),
                           void *userData);

    void render(void);

    // query methods
    const char *getLine(int index) const;
    const char *getLatestLine(void) const;
    void getLineColor(int index, float color[3]) const;
    int getMaxLineWidth(void) const;
    bool isInteractive(void) const;

    // Update methods
    void addLine(const char *text, float r, float g, float b);
    void setLineColor(int index, float r, float g, float b);
    void setMaxLineWidth(int width);
    void setInteractivity(bool interactive);

    // Event handlers
    void preMouseButtonDown(int xPos, int yPos);
    bool mouseButtonDown(int xPos, int yPos);
    void mouseButtonUp(int xPos, int yPos);
    void mouseMove(bool button, int xPos, int yPos);
    void keyDown(char asciiCode);
};


#endif
