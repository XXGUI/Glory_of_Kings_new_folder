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
long Matrixa = 0, Matrix, BaseMatrix, BaseMatrix1, BaseMatrix2, Coordinate_addr;
int	m = 0, my_rd,rd, 红蓝判断, coordinates[2], bx[2];
long int getMatrix(){

if ( m == 0 ) {
	   for (int i = 0; i < 10; i++)
		{
			Matrix = ReadZZ(ReadZZ(BaseMatrix2) + i * 0x8) + 0xC0;
				if ((ReadFloat(Matrix) > 1.0f && ReadFloat(Matrix) < 2.0f) || (ReadFloat(Matrix) < -1.0f && ReadFloat(Matrix) > -2.0f))
			{
                	if (ReadFloat(Matrix + 0x40) == 1.0F &&  ReadDword(Matrix + 0x10)==0&& ReadDword(Matrix + 0x14)!=0)
				{
                    Matrixa = Matrix;
					// m = 1;
					return(Matrixa);
				}
			}
		}
}




}
void yxpd(){
	while ( 1 ) {
		pid = GetProcessID( (char*)"com.tencent.tmgp.sgame" );
		if ( pid < 1 ) {
			exit( 0 );
		}
		sleep( 2 );
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
int main( int argc, char **argv ){
	GetProcessID( (char*)"com.tencent.tmgp.sgame" );
	std::thread t( yxpd );
	// BaseMatrix	= getModuleBase( (char*)"libunity.so", 1 );
	BaseMatrix2	= getModuleBase( (char*)"libunity.so", 1 ) + 0x1416C30;
	float py = 1080 / 2;      /* 1080 */
	float px = 2340 / 2;      /* 2340 在cb */
	long lib = getModuleBase( (char*)"libGameCore.so", 1 ) + 0x26E4000;
	long libbuff = getModuleBase( (char*)"libGameCore.so", 1 ) + 0x26E4000;
	long 开始 = getModuleBase( (char*)"libGameCore.so", 1 ) + 0x26E4000+0x180020;
	FILE *fp;
	
	while (1) {
	
		if ( ReadDword( 开始 ) == 1 ) {
			printf("6\n");
			std::string result="{";
			std::string hero="\"hero\":[";
			getMatrix();
			// int	数量 = ReadDword( ReadZZ( lib ) + 0x228 + 0x1C );
			 int 数量 = ReadDword( ReadZZ(ReadZZ(ReadZZ(lib + 0x1628D8)+0x0)+0x3D8)+0x40+0x1C );
       
			// long Buff_addr = ReadZZ( ReadZZ( ReadZZ( ReadZZ( ReadZZ( libbuff ) + 0x278 ) + 0x50 ) + 0x74 ) + 0x2C8 ) + 0x120;
			// long 野怪数量 = ReadDword( Buff_addr + 0x1C );
			// long 自身阵营 = ReadDword( ReadZZ( ReadZZ( lib ) + 0x228 ) + 0x2C );
			int	buff_id;
			int	buff_team;
			int	蓝bufftime1, 蓝bufftime2, 红bufftime1, 红bufftime2;
			if ( ReadFloat( Matrixa ) > 1 ) {
				红蓝判断 = 1;
				my_rd	= 1;
			} else {
				红蓝判断 = -1;
				my_rd = 2;
			}
			int	Ture_count = 0;
			char 野怪数据[3048], 人物数据[256], 数据统计[3048] = "";
			for ( int i = 0; i < 16; i++ ) {
				matrix[i] = ReadFloat( Matrixa + 0x4 * i );
			}
			for ( int i = 0; i < 数量; i++ ) {
			long Array_structure=0;
			if(ReadDword( ReadZZ(ReadZZ(ReadZZ(lib + 0x1628D8)+0x0)+0x3D8)+0x80 ) ==0){
        		Array_structure = ReadZZ( ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(lib + 0x1628D8)+0x0)+0x3D8)+0x40)+0x8+0x20*i)+0xA0 );}
				else{continue;
				}


				rd = ReadDword( Array_structure + 0x2c );
				if ( rd == my_rd ){
					continue;
				}
				int id = ReadDword( Array_structure + 0x20 );
				Coordinate_addr = ReadZZ(ReadZZ(ReadZZ(Array_structure + 0x250) + 0xA0)+0x70);
   
				for ( int a = 0; a < 2; a++ ){
					coordinates[a] = ReadDword( Coordinate_addr + 0x8 * a );
				}
				if ( coordinates[0] == 0 || coordinates[1] == 0 ){
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
				float r_w = py - (matrix[1] * d_x + matrix[5] * (d_z + 3.7) + matrix[9] * d_y +  matrix[13]) / camear_r * py;
				int 血量 = ReadDword( ReadZZ( Array_structure + 0x110 ) + 0xA0 ) * 100 / ReadDword( ReadZZ( Array_structure + 0x110 ) + 0xA0 + 8 );
				int 回城 = ReadDword(ReadZZ(ReadZZ(ReadZZ( Array_structure + 0xF8) + 0x168) + 0x110) + 0x20);
				int 技能id = ReadDword(ReadZZ(ReadZZ(Array_structure + 0xC8) + 0xF8) + 2176);
				
				int 召唤 = ReadDword( ReadZZ( ReadZZ( ReadZZ( Array_structure + 0xF8 ) + 0x150 ) + 0xA0 ) + 0x38 ) / 8192000;
				int 大招 = ReadDword( ReadZZ( ReadZZ( ReadZZ( Array_structure + 0xF8 ) + 0x108 ) + 0xA0 ) + 0x38 ) / 8192000;
				float 实体X	= r_x - (r_y - r_w) / 16, 实体Y	= r_y - (r_y - r_w) / 5, 宽度	= (r_y - r_w) / 2;
					  
				hero+="{\"heroId\":"+std::to_string(id)
					+",\"hpPercentage\":"+std::to_string(血量)
					+",\"mapXY\":\""+std::to_string(地图X)+","+std::to_string(地图Y)
					+"\",\"summonercdSkill\":"+std::to_string(召唤)
					+",\"bigMoveCd\":"+std::to_string(大招)
					+",\"summonercdSkillId\":"+std::to_string(技能id)
					+",\"goHome\":"+std::to_string(回城)
					+",\"entityXY\":\""+std::to_string(实体X)+","+std::to_string(实体Y)+"\"},";
			}



				long ygzz = ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(lib + 0x1628D8) + 0x0)+0x360)+0x80)+0x1D4)+0xD0)+0x120);
    			蓝bufftime1 = ReadDword(ReadZZ(ygzz+0x0)+0x220)/1000;
    			红bufftime1 = ReadDword(ReadZZ(ygzz+0x48)+0x220)/1000;
    			蓝bufftime2 = ReadDword(ReadZZ(ygzz+0x90)+0x220)/1000;
    			红bufftime2 = ReadDword(ReadZZ(ygzz+0xD8)+0x220)/1000;

			
			if (rd == 1) {
				result+="\"weBlueBuffcd\":"+std::to_string(蓝bufftime1)
					  +",\"weRedBuffcd\":"+std::to_string(红bufftime2)
					  +",\"enemyBlueBuffcd\":"+std::to_string(蓝bufftime2)
					  +",\"enemyRedBuffcd\":"+std::to_string(红bufftime1)
					  +",";
			} else if (rd = 2) {
				result+="\"weBlueBuffcd\":"+std::to_string(蓝bufftime2)
					  +",\"weRedBuffcd\":"+std::to_string(红bufftime1)
					  +",\"enemyBlueBuffcd\":"+std::to_string(蓝bufftime1)
					  +",\"enemyRedBuffcd\":"+std::to_string(红bufftime2)
					  +",";
			}
			
			int 小兵数量 = ReadDword( ReadZZ( ReadZZ( libbuff ) + 0x390 ) + 0xE8 + 0x1C );
			std::string soldier="\"soldier\":[";
			for ( int i = 0; i < 0; i++ ) {
				long txx = ReadZZ( ReadZZ( ReadZZ( ReadZZ( libbuff ) + 0x390 ) + 0xE8 ) + 0x18 * i );
				long tyy = ReadDword( txx );
				if ( tyy == 0 )
					continue;
				int ID = ReadDword( txx + 0x20 );
				if ( ID < 6000 )
					continue;
				int 阵营 = ReadDword( txx + 0x2C );
				if ( 阵营 == my_rd )
					continue;
				int 血量 = ReadDword( ReadZZ( txx + 0x110 ) + 0xA0 );
				if ( 血量 == 0 )
					continue;
				long 坐标 = ReadZZ( ReadZZ( txx + 0x250 ) + 0xA0 ) + 0x70;
				preadv( 坐标, bx, 4 * 3 );
				for ( int a = 0; a < 2; a++ ) {
					bx[a] = ReadDword( 坐标 + 0x8 * a );
				}

				int x = bx[0] * 红蓝判断 * 0.0033 * +1, y = bx[2] * 红蓝判断 * 0.0033 * -1;
				soldier+="\""+std::to_string(x)+","+std::to_string(y)+"\",";
			}
			soldier+="],";
			result+=soldier+hero+"]}";
			write_string_to_file_append("/storage/emulated/0/ST/kkData",result);
			usleep( 1000 );
		}else{
			m = 0;
			write_string_to_file_append("/storage/emulated/0/ST/kkData","对局加载中");
		}
	}
}