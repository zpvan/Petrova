//
// Created by knox on 28/3/2019.
//

#ifndef PETROVA_PLAYER_DATA_H
#define PETROVA_PLAYER_DATA_H

typedef enum {
    PLAYER_CMD_SET_DATA_SOURCE,
    PLAYER_CMD_SET_DISPLAY,
    PLAYER_CMD_PREPARE,
    PLAYER_CMD_START,
    PLAYER_CMD_STOP,
    PLAYER_CMD_RESET,
    PLAYER_CMD_PAUSE,
    PLAYER_CMD_RELEASE,
} player_cmd_t;

typedef struct {
    player_cmd_t cmd;
    void *data;
} player_msg_t;

#endif //PETROVA_PLAYER_DATA_H
