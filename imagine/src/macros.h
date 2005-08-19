#ifndef MACROS_H
#define MACROS_H

#define SAFE_delete(_smth_) { \
        delete (_smth_);   \
        (_smth_) = NULL; \
}

#endif
