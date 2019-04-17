README for the VLC media player
===============================

VLC is a popular libre and open source media player and multimedia engine,
used by a large number of individuals, professionals, companies and
institutions. Using open source technologies and libraries, VLC has been
ported to most computing platforms, including GNU/Linux, Windows, Mac OS X,
BSD, iOS and Android.
VLC can play most multimedia files, discs, streams, allows playback from
devices, and is able to convert to or stream in various formats.
The VideoLAN project was started at the university École Centrale Paris who
relicensed VLC under the GPLv2 license in February 2001. Since then, VLC has
been downloaded close to one billion times.

Links:
======

The VLC web site  . . . . . http://www.videolan.org/
Support . . . . . . . . . . http://www.videolan.org/support/
Forums  . . . . . . . . . . http://forum.videolan.org/
Wiki  . . . . . . . . . . . http://wiki.videolan.org/
The Developers site . . . . http://wiki.videolan.org/Developers_Corner
VLC hacking guide . . . . . http://wiki.videolan.org/Hacker_Guide
Bugtracker  . . . . . . . . http://trac.videolan.org/vlc/
The VideoLAN web site . . . http://www.videolan.org/

Source Code Content:
===================
ABOUT-NLS          - Notes on the Free Translation Project.
AUTHORS            - VLC authors.
COPYING            - The GPL license.
COPYING.LIB        - The LGPL license.
INSTALL            - Installation and building instructions.
NEWS               - Important modifications between the releases.
README             - This file.
THANKS             - VLC contributors.

bin/               - VLC binaries.
bindings/          - libVLC bindings to other languages.
compat/            - compatibility library for operating systems missing
                     essential functionalities.
contrib/           - Facilities for retrieving external libraries and building
                     them for systems that don't have the right versions.
doc/               - Miscellaneous documentation.
extras/analyser    - Code analyser and editor specific files.
extras/buildsystem - different buildsystems specific files.
extras/misc        - Files that don't fit in the other extras/ categories.
extras/package     - VLC packaging specific files such as spec files.
extras/tools/      - Facilities for retrieving external building tools needed
                     for systems that don't have the right versions.
include/           - Header files.
lib/               - libVLC source code.
modules/           - VLC plugins and modules. Most of the code is here.
po/                - VLC translations.
share/             - Common Resources files.
src/               - libvlccore source code.
test/              - testing system.








compile vlc-3.0.6
===============================

Prepare 3rd party libraries
===========================
mkdir -p contrib/win32
cd contrib/win32
../bootstrap --host=HOST-TRIPLET
make fetch
make

tips:
config.mak:
PKGS_DISABLE := dca goom chromaprint schroedinger sdl SDL_image fontconfig kate caca gettext mpcdec gme tremor sidplay2 samplerate faad2 aribb24 aribb25 libmpeg2 mad vncclient vnc srt x265
PKGS_ENABLE := dvdread dvdnav lua zvbi upnp vorbis harfbuzz iconv mpg123 libarchive soxr nfs microdns fluidlite jpeg libplacebo vpx



Configuring the build
===============================
./bootstrap
mkdir win32
cd win32
export PKG_CONFIG_LIBDIR=$HOME/vlc/contrib/i686-w64-mingw32/lib/pkgconfig   \
export CFLAGS="-O2" \
export CXXFLAGS="-O2"
../configure --host=i686-w64-mingw32 \
	--disable-nls \
    --enable-live555 \
    --disable-realrtsp \
    --enable-avformat \
    --enable-swscale \
    --enable-avcodec \
    --enable-opus \
    --enable-opensles \
    --enable-matroska \
    --enable-taglib \
    --enable-dvbpsi \
    --enable-vlc \
    --enable-shared \
    --disable-update-check \
    --disable-vlm \
    --disable-dbus \
    --enable-lua \
    --disable-vcd \
    --disable-v4l2 \
    --enable-dvdread \
    --enable-dvdnav \
    --disable-bluray \
    --disable-linsys \
    --disable-decklink \
    --disable-libva \
    --disable-dv1394 \
    --enable-mod \
    --disable-sid \
    --disable-gme \
    --disable-tremor \
    --disable-mad \
    --enable-mpg123 \
    --disable-dca \
    --disable-sdl-image \
    --enable-zvbi \
    --disable-fluidsynth \
    --enable-fluidlite \
    --disable-jack \
    --disable-pulse \
    --disable-alsa \
    --disable-samplerate \
    --disable-sdl \
    --disable-xcb \
    --disable-qt \
    --disable-skins2 \
    --disable-mtp \
    --disable-notify \
    --enable-libass \
    --disable-svg \
    --disable-udev \
    --enable-libxml2 \
    --disable-caca \
    --disable-goom \
    --disable-projectm \
    --enable-sout \
    --enable-vorbis \
    --disable-faad \
    --enable-x264 \
    --disable-schroedinger \
    --disable-vncclient \
    --disable-vnc \
    --enable-jpeg
make
make package-win-common


all needed
==========================
cd needed/




















