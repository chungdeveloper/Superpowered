package vn.soft.dc.recordengine30.utube;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.soft.dc.recordengine30.R;
import vn.soft.dc.recordengine30.utube.utils.VideoUtils;
import vn.soft.dc.recordengine30.utube.utils.YouTubeFailureRecoveryActivity;

public class YoutuberecordActivity extends YouTubeFailureRecoveryActivity {

    @BindView(R.id.vYoutubePlayer)
    YouTubePlayerView vYoutubePlayer;
    @BindView(R.id.btnPlay)
    Button btnPlay;
    @BindView(R.id.btnPause)
    Button btnPause;
    @BindView(R.id.tvStatus)
    TextView tvStatus;

    private YoutubeRecordPresenter mYoutubeRecordPresenter;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtuberecord);
        ButterKnife.bind(this);

        vYoutubePlayer.initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return vYoutubePlayer;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        mYoutubeRecordPresenter = new YoutubeRecordPresenter(getApplicationContext(), youTubePlayer, "ygBpqTot6K4");
        mYoutubeRecordPresenter.enableRecord(true);
    }

    @OnClick({R.id.btnPlay, R.id.btnPause, R.id.btnPlayOrigin, R.id.btnFinish, R.id.btnReplay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                mYoutubeRecordPresenter.resume();
                break;
            case R.id.btnPause:
                mYoutubeRecordPresenter.pause();
                break;
            case R.id.btnPlayOrigin:
                mYoutubeRecordPresenter.play();
                break;
            case R.id.btnFinish:
                mYoutubeRecordPresenter.finish(new VideoUtils.OnAudioCutterListener() {
                    @Override
                    public void onSuccess(String msg) {
                        path = msg;
                        Toast.makeText(YoutuberecordActivity.this, "File: " + msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String err) {
                        Toast.makeText(YoutuberecordActivity.this, err, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btnReplay:
                mYoutubeRecordPresenter.enableRecord(false);
                mYoutubeRecordPresenter.restartVideo(path);
                break;
        }
    }
}
