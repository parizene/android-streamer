#!/bin/bash
pushd `dirname $0`
. settings.sh
pushd ffmpeg
make -j4 install
popd; popd
