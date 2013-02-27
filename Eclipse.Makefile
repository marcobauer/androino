
ANDROID_ADB = "/devel/android-sdk-linux/platform-tools/adb"
ANDROID_NDK_BUILD = "/devel/android-ndk/ndk-build"

#################################################### Build Configuration -> Build Executables
# Build project
build:
	@echo "\nCompile sources"
	${ANDROID_NDK_BUILD}
	
clean:	
	@echo "\Cleanup sources"
	${ANDROID_NDK_BUILD} clean
	rm -r libs obj
	