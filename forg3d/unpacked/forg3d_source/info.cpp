#include <string>

using namespace std;

#include "info.h"



INFO *INFO::create(const char *key, const char *data)
{
  INFO *i=new INFO();
  
  i->setKey(key);
  i->setData(data);
  
  return i;
}


const char *INFO::getKey(void) const
{
  return key.c_str();      
}

const char *INFO::getData(void) const
{
  return data.c_str();      
}  

void INFO::setKey(const char *key)
{
  this->key=key;     
}


void INFO::setData(const char *data)
{
  this->data=data;
}
