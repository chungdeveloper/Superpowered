#include "media-engine.h"
#include <SuperpoweredSimple.h>
#include <SuperpoweredCPU.h>
#include <android/log.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <malloc.h>
#include <string.h>

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
    return ((SuperpoweredProcess *) clientdata)->process(audioIO, (unsigned int) numberOfSamples);
}

SuperpoweredProcess::SuperpoweredProcess(JNIEnv *env, jobject obj, unsigned int samplerate,
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
    jniEnv = env;
    objectClass = obj;
    jclass targetClass = (jniEnv)->GetObjectClass(objectClass);

//    midStrCallbackFromC = (jniEnv)->GetMethodID(targetClass, "callbackFromC", sigStr);
//    midMusicCompletion = (jniEnv)->GetMethodID(targetClass, "onMusicCompletion", sigStr);
//    midMusicStart = (jniEnv)->GetMethodID(targetClass, "onMusicStart", sigStr);

    isRemove = false;

//    callbackFromC();
//    onMusicStart();
//    onMusicCompletion();
    audioSystem = new SuperpoweredAndroidAudioIO(samplerate, buffersize, false, true,
                                                 audioProcessing, this, -1,
                                                 SL_ANDROID_STREAM_MEDIA,
                                                 buffersize * 2);
}

SuperpoweredProcess::~SuperpoweredProcess() {
    if (isRemove)
        return;
    delete audioSystem;
    delete playerA;
    delete playerB;
    free(stereoBuffer);
    free(eqBandList);
}

void SuperpoweredProcess::onReset() {
    playerA->~SuperpoweredAdvancedAudioPlayer();
    playerB->~SuperpoweredAdvancedAudioPlayer();
}

void SuperpoweredProcess::onPlayPause(bool play) {
    if (!play) {
        playerA->pause();
        playerB->pause();
    } else {
//        bool masterIsA = (crossValue <= 0.5f);
        playerA->play(true);
        playerB->play(true);
    };
    SuperpoweredCPU::setSustainedPerformanceMode(play); // <-- Important to prevent audio dropouts.
}

void SuperpoweredProcess::onLimiterState(bool state) {
    limiter->enable(state);
}

void SuperpoweredProcess::onCrossfader(int value) {
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

void SuperpoweredProcess::onFxSelect(int value) {
    __android_log_print(ANDROID_LOG_VERBOSE, "SuperpoweredProcess", "FXSEL %i", value);
    activeFx = (unsigned char) value;
}

void SuperpoweredProcess::onFxOff() {
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

void SuperpoweredProcess::onFxValue(int ivalue) {
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

void SuperpoweredProcess::onCompressorValue(float dryWetPercent, float ratio, float attack,
                                            float release, float threshold, float hpCutOffHz) {
    compressor->enable(true);
    compressor->wet = dryWetPercent;
    compressor->ratio = ratio;
    compressor->attackSec = attack;
    compressor->releaseSec = release;
    compressor->thresholdDb = threshold;
    compressor->hpCutOffHz = hpCutOffHz;
}

void SuperpoweredProcess::onCompressEnable(bool isEnable) {
    compressor->enable(isEnable);
}

void SuperpoweredProcess::onPitchShift(int value) {
    playerA->setPitchShiftCents(value);
}

void SuperpoweredProcess::onBandValues(float low, float mid, float high) {
    threeBandEQ->enable(true);
    threeBandEQ->bands[0] = low;
    threeBandEQ->bands[1] = mid;
    threeBandEQ->bands[2] = high;
}

void SuperpoweredProcess::onEchoValue(float dry, float wet, float bpm, float beats, float decay,
                                      float mix) {
    echo->enable(true);
    echo->dry = dry;
    echo->wet = wet;
    echo->beats = beats;
    echo->bpm = bpm;
    echo->decay = decay;
    echo->setMix(mix);
}


void SuperpoweredProcess::onProcessBandEQ(float value0, float value1, float value2, float value3,
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

void SuperpoweredProcess::onSeekTime(double d) {
    playerA->setPosition(d, false, false);
    playerB->setPosition(d, false, false);
}

double SuperpoweredProcess::onGetPosition() {
    return playerA->positionMs;
}

double SuperpoweredProcess::onGetTotalDuration() {
    return playerA->durationMs;
}

void SuperpoweredProcess::onFxReverbValue(int param, float value) {
    double scaleValue = value;//
    __android_log_print(ANDROID_LOG_VERBOSE, "SuperpoweredProcess", "Param: %i , Value: %0.2f",
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


void SuperpoweredProcess::onVolumeVoice(int value) {
    volA = value * 0.01;
}

void SuperpoweredProcess::onVolumeBeat(int value) {
    volB = value * 0.01;
//    if (volumeChange > 0.99f) {
//        volB = 1.0f * headroom;
//    } else if (volumeChange < 0.01f) {
//        volB = 0.0f;
//    } else {
//        volB = cosf(float(M_PI_2) * volumeChange) * headroom;
//    }
}

double SuperpoweredProcess::getReverbParams() {
    return 0;
}

bool SuperpoweredProcess::process(short int *output, unsigned int numberOfSamples) {
    bool masterIsA = (crossValue <= 0.5f);
    double masterBpm = masterIsA ? playerA->currentBpm : playerB->currentBpm;
    double msElapsedSinceLastBeatA = playerA->msElapsedSinceLastBeat;

    bool silence = !playerA->process(stereoBuffer, false, numberOfSamples, volA, masterBpm,
                                     playerB->msElapsedSinceLastBeat);

    roll->bpm = flanger->bpm = (float) masterBpm; // Syncing fx is one line.
//
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
        nBandEQ->process(stereoBuffer, stereoBuffer, numberOfSamples);
    };

    playerB->process(stereoBuffer, !silence, numberOfSamples, volB, masterBpm,
                     msElapsedSinceLastBeatA);
//    playerB->process(stereoBuffer, true, numberOfSamples, volB, masterBpm,
//                     msElapsedSinceLastBeatA);

    // The stereoBuffer is ready now, let's put the finished audio into the requested buffers.
//    if (!silence) SuperpoweredFloatToShortInt(stereoBuffer, output, numberOfSamples);
    if (!silence) SuperpoweredFloatToShortInt(stereoBuffer, output, numberOfSamples);
    return !silence;
}

static SuperpoweredProcess *process = NULL;


extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_MediaEngine_onVolumeVoice(JNIEnv *env, jobject instance,
                                                       jint value) {

    process->onVolumeVoice(value);

}

extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_MediaEngine_onVolumeBeat(JNIEnv *env, jobject instance,
                                                      jint value) {

    process->onVolumeBeat(value);

}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onPlayPause(JNIEnv *env, jobject instance,
                                                     jboolean play) {
    process->onPlayPause(play);
}


extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onFxSelect(JNIEnv *env, jobject instance,
                                                    jint value) {
    process->onFxSelect(value);
}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onSeekTime(JNIEnv *env, jobject instance,
                                                    jdouble v) {

    process->onSeekTime(v);

}

extern "C" JNIEXPORT  jdouble
Java_vn_soft_dc_recordengine_MediaEngine_onGetPosition(JNIEnv *env, jobject instance) {

    return process->onGetPosition();

}

extern "C" JNIEXPORT  jdouble
Java_vn_soft_dc_recordengine_MediaEngine_onGetTotalDuration(JNIEnv *env,
                                                            jobject instance) {

    return process->onGetTotalDuration();

}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onCompressEnable(JNIEnv *env, jobject instance,
                                                          jboolean isEnable) {
    process->onCompressEnable(isEnable);
}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onCompressorValue(JNIEnv *env,
                                                           jobject instance,
                                                           jfloat dryWetPercent,
                                                           jfloat ratio, jfloat attack,
                                                           jfloat release,
                                                           jfloat threshold,
                                                           jfloat hpCutOffHz) {

    process->onCompressorValue(dryWetPercent, ratio, attack, release, threshold, hpCutOffHz);

}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onFxValue(JNIEnv *env, jobject instance,
                                                   jint value) {

    process->onFxValue(value);

}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onPitchShift(JNIEnv *env, jobject instance,
                                                      jint value) {

    process->onPitchShift(value);

}

//extern "C" JNIEXPORT  jdoubleArray
//Java_vn_soft_dc_recordengine_MediaEngine_getReverbParams(JNIEnv *env,
//                                                                  jobject instance) {
//
//    // TODO
//
//}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onEchoValue(JNIEnv *env, jobject instance,
                                                     jfloat dry, jfloat wet, jfloat bpm,
                                                     jfloat beats, jfloat decay,
                                                     jfloat mix) {

    process->onEchoValue(dry, wet, bpm, beats, decay, mix);

}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onBandValues(JNIEnv *env, jobject instance,
                                                      jfloat low, jfloat mid,
                                                      jfloat hi) {

    process->onBandValues(low, mid, hi);

}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onFxReverbValue(JNIEnv *env, jobject instance,
                                                         jint reverb_type, jfloat value) {

    process->onFxReverbValue(reverb_type, value);

}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_stopRecord(JNIEnv *env, jobject instance) {

    // TODO

}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onReset(JNIEnv *env, jobject instance) {

    process->onReset();

}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_startRecord(JNIEnv *env, jobject instance,
                                                     jstring path_, jstring tempPath_) {
    const char *path = env->GetStringUTFChars(path_, 0);
    const char *tempPath = env->GetStringUTFChars(tempPath_, 0);

    // TODO

    env->ReleaseStringUTFChars(path_, path);
    env->ReleaseStringUTFChars(tempPath_, tempPath);
}

extern "C" JNIEXPORT  void
Java_vn_soft_dc_recordengine_MediaEngine_onProcessBandEQ(JNIEnv *env, jobject instance,
                                                         jfloat value0, jfloat value1,
                                                         jfloat value2, jfloat value3,
                                                         jfloat value4, jfloat value5,
                                                         jfloat value6, jfloat value7,
                                                         jfloat value8, jfloat value9) {

    process->onProcessBandEQ(value0, value1, value2, value3, value4, value5, value6, value7, value8,
                             value9);

}


extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_MediaEngine_onLimiterOpenClose(JNIEnv *env,
                                                            jobject instance,
                                                            jboolean state) {

    process->onLimiterState(state);
}

extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_MediaEngine_onCrossfader(JNIEnv *env, jobject instance,
                                                      jint value) {

    process->onCrossfader(value);

}

extern "C" JNIEXPORT void
Java_vn_soft_dc_recordengine_MediaEngine_SuperpoweredProcessDouble(JNIEnv *env,
                                                                   jobject instance,
                                                                   jint samplerate,
                                                                   jint buffersize,
                                                                   jstring pathVoice_,
                                                                   jstring pathBeat_,
                                                                   jint fileAoffset,
                                                                   jint fileAlength,
                                                                   jint fileBoffset,
                                                                   jint fileBlength) {
    const char *pathVoice = env->GetStringUTFChars(pathVoice_, 0);
    const char *pathBeat = env->GetStringUTFChars(pathBeat_, 0);

    process = new SuperpoweredProcess(env, instance, (unsigned int) samplerate,
                                      (unsigned int) buffersize,
                                      pathVoice, pathBeat, fileAoffset, fileAlength, fileBoffset,
                                      fileBlength);

    env->ReleaseStringUTFChars(pathVoice_, pathVoice);
    env->ReleaseStringUTFChars(pathBeat_, pathBeat);
}