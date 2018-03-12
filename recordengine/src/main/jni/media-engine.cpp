#include <media-engine.h>
#include <SuperpoweredSimple.h>
#include <SuperpoweredCPU.h>
#include <android/log.h>
#include <malloc.h>
#include <stdio.h>
#include <string.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include "record-engine.h"

static jmethodID midStrCallbackFromC;
static jmethodID midMusicCompletion;
static jmethodID midMusicStart;
static const char *sigStr = "()V";

static JNIEnv *jniEnv;
static jobject objectClass;
static JavaVM *javaVM = NULL;
static jclass activityClass;

static void callbackFromC() {
    (jniEnv)->CallVoidMethod(objectClass, midStrCallbackFromC);
}

static void onMusicCompletion() {
    (jniEnv)->CallVoidMethod(objectClass, midMusicCompletion);
}

static void onMusicStart() {
//    (jniEnv)->GetJavaVM(&javaVM);
//    objectClass = jniEnv->NewGlobalRef(objectClass);
//
//    javaVM->AttachCurrentThread(&jniEnv, NULL);
//    midMusicStart = (jniEnv)->GetMethodID(targetClass, "onMusicStart", sigStr);
//
//    (jniEnv)->CallVoidMethod(objectClass, midMusicStart);
}


static void playerEventCallbackA(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event,
                                 void *__unused value) {
//    if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess) {
//        SuperpoweredAdvancedAudioPlayer *playerA = *((SuperpoweredAdvancedAudioPlayer **) clientData);
//        playerA->setBpm(126.0f);
//        playerA->setFirstBeatMs(353);
//        playerA->setPosition(playerA->firstBeatMs, false, false);
//    };
    SuperpoweredAdvancedAudioPlayer *playerA = *((SuperpoweredAdvancedAudioPlayer **) clientData);
//    switch (event) {
//        case SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess:
//            __android_log_print(ANDROID_LOG_DEBUG, "audio_callback", "Audio Voice load success");
//            break;
//        case SuperpoweredAdvancedAudioPlayerEvent_EOF:
//            __android_log_print(ANDROID_LOG_DEBUG, "audio_callback",
//                                "Audio Voice End Of File: %d, CurrentPosition: %.2f",
//                                playerA->durationMs, playerA->positionMs);
//            break;
//        case SuperpoweredAdvancedAudioPlayerEvent_LoopEnd:
//            __android_log_print(ANDROID_LOG_DEBUG, "audio_callback", "Audio Voice Loop End");
//            break;
//    }
}

static void playerEventCallbackB(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event,
                                 void *__unused value) {
//    if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess) {
//        SuperpoweredAdvancedAudioPlayer *playerB = *((SuperpoweredAdvancedAudioPlayer **) clientData);
//        playerB->setBpm(123.0f);
//        playerB->setFirstBeatMs(40);
//        playerB->setPosition(playerB->firstBeatMs, false, false);
//    };

//    switch (event) {
//        case SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess:
////            onMusicStart();
//            printf("Audio Beat load success");
//            break;
//        case SuperpoweredAdvancedAudioPlayerEvent_EOF:
////            onMusicCompletion();
//            printf("Audio Beat End Of File");
//
//            break;
//        case SuperpoweredAdvancedAudioPlayerEvent_LoopEnd:
//            printf("Audio Beat Loop End");
//            break;
//    }
}

static bool audioProcessing(void *clientdata, short int *audioIO, int numberOfSamples,
                            int __unused samplerate) {
    return ((MediaEngine *) clientdata)->process(audioIO, (unsigned int) numberOfSamples);
}

MediaEngine::MediaEngine(unsigned int samplerate, unsigned int buffersize,
                         const char *path, int fileAoffset, int fileAlength,
                         int fileBoffset, int fileBlength) : activeFx(0),
                                                             crossValue(0.0f),
                                                             volB(0.0f),
                                                             volA(1.0f * headroom) {
    stereoBuffer = (float *) memalign(16, (buffersize + 16) * sizeof(float) * 2);

    playerA = new SuperpoweredAdvancedAudioPlayer(&playerA, playerEventCallbackA, samplerate, 0);
    playerA->open(path, fileAoffset, fileAlength);
    playerB = new SuperpoweredAdvancedAudioPlayer(&playerB, playerEventCallbackB, samplerate, 0);
    playerB->open(path, fileBoffset, fileBlength);

    playerA->syncMode = playerB->syncMode = SuperpoweredAdvancedAudioPlayerSyncMode_TempoAndBeat;

    roll = new SuperpoweredRoll(samplerate);
    filter = new SuperpoweredFilter(SuperpoweredFilter_Resonant_Lowpass, samplerate);
    flanger = new SuperpoweredFlanger(samplerate);
    reverb = new SuperpoweredReverb(samplerate);
    limiter = new SuperpoweredLimiter(samplerate);
    compressor = new SuperpoweredCompressor(samplerate);
    threeBandEQ = new Superpowered3BandEQ(samplerate);
    echo = new SuperpoweredEcho(samplerate);

    audioSystem = new SuperpoweredAndroidAudioIO(samplerate, buffersize, false, true,
                                                 audioProcessing, this, -1,
                                                 SL_ANDROID_STREAM_MEDIA,
                                                 buffersize * 2);
}

MediaEngine::MediaEngine(JNIEnv *env, jobject obj, unsigned int samplerate,
                         unsigned int buffersize,
                         const char *pathVoice, const char *pathBeat,
                         int fileAoffset, int fileAlength,
                         int fileBoffset, int fileBlength) : activeFx(0),
                                                             crossValue(0.0f),
                                                             volB(0.5f),
                                                             volA(0.5f) {

    stereoBuffer = (float *) memalign(16, (buffersize + 16) * sizeof(float) * 2);
    playerA = new SuperpoweredAdvancedAudioPlayer(&playerA, playerEventCallbackA, samplerate, 0);
    playerA->open(pathVoice, fileAoffset, fileAlength);

    playerB = new SuperpoweredAdvancedAudioPlayer(&playerB, playerEventCallbackB, samplerate, 0);
    playerB->open(pathBeat, fileBoffset, fileBlength);

    playerA->syncMode = playerB->syncMode = SuperpoweredAdvancedAudioPlayerSyncMode_TempoAndBeat;

    roll = new SuperpoweredRoll(samplerate);
    filter = new SuperpoweredFilter(SuperpoweredFilter_Resonant_Lowpass, samplerate);
    flanger = new SuperpoweredFlanger(samplerate);
    reverb = new SuperpoweredReverb(samplerate);
    limiter = new SuperpoweredLimiter(samplerate);
    compressor = new SuperpoweredCompressor(samplerate);
    threeBandEQ = new Superpowered3BandEQ(samplerate);
    echo = new SuperpoweredEcho(samplerate);

    float tmpValue[] = {40.0f, 63.0f, 125.0f, 250.0f, 500.0f, 1000.0f, 2000.0f, 4000.0f, 8000.0f,
                        16000.0f, 0.0f};
    eqBandList = tmpValue;

    nBandEQ = new SuperpoweredNBandEQ(samplerate, eqBandList);

    const char *pathTemp = "tempFile";
    char *tempResult = (char *) malloc(1 + strlen(pathVoice) + strlen(pathTemp));
    strcpy(tempResult, pathVoice);
    strcat(tempResult, pathTemp);
    recorder = new SuperpoweredRecorder(tempResult, 44100, 1);

    jniEnv = env;
    objectClass = obj;
    jclass targetClass = (jniEnv)->GetObjectClass(objectClass);

    midStrCallbackFromC = (jniEnv)->GetMethodID(targetClass, "callbackFromC", sigStr);
    midMusicCompletion = (jniEnv)->GetMethodID(targetClass, "onMusicCompletion", sigStr);
    midMusicStart = (jniEnv)->GetMethodID(targetClass, "onMusicStart", sigStr);

    isRemove = false;

    callbackFromC();
    audioSystem = new SuperpoweredAndroidAudioIO(samplerate, buffersize, false, true,
                                                 audioProcessing, this, -1,
                                                 SL_ANDROID_STREAM_MEDIA,
                                                 buffersize * 2);
}

MediaEngine::~MediaEngine() {
    if (isRemove)
        return;
    delete audioSystem;
    delete playerA;
    delete playerB;
    free(stereoBuffer);
    free(eqBandList);
}

void MediaEngine::onReset() {
    playerA->~SuperpoweredAdvancedAudioPlayer();
    playerB->~SuperpoweredAdvancedAudioPlayer();
}

void MediaEngine::onPlayPause(bool play) {
    if (!play) {
        playerA->pause();
        playerB->pause();
    } else {
//        bool masterIsA = (crossValue <= 0.5f);
//        playerA->play(!masterIsA);
//        playerB->play(masterIsA);
        playerA->play(false);
        playerB->play(false);
    };
    SuperpoweredCPU::setSustainedPerformanceMode(play); // <-- Important to prevent audio dropouts.
}

void MediaEngine::onLimiterState(bool state) {
    limiter->enable(state);
}

void MediaEngine::onCrossfader(int value) {
    crossValue = float(value) * 0.01f;
    if (crossValue < 0.01f) {
        volA = 1.0f * headroom;
        volB = 0.0f;
    } else if (crossValue > 0.99f) {
        volA = 0.0f;
        volB = 1.0f * headroom;
    } else { // constant power curve
        volA = cosf(float(M_PI_2) * crossValue) * headroom;
        volB = cosf(float(M_PI_2) * (1.0f - crossValue)) * headroom;
    };
}

void MediaEngine::onFxSelect(int value) {
    __android_log_print(ANDROID_LOG_VERBOSE, "MediaEngine", "FXSEL %i", value);
    activeFx = (unsigned char) value;
}

void MediaEngine::onFxOff() {
    filter->enable(false);
    roll->enable(false);
    flanger->enable(false);
}

#define MINFREQ 60.0f
#define MAXFREQ 20000.0f

static inline float floatToFrequency(float value) {
    if (value > 0.97f) return MAXFREQ;
    if (value < 0.03f) return MINFREQ;
    value = powf(10.0f,
                 (value + ((0.4f - fabsf(value - 0.4f)) * 0.3f)) * log10f(MAXFREQ - MINFREQ)) +
            MINFREQ;
    return value < MAXFREQ ? value : MAXFREQ;
}

void MediaEngine::onFxValue(int ivalue) {
    float value = float(ivalue) * 0.01f;
    switch (activeFx) {
        case 1:
            filter->setResonantParameters(floatToFrequency(1.0f - value), 0.2f);
            filter->enable(true);
            flanger->enable(false);
            roll->enable(false);
            break;
        case 2:
            if (value > 0.8f) roll->beats = 0.0625f;
            else if (value > 0.6f) roll->beats = 0.125f;
            else if (value > 0.4f) roll->beats = 0.25f;
            else if (value > 0.2f) roll->beats = 0.5f;
            else roll->beats = 1.0f;
            roll->enable(true);
            filter->enable(false);
            flanger->enable(false);
            break;
        default:
            flanger->setWet(value);
            flanger->enable(true);
            filter->enable(false);
            roll->enable(false);
    };
}

void MediaEngine::onCompressorValue(float dryWetPercent, float ratio, float attack,
                                    float release, float threshold, float hpCutOffHz) {
    compressor->enable(true);
    compressor->wet = dryWetPercent;
    compressor->ratio = ratio;
    compressor->attackSec = attack;
    compressor->releaseSec = release;
    compressor->thresholdDb = threshold;
    compressor->hpCutOffHz = hpCutOffHz;
}

void MediaEngine::onCompressEnable(bool isEnable) {
    compressor->enable(isEnable);
}

void MediaEngine::onPitchShift(int value) {
    playerA->setPitchShiftCents(value);
}

void MediaEngine::onBandValues(float low, float mid, float high) {
    threeBandEQ->enable(true);
    threeBandEQ->bands[0] = low;
    threeBandEQ->bands[1] = mid;
    threeBandEQ->bands[2] = high;
}

void MediaEngine::onEchoValue(float dry, float wet, float bpm, float beats, float decay,
                              float mix) {
    echo->enable(true);
    echo->dry = dry;
    echo->wet = wet;
    echo->beats = beats;
    echo->bpm = bpm;
    echo->decay = decay;
    echo->setMix(mix);
}


void MediaEngine::onProcessBandEQ(float value0, float value1, float value2, float value3,
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

void MediaEngine::onSeekTime(double d) {
    playerA->setPosition(d, false, false);
    playerB->setPosition(d, false, false);
}

double MediaEngine::onGetPosition() {
    return playerA->positionMs;
}

double MediaEngine::onGetTotalDuration() {
    return playerA->durationMs;
}

void MediaEngine::onFxReverbValue(int param, int value) {
    double scaleValue = 0.01 * value;
    __android_log_print(ANDROID_LOG_VERBOSE, "MediaEngine", "Param: %i , Value: %0.2f",
                        param, scaleValue);
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
    }
}

double MediaEngine::getReverbParams() {
    return 0;
}

bool MediaEngine::process(short int *output, unsigned int numberOfSamples) {
    bool masterIsA = (crossValue <= 0.5f);
    double masterBpm = masterIsA ? playerA->currentBpm : playerB->currentBpm;
    double msElapsedSinceLastBeatA = playerA->msElapsedSinceLastBeat;

    bool silence = !playerA->process(stereoBuffer, false, numberOfSamples, volA, masterBpm,
                                     playerB->msElapsedSinceLastBeat);

    roll->bpm = flanger->bpm = (float) masterBpm; // Syncing fx is one line.

    if (roll->process(silence ? NULL : stereoBuffer, stereoBuffer, numberOfSamples) &&
        silence)
        silence = false;
    if (!silence) {
        filter->process(stereoBuffer, stereoBuffer, numberOfSamples);
        flanger->process(stereoBuffer, stereoBuffer, numberOfSamples);
        reverb->process(stereoBuffer, stereoBuffer, numberOfSamples);
        limiter->process(stereoBuffer, stereoBuffer, numberOfSamples);
        compressor->process(stereoBuffer, stereoBuffer, numberOfSamples);
        threeBandEQ->process(stereoBuffer, stereoBuffer, numberOfSamples);
        echo->process(stereoBuffer, stereoBuffer, numberOfSamples);
//        recorder->process(stereoBuffer, stereoBuffer, numberOfSamples);
        nBandEQ->process(stereoBuffer, stereoBuffer, numberOfSamples);
    };

    playerB->process(stereoBuffer, !silence, numberOfSamples, volB, masterBpm,
                     msElapsedSinceLastBeatA);

    if (!silence) SuperpoweredFloatToShortInt(stereoBuffer, output, numberOfSamples);
    return !silence;
}

void MediaEngine::startRecord(JNIEnv *env, jstring path, jstring temp) {
    const char *pathTemp = env->GetStringUTFChars(temp, false);
    const char *outPath = env->GetStringUTFChars(path, false);
//    recorder->setSamplerate(44100);
//    isRecording = recorder->start(outPath);
    env->ReleaseStringUTFChars(temp, pathTemp);
    env->ReleaseStringUTFChars(path, outPath);
}

void MediaEngine::stopRecord() {
//    if (isRecording) {
//        recorder->stop();
//    }
}


static MediaEngine *example = NULL;

extern "C" JNIEXPORT void Java_com_superpowered_crossexample_MediaEngine_MediaEngine(
        JNIEnv *javaEnvironment, jobject __unused obj, jint samplerate, jint buffersize,
        jstring apkPath, jint fileAoffset, jint fileAlength, jint fileBoffset, jint fileBlength) {

    const char *path = javaEnvironment->GetStringUTFChars(apkPath, JNI_FALSE);
    example = new MediaEngine((unsigned int) samplerate, (unsigned int) buffersize, path,
                              fileAoffset, fileAlength, fileBoffset, fileBlength);
    javaEnvironment->ReleaseStringUTFChars(apkPath, path);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_SuperpoweredProcessDouble(
        JNIEnv *env, jobject __unused obj, jint samplerate, jint buffersize,
        jstring pathVoice, jstring pathBeat, jint fileAoffset, jint fileAlength, jint fileBoffset,
        jint fileBlength) {
    const char *pathVoiceC = env->GetStringUTFChars(pathVoice, 0);
    const char *pathBeatC = env->GetStringUTFChars(pathBeat, 0);

    example = new MediaEngine(env, obj, (unsigned int) samplerate,
                              (unsigned int) buffersize,
                              pathVoiceC, pathBeatC, fileAoffset, fileAlength, fileBoffset,
                              fileBlength);

    env->ReleaseStringUTFChars(pathVoice, pathVoiceC);
    env->ReleaseStringUTFChars(pathBeat, pathBeatC);

}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onPlayPause(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, jboolean play) {
    example->onPlayPause(play);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onCrossfader(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, jint value) {
    example->onCrossfader(value);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onPitchShift(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, jint value) {
    example->onPitchShift(value);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onBandValues(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, jfloat low, jfloat mid, jfloat hi) {
    example->onBandValues(low, mid, hi);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onCompressEnable(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, jboolean value) {
    example->onCompressEnable(value);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onCompressorValue(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, float dryWetPercent, float ratio,
        float attack, float release, float threshold, float hpCutOffHz) {
    example->onCompressorValue(dryWetPercent, ratio, attack, release, threshold, hpCutOffHz);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onFxSelect(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, jint value) {
    example->onFxSelect(value);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onSeekTime(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, double value) {
    example->onSeekTime(value);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onProcessBandEQ(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, float value0, float value1,
        float value2, float value3,
        float value4, float value5, float value6, float value7,
        float value8, float value9) {
    example->onProcessBandEQ(value0, value1, value2, value3, value4, value5, value6, value7, value8,
                             value9);
}

extern "C" JNIEXPORT double Java_vn_soft_dc_recordengine_MediaEngine_onGetPosition(
        JNIEnv *__unused javaEnvironment, jobject __unused obj) {
    return example->onGetPosition();
}

extern "C" JNIEXPORT double Java_com_superpowered_crossexample_MediaEngine_getReverbParams(
        JNIEnv *__unused javaEnvironment, jobject __unused obj) {
    return example->getReverbParams();
}

extern "C" JNIEXPORT double Java_vn_soft_dc_recordengine_MediaEngine_onGetTotalDuration(
        JNIEnv *__unused javaEnvironment, jobject __unused obj) {
    return example->onGetTotalDuration();
}

extern "C" JNIEXPORT void Java_com_superpowered_crossexample_MediaEngine_onFxOff(
        JNIEnv *__unused javaEnvironment, jobject __unused obj) {
    example->onFxOff();
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onReset(
        JNIEnv *__unused javaEnvironment, jobject __unused obj) {
    example->onReset();
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onFxValue(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, jint value) {
    example->onFxValue(value);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onEchoValue(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, float dry, float wet, float bpm,
        float beats, float decay,
        float mix) {
    example->onEchoValue(dry, wet, bpm, beats, decay, mix);
}

extern "C" JNIEXPORT void Java_com_superpowered_crossexample_MediaEngine_startRecord(
        JNIEnv *javaEnvironment, jobject __unused obj, jstring path, jstring temp) {
    example->startRecord(javaEnvironment, path, temp);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_stopRecord(
        JNIEnv *__unused javaEnvironment, jobject __unused obj) {
    example->stopRecord();
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onFxReverbValue(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, jint param, jint value) {
    example->onFxReverbValue(param, value);
}

extern "C" JNIEXPORT void Java_vn_soft_dc_recordengine_MediaEngine_onLimiterOpenClose(
        JNIEnv *__unused javaEnvironment, jobject __unused obj, jboolean state) {
    example->onLimiterState(state);
}