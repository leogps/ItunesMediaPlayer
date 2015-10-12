package com.gps.itunes.media.player.vlcj.player.impl;

import com.gps.itunes.media.player.vlcj.player.FXPlayerFrame;
import com.gps.itunes.media.player.vlcj.ui.player.BasicPlayerControlPanel;
import com.gps.itunes.media.player.vlcj.ui.player.events.PlayerControlEventListener;
import uk.co.caprica.vlcj.binding.internal.*;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.*;
import uk.co.caprica.vlcj.player.media.Media;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.List;

/**
 * FIXME: Find a way to create complete dummy objects in here.
 *
 * Created by leogps on 10/1/15.
 */
public class DummyFXPlayerFrame implements FXPlayerFrame {

    private final MediaPlayer dummyMediaPlayer = new MediaPlayer() {
        public void addMediaPlayerEventListener(MediaPlayerEventListener mediaPlayerEventListener) {

        }

        public void removeMediaPlayerEventListener(MediaPlayerEventListener mediaPlayerEventListener) {

        }

        public void enableEvents(long l) {

        }

        public void setStandardMediaOptions(String... strings) {

        }

        public boolean playMedia(String s, String... strings) {
            return false;
        }

        public boolean playMedia(Media media) {
            return false;
        }

        public boolean prepareMedia(String s, String... strings) {
            return false;
        }

        public boolean prepareMedia(Media media) {
            return false;
        }

        public boolean startMedia(String s, String... strings) {
            return false;
        }

        public boolean startMedia(Media media) {
            return false;
        }

        public void parseMedia() {

        }

        public void requestParseMedia() {

        }

        public boolean requestParseMediaWithOptions(libvlc_media_parse_flag_t... libvlc_media_parse_flag_ts) {
            return false;
        }

        public boolean isMediaParsed() {
            return false;
        }

        public MediaMeta getMediaMeta() {
            return null;
        }

        public MediaMeta getMediaMeta(libvlc_media_t libvlc_media_t) {
            return null;
        }

        public List<MediaMeta> getSubItemMediaMeta() {
            return null;
        }

        public MediaMetaData getMediaMetaData() {
            return null;
        }

        public List<MediaMetaData> getSubItemMediaMetaData() {
            return null;
        }

        public void addMediaOptions(String... strings) {

        }

        public void setRepeat(boolean b) {

        }

        public boolean getRepeat() {
            return false;
        }

        public void setPlaySubItems(boolean b) {

        }

        public int subItemCount() {
            return 0;
        }

        public int subItemIndex() {
            return 0;
        }

        public List<String> subItems() {
            return null;
        }

        public List<libvlc_media_t> subItemsMedia() {
            return null;
        }

        public MediaList subItemsMediaList() {
            return null;
        }

        public boolean playNextSubItem(String... strings) {
            return false;
        }

        public boolean playSubItem(int i, String... strings) {
            return false;
        }

        public boolean isPlayable() {
            return false;
        }

        public boolean isPlaying() {
            return false;
        }

        public boolean isSeekable() {
            return false;
        }

        public boolean canPause() {
            return false;
        }

        public boolean programScrambled() {
            return false;
        }

        public long getLength() {
            return 0;
        }

        public long getTime() {
            return 0;
        }

        public float getPosition() {
            return 0;
        }

        public float getFps() {
            return 0;
        }

        public float getRate() {
            return 0;
        }

        public int getVideoOutputs() {
            return 0;
        }

        public Dimension getVideoDimension() {
            return null;
        }

        public MediaDetails getMediaDetails() {
            return null;
        }

        public String getAspectRatio() {
            return null;
        }

        public float getScale() {
            return 0;
        }

        public String getCropGeometry() {
            return null;
        }

        public libvlc_media_stats_t getMediaStatistics() {
            return null;
        }

        public libvlc_media_stats_t getMediaStatistics(libvlc_media_t libvlc_media_t) {
            return null;
        }

        public libvlc_state_t getMediaState() {
            return null;
        }

        public libvlc_state_t getMediaPlayerState() {
            return null;
        }

        public int getTitleCount() {
            return 0;
        }

        public int getTitle() {
            return 0;
        }

        public void setTitle(int i) {

        }

        public int getVideoTrackCount() {
            return 0;
        }

        public int getVideoTrack() {
            return 0;
        }

        public int setVideoTrack(int i) {
            return 0;
        }

        public int getAudioTrackCount() {
            return 0;
        }

        public int getAudioTrack() {
            return 0;
        }

        public int setAudioTrack(int i) {
            return 0;
        }

        public void play() {

        }

        public boolean start() {
            return false;
        }

        public void stop() {

        }

        public void setPause(boolean b) {

        }

        public void pause() {

        }

        public void nextFrame() {

        }

        public void skip(long l) {

        }

        public void skipPosition(float v) {

        }

        public void setTime(long l) {

        }

        public void setPosition(float v) {

        }

        public int setRate(float v) {
            return 0;
        }

        public void setAspectRatio(String s) {

        }

        public void setScale(float v) {

        }

        public void setCropGeometry(String s) {

        }

        public boolean setAudioOutput(String s) {
            return false;
        }

        public void setAudioOutputDevice(String s, String s1) {

        }

        public String getAudioOutputDevice() {
            return null;
        }

        public List<AudioDevice> getAudioOutputDevices() {
            return null;
        }

        public boolean mute() {
            return false;
        }

        public void mute(boolean b) {

        }

        public boolean isMute() {
            return false;
        }

        public int getVolume() {
            return 0;
        }

        public void setVolume(int i) {

        }

        public int getAudioChannel() {
            return 0;
        }

        public void setAudioChannel(int i) {

        }

        public long getAudioDelay() {
            return 0;
        }

        public void setAudioDelay(long l) {

        }

        public int getChapterCount() {
            return 0;
        }

        public int getChapter() {
            return 0;
        }

        public void setChapter(int i) {

        }

        public void nextChapter() {

        }

        public void previousChapter() {

        }

        public void menuActivate() {

        }

        public void menuUp() {

        }

        public void menuDown() {

        }

        public void menuLeft() {

        }

        public void menuRight() {

        }

        public int getSpuCount() {
            return 0;
        }

        public int getSpu() {
            return 0;
        }

        public int setSpu(int i) {
            return 0;
        }

        public long getSpuDelay() {
            return 0;
        }

        public void setSpuDelay(long l) {

        }

        public void setSubTitleFile(String s) {

        }

        public void setSubTitleFile(File file) {

        }

        public int getTeletextPage() {
            return 0;
        }

        public void setTeletextPage(int i) {

        }

        public void toggleTeletext() {

        }

        public List<TrackDescription> getTitleDescriptions() {
            return null;
        }

        public List<TrackDescription> getVideoDescriptions() {
            return null;
        }

        public List<TrackDescription> getAudioDescriptions() {
            return null;
        }

        public List<TrackDescription> getSpuDescriptions() {
            return null;
        }

        public List<String> getChapterDescriptions(int i) {
            return null;
        }

        public List<String> getChapterDescriptions() {
            return null;
        }

        public List<List<String>> getAllChapterDescriptions() {
            return null;
        }

        public List<TitleDescription> getExtendedTitleDescriptions() {
            return null;
        }

        public List<ChapterDescription> getExtendedChapterDescriptions() {
            return null;
        }

        public List<ChapterDescription> getExtendedChapterDescriptions(int i) {
            return null;
        }

        public List<TrackInfo> getTrackInfo(TrackType... trackTypes) {
            return null;
        }

        public List<TrackInfo> getTrackInfo(libvlc_media_t libvlc_media_t, TrackType... trackTypes) {
            return null;
        }

        public libvlc_media_type_e getMediaType() {
            return null;
        }

        public libvlc_media_type_e getMediaType(libvlc_media_t libvlc_media_t) {
            return null;
        }

        public String getCodecDescription(libvlc_track_type_t libvlc_track_type_t, int i) {
            return null;
        }

        public List<List<TrackInfo>> getSubItemTrackInfo(TrackType... trackTypes) {
            return null;
        }

        public void setSnapshotDirectory(String s) {

        }

        public boolean saveSnapshot() {
            return false;
        }

        public boolean saveSnapshot(int i, int i1) {
            return false;
        }

        public boolean saveSnapshot(File file) {
            return false;
        }

        public boolean saveSnapshot(File file, int i, int i1) {
            return false;
        }

        public BufferedImage getSnapshot() {
            return null;
        }

        public BufferedImage getSnapshot(int i, int i1) {
            return null;
        }

        public void enableLogo(boolean b) {

        }

        public void setLogoOpacity(int i) {

        }

        public void setLogoOpacity(float v) {

        }

        public void setLogoLocation(int i, int i1) {

        }

        public void setLogoPosition(libvlc_logo_position_e libvlc_logo_position_e) {

        }

        public void setLogoFile(String s) {

        }

        public void setLogoImage(RenderedImage renderedImage) {

        }

        public void setLogo(Logo logo) {

        }

        public void enableMarquee(boolean b) {

        }

        public void setMarqueeText(String s) {

        }

        public void setMarqueeColour(Color color) {

        }

        public void setMarqueeColour(int i) {

        }

        public void setMarqueeOpacity(int i) {

        }

        public void setMarqueeOpacity(float v) {

        }

        public void setMarqueeSize(int i) {

        }

        public void setMarqueeTimeout(int i) {

        }

        public void setMarqueeLocation(int i, int i1) {

        }

        public void setMarqueePosition(libvlc_marquee_position_e libvlc_marquee_position_e) {

        }

        public void setMarquee(Marquee marquee) {

        }

        public void setDeinterlace(DeinterlaceMode deinterlaceMode) {

        }

        public void setAdjustVideo(boolean b) {

        }

        public boolean isAdjustVideo() {
            return false;
        }

        public float getContrast() {
            return 0;
        }

        public void setContrast(float v) {

        }

        public float getBrightness() {
            return 0;
        }

        public void setBrightness(float v) {

        }

        public int getHue() {
            return 0;
        }

        public void setHue(int i) {

        }

        public float getSaturation() {
            return 0;
        }

        public void setSaturation(float v) {

        }

        public float getGamma() {
            return 0;
        }

        public void setGamma(float v) {

        }

        public void setVideoTitleDisplay(libvlc_position_e libvlc_position_e, int i) {

        }

        public Equalizer getEqualizer() {
            return null;
        }

        public void setEqualizer(Equalizer equalizer) {

        }

        public String mrl() {
            return null;
        }

        public String mrl(libvlc_media_t libvlc_media_t) {
            return null;
        }

        public Object userData() {
            return null;
        }

        public void userData(Object o) {

        }

        public void release() {

        }

        public libvlc_media_player_t mediaPlayerInstance() {
            return null;
        }
    };

    private static final JSlider dummySlider = new JSlider();
    private static final JSlider dummyVolumeSlider = new JSlider();
    static {
        dummySlider.setVisible(false);
    }

    private static final JLabel dummyLabel = new JLabel();
    static {
        dummyLabel.setVisible(false);
    }

    private static final JPanel dummyPanel = new JPanel();
    static {
        dummyPanel.setVisible(false);
    }

    private static final Canvas dummyCanvas = new Canvas();
    static {
        dummyCanvas.setVisible(false);
    }

    private static final BasicPlayerControlPanel dummyBasicPlayerControlPanel = new BasicPlayerControlPanel() {
        @Override
        public void setPaused() {
            //
        }

        @Override
        public void setPlaying() {
            //
        }

        @Override
        public void setPlayerControlEventListener(PlayerControlEventListener listener) {
            //
        }

        @Override
        public JSlider getVolumeSlider() {
            return dummyVolumeSlider;
        }
    };
    static {
        dummyBasicPlayerControlPanel.setVisible(false);
    }

    private DummyFXPlayerFrame(Object o) {
        super();
    }

    public static FXPlayerFrame getDummyInstance() {
        return new DummyFXPlayerFrame(null);
    }

    public MediaPlayer getPlayer() {
        return dummyMediaPlayer;
    }

    public JSlider getSeekbar() {
        return dummySlider;
    }

    public JLabel getStartTimeLabel() {
        return dummyLabel;
    }

    public JLabel getEndTimeLabel() {
        return dummyLabel;
    }

    public void updateSeekbar() {

    }

    public void requestFocus() {

    }

    public JPanel getVideoPanel() {
        return dummyPanel;
    }

    public Canvas getFrameCanvas() {
        return dummyCanvas;
    }

    public BasicPlayerControlPanel getBasicPlayerControlPanel() {
        return dummyBasicPlayerControlPanel;
    }

    public void play(String location) {

    }

    public void setTitle(String title) {

    }
}
