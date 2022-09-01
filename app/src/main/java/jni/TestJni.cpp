#include  "com_jni_JniDemo.h"
#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>
#include <dirent.h>
#include <pthread.h>
#include <malloc.h>
#include <math.h>
#include <thread>
#include <iostream>
#include <sys/stat.h>
#include <errno.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <iostream>
#include <locale>
#include <string>
#include <codecvt>
#include <sys/socket.h>
#include <sys/syscall.h>
#include <sys/mman.h>
#include <linux/input.h>
#include <android/input.h>
#include <iconv.h>
#include <android/log.h>
#define TAG "++++++++" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型


 int fd ; // dev的文件描述符
 int fds[2];  //管道

 jfloat x;
 jfloat y;
 jfloat pressure;
 jfloat touchMajor;
 jfloat touchMinor;

 static int getTouchEventNum(){ //判断触摸框事件是哪一个event
        char          name[64];           /* RATS: Use ok, but could be better */
        char          buf[256] = { 0, };  /* RATS: Use ok */
        int           fd = 0;
        int           i;
     for (i = 0; i < 10; i++){
         sprintf(name, "/dev/input/event%d", i);
         if ((fd = open(name, O_RDONLY, 0)) >= 0){
             LOGI("打开成功Event：%d",i);
             ioctl(fd, EVIOCGNAME(sizeof(buf)), buf);
             if(strstr(buf, "MTOUC Touch")){
                 close(fd);
                 return i;
             }
             close(fd);
         }
     }
     return -1;
 }

 static void* readInput(void *data){
     LOGI("进入readInput");
     struct input_event inputEvent;
     while(1){
         read(fd, &inputEvent, sizeof(struct input_event));
         // 向管道中写数据
         write(fds[1], &inputEvent, sizeof(struct input_event));
     }
     return NULL;
 }

 static void* dispatchInput(void *data){
     LOGI("进入dispatchInput");
     struct input_event inputEvent;
     int flag = 1;
     while(1){
         //从管道中读取数据
         read(fds[0], &inputEvent, sizeof(struct input_event));

         if(inputEvent.type == EV_ABS && inputEvent.code == ABS_X ){
             float fv = inputEvent.value * 1.0;
             x = fv ;
             continue;
         }
         if(inputEvent.type == EV_ABS && inputEvent.code == ABS_Y ){
              float fv = inputEvent.value * 1.0;
              y = fv ;
              continue;
         }
         if(inputEvent.type == EV_KEY && inputEvent.code == BTN_TOUCH ){
             pressure = inputEvent.value;
             if(1 == pressure && flag)
             {
                 flag = 0;
             }
             else if(0 == pressure)
             {
                 flag  = 1;
             }
             continue;
         }
         //增加flag的判断作用是touchMajor和toushMinor事件在pressure事件之前的比较准确
         if(inputEvent.type == EV_ABS && inputEvent.code == ABS_MT_TOUCH_MAJOR && flag ){
              float fv = inputEvent.value * 1.0;
              touchMajor = fv ;
              continue;
         }
         if(inputEvent.type == EV_ABS && inputEvent.code == ABS_MT_TOUCH_MINOR && flag ){
             float fv = inputEvent.value * 1.0;
             touchMinor = fv;
             continue;
         }
     }
     return NULL;
 }


 JNIEXPORT jint JNICALL Java_com_jni_JniDemo_initEvent(JNIEnv *env, jclass clazz){
     int num = 3;
     if( num == -1){
         LOGI("No Touch Event");
         return -1;
     }
     char name[64];
     sprintf(name, "/dev/input/event%d", num);
     fd = open(name, O_RDWR);
     if(fd < 0){
         LOGI("Open dev Error");
         return fd;
     }

     //创建无名管道
     if(-1 == pipe(fds)){
         printf("pipe");
         exit(-1);
     }

     pthread_t readId, disPatchId;
     LOGI("准备进入readInput");
     pthread_create(&readId, NULL, readInput, NULL);
     sleep(1);
     LOGI("准备进入dispatchInput");
     pthread_create(&disPatchId, NULL, dispatchInput, NULL);

     return fd;
 }

 JNIEXPORT jfloat JNICALL Java_com_jni_JniDemo_getXX(JNIEnv *env, jclass clazz){
     return x;
 }

 JNIEXPORT jfloat JNICALL Java_com_jni_JniDemo_getYY(JNIEnv *env, jclass clazz){
     return y;
 }

 JNIEXPORT jfloat JNICALL Java_com_jni_JniDemo_getW(JNIEnv *env, jclass clazz){
     return touchMajor;
 }

 JNIEXPORT jfloat JNICALL Java_com_jni_JniDemo_getH(JNIEnv *env, jclass clazz){
     return touchMinor;
 }

JNIEXPORT jstring JNICALL Java_com_jni_JniDemo_test (JNIEnv *env,jclass jc, int x, int y){
    LOGI("传入参数x%d,y%d", x,y);
    char l[50] = {"/dev/input/event3"};
    int fd = open(l, O_RDWR);
    if(-1==fd){
        return env->NewStringUTF("调用驱动失败");
    }
    int FingerMax = 2;
    struct input_event event;

    event.type = EV_KEY;
    event.code = BTN_TOUCH;
    gettimeofday(&event.time, 0);
    event.value = KEY_DOWN;
    write(fd, &event, sizeof(struct input_event));

    event.type = EV_KEY;
    event.code = BTN_TOOL_FINGER;
    gettimeofday(&event.time, 0);
    event.value = KEY_DOWN;
    write(fd, &event, sizeof(struct input_event));

    event.type = EV_SYN;
    event.code = SYN_REPORT;
    gettimeofday(&event.time, 0);
    event.value = 0;
    write(fd, &event, sizeof(struct input_event));

    event.type = EV_ABS;
    event.code = ABS_MT_POSITION_X;
    gettimeofday(&event.time, 0);
    event.value = x;
    write(fd, &event, sizeof(struct input_event));

    event.type = EV_ABS;
    event.code = ABS_MT_POSITION_Y;
    gettimeofday(&event.time, 0);
    event.value = y;
    write(fd, &event, sizeof(struct input_event));

    event.type = EV_SYN;
    event.code = SYN_REPORT;
    gettimeofday(&event.time, 0);
    event.value = 0;
    write(fd, &event, sizeof(struct input_event));



    return env->NewStringUTF("event执行完成");
  }


