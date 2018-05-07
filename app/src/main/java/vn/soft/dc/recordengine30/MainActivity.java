package vn.soft.dc.recordengine30;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.soft.dc.recordengine.RecorderEngine;
import vn.soft.dc.recordengine.model.Preset;

import static vn.soft.dc.recordengine.util.FileUtils.readRawTextFile;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 100;

    @BindView(R.id.etOffset)
    TextView etOffset;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnStop)
    Button btnStop;
    @BindView(R.id.btnEnable)
    Button btnEnable;
    @BindView(R.id.btnAcoustic)
    Button btnAcoustic;
    @BindView(R.id.btnBolero)
    Button btnBolero;
    @BindView(R.id.btnMaster)
    Button btnMaster;
    @BindView(R.id.btnPopStar)
    Button btnPopStar;
    @BindView(R.id.btnPopStarFix)
    Button btnPopStarFix;
    @BindView(R.id.btnRap)
    Button btnRap;
    @BindView(R.id.btnStudio)
    Button btnStudio;

    private RecorderEngine mRecorderEngine;
    private List<Preset> mPresets;
    private Gson gson;
    private boolean isEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        claimPermission();
    }

    @OnClick({R.id.btnMedia, R.id.btnStart, R.id.btnStop, R.id.btnEnable, R.id.btnAcoustic, R.id.btnBolero, R.id.btnMaster, R.id.btnPopStar, R.id.btnPopStarFix, R.id.btnRap, R.id.btnStudio, R.id.btnRelease, R.id.btnInit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnMedia:
                mRecorderEngine.release();
                Intent intent = new Intent(getApplicationContext(), MediaActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnStart:
                mRecorderEngine.startRecord(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".wav");
                break;
            case R.id.btnStop:
                mRecorderEngine.stopRecord();
                break;
            case R.id.btnEnable:
                isEnable = !isEnable;
                mRecorderEngine.enableEffectVocal(isEnable);
                btnEnable.setText(isEnable ? "Đang hoạt động" : "Không hoạt động");
                mRecorderEngine.enablePlayback(isEnable);
                break;
            case R.id.btnAcoustic:
                mRecorderEngine.changeEffect(mPresets.get(0));
                break;
            case R.id.btnBolero:
                mRecorderEngine.changeEffect(mPresets.get(1));
                break;
            case R.id.btnMaster:
                mRecorderEngine.changeEffect(mPresets.get(2));
                break;
            case R.id.btnPopStar:
                mRecorderEngine.changeEffect(mPresets.get(3));
                break;
            case R.id.btnPopStarFix:
                mRecorderEngine.changeEffect(mPresets.get(4));
                break;
            case R.id.btnRap:
                mRecorderEngine.changeEffect(mPresets.get(5));
                break;
            case R.id.btnStudio:
                mRecorderEngine.changeEffect(mPresets.get(6));
                break;
            case R.id.btnRelease:
                Log.d("RecordEngine", "start release: " + System.currentTimeMillis());
                mRecorderEngine.release();
                Log.d("RecordEngine", "end release: " + System.currentTimeMillis());
                break;
            case R.id.btnInit:
                claimPermission();
                break;
        }
    }

    private void claimPermission() {
        if (isPermissionGranted(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            doCreate();
            return;
        }

        requestPermission(PERMISSION_CODE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void doCreate() {
        gson = new Gson();
        mPresets = new ArrayList<>();
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.acoustic), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.bolero), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.master), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.pop_star), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.pop_star_fix), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.rap), Preset.class));
        mPresets.add(gson.fromJson(readRawTextFile(getApplicationContext(), R.raw.studio), Preset.class));

        String samplerateString = null, buffersizeString = null;
        if (Build.VERSION.SDK_INT >= 17) {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        }
        samplerateString = "48000";
        if (buffersizeString == null) buffersizeString = "512";
        buffersizeString = "16";
        Log.d("ChungLD", buffersizeString);
        mRecorderEngine = new RecorderEngine(Integer.parseInt(samplerateString), Integer.parseInt(buffersizeString), onRecordEventListener);
        isEnable = false;
        btnEnable.setText(isEnable ? "Đang hoạt động" : "Đã vô hiệu hóa");
    }

    private RecorderEngine.OnRecordEventListener onRecordEventListener = new RecorderEngine.OnRecordEventListener() {
        @Override
        public void onInitSuccess() {
            Toast.makeText(MainActivity.this, "Khởi tạo thành công", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFrequencyListener(final double freq) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etOffset.setText(freq + "Hz");
                }
            });
        }
    };

    public boolean isPermissionGranted(String... permissions) {
        for (String value : permissions) {
            if (isPermissionGranted(value)) continue;
            return false;
        }
        return true;
    }

    public void requestPermission(int requestCode, String... permissions) {
        requestPermission(permissions, requestCode);
    }

    public void requestPermission(String[] permissionList, int codeRequest) {
        ActivityCompat.requestPermissions(this, permissionList, codeRequest);
    }

    public boolean isPermissionGranted(String permissions) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        claimPermission();
    }
}
