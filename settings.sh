#!/bin/bash

# set the base path to your Android NDK (or export NDK to environment)

if [[ "x$ANDROID_NDK_BASE" == "x" ]]; then
    ANDROID_NDK_BASE=/home/tim/dev/android-ndk-r8b
    echo "No ANDROID_NDK_BASE set, using $ANDROID_NDK_BASE"
fi

NDK_PLATFORM_VERSION=3
NDK_ABI=arm
NDK_COMPILER_VERSION=4.6
NDK_SYSROOT=$ANDROID_NDK_BASE/platforms/android-$NDK_PLATFORM_VERSION/arch-$NDK_ABI
NDK_UNAME=`uname -s | tr '[A-Z]' '[a-z]'`
HOST=$NDK_ABI-linux-androideabi
NDK_TOOLCHAIN_BASE=$ANDROID_NDK_BASE/toolchains/$HOST-$NDK_COMPILER_VERSION/prebuilt/$NDK_UNAME-x86
CC="$NDK_TOOLCHAIN_BASE/bin/$HOST-gcc --sysroot=$NDK_SYSROOT"
LD=$NDK_TOOLCHAIN_BASE/bin/$HOST-ld

# i use only a small number of formats - set this to 0 if you want everything.
# changed 0 to the default, so it'll compile shitloads of codecs normally
if [[ "x$minimal_featureset" == "x" ]]; then
minimal_featureset=1
fi

function current_dir {
  echo "$(cd "$(dirname $0)"; pwd)"
}

