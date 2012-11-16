#!/bin/bash
pushd `dirname $0`
. settings.sh

if [[ $DEBUG == 1 ]]; then
  echo "DEBUG = 1"
  DEBUG_FLAG="--disable-stripping"
fi

patch -N -p1 --reject-file=- < libavutil-common.patch

pushd ffmpeg

./configure \
$DEBUG_FLAG \
--arch=arm \
--cpu=cortex-a8 \
--target-os=linux \
--enable-runtime-cpudetect \
--prefix=./../jni/out \
--enable-pic \
--disable-shared \
--enable-static \
--cross-prefix=$NDK_TOOLCHAIN_BASE/bin/$NDK_ABI-linux-androideabi- \
--sysroot="$NDK_SYSROOT" \
--extra-cflags="-I../x264 -mfloat-abi=softfp -mfpu=neon" \
--extra-ldflags="-L../x264" \
\
--enable-version3 \
--enable-gpl \
\
--disable-doc \
--enable-yasm \
\
--enable-decoders \
--enable-encoders \
--enable-muxers \
--enable-demuxers \
--enable-parsers \
--enable-protocols \
--enable-filters \
--enable-avresample \
\
--disable-indevs \
--enable-indev=lavfi \
--disable-outdevs \
\
--enable-hwaccels \
\
--disable-ffmpeg \
--disable-ffplay \
--disable-ffprobe \
--disable-ffserver \
--disable-network \
\
--enable-libx264 \
--enable-zlib

popd; popd


