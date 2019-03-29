/*****************************************************************************
 * Media.java
 *****************************************************************************
 * Copyright © 2015 VLC authors, VideoLAN and VideoLabs
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package net.ajcloud.wansviewplusw.support.vlc;

@SuppressWarnings("unused, JniMissingFunction")
public class Media {
    private final static String TAG = "LibVLC/Media";

    /**
     * libvlc_media_type_t
     */
    public static class Type {
        public static final int Unknown = 0;
        public static final int File = 1;
        public static final int Directory = 2;
        public static final int Disc = 3;
        public static final int Stream = 4;
        public static final int Playlist = 5;
    }

    /**
     * see libvlc_meta_t
     */
    public static class Meta {
        public static final int Title = 0;
        public static final int Artist = 1;
        public static final int Genre = 2;
        public static final int Copyright = 3;
        public static final int Album = 4;
        public static final int TrackNumber = 5;
        public static final int Description = 6;
        public static final int Rating = 7;
        public static final int Date = 8;
        public static final int Setting = 9;
        public static final int URL = 10;
        public static final int Language = 11;
        public static final int NowPlaying = 12;
        public static final int Publisher = 13;
        public static final int EncodedBy = 14;
        public static final int ArtworkURL = 15;
        public static final int TrackID = 16;
        public static final int TrackTotal = 17;
        public static final int Director = 18;
        public static final int Season = 19;
        public static final int Episode = 20;
        public static final int ShowName = 21;
        public static final int Actors = 22;
        public static final int AlbumArtist = 23;
        public static final int DiscNumber = 24;
        public static final int MAX = 25;
    }

    /**
     * see libvlc_state_t
     */
    public static class State {
        public static final int NothingSpecial = 0;
        public static final int Opening = 1;
        public static final int Buffering = 2;
        public static final int Playing = 3;
        public static final int Paused = 4;
        public static final int Stopped = 5;
        public static final int Ended = 6;
        public static final int Error = 7;
        public static final int MAX = 8;
    }

    /**
     * see libvlc_media_parse_flag_t
     */
    public static class Parse {
        public static final int ParseLocal = 0;
        public static final int ParseNetwork = 0x01;
        public static final int FetchLocal = 0x02;
        public static final int FetchNetwork = 0x04;
        public static final int DoInteract = 0x08;
    }

    /*
     * see libvlc_media_parsed_status_t
     */
    public static class ParsedStatus {
        public static final int Skipped = 1;
        public static final int Failed = 2;
        public static final int Timeout = 3;
        public static final int Done = 4;
    }

    /**
     * see libvlc_media_track_t
     */
    public static abstract class Track {
        public static class Type {
            public static final int Unknown = -1;
            public static final int Audio = 0;
            public static final int Video = 1;
            public static final int Text = 2;
        }

        public final int type;
        public final String codec;
        public final String originalCodec;
        public final int id;
        public final int profile;
        public final int level;
        public final int bitrate;
        public final String language;
        public final String description;

        private Track(int type, String codec, String originalCodec, int id, int profile,
                      int level, int bitrate, String language, String description) {
            this.type = type;
            this.codec = codec;
            this.originalCodec = originalCodec;
            this.id = id;
            this.profile = profile;
            this.level = level;
            this.bitrate = bitrate;
            this.language = language;
            this.description = description;
        }
    }

    /**
     * see libvlc_audio_track_t
     */
    public static class AudioTrack extends Track {
        public final int channels;
        public final int rate;

        private AudioTrack(String codec, String originalCodec, int id, int profile,
                           int level, int bitrate, String language, String description,
                           int channels, int rate) {
            super(Type.Audio, codec, originalCodec, id, profile, level, bitrate, language, description);
            this.channels = channels;
            this.rate = rate;
        }
    }

    @SuppressWarnings("unused") /* Used from JNI */
    private static Track createAudioTrackFromNative(String codec, String originalCodec, int id, int profile,
                                                    int level, int bitrate, String language, String description,
                                                    int channels, int rate) {
        return new AudioTrack(codec, originalCodec, id, profile,
                level, bitrate, language, description,
                channels, rate);
    }

    /**
     * see libvlc_video_track_t
     */
    public static class VideoTrack extends Track {
        public static final class Orientation {
            /**
             * Top line represents top, left column left
             */
            public static final int TopLeft = 0;
            /**
             * Flipped horizontally
             */
            public static final int TopRight = 1;
            /**
             * Flipped vertically
             */
            public static final int BottomLeft = 2;
            /**
             * Rotated 180 degrees
             */
            public static final int BottomRight = 3;
            /**
             * Transposed
             */
            public static final int LeftTop = 4;
            /**
             * Rotated 90 degrees clockwise (or 270 anti-clockwise)
             */
            public static final int LeftBottom = 5;
            /**
             * Rotated 90 degrees anti-clockwise
             */
            public static final int RightTop = 6;
            /**
             * Anti-transposed
             */
            public static final int RightBottom = 7;
        }

        public static final class Projection {
            public static final int Rectangular = 0;
            /**
             * 360 spherical
             */
            public static final int EquiRectangular = 1;
            public static final int CubemapLayoutStandard = 0x100;
        }

        public final int height;
        public final int width;
        public final int sarNum;
        public final int sarDen;
        public final int frameRateNum;
        public final int frameRateDen;
        public final int orientation;
        public final int projection;

        private VideoTrack(String codec, String originalCodec, int id, int profile,
                           int level, int bitrate, String language, String description,
                           int height, int width, int sarNum, int sarDen, int frameRateNum, int frameRateDen,
                           int orientation, int projection) {
            super(Type.Video, codec, originalCodec, id, profile, level, bitrate, language, description);
            this.height = height;
            this.width = width;
            this.sarNum = sarNum;
            this.sarDen = sarDen;
            this.frameRateNum = frameRateNum;
            this.frameRateDen = frameRateDen;
            this.orientation = orientation;
            this.projection = projection;
        }
    }

    @SuppressWarnings("unused") /* Used from JNI */
    private static Track createVideoTrackFromNative(String codec, String originalCodec, int id, int profile,
                                                    int level, int bitrate, String language, String description,
                                                    int height, int width, int sarNum, int sarDen, int frameRateNum, int frameRateDen,
                                                    int orientation, int projection) {
        return new VideoTrack(codec, originalCodec, id, profile,
                level, bitrate, language, description,
                height, width, sarNum, sarDen, frameRateNum, frameRateDen, orientation, projection);
    }

    /**
     * see libvlc_subtitle_track_t
     */
    public static class SubtitleTrack extends Track {
        public final String encoding;

        private SubtitleTrack(String codec, String originalCodec, int id, int profile,
                              int level, int bitrate, String language, String description,
                              String encoding) {
            super(Type.Text, codec, originalCodec, id, profile, level, bitrate, language, description);
            this.encoding = encoding;
        }
    }

    @SuppressWarnings("unused") /* Used from JNI */
    private static Track createSubtitleTrackFromNative(String codec, String originalCodec, int id, int profile,
                                                       int level, int bitrate, String language, String description,
                                                       String encoding) {
        return new SubtitleTrack(codec, originalCodec, id, profile,
                level, bitrate, language, description,
                encoding);
    }

    /**
     * see libvlc_subtitle_track_t
     */
    public static class UnknownTrack extends Track {
        private UnknownTrack(String codec, String originalCodec, int id, int profile,
                             int level, int bitrate, String language, String description) {
            super(Type.Unknown, codec, originalCodec, id, profile, level, bitrate, language, description);
        }
    }

    @SuppressWarnings("unused") /* Used from JNI */
    private static Track createUnknownTrackFromNative(String codec, String originalCodec, int id, int profile,
                                                      int level, int bitrate, String language, String description) {
        return new UnknownTrack(codec, originalCodec, id, profile,
                level, bitrate, language, description);
    }

    /**
     * see libvlc_media_slave_t
     */
    public static class Slave {
        public static class Type {
            public static final int Subtitle = 0;
            public static final int Audio = 1;
        }

        /**
         * @see Type
         */
        public final int type;
        /**
         * From 0 (low priority) to 4 (high priority)
         */
        public final int priority;
        public final String uri;

        public Slave(int type, int priority, String uri) {
            this.type = type;
            this.priority = priority;
            this.uri = uri;
        }
    }

    @SuppressWarnings("unused") /* Used from JNI */
    private static Slave createSlaveFromNative(int type, int priority, String uri) {
        return new Slave(type, priority, uri);
    }

    /**
     * see libvlc_media_stats_t
     */
    public static class Stats {

        public final int readBytes;
        public final float inputBitrate;
        public final int demuxReadBytes;
        public final float demuxBitrate;
        public final int demuxCorrupted;
        public final int demuxDiscontinuity;
        public final int decodedVideo;
        public final int decodedAudio;
        public final int displayedPictures;
        public final int lostPictures;
        public final int playedAbuffers;
        public final int lostAbuffers;
        public final int sentPackets;
        public final int sentBytes;
        public final float sendBitrate;

        public Stats(int readBytes, float inputBitrate, int demuxReadBytes,
                     float demuxBitrate, int demuxCorrupted,
                     int demuxDiscontinuity, int decodedVideo, int decodedAudio,
                     int displayedPictures, int lostPictures, int playedAbuffers,
                     int lostAbuffers, int sentPackets, int sentBytes,
                     float sendBitrate) {
            this.readBytes = readBytes;
            this.inputBitrate = inputBitrate;
            this.demuxReadBytes = demuxReadBytes;
            this.demuxBitrate = demuxBitrate;
            this.demuxCorrupted = demuxCorrupted;
            this.demuxDiscontinuity = demuxDiscontinuity;
            this.decodedVideo = decodedVideo;
            this.decodedAudio = decodedAudio;
            this.displayedPictures = displayedPictures;
            this.lostPictures = lostPictures;
            this.playedAbuffers = playedAbuffers;
            this.lostAbuffers = lostAbuffers;
            this.sentPackets = sentPackets;
            this.sentBytes = sentBytes;
            this.sendBitrate = sendBitrate;
        }
    }
}
