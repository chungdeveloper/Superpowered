package vn.soft.dc.recordengine30.model;

import android.media.MediaMetadataRetriever;
import android.util.Log;

/**
 * Created by MSi-Gaming on 07/03/2016.
 */
public class Music {
    private String url;
    private boolean isOffline;
    private boolean isPlaying;
    private String nameTrack;
    private String Title;
    private String Artist;
    private String Album;
    private int Year;
    private byte[] albumImage;
    private MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();


    public Music() {
    }

    public Music(String url, boolean isOffline, String nameTrack) {
        this.url = url;
        this.isOffline = isOffline;
        this.nameTrack = nameTrack;
        try {
            mediaMetadataRetriever.setDataSource(this.url);
            Title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            Artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            Album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            Year = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
            albumImage = mediaMetadataRetriever.getEmbeddedPicture();
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public String getUrl() {
        return url;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }

    public String getNameTrack() {
        return nameTrack;
    }


    public String getArtist() {
        return Artist;
    }

    public String getTitle() {
        return Title;
    }

    public String getAlbum() {
        return Album;
    }

    public int getYear() {
        return Year;
    }
}
