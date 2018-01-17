#include "record-engine.h"
#include <jni.h>
#include <stdlib.h>
#include <SuperpoweredSimple.h>
#include <SuperpoweredCPU.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <string.h>
#include <pthread.h>

static RecordEngine *executeProcess = NULL;

static bool
audioProcessing(void *__unused clientdata, short int *audioInputOutput, int numberOfSamples,
                int __unused samplerate) {
    return executeProcess->process(audioInputOutput, (unsigned int) numberOfSamples);
}

// This is called periodically by the media server.
bool RecordEngine::process(short int *audioInputOutput, unsigned int numberOfSamples) {
    SuperpoweredShortIntToFloat(audioInputOutput, inputBufferFloat,
                                numberOfSamples);
    if (isRecording) {
        SuperpoweredDeInterleave(inputBufferFloat, recordBufferFloat, recordBufferFloat,
                                 numberOfSamples);
        recorder->process(recordBufferFloat, numberOfSamples);
    }
    reverb->process(inputBufferFloat, inputBufferFloat, numberOfSamples);
    compressor->process(inputBufferFloat, inputBufferFloat, numberOfSamples);
    threeBandEQ->process(inputBufferFloat, inputBufferFloat, numberOfSamples);
    nBandEQ->process(inputBufferFloat, inputBufferFloat, numberOfSamples);
    SuperpoweredFloatToShortInt(inputBufferFloat, audioInputOutput, numberOfSamples);
//    onSampleRecordListener(audioInputOutput, numberOfSamples);
    return true;
}

void RecordEngine::onSampleRecordListener(short *data, int sampleSize) {
    javaVM->AttachCurrentThread(&jniEnv, NULL);
    samples = jniEnv->NewShortArray(sampleSize);
    (jniEnv)->SetShortArrayRegion(samples, 0, sampleSize, data);
    (jniEnv)->CallVoidMethod(jGlobalObject, onRecordSampleListenerID, samples);
    javaVM->DetachCurrentThread();
}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wwritable-strings"
#pragma clang diagnostic ignored "-Wreturn-stack-address"

RecordEngine::RecordEngine(JNIEnv *env, jobject instance, const char *path, int bufferSize,
                           int sampleRate) {
    jniEnv = env;
    jObject = instance;
    targetClass = (jniEnv)->GetObjectClass(jObject);
    pathRecord = path;
    isRecording = false;
    onInitDoneMethodID = (jniEnv)->GetMethodID(targetClass, "onInitDoneListener", sigVoidIntMethod);
    (jniEnv)->GetJavaVM(&javaVM);
    jGlobalObject = jniEnv->NewGlobalRef(jObject);
    onRecordSampleListenerID = (jniEnv)->GetMethodID(targetClass, "onSampleRecordListener",
                                                     "([S)V");


    float tmpValue[] = {40.0f, 63.0f, 125.0f, 250.0f, 500.0f, 1000.0f, 2000.0f, 4000.0f, 8000.0f,
                        16000.0f, 0.0f};
    eqBandList = tmpValue;

    recorder = new SuperpoweredRecorder(path, (unsigned int) bufferSize, 0, 1, false,
                                        NULL, this);
    compressor = new SuperpoweredCompressor((unsigned int) sampleRate);
    threeBandEQ = new Superpowered3BandEQ((unsigned int) sampleRate);
    echo = new SuperpoweredEcho((unsigned int) sampleRate);
    reverb = new SuperpoweredReverb((unsigned int) sampleRate);
    nBandEQ = new SuperpoweredNBandEQ((unsigned int) sampleRate, eqBandList);

    recorder->setSamplerate((unsigned int) sampleRate);

    inputBufferFloat = (float *) malloc(bufferSize * sizeof(float) * 2 + 128);
    recordBufferFloat = (float *) malloc(bufferSize * sizeof(float) * 2 + 128);

    SuperpoweredCPU::setSustainedPerformanceMode(true);
    audioSystem = new SuperpoweredAndroidAudioIO(sampleRate, bufferSize, true, true,
                                                 audioProcessing, this,
                                                 SL_ANDROID_RECORDING_PRESET_VOICE_RECOGNITION/*-1*/,
                                                 SL_ANDROID_STREAM_MEDIA,
                                                 bufferSize);
    (jniEnv)->CallVoidMethod(jObject, onInitDoneMethodID, 10);
}

#pragma clang diagnostic pop

RecordEngine::RecordEngine(int bufferSize, int sampleRate) {
    reverb = new SuperpoweredReverb((unsigned int) sampleRate);
    reverb->enable(true);
    SuperpoweredCPU::setSustainedPerformanceMode(true);
    audioSystem = new SuperpoweredAndroidAudioIO(sampleRate, bufferSize, true, true,
                                                 audioProcessing, NULL, -1,
                                                 SL_ANDROID_STREAM_MEDIA,
                                                 bufferSize * 2);
}


void RecordEngine::setReverbParams(int param, float scaleValue) {
    reverb->enable(true);
    switch (param) {
        case REVERB_DRY:
            reverb->setDry(scaleValue);
            break;
        case REVERB_MIX:
            reverb->setMix(scaleValue);
            break;
        case REVERB_WIDTH:
            reverb->setWidth(scaleValue);
            break;
        case REVERB_ROOMSIZE:
            reverb->setRoomSize(scaleValue);
            break;
        case REVERB_WET:
            reverb->setWet(scaleValue);
            break;
        case REVERB_DAMP:
            reverb->setDamp(scaleValue);
            break;
        default:
            break;
    }
}

RecordEngine::~RecordEngine() {
    delete recorder;
    delete reverb;
    delete echo;
    delete threeBandEQ;
    delete compressor;
    delete nBandEQ;
    delete audioSystem;
    delete jniEnv;
    delete jObject;
//    delete onInitDoneMethodID;
    delete targetClass;
    free(recordBufferFloat);
    free(inputBufferFloat);
    free(samples);
    delete pathRecord;
    delete eqBandList;
}

void RecordEngine::startRecord() {
    startRecordWithOffset(0);
}

void RecordEngine::startRecordWithOffset(int offset) {
    recorder->addToTracklist(RECORD_ART, RECORD_TITLE, offset, false);
    recorder->start(pathRecord);
    isRecording = true;
}

void RecordEngine::stopRecord() {
    if (!isRecording)
        return;
    recorder->stop();
    isRecording = false;
}

void
RecordEngine::onCompressorValue(float dryWetPercent, float ratio, float attack, float release,
                                float threshold, float hpCutOffHz) {
    compressor->enable(true);
    compressor->wet = dryWetPercent;
    compressor->ratio = ratio;
    compressor->attackSec = attack;
    compressor->releaseSec = release;
    compressor->thresholdDb = threshold;
    compressor->hpCutOffHz = hpCutOffHz;
}

void
RecordEngine::onEchoValue(float dry, float wet, float bpm, float beats, float decay, float mix) {
    echo->enable(true);
    echo->dry = dry;
    echo->wet = wet;
    echo->beats = beats;
    echo->bpm = bpm;
    echo->decay = decay;
    echo->setMix(mix);
}

void RecordEngine::onBandValues(float low, float mid, float high) {
    threeBandEQ->enable(true);
    threeBandEQ->bands[0] = low;
    threeBandEQ->bands[1] = mid;
    threeBandEQ->bands[2] = high;
}

void RecordEngine::onProcessBandEQ(float value0, float value1, float value2, float value3,
                                   float value4, float value5, float value6, float value7,
                                   float value8, float value9) {
    nBandEQ->enable(true);
    nBandEQ->setBand(0, value0);
    nBandEQ->setBand(1, value1);
    nBandEQ->setBand(2, value2);
    nBandEQ->setBand(3, value3);
    nBandEQ->setBand(4, value4);
    nBandEQ->setBand(5, value5);
    nBandEQ->setBand(6, value6);
    nBandEQ->setBand(7, value7);
    nBandEQ->setBand(8, value8);
    nBandEQ->setBand(9, value9);
}

void RecordEngine::enableEffect(bool enable) {
    reverb->enable(enable);
    echo->enable(enable);
    nBandEQ->enable(enable);
    threeBandEQ->enable(enable);
    compressor->enable(enable);
}

void RecordEngine::startRecordPath(const char *path) {
    if (isRecording)
        return;
    recorder->addToTracklist(RECORD_ART, RECORD_TITLE, 0123);
    recorder->start(path);
    isRecording = true;
}


extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_FrequencyDomain(JNIEnv *__unused javaEnvironment,
                                                            jobject __unused obj,
                                                            jint samplerate,
                                                            jint buffersize) {
    executeProcess = new RecordEngine(buffersize, samplerate);
}


#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_FrequencyDomainWithRecorder(JNIEnv *env,
                                                                        jobject instance,
                                                                        jstring path_,
                                                                        jint sampleRate,
                                                                        jint bufferSize) {
    const char *path = env->GetStringUTFChars(path_, 0);
    executeProcess = new RecordEngine(env, instance, path, bufferSize, sampleRate);
    env->ReleaseStringUTFChars(path_, path);
}
#pragma clang diagnostic pop

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
extern "C" JNIEXPORT void JNICALL
Java_vn_soft_dc_recordengine_RecorderEngine_startRecordFile(JNIEnv *env, jobject instance) {

    executeProcess->startRecord();

}
#pragma clang diagnostic pop

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_stopRecordFile(JNIEnv *env, jobject instance) {

    executeProcess->stopRecord();

}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_setEchoValue(JNIEnv *env, jobject instance,
                                                         jfloat dry, jfloat wet, jfloat bpm,
                                                         jfloat beats, jfloat decay,
                                                         jfloat mix) {
    executeProcess->onEchoValue(dry, wet, bpm, beats, decay, mix);
}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_onProcessBandEQ(JNIEnv *env, jobject instance,
                                                            jfloat value0, jfloat value1,
                                                            jfloat value2, jfloat value3,
                                                            jfloat value4, jfloat value5,
                                                            jfloat value6, jfloat value7,
                                                            jfloat value8, jfloat value9) {

    executeProcess->onProcessBandEQ(value0, value1, value2, value3,
                                    value4, value5, value6, value7,
                                    value8, value9);

}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_onBandValues(JNIEnv *env, jobject instance,
                                                         jfloat low, jfloat mid,
                                                         jfloat high) {
    executeProcess->onBandValues(low, mid, high);

}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_onCompressorValue(JNIEnv *env, jobject instance,
                                                              jfloat dryWetPercent,
                                                              jfloat ratio, jfloat attack,
                                                              jfloat release,
                                                              jfloat threshold,
                                                              jfloat hpCutOffHz) {

    executeProcess->onCompressorValue(dryWetPercent, ratio, attack, release,
                                      threshold, hpCutOffHz);

}extern "C"
#pragma clang diagnostic pop
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_onFxReverbValue(JNIEnv *env, jobject instance,
                                                            jint param, jfloat scaleValue) {

    executeProcess->setReverbParams(param, scaleValue);

}
#pragma clang diagnostic pop
extern "C"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_enableEffect(JNIEnv *env, jobject instance,
                                                         jboolean enable) {

    executeProcess->enableEffect(enable);

}
#pragma clang diagnostic pop
extern "C"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void
Java_vn_soft_dc_recordengine_RecorderEngine_startRecordWithOffset(JNIEnv *env,
                                                                  jobject instance,
                                                                  jint offset) {
    executeProcess->startRecordWithOffset(offset);
}
#pragma clang diagnostic pop
extern "C"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"
JNIEXPORT void JNICALL
Java_vn_soft_dc_recordengine_RecorderEngine_startRecordFilePath(JNIEnv *env, jobject instance,
                                                                jstring pathTarget_) {
    const char *pathTarget = env->GetStringUTFChars(pathTarget_, 0);
    executeProcess->startRecordPath(pathTarget);
    env->ReleaseStringUTFChars(pathTarget_, pathTarget);
}
#pragma clang diagnostic pop