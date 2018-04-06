#!/bin/sh

PRG=$0

while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '^.*-> \(.*\)$' 2>/dev/null`
    if expr "$link" : '^/' 2> /dev/null >/dev/null; then
        PRG="$link"
    else
        PRG="`dirname "$PRG"`/$link"
    fi
done

progdir=`dirname "$PRG"`

JAVACMD="$progdir/1.6.0.jdk/Contents/Home/bin/java"
#JAVACMD=java

#if [ -n "$JAVA_HOME" ]; then
#  JAVACMD="$JAVA_HOME/bin/java"
#elif [ -x /usr/libexec/java_home ]; then
#  JAVACMD="`/usr/libexec/java_home`/bin/java"
#else
#  JAVACMD="/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/bin/java"
#fi

echo "$progdir"

#exec "$JAVACMD" -classpath "$progdir/../Java/com/gps/imp/player-ui/1.0-SNAPSHOT/player-ui-1.0-SNAPSHOT.jar;$progdir/../Java/com/gps/ilp/parser/1.0-SNAPSHOT/parser-1.0-SNAPSHOT.jar;$progdir/../Java/com/gps/ilp/utils/1.0-SNAPSHOT/utils-1.0-SNAPSHOT.jar;$progdir/../Java/com/gps/imp/vlcj-adapter/1.0-SNAPSHOT/vlcj-adapter-1.0-SNAPSHOT.jar;$progdir/../Java/uk/co/caprica/vlcj/3.9.0/vlcj-3.9.0.jar;$progdir/../Java/net/java/dev/jna/jna/3.5.2/jna-3.5.2.jar;$progdir/../Java/net/java/dev/jna/platform/3.5.2/platform-3.5.2.jar;$progdir/../Java/org/slf4j/slf4j-api/1.7.10/slf4j-api-1.7.10.jar;$progdir/../Java/com/gps/imp/utils/1.0-SNAPSHOT/utils-1.0-SNAPSHOT.jar;$progdir/../Java/com/oracle/javafx/2.2/javafx-2.2.jar;$progdir/../Java/org/jdesktop/beansbinding/1.2.1/beansbinding-1.2.1.jar;$progdir/../Java/org/swinglabs/swing-layout/1.0.3/swing-layout-1.0.3.jar;$progdir/../Java/com/intellij/forms_rt/7.0.3/forms_rt-7.0.3.jar;$progdir/../Java/asm/asm-commons/3.0/asm-commons-3.0.jar;$progdir/../Java/asm/asm-tree/3.0/asm-tree-3.0.jar;$progdir/../Java/asm/asm/3.0/asm-3.0.jar;$progdir/../Java/com/jgoodies/forms/1.1-preview/forms-1.1-preview.jar;$progdir/../Java/jdom/jdom/1.0/jdom-1.0.jar;$progdir/../Java/commons-io/commons-io/2.4/commons-io-2.4.jar;$progdir/../Java/log4j/log4j/1.2.17/log4j-1.2.17.jar" \
#       com.gps.itunes.media.player.ui.Main

exec "$JAVACMD" \
     -Dfile.encoding=UTF-8 \
     -Duser.dir="$progdir" \
     -classpath "$progdir/../Java/lib/*" \
     -jar $progdir/../Java/ItunesMediaPlayer.jar \
      "-Dapple.laf.useScreenMenuBar=true" \
      "-Dcom.apple.mrj.application.growbox.intrudes=true" \
      "-Dapple.awt.antialiasing=true" \
      "-Dcom.apple.mrj.application.live-resize=true" \
      "-Dsun.java2d.opengl=true" \
      "-Xdock:name=ITunes Media Player" \
      "-Xdock:icon=\"$progdir/../Resources/imp.icns\"" \
      "$@"
