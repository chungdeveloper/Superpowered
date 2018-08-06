package vn.soft.dc.recordengine30.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import vn.soft.dc.recordengine30.model.Music;

/**
 * Created by MSi-Gaming on 08/03/2016.
 */
public class SongsManager {
    private final String MEDIA_PATH;
    private String mp3Pattern;
    private ArrayList<Music> songsList;


    public SongsManager(String path) {
        this.MEDIA_PATH = path;
        this.songsList = new ArrayList();
        this.mp3Pattern = ".mp3";
    }

    public ArrayList<Music> getPlayList() {
        if (this.MEDIA_PATH != null) {
            File[] listFiles = new File(this.MEDIA_PATH).listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    System.out.println(file.getAbsolutePath());
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }
                }
            }
        }
        return this.songsList;
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }
                }
            }
        }
    }

    private void addSongToList(File song) {
        if (song.getName().endsWith(this.mp3Pattern)) {
            Music music = new Music(song.getPath(), true, song.getName().substring(0, song.getName().length() - 4));
            this.songsList.add(music);
        }
    }

    class FileExtensionFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {
            return filename.endsWith(".mp3") || filename.endsWith(".MP3");
        }
    }
}
