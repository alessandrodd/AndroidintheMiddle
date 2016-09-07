//
// Created by asus on 06/09/16.
//

#ifndef ANDROIDINTHEMIDDLE_DISPLAY_H
#define ANDROIDINTHEMIDDLE_DISPLAY_H

#include <stdio.h>
#include "conn.h"

int display_header(FILE *out, struct ip *ippacket, struct tcphdr *tcppacket, int payload_len);

int
        display_status(FILE *out, struct CONN *conn, enum STATUS status);

int
        out_hn(FILE *out, u_char *buf, int buflen);

int
        out_h(FILE *out, u_char *buf, int buflen);

int
        out_xa(FILE *out, u_char *buf, int buflen);

int
        out_x(FILE *out, u_char *buf, int buflen);

int
        out_p(FILE *out, u_char *buf, int buflen);

#endif //ANDROIDINTHEMIDDLE_DISPLAY_H
