LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/../lua
LOCAL_MODULE     := yaml
LOCAL_SRC_FILES  := api.c b64.c dumper.c emitter.c loader.c lyaml.c parser.c reader.c scanner.c writer.c
LOCAL_STATIC_LIBRARIES := luajava

include $(BUILD_SHARED_LIBRARY)
