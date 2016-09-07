#ifndef ANDROIDINTHEMIDDLE_FRAGMENTS_H
#define ANDROIDINTHEMIDDLE_FRAGMENTS_H

#include <sys/file.h>
#include "tcpick.h"
#include "extern.h"
#include "write.h"
/*
 * fragment.h -- the fragment_t linked-list:
 *                 used to store unacknowledged data
 *
 * Part of the tcpick project
 *
 * Author: Francesco Stablum <duskdruid @ despammed.com>
 *
 * Copyright (C) 2003, 2004  Francesco Stablum
 * Licensed under the GPL
 *
 */

enum fr_flag {
    BREAK = 0,
    CONTINUE,
};

struct FRAGMENT {
    int off;
    u_char *payload;
    int len;
    enum fr_flag flag;
    struct FRAGMENT *next;
};

 int
        addfr(struct FRAGMENT **first,
              int wlen,
              u_int32_t data_off,
              u_char *payload,
              int payload_len);
 int
        flush_ack ( struct HOST_DESC * desc,
                    struct CONN * conn_ptr,
                    int ack_num );

#endif
