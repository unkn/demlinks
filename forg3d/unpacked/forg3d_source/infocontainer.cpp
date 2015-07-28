#include <string>
#include <vector>

using namespace std;

#include "info.h"
#include "infocontainer.h"

// Constructor
INFOCONTAINER::INFOCONTAINER()
{
  // Empty
}

// Destructor
INFOCONTAINER::~INFOCONTAINER()
{
  for (unsigned int i=0; i<info.size(); i++)           
  {
    if (info[i]) delete info[i];    
  }
}

// Adds a new information to the container
void INFOCONTAINER::addInfo(const char *key, const char *data)
{
  info.push_back(INFO::create(key, data));  
}

// Deletes an info from the container
void INFOCONTAINER::deleteInfo(int index)
{
  if (index>=0 && index <info.size())
  {
    info.erase(info.begin()+index);             
  }     
}

// Set the key field of an information
void INFOCONTAINER::setInfoKey(int index, const char *key)
{
  info[index]->setKey(key);     
}

// set the data field of an information
void INFOCONTAINER::setInfoData(int index, const char *data)
{
  info[index]->setData(data);     
}

// Returns the number fo information fields
int INFOCONTAINER::getNumberOfInfos(void) const
{
  return info.size();    
}

// 
const char *INFOCONTAINER::getInfo(int index) const
{
  static string temp;
  
  temp=info[index]->getKey();
  temp+=": ";
  temp+=info[index]->getData();      

  return temp.c_str();
}

const char *INFOCONTAINER::getInfoKey(int index) const
{
  return info[index]->getKey();
}

const char *INFOCONTAINER::getInfoData(int index) const
{
  return info[index]->getData();
}


