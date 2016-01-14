ItunesMediaPlayer
===================

Description:
-----------
It's a Media Player built on top of VLC.

Features:
    1. Direct integration with Itunes Library.
    2. Copy Itunes playlists.
    3. No VLC's UI or Itune's UI dependencies. Media player UI is built from ground up.
    4. Full Hardware acceleration that's part of VLC libraries.

[License](http://creativecommons.org/licenses/by/4.0/legalcode)
[License summary](http://creativecommons.org/licenses/by/4.0/)


Known Limitations and Workarounds
===================================

Issue: On Linux, Splash screen interferes with vlcj initialization.
Workaround: Start jvm with -DVLCJ_INITX=no 
