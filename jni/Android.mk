LOCAL_PATH := $(call my-dir)

# ***** libx264 *****
include $(CLEAR_VARS)

LOCAL_MODULE := libx264
LOCAL_SRC_FILES := out/lib/libx264.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon

include $(PREBUILT_STATIC_LIBRARY)

# ***** libavformat *****
include $(CLEAR_VARS)

LOCAL_LDFLAGS := -Wl,-rpath-link=/home/liam/dev/android-ndk-r8b/platforms/android-14/arch-arm/usr/lib/ -rpath-link=/home/liam/dev/android-ndk-r8b/platforms/android-14/arch-arm/usr/lib/
LOCAL_MODULE := libavformat
LOCAL_SRC_FILES := out/lib/libavformat.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon
LOCAL_LDLIBS := -lz -lm -llog -lc -L$(call host-path, $(LOCAL_PATH))/$(TARGET_ARCH_ABI) -landprof

include $(PREBUILT_STATIC_LIBRARY)

# ***** libavcodec *****
include $(CLEAR_VARS)

LOCAL_LDFLAGS := -Wl,-rpath-link=/home/liam/dev/android-ndk-r8b/platforms/android-14/arch-arm/usr/lib/ -rpath-link=/home/liam/dev/android-ndk-r8b/platforms/android-14/arch-arm/usr/lib/
LOCAL_MODULE := libavcodec
LOCAL_SRC_FILES := out/lib/libavcodec.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon
LOCAL_LDLIBS := -lz -lm -llog -lc -L$(call host-path, $(LOCAL_PATH))/$(TARGET_ARCH_ABI) -landprof

include $(PREBUILT_STATIC_LIBRARY)

# ***** libpostproc *****
include $(CLEAR_VARS)

LOCAL_MODULE := libpostproc
LOCAL_SRC_FILES := out/lib/libpostproc.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon

include $(PREBUILT_STATIC_LIBRARY)

# ***** libswscale *****
include $(CLEAR_VARS)

LOCAL_MODULE := libswscale
LOCAL_SRC_FILES := out/lib/libswscale.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon

include $(PREBUILT_STATIC_LIBRARY)

# ***** libavutil *****
include $(CLEAR_VARS)

LOCAL_MODULE := libavutil
LOCAL_SRC_FILES := out/lib/libavutil.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon

include $(PREBUILT_STATIC_LIBRARY)

# ***** libBasicUsageEnvironment *****
include $(CLEAR_VARS)

LOCAL_MODULE := libBasicUsageEnvironment
LOCAL_CFLAGS := -O2 -DSOCKLEN_T=socklen_t -DNO_SSTREAM=1 -D_LARGEFILE_SOURCE=1 -D_FILE_OFFSET_BITS=64
LOCAL_CPPFLAGS := $(LOCAL_CFLAGS) -Wall -DBSD=1
LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/live/BasicUsageEnvironment/include \
	$(LOCAL_PATH)/live/UsageEnvironment/include \
	$(LOCAL_PATH)/live/groupsock/include
LOCAL_SRC_FILES	:= \
	$(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/live/BasicUsageEnvironment/*.c*))
	
include $(BUILD_STATIC_LIBRARY)

# ***** libgroupsock *****
include $(CLEAR_VARS)

LOCAL_MODULE := libgroupsock
LOCAL_CFLAGS := -O2 -DSOCKLEN_T=socklen_t -DNO_SSTREAM=1 -D_LARGEFILE_SOURCE=1 -D_FILE_OFFSET_BITS=64
LOCAL_CPPFLAGS := $(LOCAL_CFLAGS) -Wall -DBSD=1
LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/live/groupsock/include \
	$(LOCAL_PATH)/live/UsageEnvironment/include
LOCAL_SRC_FILES	:= \
	$(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/live/groupsock/*.c*))	
include $(BUILD_STATIC_LIBRARY)

# ***** libliveMedia *****
include $(CLEAR_VARS)

LOCAL_MODULE := libliveMedia
LOCAL_CFLAGS := -O2 -DSOCKLEN_T=socklen_t -DNO_SSTREAM=1 -D_LARGEFILE_SOURCE=1 -D_FILE_OFFSET_BITS=64
LOCAL_CPPFLAGS := $(LOCAL_CFLAGS) -Wall -DBSD=1 -fexceptions -DLOCALE_NOT_USED
LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/live/liveMedia/include \
	$(LOCAL_PATH)/live/UsageEnvironment/include \
	$(LOCAL_PATH)/live/groupsock/include
LOCAL_SRC_FILES := \
	$(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/live/liveMedia/*.c*))
	
include $(BUILD_STATIC_LIBRARY)

# ***** libUsageEnvironment *****
include $(CLEAR_VARS)

LOCAL_MODULE := libUsageEnvironment
LOCAL_CFLAGS := -O2 -DSOCKLEN_T=socklen_t -DNO_SSTREAM=1 -D_LARGEFILE_SOURCE=1 -D_FILE_OFFSET_BITS=64
LOCAL_CPPFLAGS := $(LOCAL_CFLAGS) -Wall -DBSD=1
LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/live/UsageEnvironment/include \
	$(LOCAL_PATH)/live/groupsock/include
LOCAL_SRC_FILES := \
	$(subst $(LOCAL_PATH)/,,$(wildcard $(LOCAL_PATH)/live/UsageEnvironment/*.c*))

include $(BUILD_STATIC_LIBRARY)

# ***** streamer *****
include $(CLEAR_VARS)

LOCAL_LDLIBS += -llog -lz
LOCAL_STATIC_LIBRARIES := libavformat libavcodec libx264 libpostproc libswscale libavutil \
    libliveMedia libgroupsock libBasicUsageEnvironment libUsageEnvironment 
LOCAL_C_INCLUDES += $(LOCAL_PATH)/out/include \
    $(LOCAL_PATH)/live/BasicUsageEnvironment/include \
    $(LOCAL_PATH)/live/groupsock/include \
    $(LOCAL_PATH)/live/liveMedia/include \
    $(LOCAL_PATH)/live/UsageEnvironment/include
LOCAL_SRC_FILES := streamer.cpp
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon
LOCAL_MODULE := streamer

include $(BUILD_SHARED_LIBRARY)
