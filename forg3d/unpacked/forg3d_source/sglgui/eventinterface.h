#ifndef __eventinterface_h__
#define __eventinterface_h__


class EVENTINTERFACE
{
  protected:
    void *userData;
    void (*callback)(GUIITEM *item, void *userData);
    EVENTINTERFACE();
  public:
    void setUserData(void *userData);
    void setCallback(void (*callback)(GUIITEM *item, void *userData));
 
    void *getUserData(void *userData);
};

#endif
