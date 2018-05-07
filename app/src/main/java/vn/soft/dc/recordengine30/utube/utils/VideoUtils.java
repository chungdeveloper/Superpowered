package vn.soft.dc.recordengine30.utube.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

import java.io.File;
import java.util.List;

import vn.soft.dc.recordengine.RecorderEngine;

/**
 * Created by Le Duc Chung on 6/26/2017.
 * on project 'recordenginev3'
 */

public class VideoUtils {

    public interface OnAudioCutterListener {
        void onSuccess(String msg);

        void onFail(String err);
    }

    public static synchronized void videoCutToEnd(Context context, String inputPath, String outputPath, String start, final OnAudioCutterListener onAudioCutterListener) {
        if (onAudioCutterListener == null) return;
        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            String[] cmd = ("-i " + inputPath + " -vcodec copy -acodec copy -copyinkf -ss " + start + " -async 1 " + outputPath).split(" ");
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);
                    onAudioCutterListener.onSuccess(message);
                }

                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                    onAudioCutterListener.onFail(message);
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });
        } catch (Exception ex) {
            onAudioCutterListener.onFail(ex.toString());
        }
    }

    public static void videoCutter(final Context context, String inputPath, final String outputPath, String start, String end, final OnAudioCutterListener onAudioCutterListener) {
        if (onAudioCutterListener == null) return;
        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            String[] cmd = ("-i " + inputPath + " -vcodec copy -acodec copy -copyinkf -ss " + start + " -t " + end/* + " -async 1 -strict -2 " */ + " " + outputPath).split(" ");

            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);
                    onAudioCutterListener.onSuccess(message);
                }

                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                    onAudioCutterListener.onFail(message);
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });
        } catch (Exception ex) {
            onAudioCutterListener.onFail(ex.toString());
        }
    }


    public static void concatMediaFile(Context context, List<String> files, String outPath, OnAudioCutterListener onAudioCutterListener) {
        if (onAudioCutterListener == null || context == null) return;
        String pathStorage = FileUtils.createMusicListFile(files, RecorderEngine.RECORD_URI.MUSIC_TV_360_DIRECTORY_TEMP);
        mergeAudio(context, pathStorage, outPath, onAudioCutterListener);
    }

    public static void mergeAudio(Context context, String filesStorage, final String outPath, final OnAudioCutterListener onAudioCutterListener) {
        if (onAudioCutterListener == null) return;
        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            String[] cmd = ("-f concat -safe 0 -i " + filesStorage + " -c copy -y " + outPath).split(" ");
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);
                    onAudioCutterListener.onSuccess(outPath);
                }

                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                    onAudioCutterListener.onFail(message);
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });
        } catch (Exception ex) {
            Log.e("Cutter", ex.toString());
        }
    }

    //ffmpeg.exe -f lavfi -i color=s=640x480 -t 00:01:19.5 -f lavfi -i anullsrc -strict -2 -shortest -y out.mp4

    public static void mergeVideo(Context context, String filesStorage, String outPath, final OnAudioCutterListener onAudioCutterListener) {
        if (onAudioCutterListener == null) return;
        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            String str = "-auto_convert 1 -f concat -safe 0 -i " + filesStorage + " -vcodec copy -acodec copy -copyinkf " +/* " -c copy " +*/ outPath;
            String[] cmd = (str).split(" ");
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);
                    onAudioCutterListener.onSuccess(message);
                }

                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                    onAudioCutterListener.onFail(message);
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });
        } catch (Exception ex) {
            onAudioCutterListener.onFail(ex.toString());
        }
    }

    public static long getDuration(Context context, String inputPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.fromFile(new File(inputPath)));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        retriever.release();
        return timeInMillisec;
    }
}
