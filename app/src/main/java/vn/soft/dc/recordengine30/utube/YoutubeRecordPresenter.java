package vn.soft.dc.recordengine30.utube;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.google.android.youtube.player.YouTubePlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.soft.dc.recordengine.RecorderEngine;
import vn.soft.dc.recordengine30.utube.utils.FileUtils;
import vn.soft.dc.recordengine30.utube.utils.VideoUtils;

/**
 * Created by Le Duc Chung on 2018-03-19.
 * on project 'recordenginev3'
 */

public class YoutubeRecordPresenter {
    private static final String TAG = "chung_utube";
    private static final String TAG_FUNC = "chung_func";
    private static final String CONTROL = "chung_control";

    private RecorderEngine mRecorderEngine;

    private YouTubePlayer mYouTubePlayer;
    private int mBuffTimer;
    private boolean flagRecord;
    private boolean flagPause;
    private boolean flagResume;
    private boolean flagAd;
    private boolean isRecording;
    private boolean flagStarter;
    private Handler mHandler;
    private Context mContext;
    private MediaPlayer mVocalPlayer;

    private List<String> mRecordFiles;
    private boolean flagReplay;

    public YoutubeRecordPresenter(Context context, YouTubePlayer player, String uTubeID) {
        flagStarter = true;
        flagResume = false;
        flagRecord = false;
        flagPause = false;
        mContext = context;
        mHandler = new Handler();
        mRecordFiles = new ArrayList<>();

        initRecord();
        initYoutube(player, uTubeID);
    }

    private void initRecord() {
        String samplerateString = null, buffersizeString = null;
        if (Build.VERSION.SDK_INT >= 17) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        }
        samplerateString = "44100";
        if (buffersizeString == null) buffersizeString = "512";

        Log.d("ChungLD", buffersizeString);
        mRecorderEngine = new RecorderEngine(Integer.parseInt(samplerateString), Integer.parseInt(buffersizeString));
        mRecorderEngine.enablePlayback(false);
        mRecorderEngine.setOnRecordEventListener(new RecorderEngine.OnRecordEventListener() {
            @Override
            public void onInitSuccess() {

            }

            @Override
            public void onFrequencyListener(double freq) {

            }
        });
    }

    private void startRecord() {
        if (isRecording || !flagRecord) return;
        String recordPath = RecorderEngine.RECORD_URI.MUSIC_TV_360_DIRECTORY_TEMP + "chung_" + mRecordFiles.size();
        mRecordFiles.add(recordPath + ".wav");
        mRecorderEngine.startRecord(recordPath);
        isRecording = true;
    }

    private void pauseRecord() {
        if (!isRecording || !flagRecord) return;
        mRecorderEngine.stopRecordFile();
        isRecording = false;
    }

    private void initYoutube(YouTubePlayer player, String uTubeID) {
        mYouTubePlayer = player;
        mYouTubePlayer.cueVideo(uTubeID);
        mYouTubePlayer.setFullscreen(false);
        mYouTubePlayer.setPlayerStateChangeListener(onPlayerStateChangeListener);
        mYouTubePlayer.setPlaybackEventListener(onPlaybackEventListener);
    }

    public void enableRecord(boolean isCanRecord) {
        flagRecord = isCanRecord;
    }

    public void restartVideo(String resource) {
        try {
            mVocalPlayer = new MediaPlayer();
            mVocalPlayer.setDataSource(resource);
            mVocalPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mYouTubePlayer.seekToMillis(0);
                    flagReplay = true;
                }
            });
            mVocalPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void resume() {
        flagResume = true;
        Log.d(TAG_FUNC, "Func Resume: " + mYouTubePlayer.getCurrentTimeMillis());
        mYouTubePlayer.seekToMillis(mBuffTimer);
//        mYouTubePlayer.play();
    }

    public void play() {
        Log.d(TAG_FUNC, "Func Play: " + mYouTubePlayer.getCurrentTimeMillis());
        mYouTubePlayer.play();
    }

    public void pause() {
        Log.d(TAG_FUNC, "Func PAUSE: " + mYouTubePlayer.getCurrentTimeMillis());
        mYouTubePlayer.pause();
        flagPause = true;
        Log.d(CONTROL, "StartPause");
    }

    public void finish(VideoUtils.OnAudioCutterListener onAudioCutterListener) {
        pauseRecord();
        mYouTubePlayer.pause();
        VideoUtils.concatMediaFile(mContext, mRecordFiles
                , FileUtils.createFileDirectory(RecorderEngine.RECORD_URI.MUSIC_TV_360_DIRECTORY_DRAFT + "/recordUtube/") + System.currentTimeMillis() + ".wav"
                , onAudioCutterListener);
    }

    private Runnable onRunnableVideo = new Runnable() {
        @Override
        public void run() {
            mYouTubePlayer.play();
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private YouTubePlayer.PlayerStateChangeListener onPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {
            Log.d(TAG, "onLoading: ");
        }

        @Override
        public void onLoaded(String s) {
            Log.d(TAG, "onLoaded: " + s);
            if (flagStarter && !flagAd) {
                mYouTubePlayer.play();
                flagStarter = false;
            }
        }

        @Override
        public void onAdStarted() {
            Log.d(TAG, "onAdStarted: ");
            flagAd = true;
        }

        @Override
        public void onVideoStarted() {
            Log.d(TAG, "onVideoStarted: ");
        }

        @Override
        public void onVideoEnded() {
            Log.d(TAG, "onVideoEnded: ");
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            Log.e(TAG, "onError: " + errorReason);
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private YouTubePlayer.PlaybackEventListener onPlaybackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
            Log.d(TAG, "onPlaying: " + mYouTubePlayer.getCurrentTimeMillis());
            Log.d(CONTROL, "onPlaying: " + mYouTubePlayer.getCurrentTimeMillis());
            startRecord();
            if (flagReplay) {
                mVocalPlayer.seekTo(mYouTubePlayer.getCurrentTimeMillis());
                mVocalPlayer.start();
                flagReplay = false;
            }
        }

        @Override
        public void onPaused() {
            int oldBuffer = mBuffTimer;
            mBuffTimer = mYouTubePlayer.getCurrentTimeMillis();
            Log.d(TAG, "onPaused: " + mBuffTimer);
            if (flagPause) {
                Log.d(CONTROL, "onPaused: " + mBuffTimer);
                flagPause = false;
                pauseRecord();
                return;
            }
            if (flagResume) {
                Log.d(CONTROL, "onResume: " + (mBuffTimer - oldBuffer));
                startRecord();
//                mHandler.post(onRunnableVideo);
                mYouTubePlayer.play();
//                mHandler.postDelayed(onRunnableVideo, mBuffTimer - oldBuffer);
                flagResume = false;
            }

        }

        @Override
        public void onStopped() {
            Log.d(TAG, "onStopped: ");
        }

        @Override
        public void onBuffering(boolean b) {
            if (!b) {
                Log.d(TAG, "onBuffering: " + mYouTubePlayer.getCurrentTimeMillis());
            }
        }

        @Override
        public void onSeekTo(int i) {
            if (flagReplay) {
                mYouTubePlayer.play();
            }
            Log.d(TAG, "onSeekTo: " + i);
        }
    };
}
