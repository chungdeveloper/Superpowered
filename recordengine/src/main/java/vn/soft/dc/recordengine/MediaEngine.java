package vn.soft.dc.recordengine;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import vn.soft.dc.recordengine.model.Preset;
import vn.soft.dc.recordengine.util.FileUtils;

import static android.widget.Toast.LENGTH_SHORT;
import static vn.soft.dc.recordengine.model.Preset.REVERB_DAMP;
import static vn.soft.dc.recordengine.model.Preset.REVERB_DRY;
import static vn.soft.dc.recordengine.model.Preset.REVERB_MIX;
import static vn.soft.dc.recordengine.model.Preset.REVERB_ROOMSIZE;
import static vn.soft.dc.recordengine.model.Preset.REVERB_WET;
import static vn.soft.dc.recordengine.model.Preset.REVERB_WIDTH;

/**
 * Created by Le Duc Chung on 2018-01-17.
 * on project 'RecordEngine30'
 */

public class MediaEngine {

    private static final String LIB_MEDIA = "record-engine";

    private Context mContext;
    private int sampleRate;
    private int bufferSize;
    private Uri vocalURI;
    private Uri beatURI;

    static {
        System.loadLibrary(LIB_MEDIA);
    }

    private boolean isPlaying;

    public MediaEngine(Context context) {
        this.mContext = context;
        isPlaying = false;
    }

    @SuppressWarnings("unused")
    public MediaEngine setAudioFormat(int sampleRate, int bufferSize) {
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;
        return this;
    }

    @SuppressWarnings("unused")
    public MediaEngine setResource(String beatPath, String voicePath) {
        beatURI = FileUtils.getContentUri(mContext, new File(beatPath));
        vocalURI = FileUtils.getContentUri(mContext, new File(voicePath));
        return this;
    }

    @SuppressWarnings("ConstantConditions")
    public MediaEngine build() {
        try {
            AssetFileDescriptor
                    fd0 = mContext.getContentResolver().openAssetFileDescriptor(vocalURI, "r"),
                    fd1 = mContext.getContentResolver().openAssetFileDescriptor(beatURI, "r");
            int
                    fileAoffset = (int) fd0.getStartOffset(), fileAlength = (int) fd0.getLength(),
                    fileBoffset = (int) fd1.getStartOffset(), fileBlength = (int) fd1.getLength();

            try {
                fd0.getParcelFileDescriptor().close();
                fd1.getParcelFileDescriptor().close();
            } catch (IOException e) {
                Log.e("MediaEngine", "Khởi tạo sấp mặt");
            }

            SuperpoweredProcessDouble(sampleRate, bufferSize, FileUtils.getPath(mContext, vocalURI), FileUtils.getPath(mContext, beatURI), fileAoffset, fileAlength, fileBoffset, fileBlength);
        } catch (Exception ex) {
            Log.e("MediaEngine", ex.toString());
            Toast.makeText(mContext, "Tệp tin không tồn tại", LENGTH_SHORT).show();
        }
        return this;
    }

    @SuppressWarnings("unused")
    public void play() {
        if (isPlaying) return;
        onPlayPause(isPlaying = true);
    }

    @SuppressWarnings("unused")
    public void pause() {
        if (!isPlaying) return;
        onPlayPause(isPlaying = false);
    }

    @SuppressWarnings("unused")
    public void setPreset(Preset preset) {
        onFxReverbValue(REVERB_DRY, preset.getDryReverb());
        onFxReverbValue(REVERB_WET, preset.getWetReverb());
        onFxReverbValue(REVERB_MIX, preset.getMixReverb());
        onFxReverbValue(REVERB_WIDTH, preset.getWidthReverb());
        onFxReverbValue(REVERB_DAMP, preset.getDampReverb());
        onFxReverbValue(REVERB_ROOMSIZE, preset.getRoomsizeReverb());

        float dry = preset.getDryEcho();
        float wet = preset.getWetEcho();
        float bpm = preset.getBeatsEcho();
        float beats = preset.getBeatsEcho();
        float decay = preset.getDecayEcho();
        float mix = preset.getMixEcho();
        onEchoValue(dry, wet, bpm, beats, decay, mix);

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
        onPitchShift(preset.getPitch());
    }

    @SuppressWarnings("unused")
    public void setVolumeBeat(int vol) {
        onVolumeBeat(vol);
    }

    @SuppressWarnings("unused")
    public void setVolumeVocal(int vol) {
        onVolumeVoice(vol);
    }

    public native void SuperpoweredProcessDouble(int samplerate, int bufferSize, String pathVoice, String pathBeat, int fileAoffset, int fileAlength, int fileBoffset, int fileBlength);

    public native void onPlayPause(boolean play);

    @SuppressWarnings("unused")
    public native void onLimiterOpenClose(boolean state);

    @SuppressWarnings("unused")
    public native void onCrossfader(int value);

    @SuppressWarnings("unused")
    public native void onFxSelect(int value);

    @SuppressWarnings("unused")
    public native void onSeekTime(double v);

    @SuppressWarnings("unused")
    public native double onGetPosition();

    @SuppressWarnings("unused")
    public native double onGetTotalDuration();

    @SuppressWarnings("unused")
    public native void onCompressEnable(boolean isEnable);

    public native void onCompressorValue(float dryWetPercent, float ratio, float attack, float release, float threshold, float hpCutOffHz);

    @SuppressWarnings("unused")
    public native void onFxValue(int value);

    public native void onPitchShift(int value);

    public native void onEchoValue(float dry, float wet, float bpm, float beats, float decay, float mix);

    public native void onBandValues(float low, float mid, float hi);

    public native void onFxReverbValue(int reverb_type, float value);

    @SuppressWarnings("unused")
    public native void onReset();

    public native void onVolumeVoice(int value);

    public native void onVolumeBeat(int value);


    public native void onProcessBandEQ(float value0, float value1, float value2, float value3,
                                       float value4, float value5, float value6, float value7,
                                       float value8, float value9);


    public void onMusicCompletion() {
    }

    public void onMusicStart() {
    }

    public void callbackFromC() {
    }

}
