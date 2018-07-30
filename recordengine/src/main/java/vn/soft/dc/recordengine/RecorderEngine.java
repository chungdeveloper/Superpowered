package vn.soft.dc.recordengine;

import android.os.Environment;

import vn.soft.dc.recordengine.audio.calculators.AudioCalculator;
import vn.soft.dc.recordengine.model.Preset;

import static vn.soft.dc.recordengine.model.Preset.REVERB_DAMP;
import static vn.soft.dc.recordengine.model.Preset.REVERB_DRY;
import static vn.soft.dc.recordengine.model.Preset.REVERB_LOW_CUT;
import static vn.soft.dc.recordengine.model.Preset.REVERB_MIX;
import static vn.soft.dc.recordengine.model.Preset.REVERB_PREDELAY;
import static vn.soft.dc.recordengine.model.Preset.REVERB_ROOMSIZE;
import static vn.soft.dc.recordengine.model.Preset.REVERB_WET;
import static vn.soft.dc.recordengine.model.Preset.REVERB_WIDTH;
import static vn.soft.dc.recordengine.util.FileUtils.createFileDirectory;
import static vn.soft.dc.recordengine.util.FileUtils.short2byte;

/**
 * Created by Le Duc Chung on 2018-01-17.
 * on project 'RecordEngine30'
 */

public class RecorderEngine {

    private static final String LIB_CPP = "record-engine";
    private OnRecordEventListener onRecordEventListener;
    private AudioCalculator mAudioCalculator;
    private int thresholdAmp = 1200;

    @SuppressWarnings("unused")
    public void onInitDoneListener() {
        if (onRecordEventListener == null) return;
        onRecordEventListener.onInitSuccess();
    }

    public RecorderEngine(int sampleRate, int bufferSize) {
//        System.loadLibrary(LIB_CPP);
        mAudioCalculator = new AudioCalculator();
        FrequencyDomainWithRecorder(createFileDirectory(RECORD_URI.MUSIC_TV_360_DIRECTORY_TEMP) + System.currentTimeMillis() + ".wav", sampleRate, bufferSize);
    }

    @SuppressWarnings("unused")
    public RecorderEngine(int sampleRate, int bufferSize, OnRecordEventListener onRecordEventListener) {
//        System.loadLibrary(LIB_CPP);
        mAudioCalculator = new AudioCalculator();
        this.onRecordEventListener = onRecordEventListener;
        FrequencyDomainWithRecorder(createFileDirectory(RECORD_URI.MUSIC_TV_360_DIRECTORY_TEMP) + System.currentTimeMillis() + ".wav", sampleRate, bufferSize);
    }

    @SuppressWarnings("unused")
    public void setThresholdAmp(int thresholdAmp) {
        this.thresholdAmp = thresholdAmp;
    }

    @SuppressWarnings("unused")
    public void onSampleRecordListener(final short[] sample) {
        if (onRecordEventListener == null) return;
        mAudioCalculator.setBytes(short2byte(sample));
        int amp = mAudioCalculator.getAmplitude();
        if (amp < thresholdAmp) return;
        onRecordEventListener.onFrequencyListener(mAudioCalculator.getFrequency(sample.length));
    }

    public void enableEffectVocal(boolean enable) {
        enableEffect(enable);
    }

    public void startRecord(String path) {
        startRecordFilePath(path);
    }

    public void stopRecord() {
        stopRecordFile();
    }


    public void changeEffect(Preset preset) {
        onFxReverbValue(REVERB_DRY, preset.getDryReverb());
        onFxReverbValue(REVERB_WET, preset.getWetReverb());
        onFxReverbValue(REVERB_MIX, preset.getMixReverb());
        onFxReverbValue(REVERB_WIDTH, preset.getWidthReverb());
        onFxReverbValue(REVERB_DAMP, preset.getDampReverb());
        onFxReverbValue(REVERB_PREDELAY, preset.getPreDelay());
        onFxReverbValue(REVERB_LOW_CUT, preset.getLowcutReverb());
        onFxReverbValue(REVERB_ROOMSIZE, preset.getRoomsizeReverb());

        float dry = preset.getDryEcho();
        float wet = preset.getWetEcho();
        float bpm = preset.getBeatsEcho();
        float beats = preset.getBeatsEcho();
        float decay = preset.getDecayEcho();
        float mix = preset.getMixEcho();
        setEchoValue(dry, wet, bpm, beats, decay, mix);

        float compThreshold, compRatio, compRelease, compAttack, compWet;
        int compHP;

        compHP = preset.getHpCut();
        compThreshold = preset.getThresholdCompressor();
        compRatio = preset.getRatioCompressor();
        compRelease = preset.getReleaseCompressor();
        compAttack = preset.getAttackCompressor();
        compWet = preset.getDryCompressor();

        onCompressorValue(compWet, compRatio, compAttack, compRelease, compThreshold, compHP);

        onProcessBandEQ((preset.getValueEQ0())
                , (preset.getValueEQ1())
                , (preset.getValueEQ2())
                , (preset.getValueEQ3())
                , (preset.getValueEQ4())
                , (preset.getValueEQ5())
                , (preset.getValueEQ6())
                , (preset.getValueEQ7())
                , (preset.getValueEQ8())
                , (preset.getValueEQ9()));

        onBandValues(preset.getBass(), preset.getMid(), preset.getHi());
    }

    public void setOnRecordEventListener(OnRecordEventListener onRecordEventListener) {
        this.onRecordEventListener = onRecordEventListener;
    }

    public native void enableEffect(boolean enable);

    public native void FrequencyDomainWithRecorder(String path, int sampleRate, int bufferSize);

    @SuppressWarnings("unused")
    public native void startRecordFile();

    public native void startRecordFilePath(String pathTarget);

    @SuppressWarnings("unused")
    public native void startRecordWithOffset(int offset);

    public native void stopRecordFile();

    public native void setEchoValue(float dry, float wet, float bpm, float beats, float decay, float mix);

    public native void onProcessBandEQ(float value0, float value1, float value2, float value3,
                                       float value4, float value5, float value6, float value7,
                                       float value8, float value9);

    public native void onBandValues(float low, float mid, float high);

    public native void onCompressorValue(float dryWetPercent, float ratio, float attack, float release,
                                         float threshold, float hpCutOffHz);

    @SuppressWarnings("SpellCheckingInspection")
    public native void onFxReverbValue(int param, float scaleValue);

    public native void enablePlayback(boolean enable);

    public native void release();

    static {
        System.loadLibrary(LIB_CPP);
    }

    @SuppressWarnings("unused")
    public interface OnRecordEventListener {
        void onInitSuccess();

        void onFrequencyListener(double freq);
    }

    public interface RECORD_URI {
        String SD_CARD = Environment.getExternalStorageDirectory().toString() + "/";
        String MUSIC_TV_360_DIRECTORY = SD_CARD + "Kara360/";
        String MUSIC_TV_360_DIRECTORY_RESOURCE = MUSIC_TV_360_DIRECTORY + "Resource/";
        String MUSIC_TV_360_DIRECTORY_TEMP = MUSIC_TV_360_DIRECTORY + "Temp/";
        String MUSIC_TV_360_DIRECTORY_BEAT = MUSIC_TV_360_DIRECTORY_RESOURCE + "beat/";
        String MUSIC_TV_360_DIRECTORY_DRAFT = MUSIC_TV_360_DIRECTORY_RESOURCE + "draft/";
    }

}
