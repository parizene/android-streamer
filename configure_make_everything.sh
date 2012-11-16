#!/bin/bash

function die {
  echo "$1 failed" && exit 1
}

./clean.sh
./configure_x264.sh || die "X264 configure"
./make_x264.sh || die "X264 make"
./configure_ffmpeg.sh || die "FFMPEG configure"
./make_ffmpeg.sh || die "FFMPEG make"

tar -xzf live.2012.11.16.tar.gz -C jni/

ndk-build
