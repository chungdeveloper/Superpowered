package vn.soft.dc.recordengine30;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.soft.dc.recordengine.MediaEngine;

public class MediaActivity extends AppCompatActivity {

    @BindView(R.id.play)
    Button play;
    private MediaEngine mMediaEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        ButterKnife.bind(this);

        mMediaEngine = new MediaEngine(getApplicationContext());
        String beat = Environment.getExternalStorageDirectory() + "/Superpowered/beat/Em-Gai-Mua-Beat-Huong-Tram.mp3";
        String vocal = Environment.getExternalStorageDirectory() + "/Superpowered/voice/Em-Gai-Mua-Huong-Tram.mp3";
        String buffersizeString = 1024 + "";
        if (Build.VERSION.SDK_INT >= 17) {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        }

        mMediaEngine.setAudioFormat(44100, Integer.parseInt(buffersizeString))
                .setResource(beat, vocal)
                .build();
    }

    @OnClick(R.id.play)
    public void onViewClicked() {
        mMediaEngine.play();
        mMediaEngine.setVolumeBeat(100);
        mMediaEngine.setVolumeVocal(100);
    }
}
