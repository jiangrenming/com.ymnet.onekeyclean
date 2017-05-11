LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := appinfo
LOCAL_SRC_FILES := $(LOCAL_PATH)/security.c $(LOCAL_PATH)/sha256.c
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)