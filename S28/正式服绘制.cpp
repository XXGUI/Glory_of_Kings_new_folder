#include <MemoryTools.H>
#include <string>
long Matrixa = 0,Matrix,BaseMatrix,Coordinate_addr;
int m = 0;


int getMatrix()
{
	if (m == 0)
	{
		for (int i = 0; Matrix != 128; i++)
		{
			Matrix = ReadZZ(ReadZZ(BaseMatrix) + i * 0x8) + 0x80;
			if (ReadFloat(Matrix) > 1)
			{
				if (ReadDword(Matrix + 0x2C) < 0)
				{
					Matrixa = Matrix + 0x40;
				}
				else
				{
					Matrixa = Matrix;
				}
				if (ReadFloat(Matrixa + 0x14) > 0)
				{
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

void yxpd()
{
	while (1)
	{
		pid = GetProcessID("com.tencent.tmgp.sgame");
		if (pid < 1)
		{
			printf("进程结束\n");
			exit(0);
		}
		sleep(2);
	}
}



float matrix[16];
int my_rd, 红蓝判断, coordinates[2], bx[2];

int main(int argc, char **argv)
{

    //bmyz();
	GetProcessID("com.tencent.tmgp.sgame");
	std::thread t(yxpd);
	BaseMatrix = getModuleBase("libunity.so", 1) + 0x1416C30;
	float py = ReadDword(BaseMatrix + 0x4A910) / 2;//1080
	float px = ReadDword(BaseMatrix + 0x4A90C) / 2;//2340 在cb
	long lib = getModuleBase("libGameCore.so", 1) + 0x26E576C;
//	long 开始 = lib + 0x44d678;//对局开始1 未开始0 在cb  
	long 开始 = getModuleBase("libGameCore.so", 1) + 0x2864020;
	FILE *fp;

	system("rm -rf /sdcard/停止.log");
	while ((fp = fopen("/sdcard/停止.log", "r")) == NULL)
	{    
		if (ReadDword(开始) == 1)
		{
			getMatrix();
			long Buff_addr =
				ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(lib) + 0xF0) + 0x18) + 0x60) + 0xD0) + 0x120); //野怪指针  5级指针
		    long 自身阵营 = ReadDword(ReadZZ(ReadZZ(ReadZZ(lib) + 0xF0) + 0xD8) + 0x2C);//自身阵营

			if (自身阵营 == 1)
			{
				红蓝判断 = 1;
				my_rd = 1;
			}
			else
			{
				红蓝判断 = -1;
				my_rd = 2;
			}
			int Ture_count = 0;
			char 野怪数据[3048], 人物数据[256], 数据统计[3048] = "";
			//Matrixa = ReadZZ(ReadZZ(ReadZZ(ReadZZ(BaseMatrix) + 0x50) + 0x28) + 0x10) + 0xC0;//矩阵
			for (int i = 0; i < 16; i++)
			{
				matrix[i] = ReadFloat(Matrixa + 0x4 * i);
			}
			//0x26E576C = [0x722CCF676C] -> + 0x168 -> + 0x68 -> + 0x250 -> + 0xA0 -> + 0x138 -> + 0x0 -> + 0x10 =
			for (int i = 0; i < 10 ; i++)
			{
				long Array_structure = ReadZZ(ReadZZ(ReadZZ(lib) + 0x168 + 0x18*i) + 0x68);//遍历坐标
                
				int rd = ReadDword(Array_structure + 0x2c);
				if (rd == my_rd)//  my_rd等于阵营1  跳出循环输出阵营2
				{
					continue;
				}

				int id = ReadDword(Array_structure + 0x20);
			
				//long 判断 = ReadDword(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0x220) + 0xB0) + 0x0) + 0x28);
           //     printf("等于=%d\n", 判断);
                
                
   Coordinate_addr = ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0x250) + 0xA0) + 0x138) + 0x0) + 0x10);//对面坐标  
               
               


				for (int a = 0; a < 2; a++)
				{
					coordinates[a] = ReadDword(Coordinate_addr + 0x8 * a);
				}

				if (coordinates[0] == 0 || coordinates[1] == 0)
				{
					continue;
				}
				else
				{
					Ture_count++;
				}

				int 地图X = coordinates[0] * 红蓝判断, 地图Y =
					coordinates[1] * 红蓝判断;

				float d_x = coordinates[0] * 0.001;
				float d_y = coordinates[1] * 0.001;
				float d_z = 0;
				
				float camear_r = matrix[3] * d_x + matrix[7] * d_z + matrix[11] * d_y + matrix[15];
				
				float r_x =
					px + (matrix[0] * d_x + matrix[4] * d_z + matrix[8] * d_y +
						  matrix[12]) / camear_r * px;
				float r_y =
					py - (matrix[1] * d_x + matrix[5] * d_z + matrix[9] * d_y +
						  matrix[13]) / camear_r * py;
				float r_w =
					py - (matrix[1] * d_x + matrix[5] * (d_z + 3.7) + matrix[9] * d_y +
						  matrix[13]) / camear_r * py;

				int 血量 =
					ReadDword(ReadZZ(Array_structure + 0x128) +
							  0x140) * 100 / ReadDword(ReadZZ(Array_structure + 0x128) + 0x140 + 8);
				int 回城 = ReadDword(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0xF8) + 0x168) + 0x110) + 0x20);
			//0x168 -> + 0x68 -> + 0xF8 -> + 0x168 -> + 0x110 -> + 0x20
                int 技能id = ReadDword(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0xF8) + 0x150) +0xF8) + 0x580);
//0x168 -> + 0x68 -> + 0xF8 -> + 0x150 -> + 0xF8 -> + 0xD0
				int 召唤 =
					ReadDword(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0xF8) + 0x150) + 0xF8) + 0xD0) / 8192000;
 
				int 大招 =
					ReadDword(ReadZZ(ReadZZ(ReadZZ(Array_structure + 0xF8) + 0x108) + 0xF8) + 0xD0) / 8192000;
					
					
					
					
				long 红蓝[4];
				for (long i = 0; i < 4; i++)
				{
					long buff4 = ReadZZ(Buff_addr + (i * 0x48));
					红蓝[i] = ReadDword(buff4 + 0x220) / 1000;
				}

//蓝野狼 0x18 红野狼 0xA8 蓝方蜥蜴 0x30 红方蜥蜴 0xC0 蓝方野猪 0x60 红方野猪 0xF0
//蓝小鸟 0x78 红小鸟 0x108
				long 野狼 = ReadZZ(Buff_addr + 0xA8);
				int 野狼time = ReadDword(野狼+0x220) / 1000;
				long 野狼2 = ReadZZ(Buff_addr + 0x18);
				int 野狼time2 = ReadDword(野狼2 + 0x220) / 1000;
				long 蜥蜴 = ReadZZ(Buff_addr + 0xC0);
				int 蜥蜴time3 = ReadDword(蜥蜴+0x220) / 1000;
				long 蜥蜴2 = ReadZZ(Buff_addr + 0x30);
				int 蜥蜴time4 = ReadDword(蜥蜴2 + 0x220) / 1000;
				long 小鸟 = ReadZZ(Buff_addr + 0x78);
				int 小鸟time5 = ReadDword(小鸟+0x220) / 1000;
				long 小鸟2 = ReadZZ(Buff_addr + 0x108);
				int 小鸟time6 = ReadDword(小鸟2 + 0x220) / 1000;
				long 野猪 = ReadZZ(Buff_addr + 0xF0);
				int 野猪time7 = ReadDword(野猪+0x220) / 1000;
				long 野猪2 = ReadZZ(Buff_addr + 0x60);
				int 野猪time8 = ReadDword(野猪2 + 0x220) / 1000;
				
				float 实体X = r_x - (r_y - r_w) / 16, 实体Y = r_y - (r_y - r_w) / 2;

				sprintf(人物数据, "1,%d,%d,%d,%d,%0.2f,%0.2f,%d,%d,%d,%d;\n", id,
						血量, 地图X, 地图Y, 实体X, 实体Y, 回城, 召唤, 大招 ,技能id);
				strcat(数据统计, 人物数据);

				if (rd == 2)
				{
					sprintf(野怪数据, "2,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d;",
							红蓝[3], 红蓝[1], 红蓝[2], 红蓝[0], 野狼time, 野狼time2,
							蜥蜴time3, 蜥蜴time4, 小鸟time5, 小鸟time6, 野猪time7,
							野猪time8);
				}
				else if (rd = 1)
				{
					sprintf(野怪数据, "2,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d;",
							红蓝[1], 红蓝[3], 红蓝[0], 红蓝[2], 野狼time2, 野狼time,
							蜥蜴time4, 蜥蜴time3, 小鸟time6, 小鸟time5, 野猪time8,
							野猪time7);
				}
			}
		
        /*
	         	for (int i = 0; i < 40 ; i++)
					{
					long txx = ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(ReadZZ(lib) + 0x48) + 0x18) + 0x48) + 0x198) + 0x18*i) ;
					//printf("等于=%lX\n", txx);
					long tyy = ReadDword(txx);
					if(tyy == 0)
					continue;
					int ID = ReadDword(txx + 0x20);				
				//	if (ID < 6000 && ID > 7000 || ID < 60000 && ID > 61000)
				if (ID < 6000 || ID > 61000)
					continue;
					int 阵营 = ReadDword(txx + 0x2C);
					if (阵营 == my_rd)
					continue;
					// 0x20 -> + 0x50 -> + 0x160 -> + 0xA0 -> + 0xD8 -> + 0x78
					int 血量 =ReadDword(ReadZZ(txx + 0x128) + 0x140);
				//	printf("扫描%d\n", 血量);
					if (血量==0)
					continue;
					long 坐标 = ReadZZ(ReadZZ(txx + 0x1C0) + 0x108) + 0x44;
				//	printf("等于=%lX\n", 坐标);
			    	preadv(坐标, bx, 4 * 3);
				for (int a = 0; a < 2; a++)
				{
					bx[a] = ReadDword(坐标 + 0x8 * a);
				}
		
				int x = bx[0] * 红蓝判断 * 0.0034 * +1 , y = bx[1] * 红蓝判断 * 0.0033 * -1;
				sprintf(人物数据, "3,%d,%d;\n", x , y);
                   strcat(数据统计, 人物数据);
				}
			*/
			
			
			strcat(数据统计, 野怪数据);
			int fd = open("/storage/emulated/0/knm/尢尢.mp3", O_WRONLY | O_CREAT);
			write(fd, 数据统计, sizeof(数据统计));
			close(fd);
			usleep(100);
		}
		else
		{
			m = 0;
			if (ReadDword(开始) == 0)
		   {	   
		   system("rm -rf /sdcard/knm/尢尢.mp3");
	   	   }
        }
	}
	system("rm -rf /sdcard/停止.log");
}