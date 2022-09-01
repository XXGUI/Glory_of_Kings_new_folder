LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := JniDemo
LOCAL_C_INCLUDES := lifaair_edifier_com_jni_JniDemo.h
LOCAL_SRC_FILES := TestJni.cpp

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)