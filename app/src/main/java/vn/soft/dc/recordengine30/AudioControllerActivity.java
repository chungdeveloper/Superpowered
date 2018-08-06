package vn.soft.dc.recordengine30;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import vn.soft.dc.recordengine.RecorderEngine;
import vn.soft.dc.recordengine.model.Preset;
import vn.soft.dc.recordengine30.model.Music;
import vn.soft.dc.recordengine30.popup.PopupChooseMusicFragment;
import vn.soft.dc.recordengine30.popup.PopupChoosePresetFragment;

import static android.widget.Toast.LENGTH_SHORT;

@SuppressWarnings("ALL")
public class AudioControllerActivity extends AppCompatActivity {

    boolean playing = false;
    boolean open = false;

    static final int VOICE_SELECT_CODE = 321;
    static final int BEAT_SELECT_CODE = 123;
    static int REVERB_NON = 0;
    static int REVERB_DRY = 1;
    static int REVERB_WET = 2;
    static int REVERB_WIDTH = 3;
    static int REVERB_MIX = 4;
    static int REVERB_ROOMSIZE = 5;
    static int REVERB_DAMP = 6;
    static int REVERB_PREDELAY = 7;
    static int REVERB_LOW_CUT = 8;

    private TextView txtDry, txtWidth, txtRoomSize, txtMix, txtWet, txtDamp, txtPredelay, txtLowcutHz, txtDelayValue, tvPitchCents;
    private TextView txtDryEcho, txtBeatsEcho, txtDecayEcho, txtBPMEcho, txtWetEcho, txtMixEcho;

    private SeekBar sbDryCompressor, sbAttackCompressor, sbReleaseSecondCompressor, sbRatioCompressor, sbThresHoldCompressor, sbHpCutCompressor;
    private TextView tvCompHP, tvCompThreshold, tvCompRatio, tvCompRelease, tvCompAttack, tvCompWet;
    private CheckBox checkBox;
    private Button btnChooseVoice, btnChooseBeat, btnSuperpowered;
    private TextView tvVoice, tvBeat, tvPreset;
    private SeekBar fxDryEcho, fxWetEcho, fxDecayEcho, fxBpmEcho, fxBeats, fxMixEcho;
    private SeekBar sbBass, sbMid, sbHigh;
    private TextView tvBandBass, tvBandMid, tvBandHi, tvTime;
    private Uri voiceURI;
    private Uri beatURI;
    private EditText etName;
    private Button btnSave;
    private Button btnPresetChoose;
    private SeekBar sbTime;
    private TextView tvValue0, tvValue1, tvValue2, tvValue3, tvValue4, tvValue5, tvValue6, tvValue7, tvValue8, tvValue9;
    private Handler handler;
    private SeekBar fxDry, fxWidth, fxMix, fxWet, fxRoomSize, fxDamp, fxReverbPredelay, fxReverbLowCut, fxDelay, pitchCents;
    private SeekBar sbValue0, sbValue1, sbValue2, sbValue3, sbValue4, sbValue5, sbValue6, sbValue7, sbValue8, sbValue9;
    private RecorderEngine mMediaEngine;
    private String beatUri, vocalUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_controller);

        String samplerateString = null, buffersizeString = null;
        if (Build.VERSION.SDK_INT >= 17) {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                samplerateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
                buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            }
        }
        if (samplerateString == null) samplerateString = "48000";
        if (buffersizeString == null) buffersizeString = "512";
        int samplerate = Integer.parseInt(samplerateString);
        int buffersize = Integer.parseInt(buffersizeString);

        mMediaEngine = new RecorderEngine(samplerate, buffersize < 32 ? buffersize : 32, onRecordEventListener);

        initView();

    }

    private void initView() {
        // crossfader events
//        final SeekBar crossfader = (SeekBar) findViewById(R.id.crossFader);
//        if (crossfader != null)
//            crossfader.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    mMediaEngine.onCrossfader(progress);
//                }
//
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                }
//
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                }
//            });

        // fx fader events
//        final SeekBar fxfader = (SeekBar) findViewById(R.id.fxFader);
//        if (fxfader != null)
//            fxfader.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    mMediaEngine.onFxValue(progress);
//                }
//
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                    mMediaEngine.onFxValue(seekBar.getProgress());
//                }
//
//                public void onStopTrackingTouch(SeekBar seekBar) {
////                onFxOff();
//                }
//            });

        handler = new Handler();
//        tvPitchCents = (TextView) findViewById(R.id.tv_pitch_cents_value);
//        pitchCents = (SeekBar) findViewById(R.id.pitch_cents_seek);
//        pitchCents.setMax(2400);
//        if (pitchCents != null)
//            pitchCents.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    int progressValue = progress - 1200;
//                    mMediaEngine.onPitchShift(progressValue);
//                    tvPitchCents.setText(progressValue + "");
//                }
//
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                    mMediaEngine.onPitchShift(seekBar.getProgress());
//                }
//
//                public void onStopTrackingTouch(SeekBar seekBar) {
////                onFxOff();
//                }
//            });

        // fx select event
        final RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup1);
//        if (group != null)
//            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                    RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
//                    mMediaEngine.onFxSelect(radioGroup.indexOfChild(checkedRadioButton));
//                }
//            });
        // reverb events
        fxDry = (SeekBar) findViewById(R.id.reverb_dry);
        fxDry.setProgress(50);
        fxDry.setOnSeekBarChangeListener(seekBarChangeListener);
        fxWidth = (SeekBar) findViewById(R.id.reverb_width);
        fxWidth.setProgress(0);
        fxWidth.setOnSeekBarChangeListener(seekBarChangeListener);
        fxMix = (SeekBar) findViewById(R.id.reverb_mix);
        fxMix.setProgress(0);
        fxMix.setOnSeekBarChangeListener(seekBarChangeListener);
        fxWet = (SeekBar) findViewById(R.id.reverb_wet);
        fxWet.setProgress(0);
        fxWet.setOnSeekBarChangeListener(seekBarChangeListener);
        fxRoomSize = (SeekBar) findViewById(R.id.reverb_roomsize);
        fxRoomSize.setProgress(0);
        fxRoomSize.setOnSeekBarChangeListener(seekBarChangeListener);
        fxDamp = (SeekBar) findViewById(R.id.reverb_damp);
        fxDamp.setProgress(0);
        fxDamp.setOnSeekBarChangeListener(seekBarChangeListener);
        fxReverbPredelay = (SeekBar) findViewById(R.id.sbPredelay);
        fxReverbPredelay.setProgress(0);
        fxReverbPredelay.setMax(500);
        fxReverbPredelay.setOnSeekBarChangeListener(seekBarChangeListener);
        fxReverbLowCut = (SeekBar) findViewById(R.id.sbLowcutHzReverb);
        fxReverbLowCut.setProgress(0);
        fxReverbLowCut.setMax(2000);
        fxReverbLowCut.setOnSeekBarChangeListener(seekBarChangeListener);

//        fxDelay = (SeekBar) findViewById(R.id.sbDelay);
//        fxDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                txtDelayValue.setText(progress + "");
//                mMediaEngine.onDelayValue(progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

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

        sbTime = (SeekBar) findViewById(R.id.sbTime);
        sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvTime.setText(getTimeAgo(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(onUpdateTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //todo seeker music time
                handler.post(onUpdateTime);
            }
        });

        // echo events
        fxDryEcho = (SeekBar) findViewById(R.id.sbDryEcho);
        fxDryEcho.setProgress(50);
        fxDryEcho.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        fxWetEcho = (SeekBar) findViewById(R.id.sbWetEcho);
        fxWetEcho.setProgress(0);
        fxWetEcho.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        fxDecayEcho = (SeekBar) findViewById(R.id.sbDecayEcho);
        fxDecayEcho.setProgress(0);
        fxDecayEcho.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        fxBpmEcho = (SeekBar) findViewById(R.id.sbBpm);
        fxBpmEcho.setProgress(0);
        fxBpmEcho.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        fxBeats = (SeekBar) findViewById(R.id.sbBeatsEcho);
        fxBeats.setProgress(0);
        fxBeats.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        fxMixEcho = (SeekBar) findViewById(R.id.sbEchoMix);
        fxMixEcho.setProgress(0);
        fxMixEcho.setOnSeekBarChangeListener(seekBarEchoChangeListener);

        txtDry = (TextView) findViewById(R.id.dry_value);
        tvTime = (TextView) findViewById(R.id.timeValue);
        txtMix = (TextView) findViewById(R.id.mix_value);
        txtRoomSize = (TextView) findViewById(R.id.roomsize_value);
        txtWet = (TextView) findViewById(R.id.wet_value);
        txtWidth = (TextView) findViewById(R.id.width_value);
        txtDamp = (TextView) findViewById(R.id.damp_value);
        txtPredelay = (TextView) findViewById(R.id.predelay_value);
        txtLowcutHz = (TextView) findViewById(R.id.lowCutReverb);
        txtDelayValue = (TextView) findViewById(R.id.delayValue);

        txtDryEcho = (TextView) findViewById(R.id.dry_valueEcho);
        txtWetEcho = (TextView) findViewById(R.id.wet_valueEcho);
        txtMixEcho = (TextView) findViewById(R.id.mixEchoValue);
        txtBeatsEcho = (TextView) findViewById(R.id.beatValue);
        txtDecayEcho = (TextView) findViewById(R.id.decayEchoValue);
        txtBPMEcho = (TextView) findViewById(R.id.bpmValue);

        initCompressorView();
        initThreeBandEQView();

        btnChooseVoice = (Button) findViewById(R.id.btnChooseVoice);
        btnSuperpowered = (Button) findViewById(R.id.btnInit);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnPresetChoose = (Button) findViewById(R.id.btnChoosePreset);
        btnChooseBeat = (Button) findViewById(R.id.btnChooseBeat);
        tvVoice = (TextView) findViewById(R.id.tvVoice);
        tvPreset = (TextView) findViewById(R.id.tvPreset);
        tvBeat = (TextView) findViewById(R.id.tvBeat);
        etName = (EditText) findViewById(R.id.etName);

        tvValue0 = (TextView) findViewById(R.id.value0);
        tvValue1 = (TextView) findViewById(R.id.value1);
        tvValue2 = (TextView) findViewById(R.id.value2);
        tvValue3 = (TextView) findViewById(R.id.value3);
        tvValue4 = (TextView) findViewById(R.id.value4);
        tvValue5 = (TextView) findViewById(R.id.value5);
        tvValue6 = (TextView) findViewById(R.id.value6);
        tvValue7 = (TextView) findViewById(R.id.value7);
        tvValue8 = (TextView) findViewById(R.id.value8);
        tvValue9 = (TextView) findViewById(R.id.value9);

//        btnSuperpowered.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (beatURI == null || voiceURI == null) {
//                    Toast.makeText(getApplicationContext(), "Phải chọn đủ 2 file", LENGTH_SHORT).show();
//                    return;
//                }
//
//                initSuperpoweredTech();
//            }
//        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        btnPresetChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupChoosePresetFragment.newInstance().setOnPresetChoose(new PopupChoosePresetFragment.OnPresetChoose() {
                    @Override
                    public void onChoose(Preset preset) {
                        onProcessPresetChoose(new Gson().fromJson(mReadJsonData(preset.getName()), Preset.class));
                    }
                }).show(getSupportFragmentManager(), null);
            }
        });

        btnChooseVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupChooseMusicFragment.newInstance(Environment.getExternalStorageDirectory() + "/Superpowered/voice").setOnMusicChoose(new PopupChooseMusicFragment.OnMusicChoose() {
                    @Override
                    public void onChoose(Music music) {
                        processFile(music, VOICE_SELECT_CODE);
                    }
                }).show(getSupportFragmentManager(), null);
            }
        });

        btnChooseBeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupChooseMusicFragment.newInstance(Environment.getExternalStorageDirectory() + "/Superpowered/beat").setOnMusicChoose(new PopupChooseMusicFragment.OnMusicChoose() {
                    @Override
                    public void onChoose(Music music) {
                        processFile(music, BEAT_SELECT_CODE);
                    }
                }).show(getSupportFragmentManager(), null);
            }
        });
    }


    private RecorderEngine.OnRecordEventListener onRecordEventListener = new RecorderEngine.OnRecordEventListener() {
        @Override
        public void onInitSuccess() {
            Toast.makeText(AudioControllerActivity.this, "Khởi tạo thành công", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFrequencyListener(final double freq) {

        }
    };


    public String mReadJsonData(String path) {
        try {
            File f = new File(path);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    private void onProcessPresetChoose(Preset preset) {
        String[] path = preset.getName().split("/");
        tvPreset.setText(path[path.length - 1]);

        fxDry.setProgress((int) (preset.getDryReverb() * 100));
        fxWet.setProgress((int) (preset.getWetReverb() * 100));
        fxMix.setProgress((int) (preset.getMixReverb() * 100));
        fxWidth.setProgress((int) (preset.getWidthReverb() * 100));
        fxDamp.setProgress((int) (preset.getDampReverb() * 100));
        fxReverbLowCut.setProgress((int) (preset.getLowcutReverb()));
        fxDelay.setProgress((int) (preset.getDelay()));
        fxReverbPredelay.setProgress((int) (preset.getPreDelay()));
        fxRoomSize.setProgress((int) (preset.getRoomsizeReverb() * 100));

        fxDryEcho.setProgress((int) (preset.getDryEcho() * 100));
        fxWetEcho.setProgress((int) (preset.getWetEcho() * 100));
        fxMixEcho.setProgress((int) (preset.getMixEcho() * 100));
        fxDecayEcho.setProgress((int) (preset.getDecayEcho() * 100));
        fxBeats.setProgress((int) (preset.getBeatsEcho() * 100 - 125));
        fxBpmEcho.setProgress((int) (preset.getBpmEcho() - 60));

        sbDryCompressor.setProgress((int) (preset.getDryCompressor() * 100));
        sbAttackCompressor.setProgress((int) (preset.getAttackCompressor() * 10000 - 1));
        sbRatioCompressor.setProgress((int) (preset.getRatioCompressor() * 10 - 15));
        sbThresHoldCompressor.setProgress((int) (0 - preset.getThresholdCompressor()));
        sbHpCutCompressor.setProgress((int) (preset.getHpCut() - 1));
        sbReleaseSecondCompressor.setProgress((int) (preset.getReleaseCompressor() * 10 - 1));

        sbValue0.setProgress((int) (preset.getValueEQ0() + 24));
        sbValue1.setProgress((int) (preset.getValueEQ1() + 24));
        sbValue2.setProgress((int) (preset.getValueEQ2() + 24));
        sbValue3.setProgress((int) (preset.getValueEQ3() + 24));
        sbValue4.setProgress((int) (preset.getValueEQ4() + 24));
        sbValue5.setProgress((int) (preset.getValueEQ5() + 24));
        sbValue6.setProgress((int) (preset.getValueEQ6() + 24));
        sbValue7.setProgress((int) (preset.getValueEQ7() + 24));
        sbValue8.setProgress((int) (preset.getValueEQ8() + 24));
        sbValue9.setProgress((int) (preset.getValueEQ9() + 24));

        pitchCents.setProgress(preset.getPitch() + 1200);

        sbBass.setProgress((int) (preset.getBass() * 10));
        sbMid.setProgress((int) (preset.getMid() * 10));
        sbHigh.setProgress((int) (preset.getHi() * 10));
    }

    private void saveData() {
        if (etName.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Phải nhập tên preset", LENGTH_SHORT).show();
            return;
        }

        Preset preset = new Preset();

        preset.setDryEcho(convertStringToFloat(txtDryEcho));
        preset.setWetEcho(convertStringToFloat(txtWetEcho));
        preset.setDecayEcho(convertStringToFloat(txtDecayEcho));
        preset.setBeatsEcho(convertStringToFloat(txtBeatsEcho));
        preset.setMixEcho(convertStringToFloat(txtMixEcho));
        preset.setBpmEcho(convertStringToFloat(txtBPMEcho));

        preset.setDryReverb(convertStringToFloat(txtDry));
        preset.setMixReverb(convertStringToFloat(txtMix));
        preset.setWetReverb(convertStringToFloat(txtWet));
        preset.setDampReverb(convertStringToFloat(txtDamp));
        preset.setLowcutReverb(convertStringToFloat(txtLowcutHz));
        preset.setDelay(convertStringToFloat(txtDelayValue));
        preset.setPreDelay(convertStringToFloat(txtPredelay));
        preset.setRoomsizeReverb(convertStringToFloat(txtRoomSize));
        preset.setWidthReverb(convertStringToFloat(txtWidth));

        preset.setDryCompressor(convertStringToFloat(tvCompWet.getText().toString().replace("Dry - Wet ratio: ", "")));
        preset.setAttackCompressor(convertStringToFloat(tvCompAttack.getText().toString().replace("Attack second: ", "")));
        preset.setRatioCompressor(convertStringToFloat(tvCompRatio.getText().toString().replace("Ratio: ", "")));
        preset.setReleaseCompressor(convertStringToFloat(tvCompRelease.getText().toString().replace("Release second: ", "")));
        preset.setThresholdCompressor(convertStringToFloat(tvCompThreshold.getText().toString().replace("Threshold: ", "")));
        preset.setHpCut(convertStringToInteger(tvCompHP.getText().toString().replace("Hp Cut: ", "")));

        preset.setPitch(convertStringToInteger(tvPitchCents));

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

    private void initSuperpoweredTech() {
        //todo init music
    }

    public static Uri getContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
//        processFile(data, requestCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processFile(Music data, int requestCode) {
        try {
            String path;
            String[] arrayString;
            path = data.getUrl();
            arrayString = path.split("/");
            switch (requestCode) {
                case VOICE_SELECT_CODE:
                    tvVoice.setText(arrayString[arrayString.length - 1]);
                    voiceURI = getContentUri(getApplicationContext(), new File(vocalUri = data.getUrl()));
                    break;
                case BEAT_SELECT_CODE:
                    tvBeat.setText(arrayString[arrayString.length - 1]);
                    beatURI = getContentUri(getApplicationContext(), new File(beatUri = data.getUrl()));// Uri.parse(data.getUrl());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Load file fail", LENGTH_SHORT).show();
        }
    }

    private void showFileChooser(int code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), code);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    LENGTH_SHORT).show();
        }
    }

    private void initCompressorView() {
        sbDryCompressor = (SeekBar) findViewById(R.id.sbDryCompressor);
        sbAttackCompressor = (SeekBar) findViewById(R.id.sbAttackCompressor);
        sbReleaseSecondCompressor = (SeekBar) findViewById(R.id.sbReleaseSecondCompressor);
        sbRatioCompressor = (SeekBar) findViewById(R.id.sbRatioCompressor);
        sbThresHoldCompressor = (SeekBar) findViewById(R.id.sbThresHoldCompressor);
        sbHpCutCompressor = (SeekBar) findViewById(R.id.sbHpCutCompressor);

        sbDryCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbAttackCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbReleaseSecondCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbRatioCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbThresHoldCompressor.setOnSeekBarChangeListener(onCompressChangeListener);
        sbHpCutCompressor.setOnSeekBarChangeListener(onCompressChangeListener);

        tvCompHP = (TextView) findViewById(R.id.tvCompHP);
        tvCompThreshold = (TextView) findViewById(R.id.tvCompThreshold);
        tvCompRatio = (TextView) findViewById(R.id.tvCompRatio);
        tvCompRelease = (TextView) findViewById(R.id.tvCompRelease);
        tvCompAttack = (TextView) findViewById(R.id.tvCompAttack);
        tvCompWet = (TextView) findViewById(R.id.tvCompWet);
        checkBox = (CheckBox) findViewById(R.id.cbCompressorEnable);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getApplicationContext(), "Compressor enable: " + b, LENGTH_SHORT).show();
                mMediaEngine.onCompressEnable(b);
            }
        });
    }

    private void initThreeBandEQView() {
        sbBass = (SeekBar) findViewById(R.id.sbBass);
        sbMid = (SeekBar) findViewById(R.id.sbMid);
        sbHigh = (SeekBar) findViewById(R.id.sbHi);

        sbBass.setOnSeekBarChangeListener(onEQChangeListener);
        sbMid.setOnSeekBarChangeListener(onEQChangeListener);
        sbHigh.setOnSeekBarChangeListener(onEQChangeListener);

        tvBandBass = (TextView) findViewById(R.id.tvBandBass);
        tvBandMid = (TextView) findViewById(R.id.tvBandMid);
        tvBandHi = (TextView) findViewById(R.id.tvBandHi);
    }

    private SeekBar.OnSeekBarChangeListener onEQChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            tvBandBass.setText("Bass: " + sbBass.getProgress() / 10.0f);
            tvBandMid.setText("Mid: " + sbMid.getProgress() / 10.0f);
            tvBandHi.setText("High: " + sbHigh.getProgress() / 10.0f);
            mMediaEngine.onBandValues(sbBass.getProgress() / 10.0f, sbMid.getProgress() / 10.0f, sbHigh.getProgress() / 10.0f);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

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

        mMediaEngine.onCompressorValue(compWet, compRatio, compAttack, compRelease, compThreshold, compHP);
    }

    private SeekBar.OnSeekBarChangeListener onNBandEQChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            mMediaEngine.onProcessBandEQ(((float) (sbValue0.getProgress() - 24))
                    , ((float) (sbValue1.getProgress() - 24))
                    , ((float) (sbValue2.getProgress() - 24))
                    , ((float) (sbValue3.getProgress() - 24))
                    , ((float) (sbValue4.getProgress() - 24))
                    , ((float) (sbValue5.getProgress() - 24))
                    , ((float) (sbValue6.getProgress() - 24))
                    , ((float) (sbValue7.getProgress() - 24))
                    , ((float) (sbValue8.getProgress() - 24))
                    , ((float) (sbValue9.getProgress() - 24)));

            tvValue0.setText((sbValue0.getProgress() - 24) + " dB");
            tvValue1.setText((sbValue1.getProgress() - 24) + " dB");
            tvValue2.setText((sbValue2.getProgress() - 24) + " dB");
            tvValue3.setText((sbValue3.getProgress() - 24) + " dB");
            tvValue4.setText((sbValue4.getProgress() - 24) + " dB");
            tvValue5.setText((sbValue5.getProgress() - 24) + " dB");
            tvValue6.setText((sbValue6.getProgress() - 24) + " dB");
            tvValue7.setText((sbValue7.getProgress() - 24) + " dB");
            tvValue8.setText((sbValue8.getProgress() - 24) + " dB");
            tvValue9.setText((sbValue9.getProgress() - 24) + " dB");
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
        public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
            int id = seekBar.getId();
            float progress = (float) (progressValue * 0.01);
            String value = String.valueOf(progress);
            switch (id) {
                case R.id.reverb_dry:
                    mMediaEngine.onFxReverbValue(REVERB_DRY, progress);
                    txtDry.setText(value);
                    break;
                case R.id.reverb_mix:
                    mMediaEngine.onFxReverbValue(REVERB_MIX, progress);
                    txtMix.setText(value);
                    break;
                case R.id.reverb_roomsize:
                    mMediaEngine.onFxReverbValue(REVERB_ROOMSIZE, progress);
                    txtRoomSize.setText(value);
                    break;
                case R.id.reverb_wet:
                    mMediaEngine.onFxReverbValue(REVERB_WET, progress);
                    txtWet.setText(value);
                    break;
                case R.id.reverb_width:
                    mMediaEngine.onFxReverbValue(REVERB_WIDTH, progress);
                    txtWidth.setText(value);
                    break;
                case R.id.reverb_damp:
                    mMediaEngine.onFxReverbValue(REVERB_DAMP, progress);
                    txtDamp.setText(value);
                    break;
                case R.id.sbPredelay:
                    mMediaEngine.onFxReverbValue(REVERB_PREDELAY, progress * 1.0f);
                    txtPredelay.setText(progress + "");
                    break;
                case R.id.sbLowcutHzReverb:
                    mMediaEngine.onFxReverbValue(REVERB_LOW_CUT, progress * 1.0f);
                    txtLowcutHz.setText(progress + "");
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
    private SeekBar.OnSeekBarChangeListener seekBarEchoChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float dry = 1.0f * fxDryEcho.getProgress() / 100;
            float wet = 1.0f * fxWetEcho.getProgress() / 100;
            float bpm = fxBpmEcho.getProgress() + 60;
            float beats = ((fxBeats.getProgress() + 125) * 1.0f) / 1000;
            float decay = 1.0f * fxDecayEcho.getProgress() / 100;
            float mix = 1.0f * fxMixEcho.getProgress() / 100;

            txtDryEcho.setText(dry + "");
            txtWetEcho.setText(wet + "");
            txtBPMEcho.setText(bpm + "");
            txtBeatsEcho.setText(beats + "");
            txtDecayEcho.setText(decay + "");
            txtMixEcho.setText(mix + "");

            mMediaEngine.setEchoValue(dry, wet, bpm, beats, decay, mix);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public void SuperpoweredExample_PlayPause(View button) {
        mMediaEngine.enablePlayback(playing = !playing);
//        playing = !playing;
//        mMediaEngine.onPlayPause(playing);
//        sbTime.setMax((int) mMediaEngine.onGetTotalDuration());
//        if (playing) {
//            handler.post(onUpdateTime);
//        } else {
//            handler.removeCallbacks(onUpdateTime);
//        }
//        Button b = (Button) findViewById(R.id.playPause);
//        if (b != null) b.setText(playing ? "Pause" : "Play");
    }

    private Runnable onUpdateTime = new Runnable() {
        @Override
        public void run() {
//            tvTime.setText(getTimeAgo((int) mMediaEngine.onGetPosition()));
//            sbTime.setProgress((int) mMediaEngine.onGetPosition());
//            handler.postDelayed(onUpdateTime, 100);
        }
    };

    public static String getTimeAgo(int pastTime) {
        int millisecond = pastTime % 1000;
        int second = pastTime / 1000;
        int minutes = second / 60;
        int hour = minutes / 60;

        minutes = minutes % 60;
        second = second % 60;

        return (hour < 10 ? "0" + hour : hour) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (second < 10 ? "0" + second : second) + ":" + millisecond;
    }

    /**
     * Limiter open close
     */
    public void SuperpoweredExample_LimiterOpenClose(View button) {
//        open = !open;
//        mMediaEngine.onLimiterOpenClose(open);
//        Button b = (Button) findViewById(R.id.limiterOpenClose);
//        if (b != null) b.setText(open ? "LimiterClose" : "LimiterOpen");
    }

    public void Reset(View button) {
//        playing = false;
//        mMediaEngine.onPlayPause(playing);
//        Button b = (Button) findViewById(R.id.playPause);
//        if (b != null) b.setText(playing ? "Pause" : "Play");
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        mMediaEngine.release();
        super.onDestroy();
    }
}
