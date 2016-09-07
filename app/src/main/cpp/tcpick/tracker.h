//
// Created by asus on 06/09/16.
//

#ifndef ANDROIDINTHEMIDDLE_TRACKER_H
#define ANDROIDINTHEMIDDLE_TRACKER_H

#include "conn.h"

int
        status_switch(struct CONN * prev, enum STATUS status);
int
        newconn(struct CONN *prev_ring);
#endif //ANDROIDINTHEMIDDLE_TRACKER_H
