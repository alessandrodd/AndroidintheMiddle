#include <sys/types.h>
#include <signal.h>
#include <dirent.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

void usage(void) {
    fprintf(stderr, "usage: mykill name <sig>\n");
    exit(EXIT_FAILURE);
}

int kill_by_name(const char *name, int signal_type) {
    pid_t pid;
    size_t i, j;
    char *current_folder = (char *) malloc(264);
    char buf[128];
    FILE *st;
    DIR *dir = opendir("/proc");
    if (dir == NULL) {
        free(current_folder);
        return 0;
    }
    struct dirent *f;
    while ((f = readdir(dir)) != NULL) {
        if (f->d_name[0] == '.') continue;
        for (i = 0; isdigit(f->d_name[i]); i++);
        if (i < strlen(f->d_name)) continue;
        strcpy(current_folder, "/proc/");
        strcat(current_folder, f->d_name);
        strcat(current_folder, "/status");
        st = fopen(current_folder, "r");
        if (st == NULL) {
            closedir(dir);
            free(current_folder);
            return 0;
        }
        do {
            if (fgets(buf, 128, st) == NULL) {
                fclose(st);
                closedir(dir);
                free(current_folder);
                return 0;
            }
        } while (strncmp(buf, "Name:", 5));
        fclose(st);
        for (j = 5; isspace(buf[j]); j++);
        *strchr(buf, '\n') = 0;
        if (!strcmp(&(buf[j]), name)) {
            sscanf(&(current_folder[6]), "%d", &pid);
            kill(pid, signal_type);
        }
    }
    closedir(dir);
    free(current_folder);
    return 1;
}

int main(int argc, char *argv[]) {
    if (argc <= 1 || argc > 3) usage();
    char process_name[1000];
    strcpy(process_name, argv[1]);
    int signal_type = SIGINT;
    if (argc == 3) {
        signal_type = strtol(argv[2], NULL, 10);
    }

    return kill_by_name(process_name, signal_type);
}
