#ifndef ADDITIONAL_H
#define ADDITIONAL_H

#if HAVE_CONFIG_H
#include "config.h"
#endif


#ifndef HAVE_STRNLEN
#include <sys/types.h>
extern "C" {
size_t strnlen (const char *string, size_t maxlen);
}
#endif


#endif

