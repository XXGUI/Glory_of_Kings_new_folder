#ifndef __MEMORYTOOLS_H__
#define __MEMORYTOOLS_H__

#include <malloc.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <thread>
#include <iostream>
#include <sys/uio.h>
#include <sys/types.h>
#include <sys/syscall.h>
#include <math.h>
#include <dirent.h>
#include <ctime>
#include <string>
using namespace std;


int pid;
int GetProcessID(char *PackageName)
{
	int id = -1;
	DIR *dir;
	FILE *fp;
	char filename[32];
	char cmdline[256];
	struct dirent *entry;
	dir = opendir("/proc");
	while ((entry = readdir(dir)) != NULL)
	{
		id = atoi(entry->d_name);
		if (id != 0)
		{
			sprintf(filename, "/proc/%d/cmdline", id);
			fp = fopen(filename, "r");
			if (fp)
			{
				fgets(cmdline, sizeof(cmdline), fp);
				fclose(fp);
				if (strcmp(PackageName, cmdline) == 0)
				{
					pid = id;
					return id;
				}
			}
		}
	}
	closedir(dir);
	return -1;
}



long getModuleBase(char *name, int index)
{
	int i = 0, ii = 0, iii = 0;
	long start = 0, end = 0;
	char line[1024] = { 0 };
	char fname[128];
	char dname[128];
	if (strstr(name, "bss") != NULL)
	{
		sscanf(name, "%[^:]", dname);
		i++;
	}
	else
	{
		sprintf(dname, "%s", name);
	}
	sprintf(fname, "/proc/%d/maps", pid);
	FILE *p = fopen(fname, "r");
	if (p)
	{
		while (fgets(line, sizeof(line), p))
		{
			if (strstr(line, dname) != NULL)
			{
				iii++;
				if (i == 0)
				{
					if (iii == index)
					{
						sscanf(line, "%lx-%lx", &start, &end);
						break;
					}
				}
				else
				{
					ii++;
				}
			}
			if (ii > 0)
			{
				if (strstr(line, "[anon:.bss]") != NULL)
				{
					sscanf(line, "%lx-%lx", &start, &end);
					break;
				}

			}

		}
		fclose(p);
	}
	return start;
}
long getlastModuleBase(char *name, int index)
{
	int i = 0, ii = 0, iii = 0;
	long start = 0, end = 0;
	char line[1024] = { 0 };
	char fname[128];
	char dname[128];
	if (strstr(name, "bss") != NULL)
	{
		sscanf(name, "%[^:]", dname);
		i++;
	}
	else
	{
		sprintf(dname, "%s", name);
	}
	sprintf(fname, "/proc/%d/maps", pid);
	FILE *p = fopen(fname, "r");
	if (p)
	{
		while (fgets(line, sizeof(line), p))
		{
			if (strstr(line, dname) != NULL)
			{
				iii++;
				if (i == 0)
				{
					if (iii == index)
					{
						sscanf(line, "%lx-%lx", &start, &end);
						break;
					}
				}
				else
				{
					ii++;
				}
			}
			if (ii > 0)
			{
				if (strstr(line, "[anon:.bss]") != NULL)
				{
					sscanf(line, "%lx-%lx", &start, &end);
					break;
				}

			}

		}
		fclose(p);
	}
	return end;
}

long getModuleAddress(char *module)
{
	char path[1024];
	char line[1024];
	long addr;
	sprintf(path, "/proc/%d/maps", pid);
	FILE *fp = fopen(path, "r");
	if (fp)
	{
		while (fgets(line, sizeof(line), fp))
		{
			if (strstr(line, module))
			{
				addr = strtoul(line, NULL, 16);
				break;
			}
		}
	}
	fclose(fp);
	return addr;
}

int readDword(unsigned long addr)
{
	int value = 0;
	struct iovec local[1];
	local[0].iov_base = &value;
	local[0].iov_len = sizeof(int);
	struct iovec remote[1];
	remote[0].iov_base = (void *)addr;
	remote[0].iov_len = sizeof(int);
	if (syscall(__NR_process_vm_readv, pid, local, 1, remote, 1, 0) < 0)
	{
		return 0;
	}
	return value;
}

long readLong(long addr)
{
	signed long value = 0;
	struct iovec local[1];
	local[0].iov_base = &value;
	local[0].iov_len = sizeof(long);
	struct iovec remote[1];
	remote[0].iov_base = (void *)addr;
	remote[0].iov_len = sizeof(long);
	if (syscall(__NR_process_vm_readv, pid, local, 1, remote, 1, 0) < 0)
	{
		return 0;
	}
	return value;
}

size_t preadv(long address, void *buffer, size_t size)
{

	struct iovec iov_ReadBuffer, iov_ReadOffset;
	iov_ReadBuffer.iov_base = buffer;
	iov_ReadBuffer.iov_len = size;
	iov_ReadOffset.iov_base = (void *)address;
	iov_ReadOffset.iov_len = size;
	return syscall(SYS_process_vm_readv, pid, &iov_ReadBuffer, 1, &iov_ReadOffset, 1, 0);
}

size_t pwritev(long address, void *buffer, size_t size)
{
	struct iovec iov_WriteBuffer, iov_WriteOffset;
	iov_WriteBuffer.iov_base = buffer;
	iov_WriteBuffer.iov_len = size;
	iov_WriteOffset.iov_base = (void *)address;
	iov_WriteOffset.iov_len = size;
	return syscall(SYS_process_vm_writev, pid, &iov_WriteBuffer, 1, &iov_WriteOffset, 1, 0);
}

long ReadZZ(long address)
{
	if (address < 0xFFFFFFFF)
	{
		long int local_value = 0;
		preadv(address, &local_value, 4);
		return local_value;
	}
	else
	{
		long int local_value = 0;
		preadv(address, &local_value, 8);
		return local_value;
	}
}





float wrirteFloat(long address, float value)
{
	pwritev(address, &value, 4);
	return value;
}


float wrirteFloatbase(long address, float value)
{
	char filename[256] = { 0 };
	sprintf(filename, "/proc/%d/mem", pid);	// pid为获取到的游戏pid
	int fd = open(filename, O_RDWR | O_SYNC);
	pwrite(fd, &value, sizeof(value), address);	// 向指
}



int wrirteDword(long address, int value)
{
	pwritev(address, &value, 4);
	return value;
}


int wrirteDwordbase(long address, int value)
{
	char filename[256] = { 0 };
	sprintf(filename, "/proc/%d/mem", pid);	// pid为获取到的游戏pid
	int fd = open(filename, O_RDWR | O_SYNC);	// 以可读写(O_RDWR)且同步(O_SYNC)的方式打开文件
	pwrite(fd, &value, 4, address);
}

float ReadFloat(long address)
{
	float local_value = 0;
	preadv(address, &local_value, 4);
	return local_value;
}

int Readword(long address)
{
	int local_value = 0;
	preadv(address, &local_value, 2);
	return local_value;
}

int ReadDword(long address)
{
	int local_value = 0;
	preadv(address, &local_value, 4);
	return local_value;
}

void getRoot(char **argv)
{
	char shellml[64];
	sprintf(shellml, "su -c %s", *argv);
	if (getuid() != 0)
	{
		system(shellml);
		exit(1);
	}
}

char Own_path[128];

void VerifyQQ(const char *QQ)	// 好友验证
{
	int cz = 0;
	DIR *dirptr = NULL;
	char str[128];
	struct dirent *entry;
	char charlong[128];
	sprintf(charlong, "%%%ds", strlen(QQ));
	if ((dirptr = opendir("/data/data/com.tencent.mobileqq/shared_prefs/")) == NULL)
	{
		printf("opendir failed!");
		exit(0);
	}
	else
	{
		while (entry = readdir(dirptr))
		{
			sscanf(entry->d_name, charlong, str);
			if (strcmp(QQ, str) == 0)
			{
				puts("\033[32mFriend authentication passed\033[0m");
				cz++;
				break;
			}
		}
		closedir(dirptr);
	}
	if (cz == 0)
	{
		system(Own_path);
		puts("\033[31mFriend authentication failed!\033[0m");
		exit(0);
	}

}

void VerifyQQQ(const char *QQQ)	// 好友群验证(伪)
{
	time_t nowtime;
	struct tm *p;
	time(&nowtime);
	p = localtime(&nowtime);
	char shellme[128];
	if (p->tm_hour == 0)
	{
		p->tm_hour = 24;
		p->tm_mday = p->tm_mday - 1;
	}
	sprintf(shellme,
			"/storage/emulated/0/tencent/msflogs/com/tencent/mobileqq/com.tencent.mobileqq.%02d.%02d.%02d.%02d.log",
			p->tm_year - 100, p->tm_mon + 1, p->tm_mday, p->tm_hour - 1);
	char Groupnum[128];
	sprintf(Groupnum, "troopUin: %s", QQQ);
	int i = 0;
	FILE *qqpath;
	if ((qqpath = fopen(shellme, "r")) == NULL)	// 

	{
		printf("error!");
	}
	else
	{
		while (!feof(qqpath))
		{
			char buf[512] = {
				0
			};
			fgets(buf, 512, qqpath);
			if (strstr(buf, Groupnum))
			{
				printf("The groupnum is exists!");
				i++;
				break;
			}
		}
	}

	if (i == 0)
	{
		puts("The groupnum or path does not exist ");
		exit(0);
	}
}
void Deleteself(char **argv)	// 执行该函数加上验证可以删除本身二进制文件
{
	sprintf(Own_path, "rm -f %s", *argv);
}

char AppName[128];

void Getpackage(char *package)
{
	DIR *dirptr = NULL;
	char str[128];
	struct dirent *entry;
	char charlen[128];
	int i = 0;
	sprintf(charlen, "%%%ds", strlen(package));
	if ((dirptr = opendir("/data/data/")) == NULL)
	{
		printf("opendir failed!");
		exit(0);
	}
	else
	{
		while (entry = readdir(dirptr))
		{
			sscanf(entry->d_name, charlen, str);

			if (strcmp(package, str) == 0)
			{
				sprintf(AppName, "%s", entry->d_name);
				i++;
				pid = GetProcessID(AppName);
				if (pid != -1)
				{
					printf
						("\033[36mInstallation package name: \033[0m%s;\n\033[36mProcess PID: \033[0m%d;\n",
						 AppName, pid);
					break;
				}
			}

		}

		closedir(dirptr);
	}
	if (i == 0)
	{
		puts("The program does not exist!");
	}
	else
	{
		if (pid < 1)
		{
			puts("No program is running!");

		}
	}

}

char *strrpc2(char *str, char *oldstr, char *newstr)
{
	char bstr[strlen(str)];
	memset(bstr, 0, sizeof(bstr));

	for (int i = 0; i < strlen(str); i++)
	{
		if (!strncmp(str + i, oldstr, strlen(oldstr)))
		{
			strcat(bstr, newstr);
			i += strlen(oldstr) - 1;
		}
		else
		{
			strncat(bstr, str + i, 1);
		}
	}
	strcpy(str, bstr);
	return str;
}


char *Unicode(int to16d)
{
	char *retres;
	char to16s[64];
	int to16zd;
	char str[12];
	retres = (char *)malloc(sizeof(short));
	sprintf(to16s, "%p", to16d);
	strrpc2(to16s, "ffff", "");
	to16zd = strtol(to16s, NULL, 16);
	wchar_t wstr[] = { to16zd, 0 };
	setlocale(LC_ALL, "");
	wcstombs(str, wstr, sizeof(str) / sizeof(char));
	strcpy(retres, str);
	free(retres);
	return retres;
}

char Name[128] = "";
void Get_UTF16(long address, int charlen)	// 开头地址,字符长度
{
	memset(Name, 0, 128);
	for (int ii = 0; ii < charlen; ii++)
	{
		short Namecode = Readword(address + ii * 2);
		strcat(Name, Unicode(Namecode));
	}
}

void StartApp(char *package)
{
	char activity[128];
	char appactivity[128];
	FILE *fp = NULL;
	char shellme[128];
	int i = 0;
	sprintf(shellme, "dumpsys package %s", package);
	fp = popen(shellme, "r");
	while (!feof(fp))
	{
		char buf[512] = { 0 };
		fgets(buf, 512, fp);
		if (strstr(buf, package))
		{
			memset(activity, 0, 128);
			sscanf(buf, "%*[^/]/%[^ ]", activity);
		}
		if (strstr(buf, "category.LAUNCHER"))
		{
			i++;
			break;
		}
	}
	if (i > 0)
	{
		char b[128];
		sscanf(activity, "%1s", b);
		if (strstr(".", b))
		{
			sprintf(appactivity, "am start -n %s/%s%s", package, package, activity);
		}
		else
		{
			sprintf(appactivity, "am start -n %s/%s", package, activity);
		}
		system(appactivity);
	}
	else
	{
		puts("The program does not exist or start failure!");
	}
}




int sec;						/* seconds after the minute - [0,59] */
int minu;						/* minutes after the hour - [0,59] */
int hour;						/* hours since midnight - [0,23] */
int mday;						/* day of the month - [1,31] */
int mon;						/* months since January - [0,11] */
int year;						/* years since 1900 */
int wday;						/* days since Sunday - [0,6] */
int yday;						/* days since January 1 - [0,365] */
int isdst;						/* daylight savings time flag */
int timec;
struct tm *p;

void GetTime()					// 获取实时
{
	time_t nowtime;
	time(&nowtime);
	p = localtime(&nowtime);
	timec=nowtime;
	sec = p->tm_sec;
	minu = p->tm_min;
	hour = p->tm_hour;
	mday = p->tm_mday;
	mon = p->tm_mon + 1;
	wday = p->tm_wday + 1;
	year = p->tm_year + 1900;
	yday = p->tm_yday;
	isdst = p->tm_isdst;
}

int *num = (int *)malloc(8 * 100);
int find_char(char **strings, char value)
{
	int c = 0;
	char *string;
	while ((string = *strings++) != NULL)
	{
		int i = 0;
		while (*string != '\0')
		{
			if (*string++ == value)
			{
				num[c] = i;
				c++;
			}
			i++;
		}
	}
	return c;
}


int before_main()
{
	char path[1024] = { 0 };
	FILE *fp = fopen("/proc/self/cmdline", "r");
	fscanf(fp, "%s", &path);
	fclose(fp);
	if (geteuid() == 0 || strcmp(path, "/data/user/0/com.n0n3m4.droidc/files/temp") == 0)
	{
		char shellml[64];
		sprintf(shellml, "su -c %s", path);
		if (getuid() != 0)
		{
			system(shellml);
			exit(1);
		}
		printf("\033[37;1m运行环境 Root\n");
		printf("\033[36;1m插件优化者:昌海  尢\n\033[33;1m\n");
	}
	else
	{
	    printf("\033[36;1m插件优化者:昌海  尢\n\033[33;1m\n");
		printf("\033[37;1m运行环境非 Root\n");
	}
}

int a = before_main();

#endif