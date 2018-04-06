#!/bin/bash

##
## This fix is needes as jna is not linking properly when rpaths are used by dylib files.
## This will change rpath to loader_path.
##
## @see: https://github.com/caprica/vlcj/issues/602
##

otool -L libvlc.dylib

install_name_tool -change @rpath/libvlccore.dylib @loader_path/../lib/libvlccore.dylib ./libvlc.dylib

otool -L libvlc.dylib