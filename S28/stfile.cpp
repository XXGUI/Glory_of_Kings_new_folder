#include <MemoryTools.H>
#include <string>
#include <dirent.h>
#include <math.h>
#include <pthread.h>
#include <stdio.h>
#include <string.h>
#include <sys/syscall.h>
#include <sys/uio.h>
#include <unistd.h>
#include <iostream>
#include <unistd.h>
#include <sstream>
#include <fstream>
long Matrixa = 0,Matrix,BaseMatrix,Coordinate_addr;
int m = 0;
int getMatrix() {
	if (m == 0) {
		for (int i = 0; Matrix != 128; i++) {
			Matrix = ReadZZ(ReadZZ(BaseMatrix) + i * 0x8) + 0x80;
			if (ReadFloat(Matrix) > 1) {
				if (ReadDword(Matrix + 0x2C) < 0) {
					Matrixa = Matrix + 0x40;
				} else {
					Matrixa = Matrix;
				}
				if (ReadFloat(Matrixa + 0x14) > 0) {
					printf("扫描到矩阵0x%lX\n", Matrixa);
					m = 1;
					return Matrixa;
				}
			}
		}
		printf("未扫描到矩阵\n");
		return 0;
	}
}
void yxpd() {
	while (1) {
		pid = GetProcessID((char*)"com.tencent.tmgp.sgame");
		if (pid < 1) {
			printf("进程结束\n");
			exit(0);
		}
		sleep(2);
	}
}
void write_string_to_file_append(string file_string,string str ) {
	char* a = (char*)str.data();
	std::ofstream   OsWrite(file_string);
	OsWrite<<str;
	OsWrite<<std::endl;
	OsWrite.close();
}
float matrix[16];
int my_rd, 红蓝判断, coordinates[2], bx[2];
int main(int argc, char **argv) {
	GetProcessID((char*)"com.tencent.tmgp.sgame");
	std::thread t(yxpd);
	BaseMatrix = getModuleBase((char*)"libunity.so", 1) + 0x1416C30;
	float py = ReadDword(BaseMatrix + 0x4A910) / 2;
	float px = ReadDword(BaseMatrix + 0x4A90C) / 2;
	long lib = getModuleBase((char*)"libGameCore.so", 1) + 0x26E576C;
	long 开始 = getModuleBase((char*)"libGameCore.so", 1) + 0x2864020;
	while (1) {
		if (ReadDword(开始) == 1) {
			printf("对局进行中\n");
			std::string result="{";
			std::string hero="\"hero\":[";
			getMatrix();
			long Buff_addr = ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(lib) + 0xF0) + 0x18) + 0x60) + 0xD0) + 0x120);
			long 自身阵营 = ReadDword(ReadZZ(ReadZZ(ReadZZ(lib) + 0xF0) + 0xD8) + 0x2C);
			if (自身阵营 == 1) {
				红蓝判断 = 1;
				my_rd = 1;
			} else {
				红蓝判断 = -1;
				my_rd = 2;
			}
			int Ture_count = 0;
			char 野怪数据[3048], 人物数据[256], 数据统计[3048] = "";
			for (int i = 0; i < 16; i++) {
				matrix[i] = ReadFloat(Matrixa + 0x4 * i);
			}
			long 红蓝[4];
			int rd;
			for (int i = 0; i < 10 ; i++) {
				long Array_structure = ReadZZ(ReadZZ(ReadZZ(lib) + 0x168 + 0x18*i) + 0x68);
				rd = ReadDword(Array_structure + 0x2c);
				if (rd == my_rd){
					continue;
				}
				int id = ReadDword( Array_structure + 0x20 );
				Coordinate_addr = ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0x250) + 0xA0) + 0x138) + 0x0) + 0x10);
				for (int a = 0; a < 2; a++) {
					coordinates[a] = ReadDword(Coordinate_addr + 0x8 * a);
				}
				if (coordinates[0] == 0 || coordinates[1] == 0) {
					continue;
				} else {
					Ture_count++;
				}
				int 地图X = coordinates[0] * 红蓝判断, 地图Y = coordinates[1] * 红蓝判断;
				float d_x = coordinates[0] * 0.001;
				float d_y = coordinates[1] * 0.001;
				float d_z = 0;
				float camear_r = matrix[3] * d_x + matrix[7] * d_z + matrix[11] * d_y + matrix[15];
				float r_x = px + (matrix[0] * d_x + matrix[4] * d_z + matrix[8] * d_y + matrix[12]) / camear_r * px;
				float r_y = py - (matrix[1] * d_x + matrix[5] * d_z + matrix[9] * d_y + matrix[13]) / camear_r * py;
				float r_w = py - (matrix[1] * d_x + matrix[5] * (d_z + 3.7) + matrix[9] * d_y + matrix[13]) / camear_r * py;
				int 血量 = ReadDword(ReadZZ(Array_structure + 0x128) + 0x140) * 100 / ReadDword(ReadZZ(Array_structure + 0x128) + 0x140 + 8);
				int 回城 = ReadDword(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0xF8) + 0x168) + 0x110) + 0x20);
				int 技能id = ReadDword(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0xF8) + 0x150) +0xF8) + 0x580);
				int 召唤 = ReadDword(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0xF8) + 0x150) + 0xF8) + 0xD0) / 8192000;
				int 大招 = ReadDword(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0xF8) + 0x108) + 0xF8) + 0xD0) / 8192000;
				for (long i = 0; i < 4; i++) {
					long buff4 = ReadZZ(Buff_addr + (i * 0x48));
					红蓝[i] = ReadDword(buff4 + 0x220) / 1000;
				}
				float 实体X = r_x - (r_y - r_w) / 16, 实体Y = r_y - (r_y - r_w) / 2;
				hero+="{\"heroId\":"+std::to_string(id)
					+",\"hpPercentage\":"+std::to_string(血量)
					+",\"mapXY\":\""+std::to_string(地图X)+","+std::to_string(地图Y)
					+"\",\"summonercdSkill\":"+std::to_string(召唤)
					+",\"bigMoveCd\":"+std::to_string(大招)
					+",\"summonercdSkillId\":"+std::to_string(技能id)
					+",\"goHome\":"+std::to_string(回城)
					+",\"entityXY\":\""+std::to_string(实体X)+","+std::to_string(实体Y)+"\"},";
					
			}
			if (rd == 2) {
				result+="\"weBlueBuffcd\":"+std::to_string(红蓝[3])
					  +",\"weRedBuffcd\":"+std::to_string(红蓝[1])
					  +",\"enemyBlueBuffcd\":"+std::to_string(红蓝[2])
					  +",\"enemyRedBuffcd\":"+std::to_string(红蓝[0])
					  +",";
			} else if (rd = 1) {
				result+="\"weBlueBuffcd\":"+std::to_string(红蓝[3])
					  +",\"weRedBuffcd\":"+std::to_string(红蓝[3])
					  +",\"enemyBlueBuffcd\":"+std::to_string(红蓝[0])
					  +",\"enemyRedBuffcd\":"+std::to_string(红蓝[2])
					  +",";
			}
			std::string soldier="\"soldier\":[";
			soldier+="],";
			result+=soldier+hero+"]}";
			write_string_to_file_append("/storage/emulated/0/ST/kkData",result);
			usleep( 1000 );
		} else {
			m = 0;
			if (ReadDword(开始) == 0) {
				printf("对局加载中\n");
				write_string_to_file_append("/storage/emulated/0/ST/kkData","对局加载中");
			}
		}
	}
}