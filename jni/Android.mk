LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    		:= 	datastack

LOCAL_SRC_FILES 		:= 	comstack/checksum.cpp \
							comstack/Message.cpp \
							comstack/MessageHandler.cpp \
							comstack/RingBuffer.cpp \
							comstack/RxMessage.cpp \
							comstack/TxMessage.cpp \
							DataStack.cpp \
							DataStackLib.cpp
				
include $(BUILD_SHARED_LIBRARY)
