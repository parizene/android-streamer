#!/bin/bash
pushd `dirname $0`
. settings.sh
pushd x264
make -j16 install
popd;popd
