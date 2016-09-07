//
// Created by asus on 06/09/16.
//

#ifndef ANDROIDINTHEMIDDLE_WRITE_H
#define ANDROIDINTHEMIDDLE_WRITE_H

#include <sys/file.h>
#include "flags.h"

 int
        flowflush(struct CONN *conn_ptr,
                  struct HOST_DESC *desc,
                  u_char *buf,
                  int buflen);

int
        out_flavour(enum FLAVOUR flavour,
                    FILE *out,
                    u_char *buf,
                    int buflen);

#endif //ANDROIDINTHEMIDDLE_WRITE_H
