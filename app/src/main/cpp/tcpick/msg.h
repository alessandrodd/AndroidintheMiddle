//
// Created by asus on 06/09/16.
//

#ifndef ANDROIDINTHEMIDDLE_MSG_H
#define ANDROIDINTHEMIDDLE_MSG_H
void
        err( char *fmt, ... );
void
        msg( int v, int attr, int fg, char *fmt, ... );
void
        sorry ( char * func, char * desc );
#endif //ANDROIDINTHEMIDDLE_MSG_H
