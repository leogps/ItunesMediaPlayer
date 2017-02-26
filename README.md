ItunesMediaPlayer
===================

###Description

    Media Player built on top of VLC, VLCJ and various other open-source projects.

###Features

    1. Direct integration with Itunes Library.
    2. Copy Itunes playlists/Tracks.
    3. No VLC's UI or Itunes UI dependencies. Media player UI is built from ground up.
    4. Full Hardware acceleration that's part of VLC libraries.
    5. Play media from across the web including YouTube and Vimeo. This is enabled by the
       open-source project YouTube-DL (https://github.com/rg3/youtube-dl).
    6. FileSystem Tree view to pin-point the library file being played on the computer.

###Executable
    1. While this can run on any machine with Java and VLC runtimes, this is primarily developed for Mac OSX and executables are compiled as Mac native app.
    2. Linux executable can be run using the java command.            
    3. Windows executable: TBA 

###Development

####Tools:    

*Note: Most developmental changes require simply running `mvn clean install`.    
Replacing the ItunesMediaPlayer-jar-with-dependencies.jar file from player-ui/target under the pre-built ItunesMediaPlayer.app/Contents/Java folder will suffice.
    
However, in order to update youtube-dl or to create the Mac OS X native app programattically incorporating any changes, running `mvn clean install -Denvironment=dev` will do.
    
    Java
        Maven (3.3.x is tested to work)
        Please see installing dependent library ItunesLibraryParser below.
    
YoutubeDL    
    YoutubeDL project builds an executable for Linux/Mac OSX and also for Windows.
    
    Building on Mac OSX or Linux requires `CURL` to be available. The last few OSX comes with curl, some linux distributions may require this to be installed. 
        
OR
    
    Freezing Youtube-DL scripts currently seem to be broken and needs fixing. However, to freeze the youtube-dl, the following tools are required and also the corresponding freeze setup.py needs to be fixed. 
    
    git (Optional)
        To download and build youtube-dl.
                
    Python (Optional)
        To compile youtube-dl.
        youtube-dl is a very active project and contains many improvements and fixes especially updating youtube cerificates in order to resolve youtube video links correctly.
        
    cx_Freeze (Optional)
        To freeze youtube-dl executable for the OS. 
        *Note: This can be avoided by using the compiled youtube-dl executable but that would still have a dependency on python and the resulting app is not portable, avoid cx_Freeze (with python dependency) likewise: https://rg3.github.io/youtube-dl/download.html
        
        The project however makes the media player completely portable by removing python dependency on the final executable. This is achieved by the youtube-dl maven module. With the help of cx_Freeze the said module will try to remove the python dependency altogether.
        While cx_Freeze can be installed in several ways depending on the OS, general installation involves python and pip. 
        More info: http://cx-freeze.sourceforge.net

        
####Dependent libraries
#####[ItunesLibraryParser](https://github.com/leogps/ItunesLibraryParser)
        
    This is required to parse the Itunes Library XML file. The following script will clone, build and install the ItunesLibraryParser library in the local maven repository. 

`git clone https://github.com/leogps/ItunesLibraryParser.git`

`cd ItunesLibraryParser`

`mvn clean install`

`mvn install:install-file -Dfile="parser/target/com.gps.ilp-parser-2.0.0.jar" -DgroupId="com.gps" -DartifactId="ilp-parser" -Dversion="2.0.0" -Dpackaging="jar"`

---

#####[javafx](http://www.oracle.com/technetwork/java/javafx2-archive-download-1939373.html)

    This is only required for the project to compile and is only used at runtime when running under Mac OSX with JDK 7/8.

`./install_javafx_dependency.sh`

OR
        
`mvn install:install-file -Dfile="lib-runtime/jfxrt.jar" -DgroupId="com.oracle" -DartifactId="javafx" -Dversion="2.2" -Dpackaging="jar"`

OR

    Download javafx from http://www.oracle.com/technetwork/java/javafx2-archive-download-1939373.html and install in local repository.

#####VLC
    Mac OSX: Latest VLC runtime for Mac OSX is already including in the commits.
    For Linux, it is best to install the VLC which sets the LibVLC path automatically.  

#####Debian Archiver -- .deb file build
    Run `dpkg --build itunes-media-player` from archiver/debian folder.

---
       
###Credits
---

#####VLC and its license can be found here:
http://www.videolan.org/legal.html

---

#####VLCJ library and its license can be found here:
https://github.com/caprica/vlcj

---

#####JavaFX Runtime license can be found here:
http://www.oracle.com/technetwork/java/javase/downloads/java-se-archive-license-1382604.html

---

#####JDK 6 for Mac OSX by Apple can be found here:
https://support.apple.com/kb/dl1572?locale=en_US

---

#####UI Designer, UI compilation, UI jar bundling powered by IntelliJ IDEA:
https://www.jetbrains.com/idea/
 
---

###Known Limitations and Workarounds

---

#####Issue:
    On Ubuntu, Splash screen interferes with vlcj initialization.
#####Workaround: 
    Start jvm with -DVLCJ_INITX=no 

---

#####Issue: 
    On Mac OSX, Hardware Acceleration is available only in JDK 6.
#####Workaround: 
    Either use JDK 6 for better video performance or switch to JDK 6+ for Software Acceleration.

#####Issue: 
    VLC media player comes with plugin based on LUA to resolve web based video files, example YouTube videos and YouTube periodically changes the signatures to resolve the video file locations. YouTube-DL suffers from the same issue.
#####Workaround:
    YouTube-DL project is pretty up-to-date in fixing the signatures and any issues, simply recompile the youtube-dl maven module and replace the resulting executable.

---

###Licensing

[License](http://creativecommons.org/licenses/by/4.0/legalcode)

[License summary](http://creativecommons.org/licenses/by/4.0/)