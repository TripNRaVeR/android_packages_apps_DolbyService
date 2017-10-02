LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := DolbyService
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := tripndroid.dolby.audio
LOCAL_STATIC_JAVA_LIBRARIES += guava

LOCAL_RESOURCE_DIR := \
        $(LOCAL_PATH)/res

LOCAL_AAPT_FLAGS := \
        --auto-add-overlay

LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)
