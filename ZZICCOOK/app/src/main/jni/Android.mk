LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#opencv
OPENCVROOT:= K:\ZZICCOOK\android_workspace\OpenCV-3.1.0-android-sdk\OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED

include ${OPENCVROOT}\sdk\native\jni\OpenCV.mk

LOCAL_SRC_FILES := main.cpp
LOCAL_LDLIBS += -llog
LOCAL_MODULE := nativegray

include $(BUILD_SHARED_LIBRARY)
include $(CLEAR_VARS)
