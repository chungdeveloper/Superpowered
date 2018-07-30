package vn.soft.dc.recordengine.model;

/**
 * Created by Le Duc Chung on 5/25/2017.
 * on project 'musictv-android'
 */

public class Preset {
    public static int REVERB_DRY = 1;
    public static int REVERB_WET = 2;
    public static int REVERB_WIDTH = 3;
    public static int REVERB_MIX = 4;
    public static int REVERB_ROOMSIZE = 5;
    public static int REVERB_DAMP = 6;
    public static int REVERB_PREDELAY = 7;
    public static int REVERB_LOW_CUT = 8;

    private float dryEcho;
    private float wetEcho;
    private float bpmEcho;
    private float beatsEcho;
    private float mixEcho;
    private float decayEcho;

    private float dryReverb;
    private float wetReverb;
    private float widthReverb;
    private float roomsizeReverb;
    private float mixReverb;
    private float dampReverb;
    private float lowcutReverb;
    private float preDelay;

    private float dryCompressor;
    private float attackCompressor;
    private float releaseCompressor;
    private float ratioCompressor;
    private float thresholdCompressor;

    private float bass;
    private float mid;
    private float hi;

    private float valueEQ0;
    private float valueEQ1;
    private float valueEQ2;
    private float valueEQ3;
    private float valueEQ4;
    private float valueEQ5;
    private float valueEQ6;
    private float valueEQ7;
    private float valueEQ8;
    private float valueEQ9;

    private int pitch;
    private int hpCut;
    private float delay;
    private String name;


    public Preset() {
        this.dryEcho = 0;
        this.wetEcho = 0;
        this.bpmEcho = 0;
        this.beatsEcho = 0;
        this.mixEcho = 0;
        this.decayEcho = 0;
        this.dryReverb = 0;
        this.wetReverb = 0;
        this.widthReverb = 0;
        this.roomsizeReverb = 0;
        this.mixReverb = 0;
        this.dampReverb = 0;
        this.lowcutReverb = 0;
        this.preDelay = 0;
        this.dryCompressor = 0;
        this.attackCompressor = 0;
        this.releaseCompressor = 0;
        this.ratioCompressor = 0;
        this.thresholdCompressor = 0;
        this.bass = 0;
        this.mid = 0;
        this.hi = 0;
        this.pitch = 0;
        this.hpCut = 0;
        this.delay = 0;
        this.name = "";
        this.valueEQ0 = 0;
        this.valueEQ1 = 0;
        this.valueEQ2 = 0;
        this.valueEQ3 = 0;
        this.valueEQ4 = 0;
        this.valueEQ5 = 0;
        this.valueEQ6 = 0;
        this.valueEQ7 = 0;
        this.valueEQ8 = 0;
        this.valueEQ9 = 0;
    }

    public Preset(String name) {
        this.name = name;
        this.dryEcho = 0;
        this.wetEcho = 0;
        this.bpmEcho = 0;
        this.beatsEcho = 0;
        this.mixEcho = 0;
        this.decayEcho = 0;
        this.dryReverb = 0;
        this.wetReverb = 0;
        this.widthReverb = 0;
        this.roomsizeReverb = 0;
        this.mixReverb = 0;
        this.dampReverb = 0;
        this.valueEQ0 = 0;
        this.valueEQ1 = 0;
        this.lowcutReverb = 0;
        this.preDelay = 0;
        this.valueEQ2 = 0;
        this.valueEQ3 = 0;
        this.valueEQ4 = 0;
        this.valueEQ5 = 0;
        this.valueEQ6 = 0;
        this.valueEQ7 = 0;
        this.valueEQ8 = 0;
        this.valueEQ9 = 0;
        this.dryCompressor = 0;
        this.attackCompressor = 0;
        this.releaseCompressor = 0;
        this.ratioCompressor = 0;
        this.thresholdCompressor = 0;
        this.bass = 0;
        this.mid = 0;
        this.hi = 0;
        this.pitch = 0;
        this.hpCut = 0;
        this.delay = 0;
    }

    public Preset(float dryEcho, float wetEcho, float bpmEcho, float beatsEcho, float mixEcho, float decayEcho, float dryReverb, float wetReverb, float widthReverb, float roomsizeReverb, float mixReverb, float dampReverb, float dryCompressor, float attackCompressor, float releaseCompressor, float ratioCompressor, float thresholdCompressor, float bass, float mid, float hi, float valueEQ0, float valueEQ1, float valueEQ2, float valueEQ3, float valueEQ4, float valueEQ5, float valueEQ6, float valueEQ7, float valueEQ8, float valueEQ9, int pitch, int hpCut, String name) {
        this.dryEcho = dryEcho;
        this.wetEcho = wetEcho;
        this.bpmEcho = bpmEcho;
        this.beatsEcho = beatsEcho;
        this.mixEcho = mixEcho;
        this.decayEcho = decayEcho;
        this.dryReverb = dryReverb;
        this.wetReverb = wetReverb;
        this.widthReverb = widthReverb;
        this.roomsizeReverb = roomsizeReverb;
        this.mixReverb = mixReverb;
        this.dampReverb = dampReverb;
        this.dryCompressor = dryCompressor;
        this.attackCompressor = attackCompressor;
        this.releaseCompressor = releaseCompressor;
        this.ratioCompressor = ratioCompressor;
        this.thresholdCompressor = thresholdCompressor;
        this.bass = bass;
        this.mid = mid;
        this.hi = hi;
        this.valueEQ0 = valueEQ0;
        this.valueEQ1 = valueEQ1;
        this.valueEQ2 = valueEQ2;
        this.valueEQ3 = valueEQ3;
        this.valueEQ4 = valueEQ4;
        this.valueEQ5 = valueEQ5;
        this.valueEQ6 = valueEQ6;
        this.valueEQ7 = valueEQ7;
        this.valueEQ8 = valueEQ8;
        this.valueEQ9 = valueEQ9;
        this.pitch = pitch;
        this.hpCut = hpCut;
        this.name = name;
    }

    public float getDryEcho() {
        return dryEcho;
    }

    public void setDryEcho(float dryEcho) {
        this.dryEcho = dryEcho;
    }

    public float getWetEcho() {
        return wetEcho;
    }

    public void setWetEcho(float wetEcho) {
        this.wetEcho = wetEcho;
    }

    public float getBpmEcho() {
        return bpmEcho;
    }

    public void setBpmEcho(float bpmEcho) {
        this.bpmEcho = bpmEcho;
    }

    public float getBeatsEcho() {
        return beatsEcho;
    }

    public void setBeatsEcho(float beatsEcho) {
        this.beatsEcho = beatsEcho;
    }

    public float getMixEcho() {
        return mixEcho;
    }

    public void setMixEcho(float mixEcho) {
        this.mixEcho = mixEcho;
    }

    public float getDecayEcho() {
        return decayEcho;
    }

    public void setDecayEcho(float decayEcho) {
        this.decayEcho = decayEcho;
    }

    public float getDryReverb() {
        return dryReverb;
    }

    public void setDryReverb(float dryReverb) {
        this.dryReverb = dryReverb;
    }

    public float getWetReverb() {
        return wetReverb;
    }

    public void setWetReverb(float wetReverb) {
        this.wetReverb = wetReverb;
    }

    public float getWidthReverb() {
        return widthReverb;
    }

    public void setWidthReverb(float widthReverb) {
        this.widthReverb = widthReverb;
    }

    public float getRoomsizeReverb() {
        return roomsizeReverb;
    }

    public void setRoomsizeReverb(float roomsizeReverb) {
        this.roomsizeReverb = roomsizeReverb;
    }

    public float getMixReverb() {
        return mixReverb;
    }

    public void setMixReverb(float mixReverb) {
        this.mixReverb = mixReverb;
    }

    public float getDampReverb() {
        return dampReverb;
    }

    public void setDampReverb(float dampReverb) {
        this.dampReverb = dampReverb;
    }

    public float getDryCompressor() {
        return dryCompressor;
    }

    public void setDryCompressor(float dryCompressor) {
        this.dryCompressor = dryCompressor;
    }

    public float getAttackCompressor() {
        return attackCompressor;
    }

    public void setAttackCompressor(float attackCompressor) {
        this.attackCompressor = attackCompressor;
    }

    public float getReleaseCompressor() {
        return releaseCompressor;
    }

    public void setReleaseCompressor(float releaseCompressor) {
        this.releaseCompressor = releaseCompressor;
    }

    public float getRatioCompressor() {
        return ratioCompressor;
    }

    public void setRatioCompressor(float ratioCompressor) {
        this.ratioCompressor = ratioCompressor;
    }

    public float getThresholdCompressor() {
        return thresholdCompressor;
    }

    public void setThresholdCompressor(float thresholdCompressor) {
        this.thresholdCompressor = thresholdCompressor;
    }

    public float getBass() {
        return bass;
    }

    public void setBass(float bass) {
        this.bass = bass;
    }

    public float getMid() {
        return mid;
    }

    public void setMid(float mid) {
        this.mid = mid;
    }

    public float getHi() {
        return hi;
    }

    public void setHi(float hi) {
        this.hi = hi;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getHpCut() {
        return hpCut;
    }

    public void setHpCut(int hpCut) {
        this.hpCut = hpCut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValueEQ0() {
        return valueEQ0;
    }

    public void setValueEQ0(float valueEQ0) {
        this.valueEQ0 = valueEQ0;
    }

    public float getValueEQ1() {
        return valueEQ1;
    }

    public void setValueEQ1(float valueEQ1) {
        this.valueEQ1 = valueEQ1;
    }

    public float getValueEQ2() {
        return valueEQ2;
    }

    public void setValueEQ2(float valueEQ2) {
        this.valueEQ2 = valueEQ2;
    }

    public float getValueEQ3() {
        return valueEQ3;
    }

    public void setValueEQ3(float valueEQ3) {
        this.valueEQ3 = valueEQ3;
    }

    public float getValueEQ4() {
        return valueEQ4;
    }

    public void setValueEQ4(float valueEQ4) {
        this.valueEQ4 = valueEQ4;
    }

    public float getValueEQ5() {
        return valueEQ5;
    }

    public void setValueEQ5(float valueEQ5) {
        this.valueEQ5 = valueEQ5;
    }

    public float getValueEQ6() {
        return valueEQ6;
    }

    public void setValueEQ6(float valueEQ6) {
        this.valueEQ6 = valueEQ6;
    }

    public float getValueEQ7() {
        return valueEQ7;
    }

    public void setValueEQ7(float valueEQ7) {
        this.valueEQ7 = valueEQ7;
    }

    public float getValueEQ8() {
        return valueEQ8;
    }

    public void setValueEQ8(float valueEQ8) {
        this.valueEQ8 = valueEQ8;
    }

    public float getValueEQ9() {
        return valueEQ9;
    }

    public void setValueEQ9(float valueEQ9) {
        this.valueEQ9 = valueEQ9;
    }

    public float getLowcutReverb() {
        return lowcutReverb;
    }

    public void setLowcutReverb(float lowcutReverb) {
        this.lowcutReverb = lowcutReverb;
    }

    public float getPreDelay() {
        return preDelay;
    }

    public void setPreDelay(float preDelay) {
        this.preDelay = preDelay;
    }

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }
}
