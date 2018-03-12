#include <stdint.h>//
// Created by shoot on 2018-01-10.
//

#ifndef FREQUENCYDOMAIN_FREQUENCYDOMAIN_H
#define FREQUENCYDOMAIN_FREQUENCYDOMAIN_H

#include "record-engine.h"
#include <SuperpoweredReverb.h>
#include <AndroidIO/SuperpoweredAndroidAudioIO.h>
#include <SuperpoweredFrequencyDomain.h>
#include <SuperpoweredRecorder.h>
#include <SuperpoweredCompressor.h>
#include <Superpowered3BandEQ.h>
#include <SuperpoweredNBandEQ.h>
#include <SuperpoweredEcho.h>
#include <jni.h>

static const int REVERB_DRY = 1;
static const int REVERB_WET = 2;
static const int REVERB_WIDTH = 3;
static const int REVERB_MIX = 4;
static const int REVERB_ROOMSIZE = 5;
static const int REVERB_DAMP = 6;
static const char *sigVoidIntMethod = "()V";
static char *RECORD_ART = (char *) "DCSoft artical";
static char *RECORD_TITLE = (char *) "DCSoft title";

class RecordEngine {

public:
    void setReverbParams(int paramID, float value);

//    RecordEngine(int bufferSize, int sampleRate);

    RecordEngine(JNIEnv *env, jobject jObject, const char *path, int bufferSize, int sampleRate);

    bool
    process(short int *audioInputOutput, unsigned int numberOfSamples, unsigned int sampleRate);

    void startRecord();

    void startRecordPath(const char *path);

    void startRecordWithOffset(int offset);

    void stopRecord();

    void enablePlayback(bool enable);

    void onCompressorValue(float dryWetPercent, float ratio, float attack, float release,
                           float threshold, float hpCutOffHz);

    void onEchoValue(float dry, float wet, float bpm, float beats, float decay, float mix);

    void onBandValues(float low, float mid, float high);

    void onProcessBandEQ(float value0, float value1, float value2, float value3, float value4,
                         float value5, float value6, float value7, float value8, float value9);

    void enableEffect(bool enable);

    void __unused onSampleRecordListener(short *data, int sampleSize);

    ~RecordEngine();

private:
    SuperpoweredReverb *reverb;
    SuperpoweredAndroidAudioIO *audioSystem;
    SuperpoweredRecorder *recorder;
    SuperpoweredCompressor *compressor;
    Superpowered3BandEQ *threeBandEQ;
    SuperpoweredEcho *echo;
    SuperpoweredNBandEQ *nBandEQ;
//    SuperpoweredRecorderStoppedCallback recorderStoppedCallback;

    bool isPlayback = false;

    float *eqBandList;
    const char *pathRecord;
    bool isRecording;
    float *inputBufferFloat;
    JNIEnv *jniEnv;
    jobject jObject;
    jobject jGlobalObject;
    jmethodID onInitDoneMethodID;
    jmethodID onRecordSampleListenerID;
    JavaVM *javaVM;
    jclass targetClass;
    jshortArray samples;
    float *recordBufferFloat;
};

#endif
