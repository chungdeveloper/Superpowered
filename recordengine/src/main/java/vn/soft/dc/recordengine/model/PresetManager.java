package vn.soft.dc.recordengine.model;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by Le Duc Chung on 08/03/2016.
 * on project 'RecordEngine'
 */
@SuppressWarnings("ALL")
public class PresetManager {
    private final String MEDIA_PATH;
    private String mp3Pattern;
    private ArrayList<Preset> songsList;


    public PresetManager() {
        this.MEDIA_PATH = Environment.getExternalStorageDirectory() + "/Superpowered/preset";
        this.songsList = new ArrayList();
        this.mp3Pattern = ".txt";
    }

    public ArrayList<Preset> getPlayList() {
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
            Preset music = new Preset(song.getPath());
            this.songsList.add(music);
        }
    }

    class FileExtensionFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {
            return filename.endsWith(".txt") || filename.endsWith(".TXT");
        }
    }
}
