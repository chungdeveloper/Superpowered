package vn.soft.dc.recordengine30;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.soft.dc.recordengine.RecorderEngine;
import vn.soft.dc.recordengine.model.Preset;

import static android.widget.Toast.LENGTH_SHORT;
import static vn.soft.dc.recordengine.util.FileUtils.readRawTextFile;

public class DevActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 100;


    static int REVERB_NON = 0;
    static int REVERB_DRY = 1;
    static int REVERB_WET = 2;
    static int REVERB_WIDTH = 3;
    static int REVERB_MIX = 4;
    static int REVERB_ROOMSIZE = 5;
    static int REVERB_DAMP = 6;

    @BindView(R.id.dryEcho)
    TextView dryEcho;
    @BindView(R.id.dry_valueEcho)
    TextView dryValueEcho;
    @BindView(R.id.sbDryEcho)
    SeekBar sbDryEcho;
    @BindView(R.id.wetEcho)
    TextView wetEcho;
    @BindView(R.id.wet_valueEcho)
    TextView wetValueEcho;
    @BindView(R.id.sbWetEcho)
    SeekBar sbWetEcho;
    @BindView(R.id.wet_containerEcho)
    RelativeLayout wetContainerEcho;
    @BindView(R.id.widthEcho)
    TextView widthEcho;
    @BindView(R.id.bpmValue)
    TextView bpmValue;
    @BindView(R.id.sbBpm)
    SeekBar sbBpm;
    @BindView(R.id.width_containerEcho)
    RelativeLayout widthContainerEcho;
    @BindView(R.id.roomsizeEcho)
    TextView roomsizeEcho;
    @BindView(R.id.beatValue)
    TextView beatValue;
    @BindView(R.id.sbBeatsEcho)
    SeekBar sbBeatsEcho;
    @BindView(R.id.roomsize_containerEcho)
    RelativeLayout roomsizeContainerEcho;
    @BindView(R.id.mixEcho)
    TextView mixEcho;
    @BindView(R.id.mixEchoValue)
    TextView mixEchoValue;
    @BindView(R.id.sbEchoMix)
    SeekBar sbEchoMix;
    @BindView(R.id.mix_containerEcho)
    RelativeLayout mixContainerEcho;
    @BindView(R.id.decayEcho)
    TextView decayEcho;
    @BindView(R.id.decayEchoValue)
    TextView decayEchoValue;
    @BindView(R.id.sbDecayEcho)
    SeekBar sbDecayEcho;
    @BindView(R.id.damp_containerEcho)
    RelativeLayout dampContainerEcho;
    @BindView(R.id.rlEcho)
    LinearLayout rlEcho;
    @BindView(R.id.reverbTitle)
    TextView reverbTitle;
    @BindView(R.id.dryReverb)
    TextView dryReverb;
    @BindView(R.id.dry_value)
    TextView dryValue;
    @BindView(R.id.reverb_dry)
    SeekBar reverbDry;
    @BindView(R.id.dry_container)
    RelativeLayout dryContainer;
    @BindView(R.id.wet)
    TextView wet;
    @BindView(R.id.wet_value)
    TextView wetValue;
    @BindView(R.id.reverb_wet)
    SeekBar reverbWet;
    @BindView(R.id.wet_container)
    RelativeLayout wetContainer;
    @BindView(R.id.width)
    TextView width;
    @BindView(R.id.width_value)
    TextView widthValue;
    @BindView(R.id.reverb_width)
    SeekBar reverbWidth;
    @BindView(R.id.width_container)
    RelativeLayout widthContainer;
    @BindView(R.id.roomsize)
    TextView roomsize;
    @BindView(R.id.roomsize_value)
    TextView roomsizeValue;
    @BindView(R.id.reverb_roomsize)
    SeekBar reverbRoomsize;
    @BindView(R.id.roomsize_container)
    RelativeLayout roomsizeContainer;
    @BindView(R.id.mix)
    TextView mix;
    @BindView(R.id.mix_value)
    TextView mixValue;
    @BindView(R.id.reverb_mix)
    SeekBar reverbMix;
    @BindView(R.id.mix_container)
    RelativeLayout mixContainer;
    @BindView(R.id.damp)
    TextView damp;
    @BindView(R.id.damp_value)
    TextView dampValue;
    @BindView(R.id.reverb_damp)
    SeekBar reverbDamp;
    @BindView(R.id.damp_container)
    RelativeLayout dampContainer;
    @BindView(R.id.tvCompressor)
    TextView tvCompressor;
    @BindView(R.id.cbCompressorEnable)
    CheckBox cbCompressorEnable;
    @BindView(R.id.tvCompWet)
    TextView tvCompWet;
    @BindView(R.id.sbDryCompressor)
    SeekBar sbDryCompressor;
    @BindView(R.id.tvCompAttack)
    TextView tvCompAttack;
    @BindView(R.id.sbAttackCompressor)
    SeekBar sbAttackCompressor;
    @BindView(R.id.tvCompRelease)
    TextView tvCompRelease;
    @BindView(R.id.sbReleaseSecondCompressor)
    SeekBar sbReleaseSecondCompressor;
    @BindView(R.id.tvCompRatio)
    TextView tvCompRatio;
    @BindView(R.id.sbRatioCompressor)
    SeekBar sbRatioCompressor;
    @BindView(R.id.tvCompThreshold)
    TextView tvCompThreshold;
    @BindView(R.id.sbThresHoldCompressor)
    SeekBar sbThresHoldCompressor;
    @BindView(R.id.tvCompHP)
    TextView tvCompHP;
    @BindView(R.id.sbHpCutCompressor)
    SeekBar sbHpCutCompressor;
    @BindView(R.id.layoutCompressor)
    RelativeLayout layoutCompressor;
    @BindView(R.id.tvBandBass)
    TextView tvBandBass;
    @BindView(R.id.sbBass)
    SeekBar sbBass;
    @BindView(R.id.tvBandMid)
    TextView tvBandMid;
    @BindView(R.id.sbMid)
    SeekBar sbMid;
    @BindView(R.id.tvBandHi)
    TextView tvBandHi;
    @BindView(R.id.sbHi)
    SeekBar sbHi;
    @BindView(R.id.lnBandEQ)
    LinearLayout lnBandEQ;
    @BindView(R.id.value0)
    TextView value0;
    @BindView(R.id.sbValue0)
    SeekBar sbValue0;
    @BindView(R.id.value1)
    TextView value1;
    @BindView(R.id.sbValue1)
    SeekBar sbValue1;
    @BindView(R.id.value2)
    TextView value2;
    @BindView(R.id.sbValue2)
    SeekBar sbValue2;
    @BindView(R.id.value3)
    TextView value3;
    @BindView(R.id.sbValue3)
    SeekBar sbValue3;
    @BindView(R.id.value4)
    TextView value4;
    @BindView(R.id.sbValue4)
    SeekBar sbValue4;
    @BindView(R.id.value5)
    TextView value5;
    @BindView(R.id.sbValue5)
    SeekBar sbValue5;
    @BindView(R.id.value6)
    TextView value6;
    @BindView(R.id.sbValue6)
    SeekBar sbValue6;
    @BindView(R.id.value7)
    TextView value7;
    @BindView(R.id.sbValue7)
    SeekBar sbValue7;
    @BindView(R.id.value8)
    TextView value8;
    @BindView(R.id.sbValue8)
    SeekBar sbValue8;
    @BindView(R.id.value9)
    TextView value9;
    @BindView(R.id.sbValue9)
    SeekBar sbValue9;
    @BindView(R.id.panel_n_band_eq)
    LinearLayout panelNBandEq;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.btnSave)
    Button btnSave;

    private RecorderEngine mRecorderEngine;
    private List<Preset> mPresets;
    private Gson gson;
    private boolean isEnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
        ButterKnife.bind(this);

        claimPermission();
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
        samplerateString = "44100";
        if (buffersizeString == null) buffersizeString = "512";

        Log.d("ChungLD", buffersizeString);
        mRecorderEngine = new RecorderEngine(Integer.parseInt(samplerateString), Integer.parseInt(buffersizeString));
        mRecorderEngine.setOnRecordEventListener(onRecordEventListener);
        isEnable = false;
//        btnEnable.setText(isEnable ? "Đang hoạt động" : "Đã vô hiệu hóa");


        initViewControl();
    }

    private void initViewControl() {
        handler = new Handler();
        // fx select event
        reverbDry.setProgress(50);
        reverbDry.setOnSeekBarChangeListener(seekBarChangeListener);
        reverbWidth.setProgress(0);
        reverbWidth.setOnSeekBarChangeListener(seekBarChangeListener);
        reverbMix.setProgress(0);
        reverbMix.setOnSeekBarChangeListener(seekBarChangeListener);
        reverbWet.setProgress(0);
        reverbWet.setOnSeekBarChangeListener(seekBarChangeListener);
        reverbRoomsize.setProgress(0);
        reverbRoomsize.setOnSeekBarChangeListener(seekBarChangeListener);
        reverbDamp.setProgress(0);
        reverbDamp.setOnSeekBarChangeListener(seekBarChangeListener);

        sbValue0 = (SeekBar) findViewById(R.id.sbValue0);
        sbValue0.setOnSeekBarChangeListener(onNBandEQChangeListener);
        sbValue1 = (SeekBar) findViewById(R.id.sbValue1);
        sbValue1.setOnSeekBarChangeListener(onNBandEQChangeListener);
        sbValue2 = (SeekBar) findViewById(R.id.sbValue2);
        sbValue2.setOnSeekBarChangeListener(onNBandEQChangeListener);
        sbValue3 = (SeekBar) findViewById(R.id.sbValue3);
        sbValue3.setOnSeekBarChangeListener(onNBandEQChangeListener);
        sbValue4 = (SeekBar) findViewById(R.id.sbValue4);
        sbValue4.setOnSeekBarChangeListener(onNBandEQChangeListener);
        sbValue5 = (SeekBar) findViewById(R.id.sbValue5);
        sbValue5.setOnSeekBarChangeListener(onNBandEQChangeListener);
        sbValue6 = (SeekBar) findViewById(R.id.sbValue6);
        sbValue6.setOnSeekBarChangeListener(onNBandEQChangeListener);
        sbValue7 = (SeekBar) findViewById(R.id.sbValue7);
        sbValue7.setOnSeekBarChangeListener(onNBandEQChangeListener);
        sbValue8 = (SeekBar) findViewById(R.id.sbValue8);
        sbValue8.setOnSeekBarChangeListener(onNBandEQChangeListener);
        sbValue9 = (SeekBar) findViewById(R.id.sbValue9);
        sbValue9.setOnSeekBarChangeListener(onNBandEQChangeListener);
        // echo events
        sbDryEcho = (SeekBar) findViewById(R.id.sbDryEcho);
        sbDryEcho.setProgress(50);
        sbDryEcho.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        sbWetEcho = (SeekBar) findViewById(R.id.sbWetEcho);
        sbWetEcho.setProgress(0);
        sbWetEcho.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        sbDecayEcho = (SeekBar) findViewById(R.id.sbDecayEcho);
        sbDecayEcho.setProgress(0);
        sbDecayEcho.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        sbBpm = (SeekBar) findViewById(R.id.sbBpm);
        sbBpm.setProgress(0);
        sbBpm.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        sbBeatsEcho = (SeekBar) findViewById(R.id.sbBeatsEcho);
        sbBeatsEcho.setProgress(0);
        sbBeatsEcho.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        sbEchoMix = (SeekBar) findViewById(R.id.sbEchoMix);
        sbEchoMix.setProgress(0);
        sbEchoMix.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        initCompressorView();
        initThreeBandEQView();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

//        btnPresetChoose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupChoosePresetFragment.newInstance().setOnPresetChoose(new PopupChoosePresetFragment.OnPresetChoose() {
//                    @Override
//                    public void onChoose(Preset preset) {
//                        onProcessPresetChoose(new Gson().fromJson(mReadJsonData(preset.getName()), Preset.class));
//                    }
//                }).show(getSupportFragmentManager(), null);
//            }
//        });
    }

    private void saveData() {
        if (etName.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Phải nhập tên preset", LENGTH_SHORT).show();
            return;
        }

        Preset preset = new Preset();

        preset.setDryEcho(convertStringToFloat(dryValueEcho));
        preset.setWetEcho(convertStringToFloat(wetValueEcho));
        preset.setDecayEcho(convertStringToFloat(decayEchoValue));
        preset.setBeatsEcho(convertStringToFloat(beatValue));
        preset.setMixEcho(convertStringToFloat(mixEchoValue));
        preset.setBpmEcho(convertStringToFloat(bpmValue));

        preset.setDryReverb(convertStringToFloat(dryValue));
        preset.setMixReverb(convertStringToFloat(mixValue));
        preset.setWetReverb(convertStringToFloat(wetValue));
        preset.setDampReverb(convertStringToFloat(dampValue));
        preset.setRoomsizeReverb(convertStringToFloat(roomsizeValue));
        preset.setWidthReverb(convertStringToFloat(widthValue));

        preset.setDryCompressor(convertStringToFloat(tvCompWet.getText().toString().replace("Dry - Wet ratio: ", "")));
        preset.setAttackCompressor(convertStringToFloat(tvCompAttack.getText().toString().replace("Attack second: ", "")));
        preset.setRatioCompressor(convertStringToFloat(tvCompRatio.getText().toString().replace("Ratio: ", "")));
        preset.setReleaseCompressor(convertStringToFloat(tvCompRelease.getText().toString().replace("Release second: ", "")));
        preset.setThresholdCompressor(convertStringToFloat(tvCompThreshold.getText().toString().replace("Threshold: ", "")));
        preset.setHpCut(convertStringToInteger(tvCompHP.getText().toString().replace("Hp Cut: ", "")));


        preset.setBass(convertStringToFloat(tvBandBass.getText().toString().replace("Bass: ", "")));
        preset.setMid(convertStringToFloat(tvBandMid.getText().toString().replace("Mid: ", "")));
        preset.setHi(convertStringToFloat(tvBandHi.getText().toString().replace("High: ", "")));

        preset.setValueEQ0((sbValue0.getProgress() - 24));
        preset.setValueEQ1((sbValue1.getProgress() - 24));
        preset.setValueEQ2((sbValue2.getProgress() - 24));
        preset.setValueEQ3((sbValue3.getProgress() - 24));
        preset.setValueEQ4((sbValue4.getProgress() - 24));
        preset.setValueEQ5((sbValue5.getProgress() - 24));
        preset.setValueEQ6((sbValue6.getProgress() - 24));
        preset.setValueEQ7((sbValue7.getProgress() - 24));
        preset.setValueEQ8((sbValue8.getProgress() - 24));
        preset.setValueEQ9((sbValue9.getProgress() - 24));

        preset.setName(etName.getText().toString().trim());
        String data = new Gson().toJson(preset);
        writeToFile(data, preset.getName());
    }

    private void writeToFile(String data, String fileName) {
        try {
            File root = new File(Environment.getExternalStorageDirectory() + "/Superpowered/preset");
            File gpxfile = new File(root, fileName + ".txt");
            if (!gpxfile.exists()) {
                gpxfile.createNewFile();
            }
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "File write failed" + e.toString(), LENGTH_SHORT).show();
            Log.e("LOL", "File write failed: " + e.toString());
            return;
        }
        Toast.makeText(getApplicationContext(), "File write success", LENGTH_SHORT).show();
    }

    private float convertStringToFloat(TextView textView) {
        return convertStringToFloat(textView.getText().toString());
    }

    private float convertStringToFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception ex) {
            return 0.0f;
        }
    }

    private int convertStringToInteger(TextView textView) {
        return convertStringToInteger(textView.getText().toString());
    }

    private int convertStringToInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return 0;
        }
    }

    private void initCompressorView() {

        sbDryCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbAttackCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbReleaseSecondCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbRatioCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbThresHoldCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbHpCutCompressor.setOnSeekBarChangeListener(onCompressChangeListener);

        cbCompressorEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getApplicationContext(), "Compressor enable: " + b, LENGTH_SHORT).show();
            }
        });
    }

    private SeekBar.OnSeekBarChangeListener onCompressChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            updateValueCompressor();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            updateValueCompressor();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void updateValueCompressor() {

        float compThreshold, compRatio, compRelease, compAttack, compWet;
        int compHP;

        compHP = sbHpCutCompressor.getProgress() + 1;
        compThreshold = (float) (0 - sbThresHoldCompressor.getProgress());
        compRatio = (sbRatioCompressor.getProgress() + 15) / (10.0f);
        compRelease = (sbReleaseSecondCompressor.getProgress() + 1) / (10.0f);
        compAttack = (sbAttackCompressor.getProgress() + 1) / (10000.0f);
        compWet = sbDryCompressor.getProgress() / (100.0f);

//        sbDryCompressor.setProgress((int) (preset.getDryCompressor() * 100));
//        sbAttackCompressor.setProgress((int) (preset.getAttackCompressor() * 10000 - 1));
//        sbRatioCompressor.setProgress((int) (preset.getRatioCompressor() * 10 - 15));
//        sbThresHoldCompressor.setProgress((int) (0 - preset.getThresholdCompressor()));
//        sbHpCutCompressor.setProgress((int) (preset.getHpCut() - 1));
//        sbReleaseSecondCompressor.setProgress((int) (preset.getReleaseCompressor() * 10 - 1));

        tvCompHP.setText("Hp Cut: " + compHP);
        tvCompThreshold.setText("Threshold: " + compThreshold);
        tvCompRatio.setText("Ratio: " + compRatio);
        tvCompRelease.setText("Release second: " + compRelease);
        tvCompAttack.setText("Attack second: " + compAttack);
        tvCompWet.setText("Dry - Wet ratio: " + compWet);

        mRecorderEngine.onCompressorValue(compWet, compRatio, compAttack, compRelease, compThreshold, compHP);
    }

    private void initThreeBandEQView() {
        sbBass.setOnSeekBarChangeListener(onEQChangeListener);
        sbMid.setOnSeekBarChangeListener(onEQChangeListener);
        sbHi.setOnSeekBarChangeListener(onEQChangeListener);

        tvBandBass = (TextView) findViewById(R.id.tvBandBass);
        tvBandMid = (TextView) findViewById(R.id.tvBandMid);
        tvBandHi = (TextView) findViewById(R.id.tvBandHi);
    }


    private SeekBar.OnSeekBarChangeListener onEQChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            tvBandBass.setText("Bass: " + sbBass.getProgress() / 10.0f);
            tvBandMid.setText("Mid: " + sbMid.getProgress() / 10.0f);
            tvBandHi.setText("High: " + sbHi.getProgress() / 10.0f);
            mRecorderEngine.onBandValues(sbBass.getProgress() / 10.0f, sbMid.getProgress() / 10.0f, sbHi.getProgress() / 10.0f);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarEchoChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float dry = 1.0f * sbDryEcho.getProgress() / 100;
            float wet = 1.0f * sbWetEcho.getProgress() / 100;
            float bpm = sbBpm.getProgress() + 60;
            float beats = ((sbBeatsEcho.getProgress() + 125) * 1.0f) / 1000;
            float decay = 1.0f * sbDecayEcho.getProgress() / 100;
            float mix = 1.0f * sbEchoMix.getProgress() / 100;

            dryValueEcho.setText(dry + "");
            wetValueEcho.setText(wet + "");
            bpmValue.setText(bpm + "");
            beatValue.setText(beats + "");
            decayEchoValue.setText(decay + "");
            mixEchoValue.setText(mix + "");

            mRecorderEngine.setEchoValue(dry, wet, bpm, beats, decay, mix);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener onNBandEQChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            mRecorderEngine.onProcessBandEQ(((float) (sbValue0.getProgress() - 24))
                    , ((float) (sbValue1.getProgress() - 24))
                    , ((float) (sbValue2.getProgress() - 24))
                    , ((float) (sbValue3.getProgress() - 24))
                    , ((float) (sbValue4.getProgress() - 24))
                    , ((float) (sbValue5.getProgress() - 24))
                    , ((float) (sbValue6.getProgress() - 24))
                    , ((float) (sbValue7.getProgress() - 24))
                    , ((float) (sbValue8.getProgress() - 24))
                    , ((float) (sbValue9.getProgress() - 24)));

            value0.setText((sbValue0.getProgress() - 24) + " dB");
            value1.setText((sbValue1.getProgress() - 24) + " dB");
            value2.setText((sbValue2.getProgress() - 24) + " dB");
            value3.setText((sbValue3.getProgress() - 24) + " dB");
            value4.setText((sbValue4.getProgress() - 24) + " dB");
            value5.setText((sbValue5.getProgress() - 24) + " dB");
            value6.setText((sbValue6.getProgress() - 24) + " dB");
            value7.setText((sbValue7.getProgress() - 24) + " dB");
            value8.setText((sbValue8.getProgress() - 24) + " dB");
            value9.setText((sbValue9.getProgress() - 24) + " dB");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int id = seekBar.getId();
            float valueF = progress * 0.01f;
            String value = String.valueOf(valueF);
            switch (id) {
                case R.id.reverb_dry:
                    mRecorderEngine.onFxReverbValue(REVERB_DRY, valueF);
                    dryValue.setText(value);
                    break;
                case R.id.reverb_mix:
                    mRecorderEngine.onFxReverbValue(REVERB_MIX, valueF);
                    mixValue.setText(value);
                    break;
                case R.id.reverb_roomsize:
                    mRecorderEngine.onFxReverbValue(REVERB_ROOMSIZE, valueF);
                    roomsizeValue.setText(value);
                    break;
                case R.id.reverb_wet:
                    mRecorderEngine.onFxReverbValue(REVERB_WET, valueF);
                    wetValue.setText(value);
                    break;
                case R.id.reverb_width:
                    mRecorderEngine.onFxReverbValue(REVERB_WIDTH, valueF);
                    widthValue.setText(value);
                    break;
                case R.id.reverb_damp:
                    mRecorderEngine.onFxReverbValue(REVERB_DAMP, valueF);
                    dampValue.setText(value);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private RecorderEngine.OnRecordEventListener onRecordEventListener = new RecorderEngine.OnRecordEventListener() {
        @Override
        public void onInitSuccess() {
            Toast.makeText(DevActivity.this, "Khởi tạo thành công", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFrequencyListener(final double freq) {

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

    @OnClick(R.id.btnSave)
    public void onViewClicked() {
    }
}
