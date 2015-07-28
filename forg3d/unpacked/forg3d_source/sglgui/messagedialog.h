#ifndef __messagedialog_h__
#define __messagedialog_h__

#define MESSAGEDIALOG_RIGHT_ID 1
#define MESSAGEDIALOG_LEFT_ID 2
#define MESSAGEDIALOG_BROWSE_ID 3
#define MESSAGEDIALOG_LABEL_ID 4
#define MESSAGEDIALOG_EDITFIELD_ID 5

class MESSAGEDIALOG : public PANEL
{
  private:      
    LABEL *messageLabel;
    EDITFIELD *editField;
    BUTTON *leftButton;
    BUTTON *rightButton;
    BUTTON *browseButton;
    MESSAGEDIALOG();
    
    static void browseCallback(GUIITEM *gi, void *userData);
  public:
    ~MESSAGEDIALOG();
    static MESSAGEDIALOG *create(const char *header, const char *message, const FONT *font);         
    
    
    BUTTON *getLeftButton(void);
    BUTTON *getRightButton(void);
    LABEL *getMessageLabel(void);
    EDITFIELD *getEditField(void);   
    
    void setUp(int w, int h,
               const char *header, const char *message, 
               const char *editField, bool browse,
               const char *left, const char *right,
               void (*buttonCallback)(GUIITEM *gi, void *userData),
               void *userData);
};



#endif
