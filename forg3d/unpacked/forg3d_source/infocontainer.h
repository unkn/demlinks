#ifndef __infocontainer_h__
#define __infocontainer_h__

class INFOCONTAINER
{
  private:
    vector<INFO *> info;
  protected:
    INFOCONTAINER();
    ~INFOCONTAINER();
  public:      
    void addInfo(const char *key, const char *data);
    void deleteInfo(int index);

    void setInfoKey(int index, const char *key);
    void setInfoData(int index, const char *data);
    

    const char *getInfo(int index) const;
    const char *getInfoKey(int index) const;
    const char *getInfoData(int index) const;
    int getNumberOfInfos(void) const;
};

#endif
