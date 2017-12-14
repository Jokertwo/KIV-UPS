/*

 Compile
 gcc server.c -lpthread -o server
 */
#include<signal.h>
#include<stdio.h>
#include<string.h>    //strlen
#include<stdlib.h>    //strlen
#include<sys/socket.h>
#include<arpa/inet.h> //inet_addr
#include<unistd.h>    //write
#include<pthread.h> //for threading , link with lpthread
#include"Server.h"

int number_of_client = 0;
typedef struct {
	int socket;
	char login[10];
} Client;

Client clients[MAX_USER];
pthread_mutex_t mutex;
pthread_rwlock_t lock;

int main(int argc, char *argv[]) {
	int port = 8882;

	if (pthread_rwlock_init(&lock, NULL) != 0) {
		error_log("Cant inicialize rw_lock server cant start.\n");
		exit(2);
	}
	int test;
	if (argc == 2) {
		if ((test = atoi(argv[1])) != 0) {
			if (test > 0) {
				port = test;
				printf("Set port %d\n", port);
			} else {
				error_log("Wrong argument %s\n",argv[1])
			}
		} else {
			error_log("Wrong argument %s\n",argv[1])
		}

	}

	(void) signal(SIGINT, shotdownServer);
	int socket_desc, client_sock, c;
	struct sockaddr_in server, client;
	pthread_t thread_id;

	//Create socket
	socket_desc = socket(AF_INET, SOCK_STREAM, 0);
	if (socket_desc == -1) {
		printf("Could not create socket\n");
	}
	printf("Socket created\n");

	//Prepare the sockaddr_in structure
	server.sin_family = AF_INET;
	server.sin_addr.s_addr = INADDR_ANY;
	server.sin_port = htons(port);

	//Bind
	if (bind(socket_desc, (struct sockaddr *) &server, sizeof(server)) < 0) {
		//print the error message
		perror("bind failed. Error");
		shutdown(socket_desc, 2);
		return 1;
	}
	printf("bind done\n");

	//Listen
	listen(socket_desc, 3);

	//Accept and incoming connection
	printf("Waiting for incoming connections...\n");
	c = sizeof(struct sockaddr_in);

	while ((client_sock = accept(socket_desc, (struct sockaddr *) &client,
			(socklen_t*) &c))) {
		printf("Connection accepted\n");

		if (pthread_create(&thread_id, NULL, connection_handler,
				(void*) &client_sock) < 0) {
			perror("could not create thread");
			return 1;
		}
		pthread_detach(thread_id);
		printf("Handler assigned\n");
	}

	if (client_sock < 0) {
		perror("accept failed\n");
		return 1;
	}

	return 0;
}

/*
 * This will handle connection for each client
 * */
void *connection_handler(void *socket_desc) {
	//Get the socket descriptor
	int sock = *(int*) socket_desc;
	int size;

	char sendedSize[5];
	char buf[BUFFER_SIZE];

	//Receive a message from client
	while (1) {

		memset(buf, '\0', sizeof(buf));
		memset(sendedSize, '\0', sizeof(sendedSize));
		size = recv(sock, buf, BUFFER_SIZE, 0);

		strncpy(sendedSize, buf, sizeof(sendedSize));
		sendedSize[sizeof(sendedSize) - 1] = '\0';

		//if message is not hole, wait for left
		while (atoi(sendedSize) > size) {
			size = recv(sock, buf + size, BUFFER_SIZE, 0);
		}

		if (size < 0) {
			on_error("Client read failed\n");

		} else if (size == 0) {
			printf("Client disconnect\n");
			break;
		} else {
			if (parseMessage(sock, buf + 4, size - 4) == LOGOUT) {
				printf("Client disconnect\n");
				printf("Left clients : %d\n", number_of_client);
				break;
			}
		}
	}

	printf("exit thread\n");
	return 0;

}
/**
 * Handle CTRL+C
 */
void shotdownServer(int sig) {
	printf("Server will be shotDown. \n");
	parseMessage(0, "9", 0);
	(void) signal(SIGINT, SIG_DFL);
	exit(1);
}
/**
 * parse received message
 */
int parseMessage(int sock, char *message, int size) {
	printf("Server received : %s", message);

	int i;
	if ((i = atoi(&message[0])) != 0) {
		switch (i) {

		case 1:
			pthread_rwlock_rdlock(&lock);
			if (send_to_all(size, message, sock) == FALSE) {
				error_log("Error during send message to all. \n Message : %s",message +1)
				send_error(sock, ERROR_UNABLE_SEND_PUBLIC);
				pthread_rwlock_unlock(&lock);
				return FALSE;
			}
			send_ok(sock);
			pthread_rwlock_unlock(&lock);
			return TRUE;
		case 2:
			pthread_rwlock_rdlock(&lock);
			if (send_private_message(message, size) == FALSE) {
				error_log("Can't send private message : %s",message);
				send_error(sock, ERROR_UNABLE_SEND_PRIVATE);
				pthread_rwlock_unlock(&lock);
				return FALSE;
			}
			send_ok(sock);
			pthread_rwlock_unlock(&lock);
			return TRUE;
		case 3:
			pthread_rwlock_rdlock(&lock);
			if (send_ping(sock) == FALSE) {
				error_log("Can't send ping to : %s \n", message +1);
				pthread_rwlock_unlock(&lock);
				return FALSE;
			}
			pthread_rwlock_unlock(&lock);
			return TRUE;
		case 4:
			pthread_rwlock_wrlock(&lock);
			if (number_of_client >= MAX_USER) {
				send_error(sock, ERROR_MAX_USERS);
				pthread_rwlock_unlock(&lock);
				return FALSE;
			}
			if (add_new_client(sock, message, size) == FALSE) {
				error_log("Can't create new user. User with name %s exist \n",message+1);
				send_error(sock, ERROR_USER_EXIST);
				pthread_rwlock_unlock(&lock);
				return FALSE;
			}
			send_ok(sock);
			informAboutConnectClient();
			pthread_rwlock_unlock(&lock);
			printf("Login new user : %s \n", message + 1);
			return TRUE;
		case 5:
			pthread_rwlock_wrlock(&lock);
			if (remove_client(sock, message + 1) == FALSE) {
				error_log("Can't logout client %s because I can't find his index \n",message+1);
				send_error(sock, ERROR_DEFAULT);
				pthread_rwlock_unlock(&lock);
				return FALSE;
			}
			send_ok(sock);
			informAboutConnectClient();
			pthread_rwlock_unlock(&lock);
			printf("Logout user : %s \n", message + 1);
			return LOGOUT;
		case 6:
			pthread_rwlock_rdlock(&lock);
			if (send_user_list(sock) == FALSE) {
				error_log("Can't send user list to %s", message +1);
				pthread_rwlock_unlock(&lock);
				return FALSE;
			}
			pthread_rwlock_unlock(&lock);
			return TRUE;
		case 9:
			pthread_rwlock_rdlock(&lock);
			logOutAll();
			pthread_rwlock_unlock(&lock);
			return FALSE;
		default:
			pthread_rwlock_rdlock(&lock);
			error_log("Can't parse message: %s",message)
			;
			pthread_rwlock_unlock(&lock);
			return FALSE;
		}
	} else {
		error_log("Can't parse message : %s",message);
		return FALSE;
	}
	return 0;
}
/**
 * Add new user to array where are store all users
 */
int add_new_client(int sock, char *name, int size) {
	if ((check_if_exist(name + 1) == FALSE) && (MAX_USER > number_of_client)) {
		memset(clients[number_of_client].login, 0, 10);
		strncpy(clients[number_of_client].login, name + 1, size - 2);
		clients[number_of_client].socket = sock;
		number_of_client++;
		return TRUE;
	} else {
		return FALSE;
	}
}
/**
 * Inform others users that new user
 * come online
 */
void informAboutConnectClient() {
	for (int i = 0; i < number_of_client; i++) {
		send_user_list(clients[i].socket);
	}
}
/**
 * Remove client from array where are
 * store all users
 */
int remove_client(int sock, char *name) {
	int index;
	if ((index = get_client_index(name)) == FALSE) {
		return FALSE;
	}
	if (clients[index].socket != sock) {
		return FALSE;
	}
	number_of_client--;
	for (int i = index; i < number_of_client; i++) {
		clients[i] = clients[i + 1];
	}
	return TRUE;
}
/**
 * Return index of user by name from
 * array where are store all users
 */
int get_client_index(char *name) {
	name[strlen(name) - 1] = '\0';
	for (int i = 0; i < number_of_client; i++) {
		if (strcmp(clients[i].login, name) == 0) {
			return i;
		}
	}
	error_log("Can't find user index.");
	return FALSE;
}
/**
 * Send ok notification to user
 */
int send_ok(int sock) {
	if (send(sock, OK_S, 2, 0) < 0) {
		return FALSE;
	}
	return TRUE;
}
/**
 * Send specific error notification to user
 */
int send_error(int sock, char* kindOfError) {
	if (send(sock, kindOfError, 3, 0) < 0) {
		return FALSE;
	}
	return TRUE;
}
/**
 * Check if with name exist or not
 * if exist return TRUE
 */
int check_if_exist(char *name) {
	name[strlen(name) - 1] = '\0';
	for (int i = 0; i < number_of_client; i++) {
		if (strcmp(clients[i].login, name) == 0) {
			return TRUE;
		}
	}
	return FALSE;
}
/**
 * for debug
 */
void print_all() {
	for (int i = 0; i < number_of_client; i++) {
		printf("%s,", clients[i].login);

	}
}
/**
 * Send public message
 */
int send_to_all(int size, char *buffer, int socket) {
	for (int i = 0; i < number_of_client; i++) {
		if (clients[i].socket == socket) {
			continue;
		}

		if (send(clients[i].socket, buffer, size, 0) < 0) {
			return FALSE;
		}
	}
	return TRUE;
}
/**
 * Send list with logged users
 */
int send_user_list(int socket) {
	char *temp;
	int length_of_logins = 3;

	for (int i = 0; i < number_of_client; i++) {
		if (clients[i].socket != socket) {
			length_of_logins += strlen(clients[i].login) + 1;
		}
	}
	if ((temp = malloc(length_of_logins * sizeof(char))) == NULL) {
		return FALSE;
	}
	memset(temp, '\0', length_of_logins);
	strcat(temp, "6");
	for (int i = 0; i < number_of_client; i++) {
		if (clients[i].socket != socket) {
			strcat(temp, clients[i].login);
			strcat(temp, ";");
		}
	}
	strcat(temp, "\n");

	if (send(socket, temp, strlen(temp), 0) < 0) {
		free(temp);
		return FALSE;
	}
	free(temp);
	return TRUE;

}
/**
 * send ping
 * not used
 */
int send_ping(int socket) {

	if (send(socket, PING_S, 2, 0) < 0) {
		return FALSE;
	}
	return TRUE;
}
/**
 * send private message to specific user
 */
int send_private_message(char *message, int size) {
	int size_of_name;
	int index;
	char *to_user;
	char *position;
	if ((position = strchr(message, SEPARATOR)) != NULL) {
		size_of_name = (strlen(message) - strlen(position)) + 1;
		if ((to_user = malloc(size_of_name * sizeof(char))) != NULL) {
			memset(to_user, '\0', size_of_name);
			strncpy(to_user, message + 1, size_of_name - 1);
			printf("Send message to user : %s\n", to_user);
			if ((index = get_client_index(to_user)) != FALSE) {
				if (send(clients[index].socket, message, size, 0) > 0) {
					printf("Sended message : %s", message);
					free(to_user);
					return TRUE;
				}
			}
			free(to_user);
			return FALSE;
		}
		return FALSE;
	}

	return FALSE;
}
/**
 * send to all users message that server
 * will be shutDown
 */
int logOutAll() {
	if (send_to_all(1, "9", -1) == TRUE) {
		return TRUE;
	}
	return FALSE;
}

