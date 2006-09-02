#include "additional.h"

#ifndef HAVE_STRNLEN
#include <sys/types.h>
#include <string.h>
extern "C" {
size_t strnlen (const char *string, size_t maxlen)
{
      const char *end = (const char*) memchr (string, '\0', maxlen);
        return end ? (size_t) (end - string) : maxlen;
}
}
#endif

