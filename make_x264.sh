#!/bin/bash
pushd `dirname $0`
. settings.sh
pushd x264
make -j4 install
popd;popd
