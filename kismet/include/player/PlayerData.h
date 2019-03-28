//
// Created by knox on 28/3/2019.
//

#ifndef PETROVA_PLAYER_DATA_H
#define PETROVA_PLAYER_DATA_H

typedef enum {
    SETDATASOURCE,
    RELEASE,
} player_cmd_t;

typedef struct {
    void *data;
    player_cmd_t cmd;
} player_msg_t;

#endif //PETROVA_PLAYER_DATA_H
