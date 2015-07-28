#ifndef __font_h__
#define __font_h__

class FONT
{
  private:
    int size;
    GLuint listBase;
    int characterWidth[256];
    GLuint texture;
    FONT();
  public:
    ~FONT();

    static FONT *create(int textureSize, 
                        const unsigned char *data,
                        const int characterWidth[256]);

    void print(const char *text, 
               int x, int y, int scale = 1, 
               int first = 0, int maxCount = 0, 
               int maxLength = 0, bool obeyNewLine = false) const;

    void printCentered(const char *text, 
                       int x, int y, int scale = 1,
                       int first = 0, int maxCount = 0, 
                       int maxLength = 0, bool obeyNewLine = false) const;

    void printVCentered(const char *text, 
                        int x, int y, int scale = 1,
                        int first = 0, int maxCount = 0, 
                        int maxLength = 0, bool obeyNewLine = false) const;

    void printHCentered(const char *text, 
                        int x, int y, int scale = 1,
                        int first = 0, int maxCount = 0, 
                        int maxLength = 0, bool obeyNewLine = false) const;

    int textWidth(const char *text, 
                  int first = 0, int maxCount = 0,
                  bool obeyNewLine = false) const;
    
    int textHeight(const char *text, 
                   int first = 0, int maxCount = 0,
                   bool obeyNewLine = false) const;

    int getSize(void) const;
};

#endif
