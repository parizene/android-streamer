#!/bin/bash
pushd `dirname $0`
. settings.sh
pushd ffmpeg
make -j16 install
popd; popd
