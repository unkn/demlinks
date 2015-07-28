#ifndef __guiitem_h__
#define __guiitem_h__

#define GUIITEM_DEFAULT_SIZE 256
#define GUIITEM_MAX_SIZE 4096
#define GUIITEM_MIN_SIZE 4

class GUIITEM
{
  protected:
    unsigned int id;
    bool focus;
    bool visible;
    bool active;
    int x, y;
    int w, h;
    string text;
    const FONT *font;
    float transparency;
    float borderColor[3];
    float fillColor1[3];
    float fillColor2[3];
    float fontColor[3];
    GLuint texture1, texture2;
    bool border;

    GUIITEM();
    virtual ~GUIITEM();
    void setScissor(void);    
  public:
    // Procedures
    virtual void render(void) = 0;

    // Query methods
    int getId(void) const;
    int getLeft(void)const;
    int getRight(void) const;
    int getTop(void) const;
    int getBottom(void) const;
    int getWidth(void) const;
    int getHeight(void) const;
    const char *getText(void) const;
    bool isVisible(void) const;
    bool isActive(void) const;
    GLuint getTexture1(void) const;
    GLuint getTexture2(void) const;
    bool getBorderVisibility(void);
    const FONT *getFont(void);

    // update methods
    void setPosition(int x, int y);
    virtual void setSize(int w, int h);
    virtual void setText(const char *text);
    void setTransparency(float a);
    void setBorderColor(float r, float g, float b);
    void setFillColor1(float r, float g, float b);
    void setFillColor2(float r, float g, float b);
    void setFontColor(float r, float g, float b);
    void setVisibility(bool visible);
    void setActivity(bool active);
    void setFont(const FONT *font);
    void setTexture1(GLuint texture);
    void setTexture2(GLuint texture);
    void setBorderVisibility(bool border);
    virtual void killFocus(void);


    // Event handlers
    virtual void preMouseButtonDown(int xPos, int yPos);
    virtual bool mouseButtonDown(int xPos, int yPos) = 0;
    virtual void mouseButtonUp(int xPos, int yPos) = 0;
    virtual void mouseMove(bool button, int xPos, int yPos) = 0;
    virtual void keyDown(char asciiCode) = 0;

  friend class ITEMCONTAINER;
};

#endif
