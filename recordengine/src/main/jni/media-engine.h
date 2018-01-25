#ifndef Header_SuperpoweredExample
#define Header_SuperpoweredExample

#include <math.h>
#include <pthread.h>
#include <stdio.h>

#include "media-engine.h"
#include <SuperpoweredAdvancedAudioPlayer.h>
#include <SuperpoweredFilter.h>
#include <SuperpoweredRoll.h>
#include <SuperpoweredFlanger.h>
#include <AndroidIO/SuperpoweredAndroidAudioIO.h>
#include <SuperpoweredRecorder.h>
#include <SuperpoweredReverb.h>
#include <SuperpoweredCompressor.h>
#include <SuperpoweredClipper.h>
#include <SuperpoweredLimiter.h>
#include <Superpowered3BandEQ.h>
#include <SuperpoweredNBandEQ.h>
#include <SuperpoweredEcho.h>
#include <jni.h>

#define HEADROOM_DECIBEL 3.0f
static const float headroom = powf(10.0f, -HEADROOM_DECIBEL * 0.025f);
static const int REVERB_NON = 0;
static const int REVERB_DRY = 1;
static const int REVERB_WET = 2;
static const int REVERB_WIDTH = 3;
static const int REVERB_MIX = 4;
static const int REVERB_ROOMSIZE = 5;
static const int REVERB_DAMP = 6;

class SuperpoweredProcess {
public:

    SuperpoweredProcess(JNIEnv *env, jobject obj, unsigned int samplerate, unsigned int buffersize,
                        const char *pathVoice,
                        const char *pathBeat,
                        int fileAoffset, int fileAlength, int fileBoffset, int fileBlength);

    ~SuperpoweredProcess();

    bool process(short int *output, unsigned int numberOfSamples);

    void onPlayPause(bool play);

    void onCrossfader(int value);

    void onFxSelect(int value);

    void onFxOff();

    void onInitJNI(JNIEnv *env, jobject obj);

    void onFxValue(int value);

    void onVolumeVoice(int value);

    void onVolumeBeat(int value);

    void onFxReverbValue(int param, float value);

    void onLimiterState(bool state);

    void onCompressorValue(float dryWetPercent, float ratio, float attack, float release,
                           float threshold, float hpCutOffHz);

    void onCompressEnable(bool isEnable);

    void onPitchShift(int value);

    void onBandValues(float low, float mid, float high);

    void onProcessMixer();

    void onReset();

    void onSeekTime(double d);

    double onGetPosition();

    double onGetTotalDuration();

    void onProcessBandEQ(float value0, float value1, float value2, float value3, float value4,
                         float value5, float value6, float value7, float value8, float value9);

    double getReverbParams();

    void onEchoValue(float dry, float wet, float bpm, float beats, float decay, float mix);


private:
    SuperpoweredAndroidAudioIO *audioSystem;
    SuperpoweredAdvancedAudioPlayer *playerA, *playerB;
    SuperpoweredRoll *roll;
    SuperpoweredFilter *filter;
    SuperpoweredFlanger *flanger;
    SuperpoweredReverb *reverb;
    SuperpoweredLimiter *limiter;
    SuperpoweredCompressor *compressor;
    Superpowered3BandEQ *threeBandEQ;
    SuperpoweredEcho *echo;
    SuperpoweredNBandEQ *nBandEQ;
//    SuperpoweredRecorder *recorder;

    float *eqBandList;
    float *stereoBuffer;
    unsigned char activeFx;
    float crossValue, volA, volB;
    bool isRecording;
    bool isRemove;
};

#endif
