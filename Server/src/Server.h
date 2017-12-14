/*
 * Server.h
 *
 *  Created on: 11.12.2017
 *      Author: jokertwo
 */

#ifndef SERVER_H_
#define SERVER_H_

#endif /* SERVER_H_ */

#define BUFFER_SIZE 1024
#define MAX_USER 20

#define OK_S "7\n"
#define PING_S "6\n"

#define ERROR_DEFAULT "80\n"
#define ERROR_USER_EXIST "81\n"
#define ERROR_MAX_USERS "82\n"
#define ERROR_UNABLE_SEND_PRIVATE "83\n"
#define ERROR_UNABLE_SEND_PUBLIC "84\n"

#define SEPARATOR ';'

#define LOGOUT 5

#define TRUE 1
#define FALSE -1

#define on_error(...) { fprintf(stderr, __VA_ARGS__); fflush(stderr); }
#define error_log(...) { fprintf(stderr, __VA_ARGS__); fflush(stderr); }

//the thread function
void *connection_handler(void *);
int add_new_client(int sock, char *name, int size);
int remove_client(int sock, char *name);
int send_to_all(int size, char *buffer, int socket);
int send_private_message(char *message, int size);
int send_ok(int sock);
int send_error(int sock, char* kindOfError);
int get_client_index(char *name);
int check_if_exist(char *name);
void print_all(void);
int parseMessage(int sock, char *message, int size);
int send_user_list(int socket);
int send_ping(int socket);
void informAboutConnectClient(void);
void shotdownServer(int sig);
int getSize(char* number);
int logOutAll(void);
