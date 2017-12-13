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
char buf[BUFFER_SIZE];
typedef struct {
	int socket;
	char login[10];
} Client;

Client clients[MAX_USER];
pthread_mutex_t mutex;

int main(int argc, char *argv[]) {
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
	server.sin_port = htons(8882);

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

	char sendedSize[4];

	//Receive a message from client
	while (1) {

		memset(buf, 0, BUFFER_SIZE);
		memset(sendedSize, 0, 4);
		size = recv(sock, buf, BUFFER_SIZE, 0);

		strncpy(sendedSize, buf, 4);
		while (getSize(sendedSize) > size) {
			size = recv(sock, buf + size, BUFFER_SIZE, 0);
		}
		if (getSize(sendedSize) < size) {
			break;
		}

		if (size < 0) {
			on_error("Client read failed\n");

		} else if (size == 0) {
			printf("Client disconnect\n");
			break;
		} else {
			if (parseMessage(sock, buf + 4, size - 4) == LOGOUT) {
				printf("Client disconnect\n");
				printf("Left clients : %d", number_of_client);
				break;
			}
		}
	}
	printf("exit thread\n");
	return 0;

}
void shotdownServer(int sig) {
	printf("Server will be shotDown. \n");
	parseMessage(0,"9",0);
	(void) signal(SIGINT, SIG_DFL);
	exit(0);

}

int getSize(char* number) {
	int i;
	if ((i = atoi(number)) != 0) {
		return i;
	}
	return -1;
}

int parseMessage(int sock, char *message, int size) {
	printf("Server received : %s", message);
	pthread_mutex_lock(&mutex);
	int i;
	if ((i = atoi(&message[0])) != 0) {
		switch (i) {

		case 1:
			if (send_to_all(size, message, sock) == FALSE) {
				error_log("Error during send message to all. \n Message : %s",message +1)
				send_error(sock);
				pthread_mutex_unlock(&mutex);
				return FALSE;
			}
			send_ok(sock);
			pthread_mutex_unlock(&mutex);
			return TRUE;
		case 2:
			if (send_private_message(message, size) == FALSE) {
				error_log("Can't send private message : %s",message);
				send_error(sock);
				pthread_mutex_unlock(&mutex);
				return FALSE;
			}
			send_ok(sock);
			pthread_mutex_unlock(&mutex);
			return TRUE;
		case 3:
			if (send_ping(sock) == FALSE) {
				error_log("Can't send ping to : %s", message +1);
				pthread_mutex_unlock(&mutex);
				return FALSE;
			}
			pthread_mutex_unlock(&mutex);
			return TRUE;
		case 4:
			if (add_new_client(sock, message, size) == FALSE) {
				error_log("Can't create new user. User with name %s exist",message+1);
				send_error(sock);
				pthread_mutex_unlock(&mutex);
				return FALSE;
			}
			send_ok(sock);
			informAboutConnectClient();
			pthread_mutex_unlock(&mutex);
			printf("Login new user : %s \n", message + 1);
			return TRUE;
		case 5:
			if (remove_client(sock, message + 1) == FALSE) {
				error_log("Can't logout client %s because I can't find his index",message+1);
				send_error(sock);
				pthread_mutex_unlock(&mutex);
				return FALSE;
			}
			send_ok(sock);
			informAboutConnectClient();
			pthread_mutex_unlock(&mutex);
			printf("Logout user : %s \n", message + 1);
			return LOGOUT;
		case 6:
			if (send_user_list(sock) == FALSE) {
				error_log("Can't send user list to %s", message +1);
				pthread_mutex_unlock(&mutex);
				return FALSE;
			}
			pthread_mutex_unlock(&mutex);
			return TRUE;
		case 9:
			logOutAll();
			break;
		default:
			error_log("Can't parse message: %s",message)
			;
			pthread_mutex_unlock(&mutex);
			return FALSE;
		}
	} else {
		error_log("Can't parse message : %s",message);
		pthread_mutex_unlock(&mutex);
		return FALSE;
	}
	return 0;
}

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
void informAboutConnectClient() {
	for (int i = 0; i < number_of_client; i++) {
		send_user_list(clients[i].socket);
	}
}
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

int send_ok(int sock) {
	if (send(sock, OK_S, 2, 0) < 0) {
		return FALSE;
	}
	return TRUE;
}
int send_error(int sock) {
	if (send(sock, ERROR_S, 2, 0) < 0) {
		return FALSE;
	}
	return TRUE;
}
int check_if_exist(char *name) {
	name[strlen(name) - 1] = '\0';
	for (int i = 0; i < number_of_client; i++) {
		if (strcmp(clients[i].login, name) == 0) {
			return TRUE;
		}
	}
	return FALSE;
}
void print_all() {
	for (int i = 0; i < number_of_client; i++) {
		printf("%s,", clients[i].login);

	}
}

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
int send_user_list(int socket) {
	char *temp;
	int length_of_logins = 0;

	for (int i = 0; i < number_of_client; i++) {
		if (clients[i].socket != socket) {
			length_of_logins += strlen(clients[i].login) + 1;
		}
	}
	temp = calloc(length_of_logins + 1, sizeof(char));
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
int send_ping(int socket) {

	if (send(socket, PING_S, 2, 0) < 0) {
		return FALSE;
	}
	return TRUE;
}
int send_private_message(char *message, int size) {
	int size_of_name;
	int index;
	char *to_user;
	char *position;
	if ((position = strchr(message, SEPARATOR)) != NULL) {
		size_of_name = strlen(message) - strlen(position);
		to_user = calloc(size_of_name, sizeof(char));
		strncpy(to_user, message + 1, size_of_name);
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
int logOutAll() {
	if (send_to_all(1, "9", -1) == TRUE) {
		return TRUE;
	}
	return FALSE;
}

