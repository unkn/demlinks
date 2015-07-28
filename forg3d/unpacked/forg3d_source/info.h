#ifndef __info_h__
#define __info_h__

class INFO
{
  private:      
    string key;
    string data;
  public:
    static INFO *create(const char *key, const char *data);


    const char *getKey(void) const;
    const char *getData(void) const;
    
    void setKey(const char *key);
    void setData(const char *data);
};

#endif
